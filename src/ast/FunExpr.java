package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.ExprVisitor;

public class FunExpr extends Expr {
    public FunExpr(List<Token> params, List<Stmt> body) {
        this.params = params;
        this.body = body;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final List<Token> params;
    public final List<Stmt> body;
}
