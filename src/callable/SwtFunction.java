package callable;

import java.util.List;

import ast.FunExpr;
import interpreter.Environment;
import interpreter.Interpreter;
import runtime.ReturnException;

public class SwtFunction implements SwtCallable {
    private final String name;
    private final FunExpr declaration;
    private final Environment closure;
    private final boolean isInitializer;

    public SwtFunction(String name, FunExpr declaration, Environment closure, boolean isInitializer) {
        this.isInitializer = isInitializer;
        this.name = name;
        this.declaration = declaration;
        this.closure = closure;
    }

    public SwtFunction bind(SwtInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new SwtFunction(name, declaration, environment, isInitializer);
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
            if (isInitializer)
                return closure.getAt(0, "this");
            return e.value;
        }
        if (isInitializer)
            return closure.getAt(0, "this");
        return null;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public String toString() {
        if (name == null)
            return "<anonymous fn>";
        return "<fn " + name + ">";
    }
}
