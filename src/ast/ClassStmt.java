package ast;

import java.util.List;

import util.Pair;
import token.Token;
import visitor.StmtVisitor;

public class ClassStmt extends Stmt {
    public ClassStmt(Token name, VarExpr superClass, List<FunStmt> methods) {
        this.name = name;
        this.superClass = superClass;
        this.methods = methods;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public final Token name;
    public final VarExpr superClass;
    public final List<FunStmt> methods;
}
