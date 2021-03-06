package ba55_compiler;

import java.util.LinkedList;
import java.util.regex.*;
//The lexical analyzer
public class lexical_ANALyzer {
	 static LinkedList<Token<Type, String>> tokens = new LinkedList<>();
    
//The types the lexer currently knows how to handle 
    public static enum Type {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, REMAINDER, OPERAND, STRING, CHARACTER, BOOLEAN ,INTEGER ,ASSIGNMENT ,SEMICOLON, 
        LITERAL ,OPENBRACKET ,CLOSEBRACKET ,EVAL ,IF ,ELSE ,OPEN , CLOSE, WHILE, STRINGLITERAL, FUNCTION_VOID, FUNCTION_BOOLEAN,
        FUNCTION_STRING, FUNCTION_INTEGER, FUNCTION_CHARACTER ,RETURN
        
    }
    
    
    /**
     * Gets an operand starting at the specified index
     * Parses both variable names and numeric literals
     * @param operand The string to be parsed
     * @param index The starting index
     * @return The operand
     */
    private static String getOperand(String operand, int index) {
        int i = index;
        for( ; i < operand.length(); ) {
            if(Character.isDigit(operand.charAt(i))) {
                i++;
            }
            else {
                return operand.substring(index, i);
            }
        }
        return operand.substring(index, i);
    }

    private static int evaluateString(String expression,int index) {
    	int i=index;
    	String strName="";
    	String evaluation="";
    	boolean nullString=false;
    	
    	//Get the name of the String
    	while(expression.charAt(i) != '=' & nullString == false) {
    		if(expression.charAt(i) == ';') {
    			nullString = true;
    		}else if(!Character.isWhitespace(expression.charAt(i))) {
    			strName=strName+expression.charAt(i);
    			i = i+1;
    		}else {
    			i = i+1;
    		}
    	}
    	i=i+1;
    	
    	//Get the evaluation of the String if it is not null
    	if(nullString == false) {
    	
    	boolean hasValue=false;
    	boolean started=false;
    	
    	while(expression.charAt(i)!= ';') {
    		if(expression.charAt(i) == '"') {
    			
    			if(started == false) {
    				started = true;	
    				i = i+1;
    			}else {
    				hasValue=true;
    				i = i+1;
    			}
    			
    		}else if(started == true & hasValue == false) {
    			evaluation = evaluation + expression.charAt(i);
    			i = i+1;
    		}else {
    			i = i+1;
    		}
    		
    	}
    	i = i+1;
    	
    	tokens.add(new Token<>(Type.STRING,strName+"$"+evaluation));
    	tokens.add(new Token<>(Type.SEMICOLON,";"));
    		
    	}else {
    		tokens.add(new Token<>(Type.STRING,strName+"$NULL"));
    		tokens.add(new Token<>(Type.SEMICOLON,";"));
    	}
    	
    	return i;
    }
    
    private static int evaluateChar(String expression,int index) {
    	int i=index;
    	String charName="";
    	char evaluation='-';
    	boolean nullChar=false;
    	
    	//Get the name of the Char
    	while(expression.charAt(i) != '=' & nullChar == false) {
    		if(expression.charAt(i) == ';') {
    			nullChar = true;
    		}else if(!Character.isWhitespace(expression.charAt(i))) {
    			charName=charName+expression.charAt(i);
    			i = i+1;
    		}else {
    			i = i+1;
    		}
    	}
    	i=i+1;
    	
    	//Get the evaluation of the Char if it is not null
    	if(nullChar == false) {
    	while(expression.charAt(i)!= ';') {
    		if(Character.isWhitespace(expression.charAt(i))) {
    			i = i+1;
    		}else {
    			evaluation = expression.charAt(i);
    			i = i+1;
    		}
    			
    	}
    	i = i+1;
    	tokens.add(new Token<>(Type.CHARACTER,charName+"$"+evaluation));
    	tokens.add(new Token<>(Type.SEMICOLON,";"));	
    	}else {
    		tokens.add(new Token<>(Type.CHARACTER,charName+"$NULL"));
    		tokens.add(new Token<>(Type.SEMICOLON,";"));
    	}
    	
    	
    	return i;
    }
    
    private static int skipComment(String expression,int index) {
    	int i=index+2;
    	while(!(expression.charAt(i)=='/' & expression.charAt(i+1)=='/')){
    		i=i+1;	
    	}
    	return i+2;
    	
    }
    
    private static int evaluateBoolean(String expression,int index) {
    	int i=index;
    	String boolName="";
    	String evaluation="";
    	boolean nullBool=false;
    	
    	//Get the name of the Boolean
    	while(expression.charAt(i) != '=' & nullBool == false) {
    		if(expression.charAt(i) == ';') {
    			nullBool = true;
    		}else if(!Character.isWhitespace(expression.charAt(i))) {
    			boolName=boolName+expression.charAt(i);
    			i = i+1;
    		}else {
    			i = i+1;
    		}
    	}
    	i=i+1;
    	
    	//Get the evaluation of the Boolean if it is not null
    	if(nullBool == false) {
    	while(expression.charAt(i)!= ';') {
    		if(Character.isWhitespace(expression.charAt(i))) {
    			i = i+1;
    		}else {
    			evaluation = evaluation + expression.charAt(i);
    			i = i+1;
    		}
    			
    	}
    	i = i+1;
    	if(evaluation.equals("true") | evaluation.equals("false")) {
    		tokens.add(new Token<>(Type.BOOLEAN,boolName+"$"+evaluation));
    		tokens.add(new Token<>(Type.SEMICOLON,";"));
    		}
    	}else {
    		tokens.add(new Token<>(Type.BOOLEAN,boolName+"$NULL"));
    		tokens.add(new Token<>(Type.SEMICOLON,";"));
    	}
    	
    	
    	return i;
    }
    
    private static int evaluateInteger(String expression,int index) {
    	int i=index;
    	String intName="";
    	String evaluation="";
    	boolean nullInt=false;
    	
    	//Get the name of the Integer
    	while(expression.charAt(i) != '=' & nullInt == false) {
    		if(expression.charAt(i) == ';') {
    			nullInt = true;
    		}else if(!Character.isWhitespace(expression.charAt(i))) {
    			intName=intName+expression.charAt(i);
    			i = i+1;
    		}else {
    			i = i+1;
    		}
    	}
    	i=i+1;
    	
    	//Get the evaluation of the Integer if it is not null and check that its structure is correct
    	boolean first=false;
    	if(nullInt == false) {
    		
    	String toCheck = "";
    	int checkerIndex = i;
    	while(expression.charAt(checkerIndex) != ';') {
    		if(Character.isWhitespace(expression.charAt(checkerIndex))) {
    			checkerIndex = checkerIndex + 1;
    		}else {
    			toCheck = toCheck +expression.charAt(checkerIndex);
    			checkerIndex = checkerIndex + 1;
    		}
    	}
    	toCheck = toCheck + ";";
    	String grep="(^(\\-)?(\\s)*(\\d)+(\\s)*((\\+|\\-|\\/|\\*)(\\s)*(\\-)?(\\s)*(\\d)+(\\s)*)*(\\s)*(;))";
		
		Pattern r=Pattern.compile(grep);
		Matcher m=r.matcher(toCheck);
    	if(!m.find()) {
    		throw new RuntimeException("Illegal arithmetic expression structure");
    	}
    		
    	while(expression.charAt(i)!= ';') {
    		if(Character.isWhitespace(expression.charAt(i))) {
    			i = i+1;
    		}else {
    			evaluation = evaluation + expression.charAt(i);
    			i = i+1;
    		}
    			
    	}
    	i = i+1;
    	
    		tokens.add(new Token<>(Type.INTEGER,intName+"$"+evaluation));
    		tokens.add(new Token<>(Type.SEMICOLON,";"));
    		
    	}else {
    		tokens.add(new Token<>(Type.INTEGER,intName+"$NULL"));
    		tokens.add(new Token<>(Type.SEMICOLON,";"));
    	}
    	
    	
    	return i;
    }
    
    private static int evaluateLiteral(String expression,int index) {
    	int i=index;
    	String theLiteral="";
    	if(expression.charAt(i) == '"') {
    	boolean closed = false;
    	i = i+1;
    	while(!Character.isWhitespace(expression.charAt(i)) & expression.charAt(i)!=';' & expression.charAt(i) != ')' & expression.charAt(i) != '(') {
    		if(expression.charAt(i) == '"') {
    			closed = true;
    			i = i+1;
    		}else {
    			theLiteral = theLiteral+expression.charAt(i);
    			i = i+1;
    		}
    	}
    	
    	tokens.add(new Token<>(Type.STRINGLITERAL, theLiteral));
    	}else {
    	while(!Character.isWhitespace(expression.charAt(i)) & expression.charAt(i)!=';' & expression.charAt(i) != ')' & expression.charAt(i) != '(') {
    		theLiteral=theLiteral+expression.charAt(i);
    		i = i+1;
    	}
    	tokens.add(new Token<>(Type.LITERAL, theLiteral));
    	}
    	return i;
    }
    
    private static void checkFunctionStruct(String type,String expression,int index) {
   	
    	//Check the structure of the function using Regular expressions -->If it is not in the right structure throw an exception
    	
		String toCheck=expression.substring(index);

    	if(type.equals("str")) {
    		String grep="(str_fun(\\s*)[a-zA-Z_0-9]+(\\s*)[(]((int|str|char|bool)(\\s*)[a-zA-Z_0-9]+(\\s*)[;](\\s*))*[)](\\s*)[{](.*)return(\\s*)[a-zA-Z_0-9]+(\\s*)[;](\\s*)[}])";
    		Pattern r=Pattern.compile(grep);
    		Matcher m=r.matcher(toCheck);
    		if(!m.find()) {
    			throw new RuntimeException("Incorrect str_fun structure");
    		}
    	}else if(type.equals("int")) {
    		String grep="(int_fun(\\s*)[a-zA-Z_0-9]+(\\s*)[(]((int|str|char|bool)(\\s*)[a-zA-Z_0-9]+(\\s*)[;](\\s*))*[)](\\s*)[{](.*)return(\\s*)[a-zA-Z_0-9]+(\\s*)[;](\\s*)[}])";
    		Pattern r=Pattern.compile(grep);
    		Matcher m=r.matcher(toCheck);
    		if(!m.find()) {
    			throw new RuntimeException("Incorrect int_fun structure");
    		}
    	}else if(type.equals("bool")) {
    		String grep="(bool_fun(\\s*)[a-zA-Z_0-9]+(\\s*)[(]((int|str|char|bool)(\\s*)[a-zA-Z_0-9]+(\\s*)[;](\\s*))*[)](\\s*)[{](.*)return(\\s*)[a-zA-Z_0-9]+(\\s*)[;](\\s*)[}])";
    		Pattern r=Pattern.compile(grep);
    		Matcher m=r.matcher(toCheck);
    		if(!m.find()) {
    			throw new RuntimeException("Incorrect bool_fun structure");
    		}
    	}else if(type.equals("void")) {
    		String grep="(void_fun(\\s*)[a-zA-Z_0-9]+(\\s*)[(]((int|str|char|bool)(\\s*)[a-zA-Z_0-9]+(\\s*)[;](\\s*))*[)](\\s*)[{](.*)[}])";
    		Pattern r=Pattern.compile(grep);
    		Matcher m=r.matcher(toCheck);
    		if(!m.find()) {
    			throw new RuntimeException("Incorrect void_fun structure");
    		}
    	}else if(type.equals("char")) {
    		String grep="(char_fun(\\s*)[a-zA-Z_0-9]+(\\s*)[(]((int|str|char|bool)(\\s*)[a-zA-Z_0-9]+(\\s*)[;](\\s*))*[)](\\s*)[{](.*)return(\\s*)[a-zA-Z_0-9]+(\\s*)[;](\\s*)[}])";
    		Pattern r=Pattern.compile(grep);
    		Matcher m=r.matcher(toCheck);
    		if(!m.find()) {
    			throw new RuntimeException("Incorrect char_fun structure");
    		}
    	}
    	

    }
    
    private static int analyzeArithmeticExpression(String expression,int index) {
    	int i = index;
    	
    	//The expression we will check using regular expressions
    	String toCheck = expression.substring(i);
    	
    	//Check this regular expression
    	String grep="(^(\\-)?(\\s)*(\\d)+(\\s)*((\\+|\\-|\\/|\\*)(\\s)*(\\-)?(\\s)*(\\d)+(\\s)*)*(\\s)*(;))";
		
		Pattern r=Pattern.compile(grep);
		Matcher m=r.matcher(toCheck);
		
		//If the arithmetic structure is incorrect
		if(!m.find()) {
			throw new RuntimeException("Invalid form of arithmetic expression");
		}else {
			boolean finished = false;
			while(finished == false) {
				if(expression.charAt(i) == ';') {
					finished = true;
					tokens.add(new Token<>(Type.SEMICOLON, ";"));
					i = i+1;
				}else if(Character.isWhitespace(expression.charAt(i))){
					i = i+1;
				}else if(Character.isDigit(expression.charAt(i))) {
					String operand = lexical_ANALyzer.getOperand(expression, i);
					i=i + operand.length();
					tokens.add(new Token<>(Type.OPERAND, operand));
				}else {

					switch(expression.charAt(i)) {
						case '+':
							tokens.add(new Token<>(Type.ADD, "+"));
							i = i+1;
							break;
						case '-':
							tokens.add(new Token<>(Type.SUBTRACT, "-"));
							i = i+1;
							break;	
						case '*':
							tokens.add(new Token<>(Type.MULTIPLY, "*"));
							i = i+1;
							break;	
						case '/':
							tokens.add(new Token<>(Type.DIVIDE, "/"));
							i = i+1;
							break;	
					}
				}
			}
			
		}
		
    	
        return i;
    }
    
    /**
     * Lexically analyzes the expression
     * @param expression The expression to be analyzed
     * @return A linked list of token objects
     */
    public static LinkedList<Token<Type, String>> analyze(String expression) {

        for(int i = 0; i < expression.length(); ) {
            char currentCharacter = expression.charAt(i);
        if(Character.isWhitespace(currentCharacter)) {
                i++;
                }else {
            switch(currentCharacter) {
                case '/':
                	if(expression.charAt(i+1)=='/') {
                		i=lexical_ANALyzer.skipComment(expression, i);
                		break;
                	}
                	throw new RuntimeException("Misplaced '/' symbol");
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '-':	
                	//Check the arithmetic expression structure and tokenise it if it is correct
                	
                    i = lexical_ANALyzer.analyzeArithmeticExpression(expression, i);
                    break;
                case 'i':
                	if(expression.charAt(i+1)=='f') {
                			i=i+2;
                			tokens.add(new Token<>(Type.IF,"")); 
                			break;
                       }else if(expression.charAt(i+1) == 'n' & expression.charAt(i+2) == 't') {
                    	   if(expression.charAt(i+3) == '_' && expression.charAt(i+4) == 'f' && expression.charAt(i+5) == 'u' && expression.charAt(i+6) == 'n') {
                    		   lexical_ANALyzer.checkFunctionStruct("int", expression, i);
                    		   tokens.add(new Token<>(Type.FUNCTION_INTEGER,""));
                   			   i = i+7;
                   			   break;	
                    	   }else {
                    		   i=i+3;
                    		   i=lexical_ANALyzer.evaluateInteger(expression, i);  
                    		   break; 
                    	   }
                       }else {
                    	   i=lexical_ANALyzer.evaluateLiteral(expression, i);
                           break;
                       }
                case 's':
                	if(expression.charAt(i+1)=='t' & expression.charAt(i+2)=='r') {
                		if(expression.charAt(i+3) == '_' && expression.charAt(i+4) == 'f' && expression.charAt(i+5) == 'u' && expression.charAt(i+6) == 'n') {
                			lexical_ANALyzer.checkFunctionStruct("str", expression, i);
                			tokens.add(new Token<>(Type.FUNCTION_STRING,""));
                			i = i+7;
                			break;		
                		}else {
                			i=lexical_ANALyzer.evaluateString(expression, i+3);
                			break;
                		}
                	}else {
                		i=lexical_ANALyzer.evaluateLiteral(expression, i);
                        break;
                	}
                case 'c':
                	if(expression.charAt(i+1) == 'h' && expression.charAt(i+2) == 'a' && expression.charAt(i+3) == 'r') {
                		if(expression.charAt(i+4) == '_' && expression.charAt(i+5) == 'f' && expression.charAt(i+6) == 'u' && expression.charAt(i+7) == 'n') {
                			lexical_ANALyzer.checkFunctionStruct("char", expression, i);
                			tokens.add(new Token<>(Type.FUNCTION_CHARACTER,""));
                    		i = i+8;
                    		break;
                		}else {
                			i=lexical_ANALyzer.evaluateChar(expression, i+4);
                			break;
                		}
                	}else {
                		i=lexical_ANALyzer.evaluateLiteral(expression, i);
                        break;
                	}
                case 'b':
                	if(expression.charAt(i+1) == 'o' && expression.charAt(i+2) == 'o' && expression.charAt(i+3) == 'l') {
                		if(expression.charAt(i+4) == '_' && expression.charAt(i+5) == 'f' && expression.charAt(i+6) == 'u' && expression.charAt(i+7) == 'n') {
                			lexical_ANALyzer.checkFunctionStruct("bool", expression, i);
                			tokens.add(new Token<>(Type.FUNCTION_BOOLEAN,""));
                    		i = i+8;
                    		break;
                		}else {
                			i=lexical_ANALyzer.evaluateBoolean(expression, i+4);
                			break;
                		}
                	}else {
                		i=lexical_ANALyzer.evaluateLiteral(expression, i);
                        break;
                	}
                case '=':
                	if(expression.charAt(i+1) == '=') {
                		tokens.add(new Token<>(Type.EVAL, "=="));
                		i = i+2;
                		break;
                	}else if(currentCharacter == '='){

                		tokens.add(new Token<>(Type.ASSIGNMENT, "="));
                		i = i+1;
                	break;
                	}
                case ';':
                	tokens.add(new Token<>(Type.SEMICOLON, ";"));
                	i = i+1;
                	break;
                case '(':
                	tokens.add(new Token<>(Type.OPENBRACKET, "("));
                	i = i+1;
                	break;
                case ')':
                	tokens.add(new Token<>(Type.CLOSEBRACKET, ")"));
                	i = i+1;
                	break;
                case 'e':
                	if(expression.charAt(i+1) == 'l' & expression.charAt(i+2) == 's' & expression.charAt(i+3) == 'e') {
                		i=i+4;
                		tokens.add(new Token<>(Type.ELSE, ""));
                		break;
                	}else {
                		i=lexical_ANALyzer.evaluateLiteral(expression, i);
                        break;
                	}
                case '{':
                	tokens.add(new Token<>(Type.OPEN, ""));
                	i=i+1;
                	break;
                case '}':
                	tokens.add(new Token<>(Type.CLOSE, ""));
                	i=i+1;
                	break;
                case 'w':
                	if(expression.charAt(i+1) == 'h' & expression.charAt(i+2) == 'i' & expression.charAt(i+3) == 'l' & expression.charAt(i+4) == 'e') {
                		i=i+5;
                		tokens.add(new Token<>(Type.WHILE, ""));
                		break;
                	}else {
                		i=lexical_ANALyzer.evaluateLiteral(expression, i);
                        break;
                	}
                case 'v':
                	if(expression.charAt(i+1) == 'o' & expression.charAt(i+2) == 'i' & expression.charAt(i+3) == 'd' & expression.charAt(i+4) == '_' & expression.charAt(i+5) == 'f' & expression.charAt(i+6) == 'u' & expression.charAt(i+7) == 'n') {
                		lexical_ANALyzer.checkFunctionStruct("void", expression, i);
                		tokens.add(new Token<>(Type.FUNCTION_VOID, ""));
                		i=i+8;
                		break;
                	}else {
                		i=lexical_ANALyzer.evaluateLiteral(expression, i);
                        break;
                	}
                case 'r':
                	if(expression.charAt(i+1) == 'e' & expression.charAt(i+2) == 't' & expression.charAt(i+3) == 'u' & expression.charAt(i+4) == 'r' & expression.charAt(i+5) == 'n') {
                		i=i+6;
                		tokens.add(new Token<>(Type.RETURN, ""));
                		break;
                	}else {
                		i=lexical_ANALyzer.evaluateLiteral(expression, i);
                        break;
                	}
                default:
                   i=lexical_ANALyzer.evaluateLiteral(expression, i);
                   break;
            	}
            }
        }
        return tokens;
    }
   
}
