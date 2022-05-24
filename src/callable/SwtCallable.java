package callable;

import java.util.List;

import interpreter.Interpreter;

public interface SwtCallable {
    int arity();

    Object call(Interpreter interpreter, List<Object> arguments);
}
