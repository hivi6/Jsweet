package ast;

import java.util.List;

import token.Token;
import visitor.ExprVisitor;

public class GroupExpr extends Expr {
    public GroupExpr(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Expr expr;
}
