package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.ExprVisitor;

public class VarExpr extends Expr {
    public VarExpr(Token name) {
        this.name = name;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Token name;
}
