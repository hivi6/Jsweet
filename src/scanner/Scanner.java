package scanner;

import java.util.ArrayList;
import java.util.List;

import runtime.SweetRuntime;
import token.Token;
import token.TokenType;

import static token.TokenType.*;

public class Scanner {
    private final String source; // store the source file
    private final List<Token> tokens = new ArrayList<>(); // store tokens here
    private int startIndex = -1, currIndex = 0; // indices
    private int startLine = -1, currLine = 1; // line-number of a token
    private int startColumn = -1, currColumn = 1; // column number of a token

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> getTokens() {
        // if tokens exists then no need to scan tokens again
        if (tokens.size() != 0)
            return tokens;

        // go through all the character in the source
        while (!isEnd()) {
            // usefull for finding the lexeme for the token
            startIndex = currIndex;
            // usefull for token's starting line location
            startLine = currLine;
            // usefull for token's starting column location
            startColumn = currColumn;

            getToken();
        }

        // at the end eof
        addToken(EOF, "", null, currLine, currColumn);

        return tokens;
    }

    private boolean isEnd() {
        return currIndex >= source.length();
    }

    private void getToken() {
        char ch = advance();
        switch (ch) {
            // ignore whitespaces
            case ' ':
            case '\n': // newline character handled by advance
            case '\r':
            case '\t':
                break;
            case '(':
                addToken(LPAREN);
                break;
            case ')':
                addToken(RPAREN);
                break;
            case '{':
                addToken(LBRACE);
                break;
            case '}':
                addToken(RBRACE);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '.':
                addToken(DOT);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '+':
                addToken(PLUS);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '*':
                addToken(STAR);
                break;
            case '/': {
                if (match('/')) { // '/'
                    // this is a single line comment
                    // ignore everything after this
                    while (!isEnd() && peek() != '\n')
                        advance();
                } else
                    addToken(SLASH);
                break;
            }
            default:
                SweetRuntime.error(startLine, startColumn, "Undefined Token.");
        }
    }

    private char advance() {
        // check if end reached
        if (isEnd())
            return 0;
        // get the character
        char ch = source.charAt(currIndex++);
        if (ch == '\n') { // handle new line character for line number and column number
            currLine++;
            currColumn = 0;
        }
        currColumn++;
        return ch;
    }

    private void addToken(TokenType type) {
        addToken(type, source.substring(startIndex, currIndex), null, startLine, startColumn);
    }

    private void addToken(TokenType type, String lexeme, Object literal, int line, int column) {
        tokens.add(new Token(type, lexeme, literal, line, column));
    }

    private boolean match(char ch) {
        if (isEnd())
            return false;
        if (source.charAt(currIndex) != ch)
            return false;
        advance();
        return true;
    }

    private char peek() {
        return peek(0);
    }

    private char peek(int k) {
        if (k + currIndex >= source.length())
            return 0;
        return source.charAt(k + currIndex);
    }
}
