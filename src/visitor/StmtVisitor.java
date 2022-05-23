package visitor;

import ast.*;

public interface StmtVisitor<R> {
    R visit (ExprStmt stmt);
    R visit (PrintStmt stmt);
    R visit (VarStmt stmt);
    R visit (BlockStmt stmt);
    R visit (IfStmt stmt);
}
