package interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.AssignExpr;
import ast.BinaryExpr;
import ast.BlockStmt;
import ast.BreakStmt;
import ast.CallExpr;
import ast.ClassStmt;
import ast.ContinueStmt;
import ast.DoWhileStmt;
import ast.Expr;
import ast.ExprStmt;
import ast.ForStmt;
import ast.FunExpr;
import ast.FunStmt;
import ast.GetExpr;
import ast.GroupExpr;
import ast.IfStmt;
import ast.LiteralExpr;
import ast.LogicalExpr;
import ast.PrintStmt;
import ast.RepeatStmt;
import ast.ReturnStmt;
import ast.SetExpr;
import ast.Stmt;
import ast.SuperExpr;
import ast.TernaryExpr;
import ast.ThisExpr;
import ast.UnaryExpr;
import ast.VarExpr;
import ast.VarStmt;
import ast.WhileStmt;
import callable.SwtArray;
import callable.SwtCallable;
import callable.SwtClass;
import callable.SwtFunction;
import callable.SwtInstance;
import runtime.BreakException;
import runtime.ContinueException;
import runtime.ReturnException;
import runtime.SweetRuntime;
import runtime.SwtRuntimeError;
import token.Token;
import token.TokenType;
import types.SwtString;
import util.Pair;
import visitor.ExprVisitor;
import visitor.StmtVisitor;

public class Interpreter implements ExprVisitor<Object>, StmtVisitor<Void> {
    public final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    public Interpreter() {
        globals.define("clock", new SwtCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (long) System.currentTimeMillis();
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });

        globals.define("array", new SwtCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                if (!isInt(arguments.get(0))) {
                    throw new RuntimeException("array size has to be an integer.");
                }
                long sz = (long) arguments.get(0);
                return new SwtArray(sz);
            }
        });
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (SwtRuntimeError error) {
            SweetRuntime.runtimeError(error);
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
                return divideOperation(left, expr.op, right);
            case MOD:
                return modOperation(left, expr.op, right);
            case STAR:
                return multiplyOperation(left, expr.op, right);
            case MINUS:
                return minusOperation(left, expr.op, right);
            case PLUS:
                return addOperation(left, expr.op, right);
            // TODO: To add relational operation for String
            case LESS:
                if (isInt(left, right))
                    return (long) left < (long) right;
                throw unsupportedOperator(expr.op, 2);
            case LESS_EQUAL:
                if (isInt(left, right))
                    return (long) left <= (long) right;
                throw unsupportedOperator(expr.op, 2);
            case GREATER:
                if (isInt(left, right))
                    return (long) left > (long) right;
                throw unsupportedOperator(expr.op, 2);
            case GREATER_EQUAL:
                if (isInt(left, right))
                    return (long) left >= (long) right;
                throw unsupportedOperator(expr.op, 2);
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
    public Object visit(LogicalExpr expr) {
        Object left = evaluate(expr.left);
        if (expr.op.type == TokenType.OR) {
            if (isTrue(left))
                return left;
        } else {
            if (!isTrue(left))
                return left;
        }
        return evaluate(expr.right);
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
         * accordingly evaluate the expression, but this can led to leaving type
         * mismatch(this can be resolved by a resolver, or a semantic analyzer).
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
                if (isInt(right))
                    return -(long) right;
                throw unsupportedOperator(expr.op, 1);
            case BANG:
                return !isTrue(right);
            case PLUS_PLUS:
            case MINUS_MINUS:
                if (!isInt(right))
                    throw unsupportedOperator(expr.op, 1);

                long res = (long) right + (expr.op.type == TokenType.PLUS_PLUS ? 1 : -1);
                if (expr.right instanceof VarExpr) {
                    Token name = ((VarExpr) expr.right).name;
                    environment.assign(name, res);
                } else if (expr.right instanceof GetExpr) {
                    var get = (GetExpr) expr.right;
                    Object object = evaluate(get.object);
                    if (!(object instanceof SwtInstance)) {
                        throw new SwtRuntimeError(get.name, "Only instances have fields.");
                    }
                    ((SwtInstance) object).set(get.name, res);
                }
                return res;
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

    @Override
    public Object visit(VarExpr expr) {
        return lookupVariable(expr.name, expr);
    }

    @Override
    public Object visit(AssignExpr expr) {
        Object value = evaluate(expr.value);
        Object varValue = lookupVariable(expr.name, expr);
        switch (expr.equals.type) {
            case EQUAL: // no need for any modification to value
                break;
            case PLUS_EQUAL:
                value = addOperation(varValue, expr.equals, value);
                break;
            case MINUS_EQUAL:
                value = minusOperation(varValue, expr.equals, value);
                break;
            case STAR_EQUAL:
                value = multiplyOperation(varValue, expr.equals, value);
                break;
            case SLASH_EQUAL:
                value = divideOperation(varValue, expr.equals, value);
                break;
            case MOD_EQUAL:
                value = modOperation(varValue, expr.equals, value);
                break;
            default:
                break;
        }
        environment.assign(expr.name, value);
        return value; // Unreachable
    }

    @Override
    public Object visit(CallExpr expr) {
        Object callee = evaluate(expr.callee);
        List<Object> arguments = new ArrayList<>();
        for (Expr arg : expr.arguments) {
            arguments.add(evaluate(arg));
        }

        if (!(callee instanceof SwtCallable)) {
            throw new SwtRuntimeError(expr.paren, "Can only call functions and classes");
        }

        SwtCallable function = (SwtCallable) callee;
        if (arguments.size() != function.arity()) {
            throw new SwtRuntimeError(expr.paren,
                    "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }
        return function.call(this, arguments);
    }

    @Override
    public Object visit(FunExpr expr) {
        return new SwtFunction(null, expr, environment, false);
    }

    @Override
    public Object visit(GetExpr expr) {
        Object object = evaluate(expr.object);
        if (object instanceof SwtInstance) {
            return ((SwtInstance) object).get(expr.name);
        }
        throw new SwtRuntimeError(expr.name, "Only instances have property");
    }

    @Override
    public Object visit(SetExpr expr) {
        Object object = evaluate(expr.object);
        if (!(object instanceof SwtInstance)) {
            throw new SwtRuntimeError(expr.name, "Only instances have fields.");
        }
        Object value = evaluate(expr.value);
        ((SwtInstance) object).set(expr.name, value);
        return value;
    }

    @Override
    public Object visit(ThisExpr expr) {
        return lookupVariable(expr.keyword, expr);
    }

    @Override
    public Object visit(SuperExpr expr) {
        int distance = locals.get(expr);
        var superClass = (SwtClass) environment.getAt(distance, "super");
        var object = (SwtInstance) environment.getAt(distance - 1, "this");
        var method = superClass.findMethod(expr.method.lexeme);
        if (method == null) {
            throw new SwtRuntimeError(expr.method,
                    "Undefined property '" + expr.method.lexeme + "'.");
        }
        return method.bind(object);
    }

    @Override
    public Void visit(ExprStmt stmt) {
        evaluate(stmt.expr);
        return null;
    }

    @Override
    public Void visit(PrintStmt stmt) {
        for (int i = 0; i < stmt.arguments.size(); i++) {
            if (i != 0)
                System.out.print(" ");
            System.out.print(evaluate(stmt.arguments.get(i)));
        }
        System.out.println();
        return null;
    }

    @Override
    public Void visit(VarStmt stmt) {
        for (Pair<Token, Expr> p : stmt.vars) {
            Object value = null;
            if (p.second != null)
                value = evaluate(p.second);
            environment.define(p.first.lexeme, value);
        }
        return null;
    }

    @Override
    public Void visit(BlockStmt stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visit(IfStmt stmt) {
        Object condi = evaluate(stmt.cond);
        if (isTrue(condi))
            execute(stmt.thenStmt);
        else if (stmt.elseStmt != null)
            execute(stmt.elseStmt);
        return null;
    }

    @Override
    public Void visit(WhileStmt stmt) {
        while (isTrue(evaluate(stmt.cond))) {
            try {
                execute(stmt.stmt);
            } catch (BreakException e) {
                break;
            } catch (ContinueException e) {
            }
        }
        return null;
    }

    @Override
    public Void visit(ForStmt stmt) {
        Environment previous = this.environment;
        try {
            this.environment = new Environment(this.environment);
            if (stmt.initializer != null)
                execute(stmt.initializer);
            while (isTrue(evaluate(stmt.cond))) {
                try {
                    execute(stmt.body);
                } catch (BreakException e) {
                    break;
                } catch (ContinueException e) {
                }
                if (stmt.increment != null)
                    evaluate(stmt.increment);
            }
        } finally {
            this.environment = previous;
        }
        return null;
    }

    @Override
    public Void visit(DoWhileStmt stmt) {
        do {
            try {
                execute(stmt.body);
            } catch (BreakException e) {
                break;
            } catch (ContinueException e) {
            }
        } while (isTrue(evaluate(stmt.cond)));
        return null;
    }

    @Override
    public Void visit(RepeatStmt stmt) {
        Object upto = evaluate(stmt.upto);
        if (!isInt(upto))
            throw new SwtRuntimeError(stmt.paren, "Expected a int value.");
        long len = (long) upto;
        for (long i = 0; i < len; i++) {
            try {
                execute(stmt.body);
            } catch (BreakException e) {
                break;
            } catch (ContinueException e) {
            }
        }
        return null;
    }

    @Override
    public Void visit(BreakStmt stmt) {
        throw new BreakException();
    }

    @Override
    public Void visit(ContinueStmt stmt) {
        throw new ContinueException();
    }

    @Override
    public Void visit(FunStmt stmt) {
        SwtFunction function = new SwtFunction(stmt.name.lexeme, (FunExpr) stmt.function, environment, false);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visit(ReturnStmt stmt) {
        Object value = null;
        if (stmt.value != null)
            value = evaluate(stmt.value);
        throw new ReturnException(value);
    }

    @Override
    public Void visit(ClassStmt stmt) {
        Object superClass = null;
        if (stmt.superClass != null) {
            superClass = evaluate(stmt.superClass);
            if (!(superClass instanceof SwtClass))
                throw new SwtRuntimeError(stmt.superClass.name, "Superclass must be a class");
        }

        environment.define(stmt.name.lexeme, null);

        if (stmt.superClass != null) {
            environment = new Environment(environment);
            environment.define("super", superClass);
        }

        Map<String, SwtFunction> methods = new HashMap<>();
        for (var method : stmt.methods) {
            SwtFunction function = new SwtFunction(method.name.lexeme, (FunExpr) method.function, environment,
                    method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme, function);
        }

        SwtClass newClass = new SwtClass(stmt.name.lexeme, (SwtClass) superClass, methods);
        if (superClass != null) {
            environment = environment.enclosing;
        }
        environment.assign(stmt.name, newClass);
        return null;
    }

    // **********
    // Helper Functions
    // **********

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private boolean isInt(Object... vals) {
        for (Object val : vals)
            if (!(val instanceof Long))
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
        return !(val.equals(0) || val.equals(new SwtString()));
    }

    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null)
            return true;
        if (left == null || right == null)
            return false;
        return left.equals(right);
    }

    private SwtRuntimeError unsupportedOperator(Token op, int operandCount) {
        return new SwtRuntimeError(op, "Unsupported operator for the " + operandCount + " operands.");
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    public void executeBlock(List<Stmt> stmts, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt stmt : stmts)
                execute(stmt);
        } finally {
            this.environment = previous;
        }
    }

    private Object divideOperation(Object left, Token op, Object right) {
        if (isInt(left, right)) {
            if ((long) right == 0)
                throw new SwtRuntimeError(op, "Division by zero.");
            return (long) left / (long) right;
        }
        throw unsupportedOperator(op, 2);
    }

    private Object multiplyOperation(Object left, Token op, Object right) {
        if (isInt(left, right))
            return (long) left * (long) right;
        if ((isInt(left) && isString(right)) || (isString(left) && isInt(right))) {
            /*
             * Example: "abc" * 3 == "abcabcabc"
             */
            // create a new instance
            // don't use the old left value or right
            // value in res
            SwtString res = new SwtString();
            long times = (isInt(left) ? (long) left : (long) right);
            SwtString temp = new SwtString(isString(left) ? (SwtString) left : (SwtString) right);
            if (times == 0 || temp.length() == 0)
                return res;
            if (times < 0)
                throw new SwtRuntimeError(op, "Cannot be a negative number.");
            for (int i = 0; i < times; i++)
                res.append(temp);
            return res;
        }
        throw unsupportedOperator(op, 2);
    }

    private Object minusOperation(Object left, Token op, Object right) {
        if (!isInt(left, right))
            throw new SwtRuntimeError(op, "Operands must be int.");
        return (long) left - (long) right;
    }

    private Object addOperation(Object left, Token op, Object right) {
        if (isInt(left, right))
            return (long) left + (long) right;
        if (isString(left, right)) {
            SwtString res = new SwtString((SwtString) left);
            res.append((SwtString) right);
            return res;
        }
        if ((isInt(left) && isString(right)) || (isString(left) && isInt(right))) {
            // "123" + 45 = "12345"
            return new SwtString(left.toString() + right.toString());
        }
        throw unsupportedOperator(op, 2);
    }

    private Object modOperation(Object left, Token op, Object right) {
        if (isInt(left, right)) {
            if ((long) right == 0)
                throw new SwtRuntimeError(op, "Modulus by zero.");
            return (long) left % (long) right;
        }
        throw unsupportedOperator(op, 2);
    }

    public void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    private Object lookupVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }
}