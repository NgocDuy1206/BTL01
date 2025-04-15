%{
import java_cup.runtime.Symbol;
%}
%%
%class Lexer
%unicode
%cup
%line
%column

%%

" " | \t | \r | \n     { /* skip whitespace */ }

"+"                    { return new Symbol(sym.PLUS); }
"-"                    { return new Symbol(sym.MINUS); }

[0-9]+                 { return new Symbol(sym.NUMBER, new Integer(yytext())); }

.                      { throw new Error("Illegal character: " + yytext()); }
