package callable;

import java.util.HashMap;
import java.util.Map;

import runtime.SwtRuntimeError;
import token.Token;

public class SwtInstance {
    private SwtClass presentClass;
    private final Map<String, Object> fields = new HashMap<>();

    public SwtInstance(SwtClass presentClass) {
        this.presentClass = presentClass;
    }

    public Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        SwtFunction method = presentClass.findMethod(name.lexeme);
        if (method != null)
            return method.bind(this);

        throw new SwtRuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    public void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return presentClass.name + " instance";
    }
}
