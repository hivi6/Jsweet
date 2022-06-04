package visitor;

import ast.*;

public interface ExprVisitor<R> {
    R visit (AssignExpr expr);
    R visit (BinaryExpr expr);
    R visit (LogicalExpr expr);
    R visit (TernaryExpr expr);
    R visit (UnaryExpr expr);
    R visit (CallExpr expr);
    R visit (GroupExpr expr);
    R visit (LiteralExpr expr);
    R visit (VarExpr expr);
    R visit (FunExpr expr);
    R visit (GetExpr expr);
    R visit (SetExpr expr);
    R visit (ThisExpr expr);
}
