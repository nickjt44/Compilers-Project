import java.lang.*;
import java.io.*;
import java.util.*;
import java.lang.Math;

public class ScanMeta{
	File fileInput;
	
	ScanMeta(String filename){
		fileInput = new File(filename);
		
	}
	
	String getMetas(){
		String output = "";
		RegularExpressions regex = new RegularExpressions();
		try{
			BufferedReader b = new BufferedReader(new FileReader(fileInput));
			for (String s = b.readLine(); s != null; s = b.readLine()){
				System.out.println(s);
				if (s.length() >= 1){
					if (s.charAt(0) == '#'){
						output = output + s + "\n";
					}
				}
			}
			b.close();
			return output + "\n";
		} catch (IOException e){
			System.out.println("Invallid file!");
			System.exit(0);
		}
		return output;
	}
	
}