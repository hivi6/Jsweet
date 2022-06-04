package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.StmtVisitor;

public class ClassStmt extends Stmt {
    public ClassStmt(Token name, List<FunStmt> methods) {
        this.name = name;
        this.methods = methods;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Token name;
    public final List<FunStmt> methods;
}
