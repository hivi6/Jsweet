package ast;

import java.util.List;

import token.Token;
import visitor.StmtVisitor;

public class VarStmt extends Stmt {
    public VarStmt(Token name, Expr initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Token name;
    public final Expr initializer;
}
