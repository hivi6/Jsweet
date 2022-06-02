package resolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import ast.AssignExpr;
import ast.BinaryExpr;
import ast.BlockStmt;
import ast.BreakStmt;
import ast.CallExpr;
import ast.ContinueStmt;
import ast.DoWhileStmt;
import ast.Expr;
import ast.ExprStmt;
import ast.ForStmt;
import ast.FunExpr;
import ast.FunStmt;
import ast.GroupExpr;
import ast.IfStmt;
import ast.LiteralExpr;
import ast.LogicalExpr;
import ast.PrintStmt;
import ast.RepeatStmt;
import ast.ReturnStmt;
import ast.Stmt;
import ast.TernaryExpr;
import ast.UnaryExpr;
import ast.VarExpr;
import ast.VarStmt;
import ast.WhileStmt;
import interpreter.Interpreter;
import runtime.SweetRuntime;
import token.Token;
import visitor.ExprVisitor;
import visitor.StmtVisitor;

public class Resolver implements ExprVisitor<Void>, StmtVisitor<Void> {
    private Interpreter interpreter;
    private Stack<Map<String, Boolean>> scopes = new Stack<>(); // Map<String, Boolean> is for variables in a scope
    private FunctionType currentFunction = FunctionType.NONE;

    private enum FunctionType {
        NONE,
        FUNCTION
    }

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    // **********
    // visiting the ast
    // **********

    // ********** for expressions

    @Override
    public Void visit(AssignExpr expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visit(BinaryExpr expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visit(LogicalExpr expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visit(TernaryExpr expr) {
        resolve(expr.cond);
        resolve(expr.trueExpr);
        resolve(expr.falseExpr);
        return null;
    }

    @Override
    public Void visit(UnaryExpr expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visit(CallExpr expr) {
        resolve(expr.callee);
        for (var arg : expr.arguments)
            resolve(arg);
        return null;
    }

    @Override
    public Void visit(GroupExpr expr) {
        resolve(expr.expr);
        return null;
    }

    @Override
    public Void visit(LiteralExpr expr) {
        return null;
    }

    @Override
    public Void visit(VarExpr expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE)
            SweetRuntime.error(expr.name, "Can't read local variables in its own initializer.");
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visit(FunExpr expr) {
        resolveFunction(expr, FunctionType.FUNCTION);
        return null;
    }

    // ********** for statements

    @Override
    public Void visit(ExprStmt stmt) {
        resolve(stmt.expr);
        return null;
    }

    @Override
    public Void visit(PrintStmt stmt) {
        for (var expr : stmt.arguments)
            resolve(expr);
        return null;
    }

    @Override
    public Void visit(VarStmt stmt) {
        for (var decl : stmt.vars) {
            Token name = decl.first;
            Expr initializer = decl.second;
            declare(name);
            if (initializer != null)
                resolve(initializer);
            define(name);
        }
        return null;
    }

    @Override
    public Void visit(FunStmt stmt) {
        declare(stmt.name);
        define(stmt.name);
        resolve(stmt.function);
        return null;
    }

    @Override
    public Void visit(BlockStmt stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visit(IfStmt stmt) {
        resolve(stmt.cond);
        resolve(stmt.thenStmt);
        if (stmt.elseStmt != null)
            resolve(stmt.elseStmt);
        return null;
    }

    @Override
    public Void visit(WhileStmt stmt) {
        resolve(stmt.cond);
        resolve(stmt.stmt);
        return null;
    }

    @Override
    public Void visit(ForStmt stmt) {
        beginScope();
        if (stmt.initializer != null)
            resolve(stmt.initializer);
        if (stmt.cond != null)
            resolve(stmt.cond);
        if (stmt.increment != null)
            resolve(stmt.increment);
        resolve(stmt.body);
        endScope();
        return null;
    }

    @Override
    public Void visit(DoWhileStmt stmt) {
        resolve(stmt.body);
        resolve(stmt.cond);
        return null;
    }

    @Override
    public Void visit(RepeatStmt stmt) {
        resolve(stmt.upto);
        resolve(stmt.body);
        return null;
    }

    @Override
    public Void visit(BreakStmt stmt) {
        return null;
    }

    @Override
    public Void visit(ContinueStmt stmt) {
        return null;
    }

    @Override
    public Void visit(ReturnStmt stmt) {
        if (currentFunction == FunctionType.NONE)
            SweetRuntime.error(stmt.here, "Can't return from top-level code.");
        if (stmt.value != null)
            resolve(stmt.value);
        return null;
    }

    // **********
    // Helper functions
    // **********

    public void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    public void resolve(Expr expr) {
        expr.accept(this);
    }

    public void resolve(List<Stmt> stmts) {
        for (var stmt : stmts)
            resolve(stmt);
    }

    public void resolveFunction(FunExpr expr, FunctionType type) {
        FunctionType enclosingType = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : expr.params) {
            declare(param);
            define(param);
        }
        resolve(expr.body);
        endScope();
        currentFunction = enclosingType;
    }

    public void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                break;
            }
        }
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void declare(Token name) {
        if (scopes.isEmpty())
            return;
        var scope = scopes.peek();
        scope.put(name.lexeme, false);
    }

    private void define(Token name) {
        if (scopes.isEmpty())
            return;
        scopes.peek().put(name.lexeme, true);
    }
}
