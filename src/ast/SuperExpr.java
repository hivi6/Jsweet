package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.ExprVisitor;

public class SuperExpr extends Expr {
    public SuperExpr(Token keyword, Token method) {
        this.keyword = keyword;
        this.method = method;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Token keyword;
    public final Token method;
}
