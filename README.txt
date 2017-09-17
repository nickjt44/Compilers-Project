*****README for CSC 512 Project 4: Function Code Generation by Nick Taverner*****

I used the sample parser from Part 2 as my parser for Part 3, and am building on this for Part 4.

On top of the .java files used for the original parser program, I have created four new files.

The only file (minus a couple of small modifications to Parser to include new metastatements) that I modified for 
this project is GenerateAST2.java.

My main routine (still in Parser.java) calls the scanner, then the parser, then generates the AST, then generates the output
program string from the AST, copies the metastatements to the string, and outputs the string to a file.

My compiler produces the correct output for test cases 1 and 3 from Part 3, but I couldn't get it to work for the recursive program.

I also believe it handles while loops correctly, based on my tests.

My program does follow the specifications, creating a single main function and a single array for storing data.

In order to run my program, type java Parser filename.c

where filename.c is the name of the file you wish to compile.

I'm submitting a tar file with all the classes precompiled.

My program writes the result to a file named output.c

