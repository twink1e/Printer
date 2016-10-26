import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;


public class Printer {

	// Assumptions
	// page height is 99 lines, excluding the page break
	// page width is 105 chars
	final static int MAX_H = 99, MAX_W = 105;
	final static String charSet = "US-ASCII";

	// Special chars
	final static int TAB = 9, NEW_LINE = 10, LINE_BREAK = 13, SPACE = 32;

	final static char[] PAGE_BREAK = new char[MAX_W];

	static BufferedReader reader = null;
	static BufferedWriter writer = null;

	static int c, //ascii code of current char
		charCount = 0, //number of char in the current line
		lineCount = 0, //number of lines in the current page
		currentWord = 0; //the index of the start of the current word in the line
	static char[] line = new char[MAX_W];

	public static void main(String[] args) {

		Arrays.fill(PAGE_BREAK, '=');

		if (args.length != 2) {
			System.out.println("Usage: Printer <input file> <output file>");
			System.exit(0);
		}
		String inputFileName = args[0];
		String outputFileName = args[1];

		setReader(charSet, inputFileName);
		setWriter(outputFileName);

		assert (reader != null);
		assert (writer != null);

		readAndWrite();

		closeFiles();

	}

	private static void closeFiles() {
		try {
			reader.close();
			writer.close();
		} catch (IOException e) {
			System.out.println("Can't close file");
		}
	}

	private static void readAndWrite() {
		try {
			while ((c = reader.read()) != -1) {
				// ignore these special chars if they are at the very beginning
				// of a line
				if (charCount == 0
						&& (c == TAB || c == NEW_LINE || c == LINE_BREAK || c == SPACE)) {
					continue;
				} else {
					processChar();
				}
			}
			writeLastLine(new String(line));
		} catch (IOException e) {
			System.out.println("Error reading character");
		}
	}

	private static void processChar() {
		switch (c) {
		case TAB:
			//add spaces for tab and set word breaker
			processTab();
			break;

		case SPACE:
			//set word breaker
			processSpace();
			break;

		case NEW_LINE:
		case LINE_BREAK:
			//print line
			processNewLine();
			break;
			
		default:
			line[charCount] = (char) c;
			charCount++;
		}
		//print row if row is full
		checkFullRow();
		//print page breaker if page is full
		checkFullPage();
	}

	private static void checkFullPage() {
		if (lineCount == MAX_H) {
			writeLine(new String(PAGE_BREAK));
			lineCount = 0;
		}
	}

	private static void checkFullRow()   {
		if (charCount == MAX_W) {
			boolean canBreakWord = checkNextChar();
			if (canBreakWord){
				currentWord = MAX_W;
			}
			//write line until last complete word
			writeLine(new String(line, 0, currentWord));
			lineCount++;
			prepareNextLine(canBreakWord);

		}
	}

	private static void prepareNextLine(boolean canBreakWord) {
		char[] temp = new char[MAX_W];
		charCount = 0;
		//add the unfinished word to the new line
		for (int i = 0; i < MAX_W - currentWord; i++) {
			temp[i] = line[i + currentWord];
			charCount++;
		}
		if(!canBreakWord) {
			temp[charCount] = (char)c;
			charCount++;
		}
		line = temp;
		currentWord = 0;
	}

	private static boolean checkNextChar() {
		boolean canBreakWord = false;
		try {
			if ((c = reader.read()) != -1) {
				if (c == TAB || c == NEW_LINE || c == LINE_BREAK || c == SPACE) {
					canBreakWord = true;
				} 
			}
		} catch (IOException e) {
			System.out.println("Error reading character");
		}
		return canBreakWord;
	}

	private static void processNewLine() {
		writeLine(new String(line));
		line = new char[MAX_W];
		lineCount++;
		charCount = 0;
		currentWord = 0;
	}

	private static void processSpace() {
		line[charCount] = ' ';
		charCount++;
		currentWord = charCount;
	}

	private static void processTab() {
		// add in at most 4 spaces but not exceeding width
		// set last word points to the next position
		for (int i = 0; i < 4; i++) {
			if (charCount == MAX_W) {
				break;
			}
			line[charCount] = ' ';
			charCount++;
		}
		currentWord = charCount;
	}

	private static void writeLine(String line) {
		try {
			writer.write(line);
			writer.newLine();
		} catch (IOException e) {
			System.out.println("Error writing to file");
		}
	}
	
	private static void writeLastLine(String line) {
		try {
			writer.write(line, 0, line.length());
		} catch (IOException e) {
			System.out.println("Error writing to file");
		}
	}

	private static void setWriter(String outputFileName) {
		File outputFile = new File(outputFileName);
		if (!outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				System.out.println("Failed to create file");
			}
		}
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(
					outputFile.getAbsoluteFile()));
		} catch (IOException e) {
			System.out.println("Failed to get file writer");
		}
		writer = out;
	}

	private static void setReader(final String charSet, String inputFile) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					new File(inputFile)), Charset.forName(charSet)));
		} catch (FileNotFoundException e) {
			System.out.println("Invalid file!");
			System.exit(1);
		}
		reader = in;

	}
}

