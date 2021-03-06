
/*
    program             -> statement* EOF ;
    declaration         -> classDeclaration
                         | varDeclaration
                         | functionDeclaration
                         | statement ;
    classDeclaration    -> "class" IDENTIFIER ("<" IDENTIFIER)? "{" function* "}" ;
    varDeclaration      -> "var" IDENTIFIER ("=" assignment)? ("," IDENTIFIER ("=" assignment))* ";" ;
    functionDeclaration -> "fun" function ;
    function            -> IDENTIFIER functionBody ;
    functionBody        -> "(" parameters? ")" (block | ("=>" assignment)) ;
    parameters          -> IDENTIFIER ("," IDENTIFIER)* ;
    statement           -> expressionStatement
                         | printStatement
                         | block 
                         | ifStatement
                         | whileStatement
                         | forStatement
                         | doWhileStatement
                         | repeatStatement
                         | breakStatement
                         | continueStatement 
                         | returnStatement ;
    expressionStatement -> expression ;
    printStatement      -> "print" arguments* ";" ;
    arguments           -> assignment ("," assignment)* ;
    block               -> "{" declaration* "}" ;
    ifStatement         -> "if" "(" expression ")" statement ("else" statement)? ;
    whileStatement      -> "while" "(" expression ")" statement ;
    forStatement        -> "for" "(" (varDeclaration | expressionStatement | ";") expression? ";" expression? ")"
                           statement ;
    doWhileStatement    -> "do" statement "while" "(" expression ")" ";" ;
    repeatStatement     -> "repeat" "(" expression ")" statement ;
    breakStatement      -> "break" ";" ;
    continueStatement   -> "continue" ";" ;
    returnStatement     -> "return" expression ";" ;
    expression          -> comma ;
    comma               -> assignment ("," assignment)* ;
    assignment          -> (call ".")? IDENTIFIER ("=" | "+=" | "-=" | "*=" | "/=" | "%=") assignment
                         | ternary ;
    ternary             -> logicalOr ("?" expression ":" ternary)? ;
    logicalOr           -> logicalAnd ( "or" logicalAnd )* ;
    logicalAnd          -> equality ( "and" equality )* ;
    equality            -> comparison ( ( "!=" | "==" ) comparison )* ;
    comparison          -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term                -> factor ( ( "-" | "+" ) factor )* ;
    factor              -> unary ( ( "/" | "*" | "%" ) unary )* ;
    unary               -> ( "!" | "-" | "++" | "--" ) unary
                         | call ;
    call                -> primary ("(" arguments? ")" | "." IDENTIFIER)* ;
    primary             -> NUMBER | STRING | "true" | "false" | "nil"
                         | "(" expression ")" | IDENTIFIER | functionExpr | "this"
                         | "super" "." IDENTIFIER;
    functionExpr        -> "fun" functionBody ;
*/

package parser;

import java.util.ArrayList;
import java.util.List;

import ast.AssignExpr;
import ast.BinaryExpr;
import ast.BlockStmt;
import ast.BreakStmt;
import ast.CallExpr;
import ast.ClassStmt;
import ast.ContinueStmt;
import ast.DoWhileStmt;
import ast.Expr;
import ast.ExprStmt;
import ast.ForStmt;
import ast.FunExpr;
import ast.FunStmt;
import ast.GetExpr;
import ast.GroupExpr;
import ast.IfStmt;
import ast.LiteralExpr;
import ast.LogicalExpr;
import ast.PrintStmt;
import ast.RepeatStmt;
import ast.ReturnStmt;
import ast.SetExpr;
import ast.Stmt;
import ast.SuperExpr;
import ast.TernaryExpr;
import ast.ThisExpr;
import ast.UnaryExpr;
import ast.VarExpr;
import ast.VarStmt;
import ast.WhileStmt;
import runtime.SweetRuntime;
import token.Token;
import token.TokenType;
import util.Pair;

import static token.TokenType.*;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private int loopDepth = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> stmts = new ArrayList<>();
        while (!isEnd()) {
            stmts.add(declaration());
        }
        return stmts;
    }

    // **********
    // Just Converting the grammar to functions
    // **********

    private Stmt declaration() {
        try {
            if (match(CLASS))
                return classDeclaration();
            if (match(VAR))
                return varDeclaration();
            if (check(FUN) && checkNext(IDENTIFIER)) {
                consume(FUN, null);
                return function("function");
            }
            return statement();
        } catch (ParseError e) {
            synchronize();
            return null;
        }
    }

    private Stmt classDeclaration() {
        Token name = consume(IDENTIFIER, "Expect class name.");

        VarExpr superClass = null;
        if (match(LESS)) {
            consume(IDENTIFIER, "Expect superclass name");
            superClass = new VarExpr(previous());
        }

        consume(LBRACE, "Expect '{' after class name.");

        List<FunStmt> methods = new ArrayList<>();
        while (!check(RBRACE) && !isEnd()) {
            methods.add(function("method"));
        }

        consume(RBRACE, "Expect '}' after class body.");

        return new ClassStmt(name, superClass, methods);
    }

    private Stmt varDeclaration() {
        List<Pair<Token, Expr>> vars = new ArrayList<>();
        do {
            Token name = consume(IDENTIFIER, "Expected a variable name.");
            Expr initializer = null;
            if (match(EQUAL))
                initializer = assignment();
            vars.add(new Pair<>(name, initializer));
        } while (match(COMMA));
        consume(SEMICOLON, "Expected ';' after variable declaration.");
        return new VarStmt(vars);
    }

    private FunStmt function(String kind) {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        return new FunStmt(name, functionBody(kind, true));
    }

    private Expr functionBody(String kind, boolean isDecl) {
        consume(LPAREN, "Expect '(' after " + kind + " name.");
        List<Token> params = new ArrayList<>();
        if (!check(RPAREN))
            params = parameters();
        consume(RPAREN, "Expect ')' after " + kind + " parameters.");

        // For lambda expression
        List<Stmt> body = new ArrayList<>();
        if (match(ARROW)) {
            Token here = previous();
            Expr expr = assignment();
            Stmt returnStmt = new ReturnStmt(here, expr);
            body.add(returnStmt);
            if (isDecl)
                consume(SEMICOLON, "Expect ';' after lambda function 'declaration'");
        } else {
            consume(LBRACE, "Expect '{' before " + kind + " body.");
            body = block();
        }
        return new FunExpr(params, body);
    }

    private List<Token> parameters() {
        List<Token> params = new ArrayList<>();
        do {
            if (params.size() >= 255) {
                error(peek(), "Can't have more than 255 parameters.");
            }
            params.add(consume(IDENTIFIER, "Expect parameter name."));
        } while (match(COMMA));
        return params;
    }

    private Stmt statement() {
        if (match(PRINT))
            return printStatement();
        if (match(LBRACE))
            return new BlockStmt(block());
        if (match(IF))
            return ifStatement();
        if (match(WHILE))
            return whileStatement();
        if (match(FOR))
            return forStatement();
        if (match(DO))
            return doWhileStatement();
        if (match(REPEAT))
            return repeatStatement();
        if (match(BREAK))
            return breakStatement();
        if (match(CONTINUE))
            return continueStatement();
        if (match(RETURN))
            return returnStatement();
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
        args.add(assignment());
        while (match(COMMA)) {
            if (args.size() >= 255) {
                error(peek(), "Can't have more than 255 arguments.");
            }
            args.add(assignment());
        }
        return args;
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(RBRACE) && !isEnd()) {
            statements.add(declaration());
        }
        consume(RBRACE, "Expect '}' after block.");
        return statements;
    }

    private Stmt ifStatement() {
        consume(LPAREN, "Expect '(' after if keyword.");
        Expr cond = expression();
        consume(RPAREN, "Expect ')' after condition.");
        Stmt thenStmt = statement();
        Stmt elseStmt = null;
        if (match(ELSE)) {
            elseStmt = statement();
        }
        return new IfStmt(cond, thenStmt, elseStmt);
    }

    private Stmt whileStatement() {
        try {
            loopDepth++;
            consume(LPAREN, "Expect '(' after while keyword.");
            Expr cond = expression();
            consume(RPAREN, "Expect ')' after while condition.");
            Stmt stmt = statement();
            return new WhileStmt(cond, stmt);
        } finally {
            loopDepth--;
        }
    }

    private Stmt forStatement() {
        try {
            loopDepth++;
            consume(LPAREN, "Expect '(' after for keyword.");
            Stmt initializer;
            if (match(SEMICOLON))
                initializer = null;
            else if (match(VAR))
                initializer = varDeclaration();
            else
                initializer = expressionStatement();

            Expr cond = new LiteralExpr(true); // if no condition then true
            if (!check(SEMICOLON))
                cond = expression();
            consume(SEMICOLON, "Expect ';' after for condition expression.");

            Expr increment = null;
            if (!check(RPAREN))
                increment = expression();
            consume(RPAREN, "Expect ')' after for increment expression.");

            Stmt body = statement();

            return new ForStmt(initializer, cond, increment, body);
        } finally {
            loopDepth--;
        }
    }

    private Stmt doWhileStatement() {
        try {
            loopDepth++;
            Stmt body = statement();
            consume(WHILE, "Expect while after do body.");
            consume(LPAREN, "Expect '(' after while keyword.");
            Expr cond = expression();
            consume(RPAREN, "Expect ')' after while condition.");
            consume(SEMICOLON, "Expect ';' after do while clause.");
            return new DoWhileStmt(body, cond);
        } finally {
            loopDepth--;
        }
    }

    private Stmt repeatStatement() {
        try {
            loopDepth++;
            Token paren = consume(LPAREN, "Expect '(' after repeat keyword.");
            Expr upto = expression();
            consume(RPAREN, "Expect ')' after repeat length.");
            Stmt body = statement();
            return new RepeatStmt(paren, upto, body);
        } finally {
            loopDepth--;
        }
    }

    private Stmt breakStatement() {
        if (loopDepth <= 0) {
            throw error(previous(), "'break' cannot be used outside loops.");
        }
        consume(SEMICOLON, "Expect ';' after break keyword.");
        return new BreakStmt();
    }

    private Stmt continueStatement() {
        if (loopDepth <= 0) {
            throw error(previous(), "'continue' cannot be used outside loops.");
        }
        consume(SEMICOLON, "Expect ';' after continue keyword.");
        return new ContinueStmt();
    }

    private Stmt returnStatement() {
        Token keyword = previous();
        Expr value = null;
        if (!check(SEMICOLON))
            value = expression();
        consume(SEMICOLON, "Expect ';' after return keyword");
        return new ReturnStmt(keyword, value);
    }

    private Expr expression() {
        return comma();
    }

    private Expr comma() {
        Expr left = assignment();
        while (match(COMMA)) {
            Token op = previous();
            Expr right = assignment();
            left = new BinaryExpr(left, op, right);
        }
        return left;
    }

    private Expr assignment() {
        Expr expr = ternary();
        if (match(EQUAL, PLUS_EQUAL, MINUS_EQUAL, STAR_EQUAL, SLASH_EQUAL, MOD_EQUAL)) {
            Token equals = previous();
            Expr value = assignment();
            if (expr instanceof VarExpr) {
                Token name = ((VarExpr) expr).name;
                return new AssignExpr(name, equals, value);
            } else if (expr instanceof GetExpr) {
                GetExpr get = (GetExpr) expr;
                return new SetExpr(get.object, get.name, value);
            }
            error(equals, "Invalid assignment target");
        }
        return expr;
    }

    private Expr ternary() {
        Expr expr = logicalOr();
        if (match(QMARK)) {
            Expr trueExpr = expression();
            consume(COLON, "Expected ':' inside a conditional expression.");
            Expr falseExpr = ternary();
            expr = new TernaryExpr(expr, trueExpr, falseExpr);
        }
        return expr;
    }

    private Expr logicalOr() {
        Expr left = logicalAnd();
        while (match(OR)) {
            Token op = previous();
            Expr right = logicalOr();
            left = new LogicalExpr(left, op, right);
        }
        return left;
    }

    private Expr logicalAnd() {
        Expr left = equality();
        while (match(AND)) {
            Token op = previous();
            Expr right = equality();
            left = new LogicalExpr(left, op, right);
        }
        return left;
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
        while (match(SLASH, STAR, MOD)) {
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
        } else if (match(PLUS_PLUS, MINUS_MINUS)) {
            Token op = previous();
            Expr right = unary();
            if (right instanceof VarExpr || right instanceof GetExpr) {
                return new UnaryExpr(op, right);
            }
            throw error(op, "Expect identifier after prefix operator(++/--).");
        }
        return call();
    }

    private Expr call() {
        Expr callee = primary();
        while (true) {
            if (match(LPAREN)) {
                List<Expr> args = new ArrayList<>();
                if (!check(RPAREN))
                    args = arguments();
                Token paren = consume(RPAREN, "Expect ')' after function arguments.");
                callee = new CallExpr(callee, paren, args);
            } else if (match(DOT)) {
                Token name = consume(IDENTIFIER, "Expect member name after '.'.");
                callee = new GetExpr(callee, name);
            } else
                break;
        }
        return callee;
    }

    private Expr primary() {
        if (match(INT, STRING))
            return new LiteralExpr(previous().literal);
        if (match(TRUE))
            return new LiteralExpr(true);
        if (match(FALSE))
            return new LiteralExpr(false);
        if (match(NULL))
            return new LiteralExpr(null);
        if (match(LPAREN)) {
            Expr expr = expression();
            consume(RPAREN, "Expected ')' after expression");
            return new GroupExpr(expr);
        }
        if (match(IDENTIFIER))
            return new VarExpr(previous());
        if (match(FUN))
            return functionBody("function", false);
        if (match(THIS))
            return new ThisExpr(previous());
        if (match(SUPER)) {
            Token keyword = previous();
            consume(DOT, "Expect '.' after super keyword.");
            Token method = consume(IDENTIFIER, "Expect superclass method name.");
            return new SuperExpr(keyword, method);
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

    private boolean checkNext(TokenType tokenType) {
        if (isEnd())
            return false;
        if (tokens.get(current + 1).type == EOF)
            return false;
        return tokens.get(current + 1).type == tokenType;
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
