

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Printer {
	
	// Assumptions
	final static int MAX_H = 99, MAX_W = 105;
	final static String charSet = "US-ASCII";
		//final static String charSet = "UTF-8";

	//Special chars
	final static int TAB = 9, NEW_LINE = 10, LINE_BREAK = 13, SPACE = 32;
	
	final static char[] PAGE_BREAK = new char[MAX_W];
	
	static BufferedReader reader = null;
	//static BufferedWriter writer = null;
	static PrintWriter writer = null;
	static int c, charCount = 0, lineCount = 0, freeSpace ;
	static String line = new String();
	static char[] word = new char[MAX_W];

	public static void main(String[] args) {
		
		Arrays.fill (PAGE_BREAK, '=');

		if (args.length != 2) {
			System.out.println("Usage: Printer <input file> <output file>");
			System.exit(0);
		}
		String inputFileName = args[0];
		String outputFileName = args[1];

		setReader(charSet, inputFileName);
		//setWriter(outputFileName);
		try {writer = new PrintWriter(outputFileName, charSet);
		}catch (FileNotFoundException e)  {

		}catch (UnsupportedEncodingException e){

		}
		assert(reader != null);
		assert(writer!=null);
		
		readAndWrite();
		
		closeFile();
	}

	private static void closeFile() {
		//try {
			writer.close();
		//} catch (IOException e) {
		//	System.out.println("Can't close file");
		//}
	}

	private static void readAndWrite() {
		try {
			while((c=reader.read()) != -1){
				//ignore these special chars if they are at the very beginning of a line
				if (line.length()==0 && charCount==0 && (c==TAB || c==NEW_LINE || c==LINE_BREAK || c==SPACE)){
					continue;
				} else {
					processChar();
				}	
			}
		} catch (IOException e) {
			System.out.println("Error reading character");
		}
	}

	private static void processChar() {
		//minus 1 for the space
		freeSpace = MAX_W - line.length() -1;
		if(line.length()==0||c==TAB) freeSpace++;
		//System.out.print((char)c);
		switch (c){
			case TAB: 
				//add in at most 4 spaces but not exceeding width
				//freeSpace++;
				for (int i = 0; i < 4; i++) {
					if (charCount == freeSpace){
						break;
					}
					word [charCount]=' ';
					charCount++;
				}
				line = line + new String(word);
				word = new char[MAX_W];
				charCount = 0;
				break;
				
			case SPACE:
				//word breaker
				if (line.length()==0){
					line = new String(word);
				}else{
				line = line + " " + new String(word);
			}
				word = new char[MAX_W];
				charCount = 0;
				break;
		
			case NEW_LINE:
			case LINE_BREAK:
				if (charCount<=freeSpace){
					if(line.length()==0){
						writeLine(new String(word));
					}else{
					writeLine(line +" " + new String(word));
				}
					line = "";
					word = new char[MAX_W];
					charCount = 0;
				} else {
					writeLine(line);
					line = "";
				}
				lineCount++;
				break;
			default:
				word[charCount] = (char)c;
				charCount++;
		}
		if(lineCount == MAX_H){
			writeLine(new String(PAGE_BREAK));
			lineCount = 0;
		}
	}

	private static void writeLine(String line) {
		System.out.println(line);
		//try {
			writer.println(line);
			//writer.write(line,0,line.length());
			//writer.newLine();
		//} catch (IOException e) {
			//System.out.println("Error writing to file");
		//}
	}

	private static void setWriter(String outputFileName) {
		File outputFile = new File(outputFileName);
		if (!outputFile.exists()){
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				System.out.println("Failed to create file");
			}
		}
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(outputFile.getAbsoluteFile()));
		} catch (IOException e) {
			System.out.println("Failed to get file writer");
		}
		//writer = out;
	}

	private static void setReader(final String charSet, String inputFile) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(inputFile)),
					Charset.forName(charSet)));
		} catch (FileNotFoundException e) {
			System.out.println("Invalid file!");
			System.exit(1);
		}
		reader = in; 
	}
}
