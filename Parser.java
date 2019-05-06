package ba55_compiler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import ba55_compiler.lexical_ANALyzer.Type;


public class Parser {
static LinkedList<Token> tokens;
static LinkedList parsed = new LinkedList();

//The "table" of the the literals
static LinkedList knownLiterals = new LinkedList();


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

    private static int evaluateCondition(LinkedList<Token> tokens,int index) {

    	int i=index+1;
    	
    	//Check that the if statement is followed by an opening bracket
    	if(tokens.get(i).getType().equals(Type.OPENBRACKET)) {
    		parsed.add(tokens.get(i));
    		i = i+1;
    	}else {
    		throw new RuntimeException("Illegal condition statement --> No openning bracket after 'if' ");
    	}
    	
    	//Check if the variable in the brackets is of boolean type
    	if(tokens.get(i).getType().equals(Type.LITERAL)) {
    		boolean found = false;
        	Iterator<Token> iter = knownLiterals.iterator();
        	String literalName = Parser.extractVariableName(tokens.get(i));
        	while(iter.hasNext() & !found) {
        		Token current = iter.next();
        		String currentTokenName = Parser.extractVariableName(current);
        		if(currentTokenName.equals(literalName) & current.getType().equals(Type.BOOLEAN)) {
        			found = true;
        		}
        	}

    			if(found == true) {
    				parsed.add(tokens.get(i));
    				i = i+1;
    				}else {
    					throw new RuntimeException("The literal in the if condition is not of type BOOLEAN");
    				}
    		if(tokens.size() > i && tokens.get(i).getType().equals(Type.CLOSEBRACKET)) {
    			parsed.add(tokens.get(i));
    		}else {
    			throw new RuntimeException("Illegal condition statement --> No closing bracket after 'if' ");
    		}
    	}else {
    		throw new RuntimeException("Illegal form of 'if' expression --> No LITERAL in the condition");
    	}
    	return i;
    }
    
    public LinkedList parse() {
        Stack<Token> stack = new Stack<Token>();  
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
            
            //If a condition is encountered check that it is in correct form: if<cond> --> <stmt> else--> <stmt>	
            }else if(t.getType().equals(Type.IF)){
            	parsed.add(t);
            	i = Parser.evaluateCondition(tokens, i);
            	
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
    
}
