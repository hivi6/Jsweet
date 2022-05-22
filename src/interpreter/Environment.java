package interpreter;

import java.util.HashMap;
import java.util.Map;

import runtime.SwtRuntimeError;
import token.Token;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();

    public Environment() {
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme))
            return values.get(name.lexeme);
        throw new SwtRuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
}
