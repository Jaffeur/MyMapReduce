package map_shavadoop;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Classe executée sur machine distante qui va mapper le texte et écrire le résultat dans un fichier umX.txt (X numéro de processus)
 * @author adupont
 *
 */

public class Split_Mapping{
	String text;   //ligne de texte à traiter
	String file;	//fichier dans lequel écrire le traitement
	HashMap<String, Integer> words_map = new HashMap<String, Integer>();
	
	public Split_Mapping(String text, String file){
		this.text=text;
		this.file = file;
	}
	
	public void split() throws IOException{
		String table []= text.split("\\s+");
		for (String word : table){
			if(words_map.containsKey(word)) words_map.put(word, words_map.get(word)+1);
			else words_map.put(word, 1);
		}

		FileWriter fr = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fr);
		for (String key : words_map.keySet()){
			bw.write(key + " " + words_map.get(key));
			bw.newLine();
		}
		bw.close();
		fr.close();
	}
	
	public void get_ip() throws SocketException{
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
	    while (e.hasMoreElements())
	    {
	        NetworkInterface n = e.nextElement();
	        System.out.println(n.getName());
	        Enumeration<InetAddress> ee = n.getInetAddresses();
	        while (ee.hasMoreElements())
	        {
	            InetAddress i = ee.nextElement();
	            System.out.println(i.getHostAddress());
	        }
	    }
	}
	
	public static void main(String args[]) throws IOException{
		String file = args[0];
		String text= "";
		for(int i = 1; i < args.length; i++){
			text += args[i] + " ";
		}
		Split_Mapping sm = new Split_Mapping(text, file);
		sm.split();
	}
		
}



