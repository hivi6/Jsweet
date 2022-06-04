package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.ExprVisitor;

public class ThisExpr extends Expr {
    public ThisExpr(Token keyword) {
        this.keyword = keyword;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Token keyword;
}
