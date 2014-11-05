package shavadoop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Author: arthurdupont
 * **/

public class Main {
	
	static ArrayList<String> liste_machines_dist = new ArrayList<String>();
	
	/**
	 * Main method
	 * @throws InterruptedException 
	 * **/
	public static void main(String[] args) throws IOException, InterruptedException{
		String file = "./liste_pc"; //where we will write and read our @IP 
		String command = "ls -l";
		//getMachinesIP(file);  //get all neigbourg machines IP adress
		//launchProcesses(file, command);
		map_reduce_job("input.txt");
	}
		
	
	
	/**
	 * method to get IP adresses of all neigbourg machines
	 * **/
	public static void getMachinesIP(String file) throws IOException{
		FileWriter fw = new FileWriter(file, false);
		
		//Regex d'une adress iP
		String regex = "[0-9]{3}.[0-9]{3}.([0-9]{2}|[0-9]{3}).([0-9]{3}|[0-9]{2}|[0-9])";
		Pattern pattern = Pattern.compile(regex);
		
		String a = "arp -a | awk '{split($2,a,\"(\"); split(a[2],b,\")\"); print b[1] }'"; //commande de recherche de voisins bis
		
		Process p = Runtime.getRuntime().exec("nmap -sP 137.194.35.0/24");  //executer commande qui va découvrir les pc dans le réseau
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); 
		
		String line = "";
		while((line = reader.readLine())!= null){
			Matcher matcher = pattern.matcher(line);  //récupère l'adresse ip avec un regex
			if (matcher.find()){
				fw.append(matcher.group() + "\n");
			}
		}
		fw.close();
		reader.close();
	}
	
	
	/**
	 * method that will execute the process on each machines
	 * **/
	public static void launchProcesses(String file, String command) throws IOException{
		
		//lire dans le fichier file
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		
		//pour chaque ligne (@ip)
		String line="";
		while((line = br.readLine()) != null){
			liste_machines_dist.add(line);
			/**ExecOnMachine eom = new ExecOnMachine(line, command); //créer nouveau thread
			eom.start(); //lancer le thread*/
		}
		
		//fermer le file, buffer reader
		br.close();
		fr.close();
	}
	
	/**
	 * 
	 * methode pour diviser le texte en différentes lignes
	 * @throws IOException 
	 * @throws InterruptedException 
	 *  
	 */
	
	//TODO envoyer sur des machines distantes, créer un dictionnaire avec les machines et les fichiers dessus
	public static void map_reduce_job(String input) throws IOException, InterruptedException {
		ArrayList<Split_Mapping> mappeurs = new ArrayList<Split_Mapping>();  
		ArrayList<Reduce_Map> reducers = new ArrayList<Reduce_Map>();  
		ArrayList<String> ums = new ArrayList<String>(); //liste des fichiers um //TODO ajouter les machines correspondantes
		HashMap<String, ArrayList<String>> word_um = new HashMap<String, ArrayList<String>>();  //liste des "ichiers" UM associés à un mot 
		
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
			Split_Mapping ts = new Split_Mapping(line, f.getAbsolutePath());
			ts.start();
			mappeurs.add(ts);
			n++;
		}
		
		for(Split_Mapping th : mappeurs){
			th.join();
			th.write_um();
		}
		
		br.close();
		fr.close();
		
		
		
		//remplir le dictionnaire clé/ums //pré-shuffling
		for( String um : ums){
			FileReader fr2 = new FileReader(um);
			BufferedReader br2 = new BufferedReader(fr2);
			
			String line2 = "";
			while( (line = br2.readLine()) != null){
				String key = line.split("\\s+")[0];
				if (!word_um.containsKey(key)){
					ArrayList<String> a = new ArrayList<String>();
					a.add(um);
					word_um.put(key, a);
				}else if(!word_um.get(key).contains(um)){
					word_um.get(key).add(um);
				}

			}
			br2.close();
			fr2.close();
		}
		
			
		//shuffling
		for( String key: word_um.keySet()){
			File f = new File("result.txt");
			f.createNewFile();
			Reduce_Map rm = new Reduce_Map(word_um.get(key), key, f.getAbsolutePath());
			rm.start();
			reducers.add(rm);
		}
		
		for(Reduce_Map rm : reducers){
			rm.join();
			rm.write_res();
		}
		
	}
}


