package shavadoop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Reduce_Map extends Thread{
	ArrayList<String> ums;
	String word, result_file;
	int count;
	
	public Reduce_Map(ArrayList<String> ums, String word, String result_file){
		this.ums = ums;
		this.word = word;
		this.result_file = result_file;
		this.count = 0;
	}
	
	public void run(){
		for( String um : ums){
			try{
				FileReader fr = new FileReader(um);
				BufferedReader bf = new BufferedReader(fr);
				String line = "";
				while( (line = bf.readLine()) != null){
					String l[] = line.split("\\s+");
					String key = l[0];
					int n = Integer.parseInt(l[1]);
					System.out.println(word+" " +key + " " + n);
					if (key.equals(this.word)){
						this.count += n;
					}
				}
				bf.close();
				fr.close();
			}catch(Exception a){
				
			}
		}
		//System.out.println(word+ " "+ count);
	}
	
	public void write_res() throws IOException{
		FileWriter fr = new FileWriter(result_file, true);
		/*BufferedWriter bw = new BufferedWriter(fr);
		PrintWriter pw = new PrintWriter(bw);*/
		fr.append(this.word + " " + this.count);
		fr.append(System.getProperty("line.separator"));
		//fr.flush();
		/*pw.close();
		bw.close();*/
		fr.close();
	}
	
}
