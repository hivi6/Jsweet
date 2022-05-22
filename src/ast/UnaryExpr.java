package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.ExprVisitor;

public class UnaryExpr extends Expr {
    public UnaryExpr(Token op, Expr right) {
        this.op = op;
        this.right = right;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Token op;
    public final Expr right;
}
