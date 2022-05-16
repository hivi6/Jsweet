package runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SweetRuntime {
    // =========
    // Error Handling
    // =========

    private static boolean hadError = false;

    public static void error(int line, int column, String msg) {
        report(line, column, "", msg);
    }

    public static void error(int line, String msg) {
        report(line, "", msg);
    }

    private static void report(int line, int column, String where, String msg) {
        // Format: [Line 13:14] Error: 
        System.err.println("[" + line + ": " + column + "] Error" + where + ": " + msg);
        hadError = true;
    }

    private static void report(int line, String where, String msg) {
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
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        String source = new String(bytes, Charset.defaultCharset());
        
        // run
        run(source);

        // indicating error in exit mode
        if (hadError)
            System.exit(65);
    }

    private static void run(String source) {
        System.out.println("Inside run()\n" + source);
    }
}
