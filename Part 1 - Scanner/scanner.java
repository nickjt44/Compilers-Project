import java.lang.String;
import java.io.*;

/* Code written by Nick Taverner */

class Scanner{
	
	//string of the entire input file
	String fileString = "";
	String outString = "";
	char[] fileCharacters = {};
	int index = 0; //index as we iterate through the string of characters in the file
	int wordStart = 0; //indicates the start of a token
	char currentChar;
	
	int flag = 0;
	
	//constructor, initializes fileString
	public Scanner(File f){
		try{
			BufferedReader br = new BufferedReader(new FileReader(f));
			for (String s = br.readLine(); s != null; s = br.readLine()){
				fileString = fileString + s + "\n";
			}
			fileCharacters = fileString.toCharArray(); //the array of characters the scanner will iterate over
			
			br.close();
		} catch (IOException ex){
			System.out.println("I/O Error!");
		}
	}
	
	//primary scanning function, runs through all legal characters
	//and calls methods for the more in-depth scanning processes
	public String Scan(){
		while (index < fileCharacters.length){
			if (flag == 1){
				return null;
			}
			char c = scanCharacter();
			if (c == '#'){
				wordStart = index-1;
				scanStatement();
			}
			else if (c == '/'){
				wordStart = index-1;
				scanComment();
			}
			else if (c == '\"'){
				wordStart = index-1;
				scanString();
			}
			else if (Character.isDigit(c) || c == '-'){
				wordStart = index-1;
				scanNumber();
			}
			else if (c == 'i'){
				wordStart = index-1;
				scanKeyword_i();
			}
			else if (c == 'r'){
				wordStart = index-1;
				scanKeyword_r();
			}
			else if (c == 'w'){
				wordStart = index-1;
				scanKeyword_w();
			}
			else if (c == 'b'){
				wordStart = index-1;
				scanKeyword_b();
			}
			else if (c == 'd'){
				wordStart = index-1;
				scanKeyword_decimal();
			}
			else if (c == 'p'){
				wordStart = index-1;
				scanKeyword_print();
			}
			else if (c == 'v'){
				wordStart = index-1;
				scanKeyword_void();
			}
			else if (c == 'c'){
				wordStart = index-1;
				scanKeyword_continue();
			}
			else if (Character.isLetter(c)){
				wordStart = index-1;
				scanIdentifier();
			}
			else if (c == ' '){
				outString = outString + Character.toString(c);
			}
			else if (c == '\n'){
				outString = outString + Character.toString(c);
			}
			else if (c == '\t'){
				outString = outString + Character.toString(c);
			}
			else if ((c == '(') || (c == ')') || (c == '{') || (c == '}') || (c == '[') || (c == ']')){
				outString = outString + Character.toString(c);
			}
			else if ((c == ',') || (c == ';') || (c == '+') || (c == '-') || (c == '*')){
				outString = outString + Character.toString(c);
			}
			else if (c == '='){
				c = scanCharacter();
				if (c == '='){
					index = index-1;
					outString = outString + charsToString(index-1,index+1);
					index = index+1;
				}
				else{
					index = index-1;
					outString = outString + Character.toString(fileCharacters[index-1]); /////
				}
			}
			else if (c == '!'){
				c = scanCharacter();
				if (c == '='){
					index = index-1;
					outString = outString + charsToString(index-1,index+1);
					index = index+1;
				}
				else{
					return null; //error
				}
			}
			else if (c == '>'){
				c = scanCharacter();
				if (c == '='){
					index = index-1;
					outString = outString + charsToString(index-1,index+1);
					index = index+1;
				}
				else{
					index = index-1;
					outString = outString + Character.toString(fileCharacters[index-1]);
				}
			}
			else if (c == '<'){
				c = scanCharacter();
				if (c == '='){
					index = index-1;
					outString = outString + charsToString(index-1,index+1);
					index = index+1;
				}
				else{
					index = index-1;
					outString = outString + Character.toString(fileCharacters[index-1]);
				}
			}
			else if (c == '&'){
				c = scanCharacter();
				if (c == '&'){
					index = index-1;
					outString = outString + charsToString(index-1,index+1);
					index = index+1;
				}
				else{
					return null;//error
				}
			}
			else if (c == '|'){
				c = scanCharacter();
				if (c == '|'){
					index = index-1;
					outString = outString + charsToString(index-1,index+1);
					index = index+1;
				}
				else{
					return null;//error
				}
			}
			else{
				return null;//error
			}
		}
		return outString;
	}
	
	//converts a sequence of characters in the character array back into a string
	public String charsToString(int startIndex, int endIndex){
		String s = "";
		int i = startIndex;
		while (i < endIndex){
			s = s + Character.toString(fileCharacters[i]);
			i++;
		}
		return s;
		
	}
	
	//reads the next character from the character array
	public char scanCharacter(){
		if (index == fileCharacters.length){
			return '0';//error
		}
		else{
			currentChar = fileCharacters[index];
			index += 1;
			return currentChar;
		}
	}
	
	//scans any word consisting of alphanumeric characters and _ which begins with
	//an alphabetic character and isn't a keyword
	public void scanIdentifier(){
		while (true){
			char c = scanCharacter();
			if (Character.isDigit(c) || Character.isLetter(c) || (c == '_') ){
				continue;
			}
			else{
				index = index - 1; //MAYBE CHANGE LATER
				String s = charsToString(wordStart, index);
				if (s.equals("main")){
					outString = outString + s;
				}
				else{
				outString = outString + "cs512" + s;
				}
				break;
			}
		}
	}
	
	//scans an integer, positive or negative
	public void scanNumber(){
		
		while (true){
			char c = scanCharacter();
			if (Character.isDigit(c)){
				continue;
			}
			else{
				index = index - 1;
				outString = outString + charsToString(wordStart, index);
				break;
			}
		}
	}
	
	//scans keyword beginning with i
	//if a word begins with i but doesn't match a keyword, scanIdentifier() is called
	public void scanKeyword_i(){
		char c = scanCharacter();
		if (c == 'f'){ //keyword if
			char c2 = scanCharacter();
			if (Character.isDigit(c2) || Character.isLetter(c2) || (c2 == '_') ){
				index = index-1;
				scanIdentifier();
			}
			else{
				outString = outString + charsToString(wordStart, index);
			}
		}
		else if	(c == 'n'){
			char c3 = scanCharacter();
			if (c3 == 't'){
				char c4 = scanCharacter();
				if (Character.isDigit(c4) || Character.isLetter(c4) || (c4 == '_') ){
					index = index-1;
					scanIdentifier();
				}
				else{
					outString = outString + charsToString(wordStart, index);
				}
			}
			else{
				index = index-1;
				scanIdentifier();
			}
		}
		else{
			index = index-1;
			scanIdentifier();
		}
	}
	
	//scans keyword beginning with r
	//if a word begins with r but doesn't match a keyword, scanIdentifier() is called
	public void scanKeyword_r(){
		char c = scanCharacter();
		if (c == 'e'){
			c = scanCharacter();
			if (c == 'a'){
				c = scanCharacter();
				if (c == 'd'){
					c = scanCharacter();
					if (Character.isDigit(c) || Character.isLetter(c) || (c == '_') ){
						index = index-1;
						scanIdentifier();
					}
					else{
						index = index - 1; //MAYBE CHANGE LATER
						outString = outString + charsToString(wordStart, index);
					}
				}
				else{
					index = index-1;
					scanIdentifier();
				}
			}
			else if (c == 't'){
				c = scanCharacter();
				if (c == 'u'){
					c = scanCharacter();
					if (c == 'r'){
						c = scanCharacter();
						if (c == 'n'){
							c = scanCharacter();
							if (Character.isDigit(c) || Character.isLetter(c) || (c == '_') ){
								index = index-1;
								scanIdentifier();
							}
							else{
								index = index - 1; //MAYBE CHANGE LATER
								outString = outString + charsToString(wordStart, index);
							}
						}
						else{
							index = index-1;
							scanIdentifier();
						}
					}
					else{
						index = index-1;
						scanIdentifier();
					}
				}
				else{
					index = index-1;
					scanIdentifier();
				}
			}
			else{
				index = index-1;
				scanIdentifier();
			}
		}
		else{
			index = index-1;
			scanIdentifier();
		}
	}
	
	//scans keyword void
	//if a word begins with v but doesn't match void, scanIdentifier() is called
	public void scanKeyword_void(){
		char c = scanCharacter();
		if (c == 'o'){
			c = scanCharacter();
			if (c == 'i'){
				c = scanCharacter();
				if (c == 'd'){
					c = scanCharacter();
					if (Character.isDigit(c) || Character.isLetter(c) || (c == '_') ){
						index = index-1;
						scanIdentifier();
					}
					else{
						index = index - 1; //MAYBE CHANGE LATER
						outString = outString + charsToString(wordStart, index);
					}
				}
				else{
					index = index-1;
					scanIdentifier();
				}
			}
			else{
				index = index-1;
				scanIdentifier();
			}
		}
		else{
			index = index-1;
			scanIdentifier();
		}
	}
	
	//scans keyword print
	//if a word begins with p but doesn't match print, scanIdentifier() is called
	public void scanKeyword_print(){
		char c = scanCharacter();
		if (c == 'r'){
			c = scanCharacter();
			if (c == 'i'){
				c = scanCharacter();
				if (c == 'n'){
					c = scanCharacter();
					if (c == 't'){
					c = scanCharacter();
					if (Character.isDigit(c) || Character.isLetter(c) || (c == '_') ){
						index = index-1;
						scanIdentifier();
					}
					else{
						index = index - 1; //MAYBE CHANGE LATER
						outString = outString + charsToString(wordStart, index);
					}
				}
				else{
					index = index-1;
					scanIdentifier();
				}
			}
			else{
				index = index-1;
				scanIdentifier();
			}
		}
		else{
			index = index-1;
			scanIdentifier();
		}
	}
		else{
			index = index-1;
			scanIdentifier();
		}
	}
	
	//scans keyword continue
	//if a word begins with c but doesn't match continue, scanIdentifier() is called
	public void scanKeyword_continue(){
		char c = scanCharacter();
		if (c == 'o'){
			c = scanCharacter();
			if (c == 'n'){
				c = scanCharacter();
				if (c == 't'){
					c = scanCharacter();
					if (c == 'i'){
						c = scanCharacter();
						if (c == 'n'){
							c = scanCharacter();
							if (c == 'u'){
								c = scanCharacter();
								if (c == 'e'){
									c = scanCharacter();
									if (Character.isDigit(c) || Character.isLetter(c) || (c == '_') ){
										index = index-1;
										scanIdentifier();
									}
									else{
										index = index - 1; //MAYBE CHANGE LATER
										outString = outString + charsToString(wordStart, index);
									}
								}
								else{
									index = index-1;
									scanIdentifier();
								}
							}
							else{
								index = index-1;
								scanIdentifier();
							}
						}
						else{
							index = index-1;
							scanIdentifier();
						}
					}
					else{
						index = index-1;
						scanIdentifier();
					}
				}
				else{
					index = index-1;
					scanIdentifier();
				}
			}
			else{
				index = index-1;
				scanIdentifier();
			}
		}
		else{
			index = index-1;
			scanIdentifier();
		}
	}

	//scans keyword decimal
	//if a word begins with d but doesn't match decimal, scanIdentifier() is called
	public void scanKeyword_decimal(){
		char c = scanCharacter();
		if (c == 'e'){
			c = scanCharacter();
			if (c == 'c'){
				c = scanCharacter();
				if (c == 'i'){
					c = scanCharacter();
					if (c == 'm'){
						c = scanCharacter();
						if (c == 'a'){
							c = scanCharacter();
							if (c == 'l'){
								c = scanCharacter();
								if (Character.isDigit(c) || Character.isLetter(c) || (c == '_') ){
									index = index-1;
									scanIdentifier();
								}
								else{
									index = index - 1; //MAYBE CHANGE LATER
									outString = outString + charsToString(wordStart, index);
								}
							}
								else{
									index = index-1;
									scanIdentifier();
								}
							}
							else{
								index = index-1;
								scanIdentifier();
							}
						}
						else{
							index = index-1;
							scanIdentifier();
						}
					}
					else{
						index = index-1;
						scanIdentifier();
					}
				}
				else{
					index = index-1;
					scanIdentifier();
				}
			}
			else{
				index = index-1;
				scanIdentifier();
			}
	}
	
		
	//scans keyword beginning with w
	//if a word begins with w but doesn't match a keyword, scanIdentifier() is called
	public void scanKeyword_w(){
		char c = scanCharacter();
		if (c == 'r'){
			c = scanCharacter();
			if (c == 'i'){
				c = scanCharacter();
				if (c == 't'){
					c = scanCharacter();
					if (c == 'e'){
						c = scanCharacter();
					
						if (Character.isDigit(c) || Character.isLetter(c) || (c == '_') ){
							index = index-1;
							scanIdentifier();
						}
						else{
							index = index - 1; //MAYBE CHANGE LATER
							outString = outString + charsToString(wordStart, index);
						}
					}
				else{
					index = index-1;
					scanIdentifier();
					}
				}
				else{
					index = index-1;
					scanIdentifier();
				}
			}
			else{
				index = index-1;
				scanIdentifier();
			}
		}
			else if (c == 'h'){
				c = scanCharacter();
				if (c == 'i'){
					c = scanCharacter();
					if (c == 'l'){
						c = scanCharacter();
						if (c == 'e'){
							c = scanCharacter();
							if (Character.isDigit(c) || Character.isLetter(c) || (c == '_') ){
								index = index-1;
								scanIdentifier();
							}
							else{
								index = index - 1; //MAYBE CHANGE LATER
								outString = outString + charsToString(wordStart, index);
							}
						}
						else{
							index = index-1;
							scanIdentifier();
						}
					}
					else{
						index = index-1;
						scanIdentifier();
					}
				}
				else{
					index = index-1;
					scanIdentifier();
				}
			}
			else{
				index = index-1;
				scanIdentifier();
			}
		}
	
	//scans keyword beginning with b
	//if a word begins with b but doesn't match a keyword, scanIdentifier() is called
	public void scanKeyword_b(){
		char c = scanCharacter();
		if (c == 'r'){
			c = scanCharacter();
			if (c == 'e'){
				c = scanCharacter();
				if (c == 'a'){
					c = scanCharacter();
					if (c == 'k'){
						c = scanCharacter();
					
						if (Character.isDigit(c) || Character.isLetter(c) || (c == '_') ){
							index = index-1;
							scanIdentifier();
						}
						else{
							index = index - 1; //MAYBE CHANGE LATER
							outString = outString + charsToString(wordStart, index);
						}
					}
				else{
					index = index-1; /////might change later
					scanIdentifier();
					}
				}
				else{
					index = index-1;
					scanIdentifier();
				}
			}
			else{
				index = index-1;
				scanIdentifier();
			}
		}
			else if (c == 'i'){
				c = scanCharacter();
				if (c == 'n'){
					c = scanCharacter();
					if (c == 'a'){
						c = scanCharacter();
						if (c == 'r'){
							c = scanCharacter();
							if (c == 'y'){
								c = scanCharacter();
								if (Character.isDigit(c) || Character.isLetter(c) || (c == '_') ){
									index = index-1;
								scanIdentifier();
							}
							else{
								index = index - 1; //MAYBE CHANGE LATER
								outString = outString + charsToString(wordStart, index);
							}
						}
						else{
							index = index-1;
							scanIdentifier();
						}
					}
					else{
						index = index-1;
						scanIdentifier();
					}
				}
				else{
					index = index-1;
					scanIdentifier();
				}
			}
			else{
				index = index-1;
				scanIdentifier();
			}
		}
			else{
				index = index-1;
				scanIdentifier();
			}
	}
		
	//scans a string (beginning " and ending ")
	//if \n is read before closing ", send error
	public void scanString(){
		while (true){
			char c = scanCharacter();
			if (c == '\"'){
				outString = outString + charsToString(wordStart, index);
				break;
			}
			else if (c == '\n'){
				flag = 1; //error
			}
		}
	}
	
	//scans any line beginning #
	//scans the entire line once confirmed to begin with #
	public void scanStatement(){
		while (true){
			char c = scanCharacter();
			if (c == '\n'){
				index = index - 1; //MAYBE CHANGE LATER
				outString = outString + charsToString(wordStart, index);
				break;
			}
		}
	}
	
	//scans a comment, ie. a string beginning // and ending \n, also
	//scans a single / if not followed by another /
	public void scanComment(){
		char c = scanCharacter();
		if (c != '/'){
			index = index - 1;
			outString = outString + Character.toString('/');
		}
		else{
			while (true){
				char c2 = scanCharacter();
				if (c2 == '\n'){
					index = index - 1; //MAYBE CHANGE LATER
					outString = outString + charsToString(wordStart, index);
					break;
			}
		}
	}
}
}

//class for main method
class Driver{
	public static void main(String[] args){
		File f = new File(args[0]);
		//File f = new File("fibonacci.c");
		Scanner scanner = new Scanner(f);
		String s = scanner.Scan(); //scans a program file, outputting an altered string with the same functionality
		if (s == null){ //if the returned string is null, an error was encountered
			System.out.println("Syntax Error!");
			System.exit(0);
		}
		String[] argSplit = args[0].split("\\.");
		//String[] argSplit = "fibonacci.c".split("\\.");
		
		File fout = new File(argSplit[0] + "_gen" + "." + argSplit[1]);
		
		try{
		BufferedWriter bw = new BufferedWriter(new FileWriter(fout));

		bw.write(s);
		bw.close();
		}
		catch (IOException err){
			System.out.println("IO Error!");
		}
	}
}