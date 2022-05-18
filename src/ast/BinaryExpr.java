package ast;

import java.util.List;

import token.Token;
import visitor.ExprVisitor;

public class BinaryExpr extends Expr {
    public BinaryExpr(Expr left, Token op, Expr right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Expr left;
    public final Token op;
    public final Expr right;
}
