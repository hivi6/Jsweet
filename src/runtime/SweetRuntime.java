package runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import ast.AstPrinter;
import ast.Expr;
import interpreter.Interpreter;
import parser.Parser;
import scanner.Scanner;
import token.Token;
import util.Util;
import token.TokenType;

public class SweetRuntime {
    private static final Interpreter interpreter = new Interpreter();

    // =========
    // Error Handling
    // =========

    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;

    public static void error(int line, int column, String msg) {
        report(line, column, "", msg);
    }

    public static void error(int line, String msg) {
        report(line, "", msg);
    }

    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    public static void runtimeError(SwtRuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

    private static void report(int line, int column, String where, String msg) {
        // Format: [11:14] Error: Missing semi-colon after expression.
        System.err.println("[" + line + ": " + column + "] Error" + where + ": " + msg);
        hadError = true;
    }

    private static void report(int line, String where, String msg) {
        // Format: [Line 11] Invalid statement.
        System.err.println("[Line " + line + "] Error" + where + ": " + msg);
        hadError = true;
    }

    // ==========
    // Application interface
    // ==========

    // repl - read evaluate print loop
    public static void runRepl() throws IOException {
        // make an instance of reader
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        // read loop
        for (;;) {
            // prompt
            System.out.print("> ");
            // read
            String source = reader.readLine();
            if (source == null) {
                break;
            }

            // run
            run(source);

            // reset any errors to rerun the loop
            hadError = false;
        }
    }

    public static void runFile(String filePath) throws IOException {
        // read source
        String source = Util.readFile(filePath);

        // run
        run(source);

        // indicating error in exit mode
        if (hadError)
            System.exit(65);
        if (hadRuntimeError)
            System.exit(70);
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.getTokens();
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        // Stop if there was a syntax error.
        if (hadError)
            return;

        interpreter.interpret(expression);
    }
}
