package ba55_compiler;

import java.util.LinkedList;

//The lexical analyzer
public class lexical_ANALyzer {
	 static LinkedList<Token<Type, String>> tokens = new LinkedList<>();
    
//The types the lexer currently knows how to handle 
    public static enum Type {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, REMAINDER, OPERAND, STRING, CHARACTER, BOOLEAN ,INTEGER ,ASSIGNMENT ,SEMICOLON, LITERAL ,OPENBRACKET ,CLOSEBRACKET ,EVAL
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
    
    private static int evaluateCondition(String condition,int index) {

    	int i=index+1;
    	String firstExpression="";
    	String secondExpression="";
    	char operator='0';
    	String result="";
    	boolean finishedFirst=false;
    	boolean finishedSecond=false;
    	while(condition.charAt(i)!=')') {
    		if(Character.isWhitespace(condition.charAt(i))) {
    			i=i+1;
    		}else {
    			
    			if(!finishedFirst) {
    				if(Character.isDigit(condition.charAt(i))) {
    					firstExpression=firstExpression+condition.charAt(i);
    					i=i+1;
    				}else {
    					finishedFirst=true;
    					operator=condition.charAt(i);
    					i=i+1;
    				}
    			
    				}else if(!finishedSecond) {
    					if(Character.isDigit(condition.charAt(i))) {
        					secondExpression=secondExpression+condition.charAt(i);
        					i=i+1;
        				}else {
        					finishedSecond=true;
        					i=i+2;
        				}
    				}else {
    					result=result+condition.charAt(i);
    					i=i+1;
    				}
    			
    			}
    		
    	}
    	
    	i=i+1;
    	int firstExpressionInteger=Integer.parseInt(firstExpression);
    	int secondExpressionInteger=Integer.parseInt(secondExpression);
    	int resultInteger=Integer.parseInt(result);
    	boolean checker=false;
    	switch(operator) {
    	case '+':
    		checker=(firstExpressionInteger+secondExpressionInteger==resultInteger);
    		break;
    	case '-':
    		checker=(firstExpressionInteger-secondExpressionInteger==resultInteger);
    		break;
    	case '*':
    		checker=(firstExpressionInteger*secondExpressionInteger==resultInteger);
    		break;
    	case '/':
    		checker=(firstExpressionInteger/secondExpressionInteger==resultInteger);
    		break;	
    	}
    	
    	//If the boolean condition is true create a token
    	i=i+1;
    	if(checker==true) {
    	while(condition.charAt(i)!='}') {	
     		switch (condition.charAt(i)) {
    			 case '+':
                     tokens.add(new Token<>(Type.ADD, String.valueOf(condition.charAt(i))));
                     i++;
                     break;
                 case '-':
                     tokens.add(new Token<>(Type.SUBTRACT, String.valueOf(condition.charAt(i))));
                     i++;
                     break;
                 case '*':
                     tokens.add(new Token<>(Type.MULTIPLY, String.valueOf(condition.charAt(i))));
                     i++;
                     break;
                 case '/':
                     tokens.add(new Token<>(Type.DIVIDE, String.valueOf(condition.charAt(i))));
                     i++;
                     break;
                 case '%':
                     tokens.add(new Token<>(Type.REMAINDER, String.valueOf(condition.charAt(i))));
                     i++;
                     break;
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
                 	//Get the operand and increment the counter by the operand's length to continue parsing the expression
                     String operand = lexical_ANALyzer.getOperand(condition, i);
                     i=i + operand.length();
                     tokens.add(new Token<>(Type.OPERAND, operand));
                     break;
                 case 'i':
                 	if(condition.charAt(i+1)=='f') {
                 		i=i+2;
                        }
                 	
                 	i=i + lexical_ANALyzer.evaluateCondition(condition, i);               	
                 	break;
                 default:
                     if(Character.isWhitespace(condition.charAt(i))) {
                         i++;
                     }
     		}
    	}
    	i=i+1;
    	}else {
    		while(condition.charAt(i)!='}') {
    			i=i+1;
    		}
    		i=i+1;
    	}
    	
    	return i;
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
    		
    	}else {
    		tokens.add(new Token<>(Type.STRING,strName+"$NULL"));
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
    		
    	}else {
    		tokens.add(new Token<>(Type.CHARACTER,charName+"$NULL"));
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
    		}
    	}else {
    		tokens.add(new Token<>(Type.BOOLEAN,boolName+"$NULL"));
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
    	
    	//Get the evaluation of the Integer if it is not null
    	boolean first=false;
    	if(nullInt == false) {
    	while(expression.charAt(i)!= ';') {
    		if(Character.isWhitespace(expression.charAt(i))) {
    			i = i+1;
    		}else if(expression.charAt(i) == '-' & !first) {
    			first=true;
    			evaluation = evaluation + expression.charAt(i);
    			i = i+1;
    		}else if(expression.charAt(i)-'0'>=0 & '9'-expression.charAt(i)>=0){
    			first=true;
    			evaluation = evaluation + expression.charAt(i);
    			i = i+1;
    		}
    			
    	}
    	i = i+1;
    	
    		tokens.add(new Token<>(Type.INTEGER,intName+"$"+evaluation));
    		
    	}else {
    		tokens.add(new Token<>(Type.INTEGER,intName+"$NULL"));
    	}
    	
    	
    	return i;
    }
    
    private static int evaluateLiteral(String expression,int index) {
    	int i=index;
    	String theLiteral="";
    	while(!Character.isWhitespace(expression.charAt(i))) {
    		theLiteral=theLiteral+expression.charAt(i);
    		i = i+1;
    	}
    	tokens.add(new Token<>(Type.LITERAL, theLiteral));
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
                case '+':
                    tokens.add(new Token<>(Type.ADD, String.valueOf(currentCharacter)));
                    i++;
                    break;
                case '-':
                    tokens.add(new Token<>(Type.SUBTRACT, String.valueOf(currentCharacter)));
                    i++;
                    break;
                case '*':
                    tokens.add(new Token<>(Type.MULTIPLY, String.valueOf(currentCharacter)));
                    i++;
                    break;
                case '/':
                	if(expression.charAt(i+1)=='/') {
                		i=lexical_ANALyzer.skipComment(expression, i);
                		break;
                	}else {
                    tokens.add(new Token<>(Type.DIVIDE, String.valueOf(currentCharacter)));
                    i++;
                    break;
                	}
                case '%':
                    tokens.add(new Token<>(Type.REMAINDER, String.valueOf(currentCharacter)));
                    i++;
                    break;
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
                	//Get the operand and increment the counter by the operand's length to continue parsing the expression
                    String operand = lexical_ANALyzer.getOperand(expression, i);
                    i=i + operand.length();
                    tokens.add(new Token<>(Type.OPERAND, operand));
                    break;
                case 'i':
                	if(expression.charAt(i+1)=='f') {
                			i=i+2;
                			i=lexical_ANALyzer.evaluateCondition(expression, i);  
                			break;
                       }else if(expression.charAt(i+1) == 'n' & expression.charAt(i+2) == 't') {
                    	   i=i+3;
                    	   i=lexical_ANALyzer.evaluateInteger(expression, i);  
                    	  break; 
                       }          	
                case 's':
                	if(expression.charAt(i+1)=='t' & expression.charAt(i+2)=='r') {
                		i=lexical_ANALyzer.evaluateString(expression, i+3);
                		break;
                	}
                case 'c':
                	if(expression.charAt(i+1) == 'h' && expression.charAt(i+2) == 'a' && expression.charAt(i+3) == 'r') {
                		i=lexical_ANALyzer.evaluateChar(expression, i+4);
                		break;
                	}
                case 'b':
                	if(expression.charAt(i+1) == 'o' && expression.charAt(i+2) == 'o' && expression.charAt(i+3) == 'l') {
                		i=lexical_ANALyzer.evaluateBoolean(expression, i+4);
                		break;
                	}
                case '=':
                	if(expression.charAt(i+1) == '=') {
                		tokens.add(new Token<>(Type.EVAL, "=="));
                		i = i+2;
                		break;
                	}else {
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
                default:
                   i=lexical_ANALyzer.evaluateLiteral(expression, i);
                   break;
            	}
            }
        }
        return tokens;
    }
   
}
