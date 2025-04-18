package src.Lexer_dfa;

public class Token {
    public enum Type {
        KEYWORD, IDENTIFIER, NUMBER, BOOLEAN, OPERATOR, SEPARATOR, COMMENT, STRING, END_OF_FILE, COMPARE,

        // Keyword
        DATA_TYPE,
        IF, THEN, ELSE, DO, WHILE, BEGIN, END, FOR, INT, BOOL,PRINT,
        ASSIGN,
        LPAREN, RPAREN, // ( )
        LBRACE, RBRACE, // {   }
        NULL, EOF, END_STATEMENT,
        PLUS, MUL, // + , *
    }

    public Type type;
    public String value;

    public int line;

    public Token(int line, Type type, String value) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    public String toString() {
        return "Token(" + type + ", \"" + value + "\") in line " + line;
    }
}
