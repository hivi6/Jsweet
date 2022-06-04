package types;

import java.util.List;

import callable.SwtCallable;
import callable.SwtInstance;
import interpreter.Interpreter;
import runtime.SwtRuntimeError;
import token.Token;

public class SwtString extends SwtInstance {
    final StringBuilder builder;

    public SwtString() {
        super(null);
        this.builder = new StringBuilder();
    }

    public SwtString(String str) {
        super(null);
        this.builder = new StringBuilder(str);
    }

    public SwtString(StringBuilder builder) {
        super(null);
        this.builder = builder;
    }

    public SwtString(SwtString s) {
        super(null);
        this.builder = new StringBuilder(s.builder.toString());
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
                    return (long) at((int) index);
                }
            };
        } else if (name.lexeme.equals("length")) {
            return (long) length();
        }

        throw new SwtRuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    @Override
    public void set(Token name, Object value) {
        throw new SwtRuntimeError(name, "Can't add properties to arrays.");
    }

    public int length() {
        return this.builder.length();
    }

    public char at(int i) {
        if (0 <= i && i < this.builder.length())
            return this.builder.charAt(i);
        throw new StringIndexOutOfBoundsException("Array out of bound.");
    }

    public char at(int i, char ch) {
        if (0 <= i && i < this.builder.length()) {
            this.builder.setCharAt(i, ch);
            return ch;
        }
        throw new StringIndexOutOfBoundsException("Array out of bound.");
    }

    public void append(String s) {
        this.builder.append(s);
    }

    public void append(SwtString s) {
        this.builder.append(s.builder.toString());
    }

    public void append(StringBuilder builder) {
        this.builder.append(builder.toString());
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SwtString))
            return false;
        if (((SwtString) obj).length() != length())
            return false;
        for (int i = 0; i < length(); i++)
            if (at(i) != ((SwtString) obj).at(i))
                return false;
        return true;
    }
}
