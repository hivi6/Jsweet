package token;

public enum TokenType {
    // Only Single Character
    LPAREN, RPAREN, // ( )
    LBRACE, RBRACE, // { }
    SEMICOLON, COMMA, DOT, // ; , .
    QMARK, COLON, // ? :

    ARROW, // =>

    // Arithmetic
    PLUS, PLUS_PLUS, PLUS_EQUAL, // + ++ +=
    MINUS, MINUS_MINUS, MINUS_EQUAL, // - -- -=
    STAR, STAR_EQUAL, // * *=
    SLASH, SLASH_EQUAL, // / /=
    MOD, MOD_EQUAL, // % %=
    // Relational
    EQUAL, EQUAL_EQUAL, // = ==
    BANG, BANG_EQUAL, // ! !=
    LESS, LESS_EQUAL, // < <=
    GREATER, GREATER_EQUAL, // > >=

    // Literals.
    IDENTIFIER, STRING, INT,

    // Keywords
    TRUE, FALSE, NULL, // Constants
    AND, OR, // Logical
    PRINT, // For debuging
    VAR, // Variable Declaration
    IF, ELSE, // Conditional
    DO, FOR, WHILE, REPEAT, BREAK, CONTINUE, // loops
    FUN, RETURN, // Functions
    CLASS, SUPER, THIS, // Class

    EOF
}
