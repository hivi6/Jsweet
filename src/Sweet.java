import java.io.IOException;

import runtime.SweetRuntime;

public class Sweet {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            // repl - read-evaluate-print loop
            SweetRuntime.runRepl();
        } else if (args.length == 1) {
            // read from a file and execute
            String filePath = args[0];
            SweetRuntime.runFile(filePath);
        } else {
            // error
            System.err.println("Usage: sweet [filepath]");
            System.exit(64); // usage error
        }
    }
}