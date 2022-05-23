package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.StmtVisitor;

public class ForStmt extends Stmt {
    public ForStmt(Stmt initializer, Expr cond, Expr increment, Stmt body) {
        this.initializer = initializer;
        this.cond = cond;
        this.increment = increment;
        this.body = body;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Stmt initializer;
    public final Expr cond;
    public final Expr increment;
    public final Stmt body;
}
