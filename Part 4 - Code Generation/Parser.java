import java.util.*;
import java.io.*;

/**
 * implements the main function that gets the file name and calls the scanner and parser 
 */

/**
 * @author Danny Reinheimer
 *
 */
public class Parser {

	
	/**
	 * starting point for the program
	 * @param args The file name to read in and parse
	 */
	public static void main(String[] args) {
		// checks to see if we are given any arguments
		/*if(args.length < 1) {
			System.out.println("Please provide an input file to process");
			System.exit(0);
		}*/
		Vector<Pair<TokenNames, String>> scannedTokens = new Vector<Pair<TokenNames, String>>();
		// run initialize and run the scanner
		//Scanner scanner = new Scanner(args[0]);
		Scanner scanner = new Scanner("test4.c");
		scannedTokens = scanner.runScanner();
		// initialize and run the parser
		RecursiveParsing RP = new RecursiveParsing(scannedTokens);
		int numfunctions = RP.parse();
		GenerateAST G = new GenerateAST(scannedTokens);
		ProgramNode p = G.parse();
		CodeGenerator2 CodeG = new CodeGenerator2(numfunctions);
		String output = CodeG.generateProgram(p);
		ScanMeta sm = new ScanMeta("test4.c");
		//ScanMeta sm = new ScanMeta(args[0]);
		output = "#define N 2000\n\n #define top mem[0]\n #define base mem[1]\n #define jumpReg mem[2]\n #define membase 3\n int mem[N];\n\n" + output;
		output = "#include <assert.h>\n" + "#include <stdlib.h>\n" + output;
		output = sm.getMetas() + output;
		
		File fout = new File("output1.c");
		
		try{
		BufferedWriter bw = new BufferedWriter(new FileWriter(fout));

		bw.write(output);
		bw.close();
		}
		catch (IOException err){
			System.out.println("IO Error!");
		}
	}

}
