package scanner.tests;

import java.io.IOException;

import scanner.Scanner;
import token.Token;
import util.Util;

public class ScannerTest {
    public static void main(String[] args) throws IOException {
        // read source
        String source = Util.readFile(args[0]);

        Scanner scanner = new Scanner(source);
        for (Token token : scanner.getTokens())
            System.out.println(token);
    }
}
