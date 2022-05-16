package runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SweetRuntime {
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
        }
    }

    public static void runFile(String filePath) throws IOException {
        // read source
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        String source = new String(bytes, Charset.defaultCharset());
        
        // run
        run(source);
    }

    private static void run(String source) {
        System.out.println("Inside run()\n" + source);
    }
}
