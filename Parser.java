package ba55_compiler;

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
        LinkedList postfix = new LinkedList();
        Token t;

        for (int i = 0; i < tokens.size(); i++) {
            t = (Token) tokens.get(i);
            if (t.getType().equals(Type.OPERAND)) {
                postfix.add(t);
            }else if(t.getType().equals(Type.STRING) | t.getType().equals(Type.CHARACTER)){
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
