package ba55_compiler;

import java.util.LinkedList;

//The lexical analyzer
public class lexical_ANALyzer {
	 static LinkedList<Token<Type, String>> tokens = new LinkedList<>();
    
//The types the lexer currently knows how to handle
    public static enum Type {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, REMAINDER, OPERAND 
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

    
    private static int skipComment(String expression,int index) {
    	int i=index+2;
    	while(!(expression.charAt(i)=='/' & expression.charAt(i+1)=='/')){
    		i=i+1;	
    	}
    	return i+2;
    	
    }
    /**
     * Lexically analyzes the expression
     * @param expression The expression to be analyzed
     * @return A linked list of token objects
     */
    public static LinkedList<Token<Type, String>> analyze(String expression) {
        for(int i = 0; i < expression.length(); ) {
            char currentCharacter = expression.charAt(i);
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
                       }
                	
                	i=lexical_ANALyzer.evaluateCondition(expression, i);               	
                	break;
                default:
                    if(Character.isWhitespace(currentCharacter)) {
                        i++;
                    }
            }
        }
        return tokens;
    }
   
}
