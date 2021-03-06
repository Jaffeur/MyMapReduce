package local_shavadoop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.RowFilter.Entry;


/**
 * Author: arthurdupont
 * Classe qui va contrôler le job Map Reduce
 * **/

public class Main {
	
	private int numLine;
	private ArrayList<String> liste_machines_dist;
	
	public Main(int n) {
		this.numLine = n;
		this.liste_machines_dist = new ArrayList<String>();
	}
	
	/**
	 * Main method
	 * @throws InterruptedException 
	 * **/
	public static void main(String[] args) throws IOException, InterruptedException{
		long start = System.currentTimeMillis();
		Main main = new Main(1000);  //argument is the number of line to read
		String file = "./liste_pc"; //where we will write and read our @IP 
		main.getMachinesIP(file);  //get all neigbourg machines IP adress
		main.map_reduce_job("input.txt");
		long end = System.currentTimeMillis();
		System.out.println("MAP REDUCE FINISHED in " + (end - start) + " ms");
		main.word_sort("./result.txt");
	}
		
	public void word_sort(String file) throws IOException{
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		Map<String, Integer> smap = new HashMap<String, Integer>();
		ValueComparator bvc =  new ValueComparator(smap);
		TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
		
		String line = "";
		while((line = br.readLine()) != null){
			String d[] = line.split("\\s+");
			smap.put(d[0], Integer.parseInt(d[1]));
		}
		br.close();
		fr.close();
		System.out.println("Sorting...");
		sorted_map.putAll(smap);
		System.out.println("Sorting finished");
		
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		for(java.util.Map.Entry<String, Integer> e : sorted_map.entrySet()){
			bw.write(e.getKey() + "\t" + e.getValue());
			bw.newLine();
		}
		bw.close();
		fw.close();
	}
	
	/**
	 * method to get IP adresses of all neigbourg machines
	 * **/
	public void getMachinesIP(String file) throws IOException{
		//Regex d'une adress iP
		String regex = "[0-9]{3}.[0-9]{3}.([0-9]{2}|[0-9]{3}).([0-9]{3}|[0-9]{2}|[0-9])";
		Pattern pattern = Pattern.compile(regex);
		
		//String myIP = InetAddress.getLocalHost().getHostAddress();
		String command = "nmap -sP 137.194.35.0/24";
		Process p = Runtime.getRuntime().exec(command);  //executer commande qui va découvrir les pc dans le réseau
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); 
		
		String line = "";
		//System.out.println("Liste des machines disponibles dans le réseau: ");
		while((line = reader.readLine())!= null){
			Matcher matcher = pattern.matcher(line);  //récupère l'adresse ip avec un regex
			if (matcher.find()){
				//System.out.println(matcher.group());
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
		boolean stopReading = false;
		while(!stopReading){
			
			//read as many lines as asked
			String text = "";
			for(int i=0; i<this.numLine; i++){
				if( (line = br.readLine()) == null){
					stopReading = true;
					break;
				}
				text += " " + line;
			}
			
			//créer un fichier um en l'enregistrer
			File f = new File("um"+n+".txt");
			f.createNewFile();
			ums.add(f.getAbsolutePath());
			
			//traitement dans les threads
			String ip = this.liste_machines_dist.get((n-1)%liste_machines_dist.size());  //on liste les adresses IP les unes après les autres et si la liste est finie on recommence au début
			String jar_path = "/cal/homes/adupont/workspace/SSH_client/Split_Mapping.jar";
			Parallelize par = new Parallelize(ip, jar_path, f.getAbsolutePath(), text);
			par.start();
			mappeurs.add(par);
			n++;
		}
		
		//on attends la fin de l'execution des mappeurs
		for(Parallelize par : mappeurs){
			par.join(10000);
			par.stop_me();
			dics.fill_with_mots(par.get_file_path());  //remplir le dictionnaire
		}
		br.close();
		fr.close();
		
		
			
		//shuffling
		File f = new File("result.txt");
		f.delete();
		f.createNewFile();
		int n2 = 1;
		for( String key: dics.get_mots_dans_um().keySet()){
			String ip = this.liste_machines_dist.get((n2-1)%liste_machines_dist.size()); 
			String jar_path = "/cal/homes/adupont/workspace/SSH_client/Reduce_Map.jar";
			Parallelize par = new Parallelize(ip, dics.get_mots_dans_um().get(key), key, f.getAbsolutePath(), jar_path);
			par.start();
			reducers.add(par);
			n2++;
		}
		
		for(Parallelize par : reducers){
			par.join(10000);
			par.stop_me();
		}
		for(String um : ums){
			File f2 = new File(um);
			f2.delete();
		}
	}
}


