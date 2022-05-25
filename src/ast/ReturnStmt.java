package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.StmtVisitor;

public class ReturnStmt extends Stmt {
    public ReturnStmt(Expr value) {
        this.value = value;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Expr value;
}
