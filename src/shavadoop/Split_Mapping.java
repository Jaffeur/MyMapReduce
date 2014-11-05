package shavadoop;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Split_Mapping extends Thread{
	String text;
	String file;
	HashMap<String, Integer> words_map = new HashMap<String, Integer>();
	
	public Split_Mapping(String text, String file){
		this.text=text;
		this.file = file;
	}
	
	public void run(){
		String table []= text.split("\\s+");
		for (String word : table){
			if(words_map.containsKey(word)) words_map.put(word, words_map.get(word)+1);
			else words_map.put(word, 1);
		}
	}
	
	public void write_um() throws IOException{
		FileWriter fr = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fr);
		for (String key : words_map.keySet()){
			bw.write(key + " " + words_map.get(key));
			bw.newLine();
		}
		bw.close();
		fr.close();
	}
}

static void main(String [] args){
	String file = args[0];
	String text = 
	Split_Mapping sm = new Split_Mapping(text, file);
}