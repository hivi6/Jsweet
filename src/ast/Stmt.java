package ast;

import visitor.StmtVisitor;

public abstract class Stmt {
    public abstract <R> R accept(StmtVisitor<R> visitor);
}
