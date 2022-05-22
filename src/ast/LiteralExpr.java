package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.ExprVisitor;

public class LiteralExpr extends Expr {
    public LiteralExpr(Object val) {
        this.val = val;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Object val;
}
