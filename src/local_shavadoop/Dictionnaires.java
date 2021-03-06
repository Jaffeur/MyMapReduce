package local_shavadoop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Dictionnaires {
	private HashMap<String,ArrayList<String>> mots_dans_um;
	
	public Dictionnaires(){
		this.mots_dans_um = new HashMap<String, ArrayList<String>>();
	}
	
	public void fill_with_mots(String file) throws IOException{
		if(file != null){
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			String line = null;
			while( (line = br.readLine()) != null){
				String mot  = line.split("\\s")[0];
				if (mot.length() > 1){
					if(!mots_dans_um.containsKey(mot)){
						ArrayList<String> al = new ArrayList<String>();
						al.add(file);
						mots_dans_um.put(mot, al);
					}else{
						if(!mots_dans_um.get(mot).contains(file)){
							mots_dans_um.get(mot).add(file);
						}
					}
				}
			}
			br.close();
			fr.close();
		}
		//show_dict();
	}
	
	public void show_dict(){
		for(String key : this.mots_dans_um.keySet()){
			System.out.println("key:"+key + " list:"+this.mots_dans_um.get(key));
		}
	}
	
	public HashMap<String, ArrayList<String>> get_mots_dans_um(){
		return mots_dans_um;
	}
	
}
