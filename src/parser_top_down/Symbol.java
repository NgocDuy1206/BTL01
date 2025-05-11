package src.parser_top_down;

import src.Lexer_dfa.Token;

public class Symbol {
    protected Token.Type type;
    protected String varName;
    protected boolean initial = false;
    public Symbol(Token.Type type, String varName, boolean initial) {
        this.type = type;
        this.varName = varName;
        this.initial = initial;
    }

    @Override
    public String toString() {
        return type.name() + " " + varName + " " ;
    }
}
