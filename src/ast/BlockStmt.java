package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.StmtVisitor;

public class BlockStmt extends Stmt {
    public BlockStmt(List<Stmt> statements) {
        this.statements = statements;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final List<Stmt> statements;
}
