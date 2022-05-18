package ast;

import visitor.ExprVisitor;

public abstract class Expr {
    public abstract <R> R accept(ExprVisitor<R> visitor);
}
