package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.ExprVisitor;

public class CallExpr extends Expr {
    public CallExpr(Expr callee, Token paren, List<Expr> arguments) {
        this.callee = callee;
        this.paren = paren;
        this.arguments = arguments;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Expr callee;
    public final Token paren;
    public final List<Expr> arguments;
}
