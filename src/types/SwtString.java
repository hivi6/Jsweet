package types;

public class SwtString {
    final StringBuilder builder;

    public SwtString() {
        this.builder = new StringBuilder();
    }

    public SwtString(String str) {
        this.builder = new StringBuilder(str);
    }

    public SwtString(StringBuilder builder) {
        this.builder = builder;
    }

    public SwtString(SwtString s) {
        this.builder = new StringBuilder(s.builder.toString());
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
