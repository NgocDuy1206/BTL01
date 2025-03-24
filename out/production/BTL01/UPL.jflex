/* Định nghĩa bộ phân tích từ vựng với JFlex */

// Khai báo đầu vào
%%

// Định nghĩa tùy chọn của JFlex
%public
%class Lexer
%unicode  // Hỗ trợ Unicode
%ignorecase // Không phân biệt hoa thường
%line
%column
%char
%function yylex
%type String  // Lexer trả về kiểu String để không cần CUP

// Bỏ qua khoảng trắng và xuống dòng
%{
    private String lastToken = ""; // Biến để kiểm tra khai báo biến hợp lệ
%}

// Định nghĩa từ khóa và token
BEGIN = "begin"
END = "end"
INT = "int"
BOOL = "bool"
ID = [a-zA-ZÀ-Ỹà-ỹ_] [a-zA-ZÀ-Ỹà-ỹ0-9_]*
NUMBER = [0-9]+
ASSIGN = "="
COMPARE = ">" | ">=" | "=="
PLUS = "+"
MULTIPLY = "*"
LPAREN = "("
RPAREN = ")"
IF = "if"
THEN = "then"
ELSE = "else"
DO = "do"
WHILE = "while"
FOR = "for"
PRINT = "print"
COMMENT = "//".* | "/\\*([^*]|\\*+[^*/])*\\*+/"
SYMBOL = ";" | "{" | "}" | "/"

%%

/* Định nghĩa hành động khi gặp token */
<YYINITIAL> {
    [ \t\r\n]+    { /* Bỏ qua khoảng trắng và xuống dòng */ }
    {BEGIN}      { return "BEGIN"; }
    {END}        { return "END"; }
    {INT}        { return "TYPE(INT)"; }
    {BOOL}       { return "TYPE(BOOL)"; }
    {ID}         { return "ID(" + yytext() + ")"; }
    {NUMBER}     { return "NUMBER(" + yytext() + ")"; }
    {ASSIGN}     { return "ASSIGN"; }
    {COMPARE}    { return "COMPARE(" + yytext() + ")"; }
    {PLUS}       { return "PLUS"; }
    {MULTIPLY}   { return "MULTIPLY"; }
    {LPAREN}     { return "LPAREN"; }
    {RPAREN}     { return "RPAREN"; }
    {IF}         { return "IF"; }
    {THEN}       { return "THEN"; }
    {ELSE}       { return "ELSE"; }
    {DO}         { return "DO"; }
    {WHILE}      { return "WHILE"; }
    {FOR}        { return "FOR"; }
    {PRINT}      { return "PRINT"; }
    {COMMENT}    { return "COMMENT"; }
    {SYMBOL}     { return "SYMBOL(" + yytext() + ")"; }
    .            { System.err.println("ERROR: Unrecognized token '" + yytext() + "' at line " + yyline + ", column " + yycolumn); }
}


