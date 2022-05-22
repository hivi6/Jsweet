package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.ExprVisitor;

public class TernaryExpr extends Expr {
    public TernaryExpr(Expr cond, Expr trueExpr, Expr falseExpr) {
        this.cond = cond;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Expr cond;
    public final Expr trueExpr;
    public final Expr falseExpr;
}
