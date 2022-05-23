package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.StmtVisitor;

public class WhileStmt extends Stmt {
    public WhileStmt(Expr cond, Stmt stmt) {
        this.cond = cond;
        this.stmt = stmt;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Expr cond;
    public final Stmt stmt;
}
