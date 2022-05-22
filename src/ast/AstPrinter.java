package ast;

import java.util.List;

import visitor.ExprVisitor;
import visitor.StmtVisitor;

public class AstPrinter implements ExprVisitor<String>, StmtVisitor<String> {
    private String res = "";

    public AstPrinter() {
    }

    public AstPrinter(Expr expr) {
        res = evaluate(expr);
    }

    public AstPrinter(Stmt stmt) {
        res = execute(stmt);
    }

    public AstPrinter(List<Stmt> stmts) {
        for (Stmt stmt : stmts)
            res += execute(stmt);
    }

    private String evaluate(Expr expr) {
        return expr.accept(this);
    }

    private String execute(Stmt stmt) {
        return stmt.accept(this);
    }

    @Override
    public String visit(BinaryExpr expr) {
        return "(" + expr.op.lexeme + " " + evaluate(expr.left) + " " + evaluate(expr.right) + ")";
    }

    @Override
    public String visit(UnaryExpr expr) {
        return "(" + expr.op.lexeme + " " + evaluate(expr.right) + ")";
    }

    @Override
    public String visit(GroupExpr expr) {
        return "(group " + evaluate(expr.expr) + ")";
    }

    @Override
    public String visit(LiteralExpr expr) {
        return expr.val.toString();
    }

    @Override
    public String visit(TernaryExpr expr) {
        return "(conditional " +
                evaluate(expr.cond) + " " +
                evaluate(expr.trueExpr) + " " +
                evaluate(expr.falseExpr) + ")";
    }

    @Override
    public String visit(ExprStmt stmt) {
        return evaluate(stmt.expr) + "\n";
    }

    @Override
    public String visit(PrintStmt stmt) {
        String res = "(print";
        for (int i = 0; i < stmt.arguments.size(); i++) {
            res += " " + evaluate(stmt.arguments.get(i));
        }
        return res + ")\n";
    }

    @Override
    public String visit(VarStmt stmt) {
        return "(var " + stmt.name.lexeme +
                " " + (stmt.initializer == null ? "null" : evaluate(stmt.initializer)) + ")\n";
    }

    @Override
    public String visit(VarExpr expr) {
        return expr.name.lexeme;
    }

    @Override
    public String toString() {
        return res;
    }
}