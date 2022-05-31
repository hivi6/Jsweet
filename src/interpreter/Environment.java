package interpreter;

import java.util.HashMap;
import java.util.Map;

import runtime.SwtRuntimeError;
import token.Token;

public class Environment {
    private Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme))
            return values.get(name.lexeme);

        if (enclosing != null)
            return enclosing.get(name);

        throw new SwtRuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new SwtRuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    public Environment ancestor(int distance) {
        var environment = this;
        for (int i = 0; i < distance; ++i)
            environment = environment.enclosing;
        return environment;
    }

    public Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }
}
