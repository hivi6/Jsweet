package ast;

import visitor.ExprVisitor;

public class AstPrinter implements ExprVisitor<String> {
    public String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visit(BinaryExpr expr) {
        return "(" + expr.op.lexeme + " " + expr.left.accept(this) + " " + expr.right.accept(this) + ")";
    }

    @Override
    public String visit(UnaryExpr expr) {
        return "(" + expr.op.lexeme + " " + expr.right.accept(this) + ")";
    }

    @Override
    public String visit(GroupExpr expr) {
        return "(group " + expr.expr.accept(this) + ")";
    }

    @Override
    public String visit(LiteralExpr expr) {
        return expr.val.toString();
    }

    @Override
    public String visit(TernaryExpr expr) {
        return "(conditional " +
                expr.cond.accept(this) + " " +
                expr.trueExpr.accept(this) + " " +
                expr.falseExpr.accept(this) + ")";
    }
}
