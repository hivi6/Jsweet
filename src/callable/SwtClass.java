package callable;

import java.util.List;
import java.util.Map;

import interpreter.Interpreter;

public class SwtClass implements SwtCallable {
    final String name;
    final SwtClass superClass;
    private final Map<String, SwtFunction> methods;

    public SwtClass(String name, SwtClass superClass, Map<String, SwtFunction> methods) {
        this.name = name;
        this.superClass = superClass;
        this.methods = methods;
    }

    public SwtFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superClass != null)
            return superClass.findMethod(name);

        return null;
    }

    @Override
    public int arity() {
        var initializer = findMethod("init");
        if (initializer != null)
            return initializer.arity();
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        SwtInstance instance = new SwtInstance(this);

        var initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }

        return instance;
    }

    @Override
    public String toString() {
        return name;
    }
}
