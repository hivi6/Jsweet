package interpreter;

import ast.BinaryExpr;
import ast.Expr;
import ast.GroupExpr;
import ast.LiteralExpr;
import ast.TernaryExpr;
import ast.UnaryExpr;
import runtime.SweetRuntime;
import runtime.SwtRuntimeError;
import types.SwtString;
import visitor.ExprVisitor;

public class Interpreter implements ExprVisitor<Object> {
    public void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(value);
        } catch (SwtRuntimeError e) {
            SweetRuntime.runtimeError(e);
        }
    }

    // **********
    // Ast Logic
    // **********

    @Override
    public Object visit(BinaryExpr expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.op.type) {
            case SLASH:
                if (!isInt(left, right))
                    throw new SwtRuntimeError(expr.op, "Operands must be int.");
                if ((int) right == 0)
                    throw new SwtRuntimeError(expr.op, "Division by zero.");
                return (int) left / (int) right;
            case STAR:
                if (isInt(left, right))
                    return (int) left * (int) right;
                if ((isInt(left) && isString(right)) || (isString(left) && isInt(right))) {
                    /*
                     * Example: "abc" * 3 == "abcabcabc"
                     */
                    // create a new instance
                    // don't use the old left value or right
                    // value in res
                    SwtString res = new SwtString();
                    int times = (isInt(left) ? (int) left : (int) right);
                    SwtString temp = new SwtString(isString(left) ? (SwtString) left : (SwtString) right);
                    if (times == 0 || temp.length() == 0)
                        return res;
                    if (times < 0)
                        throw new SwtRuntimeError(expr.op, "Cannot be a negative number.");
                    for (int i = 0; i < times; i++)
                        res.append(temp);
                    return res;
                }
                throw new SwtRuntimeError(expr.op, "Not support for the given 2 operands.");
            case MINUS:
                if (!isInt(left, right))
                    throw new SwtRuntimeError(expr.op, "Operands must be int.");
                return (int) left - (int) right;
            case PLUS:
                if (isInt(left, right))
                    return (int) left + (int) right;
                if (isString(left, right)) {
                    SwtString res = new SwtString((SwtString) left);
                    res.append((SwtString) right);
                    return res;
                }
                throw new SwtRuntimeError(expr.op, "Not support for the given 2 operands.");
            // TODO: To add relational operation for String
            case LESS:
                if (isInt(left, right))
                    return (int) left < (int) right;
                throw new SwtRuntimeError(expr.op, "Not support for the given 2 operands.");
            case LESS_EQUAL:
                if (isInt(left, right))
                    return (int) left <= (int) right;
                throw new SwtRuntimeError(expr.op, "Not support for the given 2 operands.");
            case GREATER:
                if (isInt(left, right))
                    return (int) left > (int) right;
                throw new SwtRuntimeError(expr.op, "Not support for the given 2 operands.");
            case GREATER_EQUAL:
                if (isInt(left, right))
                    return (int) left >= (int) right;
                throw new SwtRuntimeError(expr.op, "Not support for the given 2 operands.");
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case COMMA:
                return right;
            default:
                break;
        }
        return null; // Unreachable
    }

    @Override
    public Object visit(TernaryExpr expr) {
        /*
         * Let's say we evaluate,
         * a == 0 ? 0: 10 / a;
         * here, if we do eager propagation then (10 / a) is also evaluated and if
         * a == 0 then will led to division by zero error, even though the whole reason
         * for the expression was to not evaluate a division by zero.
         * So here we do lazy propagation, i.e. first check condition and then
         * accordingly
         * evaluate the expression, but this can led to leaving type mismatch(this can
         * be resolved
         * by a resolver, or a semantic analyzer).
         */
        Object cond = evaluate(expr.cond);
        if (isTrue(cond))
            return evaluate(expr.trueExpr);
        return evaluate(expr.falseExpr);
    }

    @Override
    public Object visit(UnaryExpr expr) {
        Object right = evaluate(expr.right);

        switch (expr.op.type) {
            case MINUS: // only available with numeric types
                if (!isInt(right))
                    throw new SwtRuntimeError(expr.op, "Operand must be a integer.");
                return -(int) right;
            case BANG:
                return !isTrue(right);
            default:
                break;
        }
        return null; // Unreachable
    }

    @Override
    public Object visit(GroupExpr expr) {
        return evaluate(expr.expr);
    }

    @Override
    public Object visit(LiteralExpr expr) {
        return expr.val;
    }

    // **********
    // Helper Functions
    // **********

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private boolean isInt(Object... vals) {
        for (Object val : vals)
            if (!(val instanceof Integer))
                return false;
        return true;
    }

    private boolean isBoolean(Object... vals) {
        for (Object val : vals)
            if (!(val instanceof Boolean))
                return false;
        return true;
    }

    private boolean isString(Object... vals) {
        for (Object val : vals)
            if (!(val instanceof SwtString))
                return false;
        return true;
    }

    private boolean isTrue(Object val) {
        if (val == null)
            return false;
        if (isBoolean(val))
            return (boolean) val;
        return !(val.equals(0) || val.equals(""));
    }

    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null)
            return true;
        if (left == null || right == null)
            return false;
        return left.equals(right);
    }
}