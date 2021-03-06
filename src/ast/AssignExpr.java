package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.ExprVisitor;

public class AssignExpr extends Expr {
    public AssignExpr(Token name, Token equals, Expr value) {
        this.name = name;
        this.equals = equals;
        this.value = value;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Token name;
    public final Token equals;
    public final Expr value;
}
