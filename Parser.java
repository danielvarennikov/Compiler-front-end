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

    private static String extractVariableValue(Token t) {
    	String name="";
    	String theToken=t.toString();
    	boolean started=false;
    	boolean finished=false;

    	if(t.getType().equals(Type.OPERAND) | t.getType().equals(Type.LITERAL)) {
    		
    		for(int i=0;i<theToken.length() & !finished;i = i+1) {
        		
        		if(theToken.charAt(i) == ',') {
        			started=true;
        		}else if(theToken.charAt(i) == '>') {
        			finished = true;
        		}else if(started == true) {
        			name = name + theToken.charAt(i);
        		}
        	}
    	}else {	
    		for(int i=0;i<theToken.length() & !finished;i = i+1) {
    		
    			if(theToken.charAt(i) == '$') {
    				started=true;
    			}else if(theToken.charAt(i) == '>') {
    				finished = true;
    			}else if(started == true) {
    				name = name + theToken.charAt(i);
    			}
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
        		if(currentTokenName.equals(literalName) & current.getType().equals(Type.BOOLEAN) & (Parser.extractVariableValue(current).equals("true") | Parser.extractVariableValue(current).equals("false"))) {
        			found = true;
        		}
        	}

    			if(found == true) {
    				parsed.add(tokens.get(i));
    				i = i+1;
    				}else {
    					throw new RuntimeException("The literal in the if condition is not of type BOOLEAN OR it wasnt initialised");
    				}
    		if(tokens.size() > i && tokens.get(i).getType().equals(Type.CLOSEBRACKET)) {
    			parsed.add(tokens.get(i));
    			i = i+1;
    		}else {
    			throw new RuntimeException("Illegal condition statement --> No closing bracket after 'if' ");
    		}
    	}else {
    		throw new RuntimeException("Illegal form of 'if' expression --> No LITERAL in the condition");
    	}
    	
    	//Check that there is an opening parenthesis after the condition
    	if(i< tokens.size() && tokens.get(i).getType().equals(Type.OPEN)) {
    		parsed.add(tokens.get(i));
    		i = i+1;
    		Token t;
    		Stack<Token> stack = new Stack<Token>();
    		boolean closed=false;
    		for (; i < tokens.size() & !closed; i++) {
                t = (Token) tokens.get(i);
                //If a closing parenthesis is encountered finish
                if(t.getType().equals(Type.CLOSE)) {
                	parsed.add(tokens.get(i));
                	closed = true;
                }else {
                	
                	if (t.getType().equals(Type.OPERAND) | t.getType().equals(Type.SEMICOLON)) {
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
                		}else {
                			throw new RuntimeException("Undeclared variable -->" + Parser.extractVariableName(t));
                		}
                
                		//If a condition is encountered check that it is in correct form: if<cond> --> <stmt> else--> <stmt>	
                	}else if(t.getType().equals(Type.IF)){
                		parsed.add(t);
                		i = Parser.evaluateCondition(tokens, i);
                	
                	}else if(t.getType().equals(Type.ELSE)) {
                    	throw new RuntimeException("'ELSE' without previous 'IF' token");
                    }else if(t.getType().equals(Type.WHILE)) {
                    	parsed.add(t);
                    	i = Parser.evaluateWhile(tokens, i);
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
    			}	
    		if(closed == false) {
    			throw new RuntimeException("Illegal form of 'if' expression --> No closing parenthesis after the statement");
    		}
    		
    		}else {
    			throw new RuntimeException("Illegal form of 'if' expression --> No opening parenthesis after the condition");
    		}
    	
    	
    	//Check if the next token is an ELSE token --> add it to the parsed list if so
    	if(tokens.get(i).getType().equals(Type.ELSE)) {
    		parsed.add(tokens.get(i));
    		i = i+1;
    		
    		//Check that the else statement is followed by open parenthesis --> If not throw an exception
    		if(i<tokens.size() && tokens.get(i).getType().equals(Type.OPEN)) {
    			parsed.add(tokens.get(i));
    			i = i+1;
    		}else {
    			throw new RuntimeException("Illegal form of 'else' expression --> No opening parenthesis after the else statement");
    		}
    		
    		//Now parse all there is inside it and throw an exception if there are no closing parenthesis
    		Token t;
    		Stack<Token> stack = new Stack<Token>();
    		boolean closed=false;
    		for (; i < tokens.size() & !closed; i++) {
                t = (Token) tokens.get(i);
                //If a closing parenthesis is encountered finish
                if(t.getType().equals(Type.CLOSE)) {
                	parsed.add(tokens.get(i));
                	closed = true;
                }else {
                	
                	if (t.getType().equals(Type.OPERAND) | t.getType().equals(Type.SEMICOLON)) {
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
                		}else {
                			throw new RuntimeException("Undeclared variable -->" + Parser.extractVariableName(t));
                		}
                
                		//If a condition is encountered check that it is in correct form: if<cond> --> <stmt> else--> <stmt>	
                	}else if(t.getType().equals(Type.IF)){
                		parsed.add(t);
                		i = Parser.evaluateCondition(tokens, i);
                	
                	}else if(t.getType().equals(Type.ELSE)) {
                    	throw new RuntimeException("'ELSE' without previous 'IF' token");
                    }else if(t.getType().equals(Type.WHILE)) {
                    	parsed.add(t);
                    	i = Parser.evaluateWhile(tokens, i);
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
    			}	
    		if(closed == false) {
    			throw new RuntimeException("Illegal form of 'else' expression --> No closing parenthesis after the statement");
    		}
    		
    		}
    
    	
    	
    	return i - 1;
    }
    
    private static int evaluateWhile(LinkedList<Token> tokens,int index) {
    	int i=index+1;
    	
    	//Check that the while statement is followed by an opening bracket
    	if(tokens.get(i).getType().equals(Type.OPENBRACKET)) {
    		parsed.add(tokens.get(i));
    		i = i+1;
    	}else {
    		throw new RuntimeException("Illegal condition statement --> No openning bracket after 'while' ");
    	}
    	
    	//Check if the variable in the brackets is of boolean type
    	if(tokens.get(i).getType().equals(Type.LITERAL)) {
    		boolean found = false;
        	Iterator<Token> iter = knownLiterals.iterator();
        	String literalName = Parser.extractVariableName(tokens.get(i));
        	while(iter.hasNext() & !found) {
        		Token current = iter.next();
        		String currentTokenName = Parser.extractVariableName(current);
        		if(currentTokenName.equals(literalName) & current.getType().equals(Type.BOOLEAN) & (Parser.extractVariableValue(current).equals("true") | Parser.extractVariableValue(current).equals("false"))) {
        			found = true;
        		}
        	}

    			if(found == true) {
    				parsed.add(tokens.get(i));
    				i = i+1;
    				}else {
    					throw new RuntimeException("The literal in the while condition is not of type BOOLEAN OR it wasnt initialised");
    				}
    		if(tokens.size() > i && tokens.get(i).getType().equals(Type.CLOSEBRACKET)) {
    			parsed.add(tokens.get(i));
    			i = i+1;
    		}else {
    			throw new RuntimeException("Illegal condition statement --> No closing bracket after 'while' ");
    		}
    	}else {
    		throw new RuntimeException("Illegal form of 'while' expression --> No LITERAL in the condition");
    	}
    	
    	//Check that there is an opening parenthesis after the condition
    	if(i< tokens.size() && tokens.get(i).getType().equals(Type.OPEN)) {
    		parsed.add(tokens.get(i));
    		i = i+1;
    		Token t;
    		Stack<Token> stack = new Stack<Token>();
    		boolean closed=false;
    		for (; i < tokens.size() & !closed; i++) {
                t = (Token) tokens.get(i);
                //If a closing parenthesis is encountered finish
                if(t.getType().equals(Type.CLOSE)) {
                	parsed.add(tokens.get(i));
                	closed = true;
                }else {
                	
                	if (t.getType().equals(Type.OPERAND) | t.getType().equals(Type.SEMICOLON)) {
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
                		}else {
                			throw new RuntimeException("Undeclared variable -->" + Parser.extractVariableName(t));
                		}
                
                		//If a condition is encountered check that it is in correct form: if<cond> --> <stmt> else--> <stmt>	
                	}else if(t.getType().equals(Type.IF)){
                		parsed.add(t);
                		i = Parser.evaluateCondition(tokens, i);
                	
                	}else if(t.getType().equals(Type.ELSE)) {
                    	throw new RuntimeException("'ELSE' without previous 'IF' token");
                    }else if(t.getType().equals(Type.WHILE)) {
                    	parsed.add(t);
                    	i = Parser.evaluateWhile(tokens, i);
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
    			}	
    		if(closed == false) {
    			throw new RuntimeException("Illegal form of 'while' expression --> No closing parenthesis after the statement");
    		}
    		
    		}else {
    			throw new RuntimeException("Illegal form of 'while' expression --> No opening parenthesis after the condition");
    		}
    	
    	return i -1;
    }
    
    private static Token extractInitial(Token t){
    	String thisTokenName = Parser.extractVariableName(t);
    	Iterator<Token> iter = knownLiterals.iterator();
    	Token raw = null;
    	boolean found = false;
    	while(iter.hasNext() & !found) {
    		Token current = iter.next();
    		String currentName = Parser.extractVariableName(current);
    		if((!current.getType().equals(Type.LITERAL)) & currentName.equals(thisTokenName)) {
    			found =true;
    			raw = current;
    		}
    		
    	}
    	
    	return raw;
    }
    
    private static boolean arithmeticExpression(LinkedList<Token> tokens,int index) {
    	int i = index;
    	boolean isValid = true;
    	while(i < tokens.size() & isValid & (!tokens.get(i).getType().equals(Type.SEMICOLON))){
    		if(!(tokens.get(i).getType().equals(Type.OPERAND) | tokens.get(i).getType().equals(Type.ADD) | tokens.get(i).getType().equals(Type.SUBTRACT) | tokens.get(i).getType().equals(Type.MULTIPLY) | tokens.get(i).getType().equals(Type.DIVIDE))){
    			isValid = false;
    		}
    		i = i+1;
    	}
    	return isValid;
    }
    
    private static int evaluateAssignment(LinkedList<Token> tokens,int index) {
    	int i =index;
    	
    	//Check that the previous token is a Literal --> if not throw an exception
    	if(tokens.get(i).getType().equals(Type.LITERAL)) {
    		Token toAssign = tokens.get(i);
    		i = i+1;
    		
    		//Add the assignment token to the parsed list
    		parsed.add(tokens.get(i));
    		i = i+1;

    		//Check that the values that we want to assign are the same type --> if not throw a type mismatch
    		if(Parser.extractInitial(toAssign).getType().equals(Type.STRING) & tokens.get(i).getType().equals(Type.STRINGLITERAL)) {
    			parsed.add(tokens.get(i));
    			i = i+1;
    		}else if(Parser.extractInitial(toAssign).getType().equals(Type.INTEGER) & Parser.arithmeticExpression(tokens, i)) {
    			parsed.add(tokens.get(i));
    			i = i+1;
    		}else if(Parser.extractInitial(toAssign).getType().equals(Type.INTEGER) & tokens.get(i).getType().equals(Type.STRINGLITERAL)) {
    			throw new RuntimeException("Type mismatch in ASSIGMENT --> CANNOT ASSIGN "+Type.STRINGLITERAL+" to "+Parser.extractInitial(toAssign).getType());
    		}else if(Parser.extractInitial(toAssign).getType().equals(Type.STRING) & Parser.arithmeticExpression(tokens, i)) {
    			throw new RuntimeException("Type mismatch in ASSIGMENT --> CANNOT ASSIGN arithmetic expression to "+Parser.extractInitial(toAssign).getType());
    		}else if((tokens.get(i).getType().equals(Type.LITERAL) | tokens.get(i).getType().equals(Type.OPERAND)) & Parser.extractInitial(toAssign).getType().equals(Type.CHARACTER)) {
    			if(Parser.extractVariableValue(tokens.get(i)).length() == 1) {
    				parsed.add(tokens.get(i));
    				i = i+1;
    			}else {

    				throw new RuntimeException("INVALID CHARACHTER ASSINGMENT --> cannot assign "+Parser.extractVariableValue(tokens.get(i))+" to CHARACHTER");
    			}
    		}else if(Parser.extractInitial(toAssign).getType().equals(Parser.extractInitial(tokens.get(i)).getType())){
    			parsed.add(tokens.get(i));
    			i = i+1;	
    		}else {
    			throw new RuntimeException("Type mismatch in ASSIGMENT --> CANNOT ASSIGN "+Parser.extractInitial(tokens.get(i)).getType()+" to "+Parser.extractInitial(toAssign).getType());
    		}
    		
    	}else {
    		throw new RuntimeException("Evaluation error --> The Variable before the aasigment is not a LITERAL");
    	}
    	
    	return i - 1;
    	
    }
    
    private static boolean checkKnown(Token t) {
    	Token toCheck = t;
    	String tokenName = Parser.extractVariableName(toCheck);
    	boolean found = false;
    	Iterator<Token> iter = knownLiterals.iterator();
    	while(iter.hasNext() & !found) {
    		Token current =iter.next();
    		String currentName = Parser.extractVariableName(current);
    		if(currentName.equals(tokenName)) {
    			found = true;
    		}
    		
    	}
    	return found;
    	
    }
    
    public LinkedList parse() {
        Stack<Token> stack = new Stack<Token>();  
        Token t;

        for (int i = 0; i < tokens.size(); i++) {
            t = (Token) tokens.get(i);
            if (t.getType().equals(Type.OPERAND) | t.getType().equals(Type.SEMICOLON)) {
                parsed.add(t);
            
            //If a new variable is initialized add it to the "table" of known variables    
            }else if(t.getType().equals(Type.STRING) | t.getType().equals(Type.CHARACTER) | t.getType().equals(Type.BOOLEAN) | t.getType().equals(Type.INTEGER) ){
            	knownLiterals.add(t);
            	parsed.add(t);
            	
            //If a literal is encountered check if it was initialized before(from the "table") and add it only if it was initialized 	
            }else if(t.getType().equals(Type.LITERAL)) {
            	boolean found = Parser.checkKnown(t);
            	if(found == true) {
            		parsed.add(t);
            	}else {
        			throw new RuntimeException("Undeclared variable -->" + Parser.extractVariableName(t));
        		}
            
            //If a condition is encountered check that it is in correct form: if<cond> --> <stmt> else--> <stmt>	
            }else if(t.getType().equals(Type.IF)){
            	parsed.add(t);
            	i = Parser.evaluateCondition(tokens, i);
            	
            }else if(t.getType().equals(Type.ELSE)) {
            	throw new RuntimeException("'ELSE' without previous 'IF' token");
            	
            //If a while loop is encountered check that it is in correct form: while<cond> --> <stmt>
            }else if(t.getType().equals(Type.WHILE)) {
            	parsed.add(t);
            	i = Parser.evaluateWhile(tokens, i);
            }else if(t.getType().equals(Type.ASSIGNMENT)) {
            	i = Parser.evaluateAssignment(tokens, i -1);
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
