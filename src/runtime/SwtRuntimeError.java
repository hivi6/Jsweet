package runtime;

import token.Token;

public class SwtRuntimeError extends RuntimeException {
    public final Token token;

    public SwtRuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}