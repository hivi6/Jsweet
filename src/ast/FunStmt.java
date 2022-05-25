package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.StmtVisitor;

public class FunStmt extends Stmt {
    public FunStmt(Token name, Expr function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Token name;
    public final Expr function;
}
