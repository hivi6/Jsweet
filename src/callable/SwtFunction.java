package callable;

import java.util.List;

import ast.FunStmt;
import interpreter.Environment;
import interpreter.Interpreter;
import runtime.ReturnException;

public class SwtFunction implements SwtCallable {

    private final FunStmt declaration;
    private final Environment closure;

    public SwtFunction(FunStmt declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (ReturnException e) {
            return e.value;
        }
        return null;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
}