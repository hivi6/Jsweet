package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.StmtVisitor;

public class VarStmt extends Stmt {
    public VarStmt(List<Pair<Token,Expr>> vars) {
        this.vars = vars;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final List<Pair<Token,Expr>> vars;
}
