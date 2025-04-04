package src.Lexer_dfa;

public class Token {
    enum Type {
        KEYWORD, IDENTIFIER, NUMBER, OPERATOR, SEPARATOR, COMMENT, STRING, END_OF_FILE, COMPARE
    }

    Type type;
    String value;

    Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        return "Token(" + type + ", \"" + value + "\")";
    }
}
