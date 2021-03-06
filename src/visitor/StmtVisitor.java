package visitor;

import ast.*;

public interface StmtVisitor<R> {
    R visit (ExprStmt stmt);
    R visit (PrintStmt stmt);
    R visit (VarStmt stmt);
    R visit (FunStmt stmt);
    R visit (BlockStmt stmt);
    R visit (IfStmt stmt);
    R visit (WhileStmt stmt);
    R visit (ForStmt stmt);
    R visit (DoWhileStmt stmt);
    R visit (RepeatStmt stmt);
    R visit (BreakStmt stmt);
    R visit (ContinueStmt stmt);
    R visit (ReturnStmt stmt);
    R visit (ClassStmt stmt);
}
