package types;

import java.util.List;

import callable.SwtCallable;
import callable.SwtInstance;
import interpreter.Interpreter;
import runtime.SwtRuntimeError;
import token.Token;

public class SwtArray extends SwtInstance {
    private final Object[] elems;
    private final long size;

    public SwtArray(long sz) {
        super(null);
        size = sz;
        elems = new Object[(int) sz];
    }

    @Override
    public Object get(Token name) {
        if (name.lexeme.equals("get")) {
            return new SwtCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    var index = (long) arguments.get(0);
                    return elems[(int) index];
                }
            };
        } else if (name.lexeme.equals("set")) {
            return new SwtCallable() {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    var index = (long) arguments.get(0);
                    Object value = arguments.get(1);
                    return elems[(int) index] = value;
                }
            };
        } else if (name.lexeme.equals("length")) {
            return size;
        }

        throw new SwtRuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    @Override
    public void set(Token name, Object value) {
        throw new SwtRuntimeError(name, "Can't add properties to arrays.");
    }

    @Override
    public String toString() {
        String res = "";
        for (var i = 0; i < size; i++) {
            if (i != 0)
                res += " ";
            res += elems[i];
        }
        return "[" + res + "]";
    }
}
