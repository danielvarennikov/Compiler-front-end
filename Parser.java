package ba55_compiler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import ba55_compiler.lexical_ANALyzer.Type;


public class Parser {
LinkedList<Token> tokens;

	public Parser(LinkedList list) {
		tokens=list;
	}

    private static int getPrecedence(Token current) {
        switch ((Type)current.getType()) {
        case ADD:
        case SUBTRACT:
            return 1;

        case MULTIPLY:
        case DIVIDE:
            return 2;
        }
        return -1;
    }

    public LinkedList parse() {
        Stack<Token> stack = new Stack<Token>();
        LinkedList parsed = new LinkedList();
        
        //The "table" of the the literals
        LinkedList knownLiterals = new LinkedList();
        
        Token t;

        for (int i = 0; i < tokens.size(); i++) {
            t = (Token) tokens.get(i);
            if (t.getType().equals(Type.OPERAND)) {
                parsed.add(t);
            
            //If a new variable is initialized add it to the "table" of known variables    
            }else if(t.getType().equals(Type.STRING) | t.getType().equals(Type.CHARACTER) | t.getType().equals(Type.BOOLEAN) | t.getType().equals(Type.INTEGER)){
            	knownLiterals.add(t);
            	parsed.add(t);
            	
            //If a literal is encountered check if it was initialized before(from the "table") and add it only if it was initialized 	
            }else if(t.getType().equals(Type.LITERAL)) {
            	String literalName = Parser.extractVariableName(t);
            	boolean found =false;
            	Iterator<Token> iter = knownLiterals.iterator();
            	while(iter.hasNext() & !found) {
            		String current = Parser.extractVariableName(iter.next());
            		if(literalName.equals(current)) {
            			found = true;
            		}
            	}
            	if(found == true) {
            		parsed.add(t);
            	}
            	
            }else {
                if (!stack.isEmpty() && getPrecedence(t) <= getPrecedence(stack.peek())) {
                	parsed.add(stack.pop());
                }
                stack.push(t);
            }
        }

        while (!stack.isEmpty()) {
        	parsed.add(stack.pop());
        }
        return parsed;
    }
    private static String extractVariableName(Token t) {
    	String name="";
    	String theToken=t.toString();
    	boolean started=false;
    	boolean finished=false;
    	for(int i=0;i<theToken.length() & !finished;i = i+1) {
    		
    		if(theToken.charAt(i) == ',') {
    			started=true;
    		}else if(theToken.charAt(i) == '$' | theToken.charAt(i) == '>') {
    			finished = true;
    		}else if(started == true) {
    			name = name + theToken.charAt(i);
    		}
    	}
    	
    	return name;
    }
	
	
}
