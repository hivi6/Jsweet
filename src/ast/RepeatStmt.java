package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.StmtVisitor;

public class RepeatStmt extends Stmt {
    public RepeatStmt(Token paren, Expr upto, Stmt body) {
        this.paren = paren;
        this.upto = upto;
        this.body = body;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Token paren;
    public final Expr upto;
    public final Stmt body;
}
