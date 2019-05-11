# Compiler-front-end
A lexical analyzer and a parser

-------------------------------------------------

How it works:

1)Create a txt file with your program

2)Change the Runner to its path

3)The lexer will split the expression into tokens

4)After the tokens are passed to the parser the parser syntactically analyzes them

-------------------------------------------------

What it is currently capable of:

1)Working with regular arithmetic expressions

2)Working with if statements of the following type -> if(LITERAL_OF_TYPE_BOOLEAN_PREVIOUSLY_INITIALIZED) { YOUR_EXPRESSION } --> throws an error otherwise

3)Working with else statements of the following type --> if<stmt> --> <expr> else { YOUR_EXPRESSION } --> throws an error otherwise

4)Ignoring comments of the following type -> //YOUR COMMENT//

5)Working with Strings of the following type -> str ENTER_STRING_NAME = "ENTER_STRING_VALUE";

6)Working with Characters of the following type -> char ENTER_CHARACTER_NAME = ENTER_CHAR_VALUE ;

7)Working with boolean variables of the following type -> bool ENTER_BOOLEAN_NAME = true/false;

8)Working with signed/unsigned integer variables of the following type -> int ENTER_INTEGER_NAME = NUMBER;

9)The parser only parses literals previously declared (by looking at its variables "table")

10)Working with while statements of the following type --> while(LITERAL_OF_TYPE_BOOLEAN_PREVIOUSLY_INITIALIZED) { YOUR_EXPRESSION } --> throws an error otherwise

11)Checking for type mismatch after assigning values to variables

12)Working with functions of the following type:

  str|int|char|bool _fun YOUR_FUNCTION_NAME (int|char|str|bool NAME_OF_THE_LITERAL_YOU_WANT_TO_PASS;)* { YOUR_FUNCTION return                  YOUR_LITERAL ;} 

  void_fun YOUR_FUNCTION_NAME (int|char|str|bool NAME_OF_THE_LITERAL_YOU_WANT_TO_PASS;)* { YOUR_FUNCTION}

  -->throws an error otherwise

13)Checking that the return type of a function matches the function type --> throws an error otherwise

-------------------------------------------------

Feel free to use and ask me questions about it :)
