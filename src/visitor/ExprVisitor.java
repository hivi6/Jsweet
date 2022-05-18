package visitor;

import ast.*;

public interface ExprVisitor<R> {
    R visit (BinaryExpr expr);
    R visit (UnaryExpr expr);
    R visit (GroupExpr expr);
    R visit (LiteralExpr expr);
}
