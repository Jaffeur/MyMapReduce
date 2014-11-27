package reduce_shavadoop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Reduce_Map{
	ArrayList<String> ums;
	String word, result_file;
	int count;
	
	public Reduce_Map(ArrayList<String> ums, String word, String result_file){
		this.ums = ums;
		this.word = word;
		this.result_file = result_file;
		this.count = 0;
	}
	
	public void reduce() throws IOException{
		for( String um : this.ums){
			try{
				FileReader fr = new FileReader(um);
				BufferedReader bf = new BufferedReader(fr);
				String line = "";
				while( (line = bf.readLine()) != null){
					String l[] = line.split("\\s+");
					String key = l[0];
					int n = Integer.parseInt(l[1]);
					if (key.equals(this.word)){
						this.count += n;
					}
				}
				bf.close();
				fr.close();
			}catch(Exception a){
				
			}
		}
		
		FileWriter fr = new FileWriter(result_file, true);
		fr.append(this.word + " " + this.count);
		fr.append(System.getProperty("line.separator"));
		fr.close();
	}
	
	public static void main(String args[]) throws IOException{
		String result_file = args[0];
		String word = args[1];
		String files="";
		ArrayList<String> um_files = new ArrayList<String>();
		for(int i = 2; i < args.length; i++){
			files += args[i] + " ";
		}
		for (String file : files.split("\\s")){
			um_files.add(file);
		}
		Reduce_Map rm = new Reduce_Map(um_files, word, result_file);
		rm.reduce();
	}
	
}
