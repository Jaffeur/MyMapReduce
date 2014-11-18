package local_shavadoop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reduce_shavadoop.Reduce_Map;


/**
 * Author: arthurdupont
 * Classe qui va contrôler le job Map Reduce
 * **/

public class Main {
	
	private ArrayList<String> liste_machines_dist;
	
	public Main() {
		this.liste_machines_dist = new ArrayList<String>();
	}
	
	/**
	 * Main method
	 * @throws InterruptedException 
	 * **/
	public static void main(String[] args) throws IOException, InterruptedException{
		Main main = new Main();
		String file = "./liste_pc"; //where we will write and read our @IP 
		String command = "ls -l";
		main.getMachinesIP(file);  //get all neigbourg machines IP adress
		main.map_reduce_job("input.txt");
	}
		
	
	
	/**
	 * method to get IP adresses of all neigbourg machines
	 * **/
	public void getMachinesIP(String file) throws IOException{
		//Regex d'une adress iP
		String regex = "[0-9]{3}.[0-9]{3}.([0-9]{2}|[0-9]{3}).([0-9]{3}|[0-9]{2}|[0-9])";
		Pattern pattern = Pattern.compile(regex);
		
		//String a = "arp -a | awk {split($2,a,"("); split(a[2],b,\")\"); print b[1] }'"; //commande de recherche de voisins bis
		String command = "nmap -sP 137.194.35.0/24";
		Process p = Runtime.getRuntime().exec(command);  //executer commande qui va découvrir les pc dans le réseau
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); 
		
		String line = "";
		System.out.println("Liste des machines disponibles dans le réseau: ");
		while((line = reader.readLine())!= null){
			Matcher matcher = pattern.matcher(line);  //récupère l'adresse ip avec un regex
			if (matcher.find()){
				System.out.println(matcher.group());
				this.liste_machines_dist.add(matcher.group());
			}
		}
		System.out.println("Nombres total de machines disponibles: " + liste_machines_dist.size());
		reader.close();
	}
	
	
	
	/**
	 * 
	 * methode pour diviser le texte en différentes lignes
	 * @throws IOException 
	 * @throws InterruptedException 
	 *  
	 */
	
	//TODO envoyer sur des machines distantes, créer un dictionnaire avec les machines et les fichiers dessus
	//TODO serialiser
	public void map_reduce_job(String input) throws IOException, InterruptedException {
		Dictionnaires dics = new Dictionnaires();
		ArrayList<Parallelize> mappeurs = new ArrayList<Parallelize>();  
		ArrayList<Parallelize> reducers = new ArrayList<Parallelize>();  
		ArrayList<String> ums = new ArrayList<String>(); //liste des fichiers um //TODO ajouter les machines correspondantes
		
		FileReader fr = new FileReader(input);
		BufferedReader br = new BufferedReader(fr);
		
		//pour chaque ligne on split et on fait traiter par un thread //une machine puis on remplir les tables
		String line = "";
		int n = 1;
		while((line = br.readLine()) != null){
			//créer un fichier um en l'enregistrer
			File f = new File("um"+n+".txt");
			f.createNewFile();
			ums.add(f.getAbsolutePath());
			
			//traitement dans les threads
			String ip = this.liste_machines_dist.get((n-1)%liste_machines_dist.size());  //on liste les adresses IP les unes après les autres et si la liste est finie on recommence au début
			String jar_path = "/cal/homes/adupont/workspace/SSH_client/Split_Mapping.jar";
			Parallelize par = new Parallelize(ip, jar_path, f.getAbsolutePath(), line);
			par.start();
			mappeurs.add(par);
			n++;
		}
		
		for(Parallelize par : mappeurs){
			par.join();
			dics.fill_with_mots(par.get_file_path());  //remplir le dictionnaire
		}
		
		br.close();
		fr.close();
		
		
		
		
			
		//shuffling
		File f = new File("result.txt");
		f.createNewFile();
		int n2 = 1;
		for( String key: dics.get_mots_dans_um().keySet()){
			String ip = this.liste_machines_dist.get((n2-1)%liste_machines_dist.size()); 
			String jar_path = "/cal/homes/adupont/workspace/SSH_client/Reduce_Map.jar";
			Parallelize par = new Parallelize(ip, dics.get_mots_dans_um().get(key), key, f.getAbsolutePath(), jar_path);
			par.start();
			reducers.add(par);
		}
		
		for(Parallelize par : reducers){
			par.join();
		}
		
	}
}


