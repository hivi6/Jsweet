
/*
    program             -> statement* EOF ;
    statement           -> expressionStatement
                         | printStatement ;
    expressionStatement -> expression ;
    printStatement      -> "print" arguments* ";" ;
    arguments           -> ternary ("," ternary)* ;
    expression          -> comma ;
    comma               -> ternary ("," ternary)* ;
    ternary             -> equality ("?" expression ":" ternary)? ;
    equality            -> comparison ( ( "!=" | "==" ) comparison )* ;
    comparison          -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term                -> factor ( ( "-" | "+" ) factor )* ;
    factor              -> unary ( ( "/" | "*" ) unary )* ;
    unary               -> ( "!" | "-" ) unary
                         | primary ;
    primary             -> NUMBER | STRING | "true" | "false" | "nil"
                         | "(" expression ")" ;
*/

package parser;

import java.util.ArrayList;
import java.util.List;

import ast.BinaryExpr;
import ast.Expr;
import ast.ExprStmt;
import ast.GroupExpr;
import ast.LiteralExpr;
import ast.PrintStmt;
import ast.Stmt;
import ast.TernaryExpr;
import ast.UnaryExpr;
import runtime.SweetRuntime;
import token.Token;
import token.TokenType;

import static token.TokenType.*;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> stmts = new ArrayList<>();
        try {
            while (!isEnd()) {
                stmts.add(statement());
            }
        } catch (ParseError e) {

        }
        return stmts;
    }

    // **********
    // Just Converting the grammar to functions
    // **********

    private Stmt statement() {
        if (match(PRINT))
            return printStatement();
        return expressionStatement();
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expected ';' after value.");
        return new ExprStmt(expr);
    }

    private Stmt printStatement() {
        if (match(SEMICOLON))
            return new PrintStmt(new ArrayList<>());
        List<Expr> args = arguments();
        consume(SEMICOLON, "Expected ';' after value.");
        return new PrintStmt(args);
    }

    private List<Expr> arguments() {
        List<Expr> args = new ArrayList<>();
        args.add(ternary());
        while (match(COMMA)) {
            args.add(ternary());
        }
        return args;
    }

    private Expr expression() {
        return comma();
    }

    private Expr comma() {
        Expr left = ternary();
        while (match(COMMA)) {
            Token op = previous();
            Expr right = ternary();
            left = new BinaryExpr(left, op, right);
        }
        return left;
    }

    private Expr ternary() {
        Expr expr = equality();
        if (match(QMARK)) {
            Expr trueExpr = expression();
            consume(COLON, "Expected ':' inside a conditional expression.");
            Expr falseExpr = ternary();
            expr = new TernaryExpr(expr, trueExpr, falseExpr);
        }
        return expr;
    }

    private Expr equality() {
        Expr left = comparision();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token op = previous();
            Expr right = comparision();
            left = new BinaryExpr(left, op, right);
        }
        return left;
    }

    private Expr comparision() {
        Expr left = term();
        while (match(LESS, LESS_EQUAL, GREATER, GREATER_EQUAL)) {
            Token op = previous();
            Expr right = term();
            left = new BinaryExpr(left, op, right);
        }
        return left;
    }

    private Expr term() {
        Expr left = factor();
        while (match(PLUS, MINUS)) {
            Token op = previous();
            Expr right = factor();
            left = new BinaryExpr(left, op, right);
        }
        return left;
    }

    private Expr factor() {
        Expr left = unary();
        while (match(SLASH, STAR)) {
            Token op = previous();
            Expr right = unary();
            left = new BinaryExpr(left, op, right);
        }
        return left;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token op = previous();
            Expr right = unary();
            return new UnaryExpr(op, right);
        }
        return primary();
    }

    private Expr primary() {
        if (match(INT, STRING))
            return new LiteralExpr(previous().literal);
        if (match(TRUE))
            return new LiteralExpr(true);
        if (match(FALSE))
            return new LiteralExpr(false);
        if (match(NIL))
            return new LiteralExpr(NIL);
        if (match(LPAREN)) {
            Expr expr = expression();
            consume(RPAREN, "Expected ')' after expression");
            return new GroupExpr(expr);
        }
        throw error(peek(), "Expect expression.");
    }

    // **********
    // Helper Functions
    // **********

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isEnd())
            return false;
        return peek().type == type;
    }

    private boolean isEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token advance() {
        if (!isEnd())
            current++;
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String msg) {
        if (check(type))
            return advance();
        throw error(peek(), msg);
    }

    private ParseError error(Token t, String msg) {
        SweetRuntime.error(t, msg);
        return new ParseError();
    }

    private void synchronize() {
        // this is the token that caused the error
        // so skip this token
        advance();

        while (!isEnd()) {
            // if semicolon or a keyword then return
            if (previous().type == SEMICOLON)
                return;
            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
                default:
            }

            advance();
        }
    }
}
