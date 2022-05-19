package parser.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import parser.Parser;
import scanner.Scanner;
import token.Token;
import ast.AstPrinter;
import ast.Expr;

public class ParserTest {
    public static void main(String[] args) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String source = reader.readLine();
            if (source == null)
                break;

            Scanner scanner = new Scanner(source);
            List<Token> tokens = scanner.getTokens();
            Parser parser = new Parser(tokens);
            Expr expr = parser.parse();
            
            System.out.println(new AstPrinter().print(expr));
        }
    }
}
