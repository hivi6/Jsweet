package ast;

import java.util.List;

import token.Token;
import visitor.StmtVisitor;

public class PrintStmt extends Stmt {
    public PrintStmt(List<Expr> arguments) {
        this.arguments = arguments;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final List<Expr> arguments;
}
