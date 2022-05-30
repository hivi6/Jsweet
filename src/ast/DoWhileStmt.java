package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.StmtVisitor;

public class DoWhileStmt extends Stmt {
    public DoWhileStmt(Stmt body, Expr cond) {
        this.body = body;
        this.cond = cond;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Stmt body;
    public final Expr cond;
}
