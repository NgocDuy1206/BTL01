package src.parser_top_down;

import src.Lexer_dfa.Token;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == Token.Type.EOF;
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean match(Token.Type... types) {
        for (Token.Type type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(Token.Type type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private void consume(Token.Type type, String message) {
        if (check(type)) {
            advance();
            return;
        }
        throw error(message);
    }

    private RuntimeException error(String message) {
        return new RuntimeException("[Parser Error] " + message + " at token: " + peek());
    }

    // Entry point
    // program -> "BEGIN" BLOCK "END"
    public void parseProgram() {
        consume(Token.Type.BEGIN, "Expect 'begin'");
        parseBlock();
        consume(Token.Type.END, "Expect 'end'");
        System.out.println("Parsing completed successfully.");
    }

    private void parseBlock() {
        if (match(Token.Type.LBRACE)) {  // nếu có dấu {
            while (!check(Token.Type.RBRACE) && !isAtEnd()) {
                parseStatement();
            }
            consume(Token.Type.RBRACE, "Expect '}' after block");
        } else {
            // không có dấu { } → parse các câu lệnh cho đến khi gặp 'end' hoặc '}'
            while (!check(Token.Type.END) && !check(Token.Type.RBRACE) && !isAtEnd()) {
                parseStatement();
            }
        }
    }


    private void parseStatement() {
        if (match(Token.Type.INT, Token.Type.BOOL)) {
            parseDeclaration();
        } else if (match(Token.Type.IDENTIFIER)) {
            parseAssignment();
            consume(Token.Type.END_STATEMENT, "Expect ';' after assignment");
        } else if (match(Token.Type.IF)) {
            parseIf();
        } else if (match(Token.Type.DO)) {
            parseDoWhile();
        } else if (match(Token.Type.FOR)) {
            parseFor();
        } else if (match(Token.Type.PRINT)) {
            parsePrint();
            consume(Token.Type.END_STATEMENT, "Expect ';' after print statement");
        } else {
            throw error("Unknown statement.");
        }
    }

    private void parseDeclaration() {
        Token.Type type = previous().type;
        consume(Token.Type.IDENTIFIER, "Expect variable name");
        if (match(Token.Type.ASSIGN)) {
            if (type == Token.Type.BOOL) {
                if (match(Token.Type.LPAREN)) {
                    parseCondition();
                    consume(Token.Type.RPAREN, "Expect ')' after '(' " );
                } else parseCondition();
            } else parseExpression();
        }
        consume(Token.Type.END_STATEMENT, "Expect ';' after declaration");
    }

    private void parseAssignment() {
        consume(Token.Type.ASSIGN, "Expect '=' in assignment");
        parseExpression();
    }

    private void parseIf() {
        consume(Token.Type.LPAREN, "Expect '(' after 'if'");
        parseCondition();
        consume(Token.Type.RPAREN, "Expect ')' after condition");
        consume(Token.Type.THEN, "Expect 'then' after condition");
        parseBlock();
        if (match(Token.Type.ELSE)) {
            parseBlock();
        }
    }

    private void parseDoWhile() {
        parseBlock();
        consume(Token.Type.WHILE, "Expect 'while' after 'do' block");
        consume(Token.Type.LPAREN, "Expect '(' after 'while'");
        parseCondition();
        consume(Token.Type.RPAREN, "Expect ')' after condition");
        consume(Token.Type.END_STATEMENT, "Expect ';' after do-while loop");
    }

    private void parseFor() {
        consume(Token.Type.LPAREN, "Expect '(' after 'for'");
        consume(Token.Type.IDENTIFIER, "Expect IDENTIFIER after '('");
        parseAssignment();
        consume(Token.Type.END_STATEMENT, "Expect ';' after initialization");
        parseCondition();
        consume(Token.Type.END_STATEMENT, "Expect ';' after condition");
        consume(Token.Type.IDENTIFIER, "Expect IDENTIFIER ");
        parseAssignment();
        consume(Token.Type.RPAREN, "Expect ')' after for loop");
        parseBlock();
    }

    private void parsePrint() {
        consume(Token.Type.LPAREN, "Expect '(' after 'print'");
        parseExpression();
        consume(Token.Type.RPAREN, "Expect ')' after expression");
    }

    private void parseCondition() {
        parseExpression();
        if (match(Token.Type.COMPARE)) {
            parseExpression();
        } else {
            throw error("Expect comparison operator in condition");
        }
    }

    private void parseExpression() {
        parseTerm();
        while (match(Token.Type.OPERATOR)) {
            parseTerm();
        }
    }

    private void parseTerm() {
        parseFactor();
        while (match(Token.Type.OPERATOR)) {
            parseFactor();
        }
    }

    private void parseFactor() {
        if (match(Token.Type.NUMBER, Token.Type.BOOL, Token.Type.IDENTIFIER)) {
            // do nothing - it's a leaf
        } else if (match(Token.Type.LPAREN)) {
            parseExpression();
            consume(Token.Type.RPAREN, "Expect ')' after expression");
        } else {
            throw error("Expect expression");
        }
    }
} // end of Parser class

class Node{
    public String value;
    public List<Node> child = new ArrayList<>();

    public void addNode(Node x) {
        child.add(x);
    }
    void print(String prefix) {
        System.out.println(prefix + value);
        for (Node x : child) {
            x.print(prefix + "  ");
        }
    }
}
