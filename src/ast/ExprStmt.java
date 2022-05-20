package ast;

import java.util.List;

import token.Token;
import visitor.StmtVisitor;

public class ExprStmt extends Stmt {
    public ExprStmt(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Expr expr;
}
