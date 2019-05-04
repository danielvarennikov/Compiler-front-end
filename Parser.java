package ba55_compiler;

import java.util.LinkedList;
import java.util.Stack;
import ba55_compiler.lexical_ANALyzer.Type;
/*
 * 
Algorithm
1. Scan the infix expression from left to right.
2. If the scanned character is an operand, output it.
3. Else,
…..3.1 If the precedence of the scanned operator is greater than the precedence of the operator in the stack(or the stack is empty), push it.
…..3.2 Else, Pop the operator from the stack until the precedence of the scanned operator is less-equal to the precedence of the operator residing on the top of the stack. Push the scanned operator to the stack.
4. If the scanned character is an ‘(‘, push it to the stack.
5. If the scanned character is an ‘)’, pop and output from the stack until an ‘(‘ is encountered.
6. Repeat steps 2-6 until infix expression is scanned.
7. Pop and output from the stack until it is not empty.

 */
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
        LinkedList postfix = new LinkedList();
        Token t;

        for (int i = 0; i < tokens.size(); i++) {
            t = (Token) tokens.get(i);
            if (t.getType().equals(Type.OPERAND)) {
                postfix.add(t);
            }else {
                if (!stack.isEmpty() && getPrecedence(t) <= getPrecedence(stack.peek())) {
                    postfix.add(stack.pop());
                }
                stack.push(t);
            }
        }

        while (!stack.isEmpty()) {
            postfix.add(stack.pop());
        }
        return postfix;
    }
	
	
}
