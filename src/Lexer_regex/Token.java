package src.Lexer_regex;

public class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "TOKEN <" + type + ", \"" + value + "\">";
    }
}
