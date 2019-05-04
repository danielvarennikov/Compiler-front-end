package ba55_compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import ba55_compiler.lexical_ANALyzer.Type;
//The class we use to check how the program works
public class Runner {

	public static void main(String[] args) {
		String input="";
		Scanner scanner;
		try {
			//Enter your text file you want to parse the expressions from
			scanner = new Scanner(new File("C://Users//Dani//Desktop//java//ba55_compiler//toParse.txt"));
			while(scanner.hasNextLine()){
			    input=input+scanner.nextLine(); 

			 }
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}
		
		lexical_ANALyzer anal=new lexical_ANALyzer();
		LinkedList<Token<Type, String>> tokens = anal.analyze(input);
		
        Parser parser=new Parser(tokens);
        LinkedList parsed= parser.parse();
        Iterator<Token> iter=parsed.iterator();
        while(iter.hasNext()) {
        	System.out.println(iter.next().toString());
        }

	}

}
