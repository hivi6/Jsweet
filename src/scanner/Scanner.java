package scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import runtime.SweetRuntime;
import token.Token;
import token.TokenType;
import types.SwtString;

import static token.TokenType.*;

public class Scanner {
    private final String source; // store the source file
    private final List<Token> tokens = new ArrayList<>(); // store tokens here
    private int startIndex = -1, currIndex = 0; // indices
    private int startLine = -1, currLine = 1; // line-number of a token
    private int startColumn = -1, currColumn = 1; // column number of a token
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("true", TRUE);
        keywords.put("false", FALSE);
        keywords.put("nil", NIL);
        keywords.put("and", AND);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("var", VAR);
        keywords.put("if", IF);
        keywords.put("else", ELSE);
        keywords.put("for", FOR);
        keywords.put("while", WHILE);
        keywords.put("fun", FUN);
        keywords.put("return", RETURN);
        keywords.put("class", CLASS);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
    }

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
            case '?':
                addToken(QMARK);
                break;
            case ':':
                addToken(COLON);
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
                    // ignore everything untill a new line
                    while (!isEnd() && peek() != '\n')
                        advance();
                } else if (match('*')) { // '/*'
                    // this is a multiple line comment
                    // ignore everything until a '*/' is reached
                    while (!isEnd() && !(peek() == '*' && peek(1) == '/'))
                        advance();
                    if (isEnd()) {
                        SweetRuntime.error(currLine,
                                currColumn,
                                "*/ is missing, Multi-line comment unterminated.");
                    } else {
                        advance(); // *
                        advance(); // /
                    }
                } else
                    addToken(SLASH);
                break;
            }
            case '"':
                stringLiteral();
                break;
            default:
                if (Character.isDigit(ch)) {
                    integerLiteral();
                } else if (Character.isLetter(ch) || ch == '_') {
                    identifier();
                } else {
                    SweetRuntime.error(startLine, startColumn, "Undefined Token.");
                }
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
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String lexeme = source.substring(startIndex, currIndex);
        addToken(type, lexeme, literal, startLine, startColumn);
    }

    private void addToken(TokenType type, String lexeme, Object literal, int startLine, int startColumn) {
        tokens.add(new Token(type, lexeme, literal, startLine, startColumn));
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

    private void integerLiteral() {
        // FIXME: Support to right integer literal support
        // For now just traverse through the string and add any character that is
        // a digit
        while (!isEnd() && Character.isDigit(peek()))
            advance();
        String lexeme = source.substring(startIndex, currIndex);
        int literal = Integer.parseInt(lexeme);
        addToken(INT, literal);
    }

    private void stringLiteral() {
        // FIXME: Support Correct format, like raw strings, normal string literals, or
        // FIXME: string interpolation
        // For now just traverse through the string and add characters untill the a
        // double-quote(") is found
        while (!isEnd() && peek() != '"')
            advance();

        // if no double-quote is found that means it is the end of the string, which
        // means its and error because there was no string termination using "
        if (!match('"')) {
            SweetRuntime.error(currLine, currColumn, "\" is missing, the string is unterminated.");
            return;
        }

        SwtString literal = new SwtString(source.substring(startIndex + 1, currIndex - 1));
        addToken(STRING, literal);
    }

    private void identifier() {
        while (!isEnd() && (Character.isLetter(peek()) || Character.isDigit(peek()) || peek() == '_'))
            advance();

        String lexeme = source.substring(startIndex, currIndex);
        TokenType type = keywords.get(lexeme);
        if (type == null)
            type = IDENTIFIER;
        addToken(type);
    }
}
