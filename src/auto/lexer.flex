import java_cup.runtime.*;

%%

%public
%class Lexer
%unicode
%ignorecase
%line
%column
%char
%cup
%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
%}



// --- Định nghĩa pattern ---
BEGIN           = "begin"
END             = "end"
INT             = "int"
BOOL            = "bool"
BOOLEAN         = "true"|"false"
ID              = [a-zA-ZÀ-Ỹà-ỹ_] [a-zA-ZÀ-Ỹà-ỹ0-9_]*
NUMBER          = [0-9]+
ASSIGN          = "="

PLUS            = "+"
MULTIPLY        = "*"
LPAREN          = "("
RPAREN          = ")"
IF              = "if"
THEN            = "then"
ELSE            = "else"
DO              = "do"
WHILE           = "while"
FOR             = "for"
PRINT           = "print"
COMMENT_LINE   = "//"[^\n]*
COMMENT_BLOCK  = "/*" ([^*] | "*" [^/])* "*"+ "/"
END_STATEMENT   = ";"
LBRACE          = "{"
RBRACE          = "}"
GT              = ">"
GE              = ">="
EQ              = "=="

%%

// --- Quy tắc phân tích ---
<YYINITIAL> {
    [ \t\r\n]+      { /* Bỏ qua khoảng trắng */ }
    {IF}            { return symbol(sym.IF); }
    {THEN}          { return symbol(sym.THEN); }
    {ELSE}          { return symbol(sym.ELSE); }
    {DO}            { return symbol(sym.DO); }
    {WHILE}         { return symbol(sym.WHILE); }
    {FOR}           { return symbol(sym.FOR); }
    {PRINT}         { return symbol(sym.PRINT); }
    {GT}            { return symbol(sym.GT);}
    {GE}            { return symbol(sym.GE);}
    {EQ}            { return symbol(sym.EQ);}
    {COMMENT_LINE}  { /* Bỏ qua comment */ }
    {COMMENT_BLOCK} { /* Bỏ qua comment */ }
    {END_STATEMENT} { return symbol(sym.END_STATEMENT);}
    {LBRACE}        { return symbol(sym.LBRACE);}
    {RBRACE}        { return symbol(sym.RBRACE);}
    {BEGIN}         { return symbol(sym.BEGIN); }
    {END}           { return symbol(sym.END); }
    {INT}           { return symbol(sym.INT); }
    {BOOL}          { return symbol(sym.BOOL); }
    {BOOLEAN}       { return symbol(sym.BOOLEAN);}
    {ASSIGN}        { return symbol(sym.ASSIGN); }
    {PLUS}          { return symbol(sym.PLUS); }
    {MULTIPLY}      { return symbol(sym.MULTIPLY); }
    {LPAREN}        { return symbol(sym.LPAREN); }
    {RPAREN}        { return symbol(sym.RPAREN); }
    {ID}            { return symbol(sym.ID, yytext()); }
    {NUMBER}        { return symbol(sym.NUMBER, Integer.parseInt(yytext())); }

    .               { System.err.println("ERROR: Unrecognized token '" + yytext() + "' at line " + (yyline+1) + ", column " + (yycolumn+1)); }
}

// System.out.println("TOKEN: {IF}           ");
// System.out.println("TOKEN: {THEN}         ");
// System.out.println("TOKEN: {ELSE}         ");
// System.out.println("TOKEN: {DO}           ");
// System.out.println("TOKEN: {WHILE}        ");
// System.out.println("TOKEN: {FOR}          ");
// System.out.println("TOKEN: {PRINT}        ");
// System.out.println("TOKEN: {GT}           ");
// System.out.println("TOKEN: {GE}           ");
// System.out.println("TOKEN: {EQ}           ");
//     {System.out.println("TOKEN: {COMMENT_LINE
//      {System.out.println("TOKEN: {COMMENT_BLO
// System.out.println("TOKEN: {END_STATEMENT}");
// System.out.println("TOKEN: {LBRACE}       ");
// System.out.println("TOKEN: {RBRACE}       ");
// System.out.println("TOKEN: {BEGIN}        ");
// System.out.println("TOKEN: {END}          ");
// System.out.println("TOKEN: {INT}          ");
// System.out.println("TOKEN: {BOOL}         ");
// System.out.println("TOKEN: {BOOLEAN}      ");
// System.out.println("TOKEN: {ASSIGN}       ");
// System.out.println("TOKEN: {PLUS}         ");
// System.out.println("TOKEN: {MULTIPLY}     ");
// System.out.println("TOKEN: {LPAREN}       ");
// System.out.println("TOKEN: {RPAREN}       ");
// System.out.println("TOKEN: {ID}           ");
// System.out.println("TOKEN: {NUMBER}       ");