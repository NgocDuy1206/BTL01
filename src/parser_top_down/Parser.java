package src.parser_top_down;

import src.Lexer_dfa.Token;

import javax.swing.tree.TreeNode;
import java.util.*;

public class Parser {
    private List<Token> tokens;
    private int current = 0;
    
    private List<String> erorrList = new ArrayList<>();
    private Set<Token.Type> syncTokens = Set.of(
            Token.Type.END_STATEMENT,
            Token.Type.RBRACE,
            Token.Type.IF,
            Token.Type.FOR,
            Token.Type.DO,
            Token.Type.PRINT,
            Token.Type.INT,
            Token.Type.BOOL,
            Token.Type.WHILE
    );

    private Set<Token.Type> startStatement = Set.of(
            Token.Type.IF,
            Token.Type.FOR,
            Token.Type.DO,
            Token.Type.PRINT,
            Token.Type.INT,
            Token.Type.BOOL,
            Token.Type.WHILE
    );

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
        } else {
            error(message);
            while (!checkSyncToken(peek().type)) current++;
        }
    }


    private boolean checkSyncToken(Token.Type x) {
        if (syncTokens.contains(x)) return true;
        return false;
    }

    private void error(String message) {
        erorrList.add("[Parser Error]: " + message + " after token: " + previous());
    }

    // Entry point
    // program -> "BEGIN" BLOCK "END"
    public Node parseProgram() {
        Node program = new Node("program");

        consume(Token.Type.BEGIN, "Expect 'begin'");
        program.addNode(new Node("begin"));

        program.addNode(parseBody());


        consume(Token.Type.END, "Expect 'end'");
        program.addNode(new Node("end"));
        return program;
    }

    private Node parseBody() {
        Node body = new Node("Body");
        if (match(Token.Type.LBRACE)) {
            body.addNode(parseStatementList());
            consume(Token.Type.RBRACE, "Expect '}' ");
        } else {
            body.addNode(parseStatementList());
        }
        return body;
    }
    private Node parseStatementList() {
        Node statementList = new Node("StatementList");
        while (startStatement.contains(peek().type)) {
            statementList.addNode(parseStatement());
        }

        return statementList;

    }
    private Node parseBlock() {
        Node block = new Node("block");
        if (match(Token.Type.LBRACE)) {  // nếu có dấu {
            block.addNode(new Node("{"));
            while (!check(Token.Type.RBRACE) && !isAtEnd()) {
                block.addNode(parseStatement());
            }
            consume(Token.Type.RBRACE, "Expect '}' after block");
            block.addNode(new Node("}"));
        } else {
            // không có dấu { } → parse các câu lệnh cho đến khi gặp 'end' hoặc '}'
            while (!check(Token.Type.END_STATEMENT) && !check(Token.Type.RBRACE) && !isAtEnd()) {
                block.addNode(parseStatement());
            }
        }
        return block;
    }


    private Node parseStatement() {
        Node statement = new Node("statement");
        if (match(Token.Type.INT, Token.Type.BOOL)) {
            statement.addNode(parseDeclaration());
        } else if (match(Token.Type.IDENTIFIER)) {
            statement.addNode(parseAssignment());
            consume(Token.Type.END_STATEMENT, "Expect ';' after assignment");
            statement.addNode(";");
        } else if (match(Token.Type.IF)) {
            statement.addNode(parseIf());
        } else if (match(Token.Type.DO)) {
            statement.addNode(parseDoWhile());
        } else if (match(Token.Type.FOR)) {
            statement.addNode(parseFor());
        } else if (match(Token.Type.PRINT)) {
            statement.addNode(parsePrint());
            consume(Token.Type.END_STATEMENT, "Expect ';' after print statement");
            statement.addNode(";");
        } else {
             error("Unknown statement.");
        }
        return statement;
    }

    private Node parseDeclaration() {
        Node declaration = new Node("declaration_statement");

        Token.Type type = previous().type;
        declaration.addNode(previous().value);

        consume(Token.Type.IDENTIFIER, "Expect variable name");
        declaration.addNode("id");


        if (match(Token.Type.ASSIGN)) {
            declaration.addNode("=");
            if (type == Token.Type.BOOL) {
                if (match(Token.Type.LPAREN)) {
                    declaration.addNode("(");
                    declaration.addNode(parseCondition());
                    consume(Token.Type.RPAREN, "Expect ')' after '(' " );
                    declaration.addNode(")");
                } else declaration.addNode(parseCondition());
            } else declaration.addNode(parseExpression());
        }
        consume(Token.Type.END_STATEMENT, "Expect ';' after declaration");
        declaration.addNode(";");
        return declaration;
    }

    private Node parseAssignment() {
        Node assign = new Node("assign");
        consume(Token.Type.ASSIGN, "Expect '=' in assignment");
        assign.addNode("=");
        assign.addNode(parseExpression());
        return assign;

    }

    private Node parseIf() {
        Node ifState = new Node("if_statement");
        ifState.addNode("if");
        consume(Token.Type.LPAREN, "Expect '(' after 'if'");
        ifState.addNode("(");
        ifState.addNode(parseCondition());
        consume(Token.Type.RPAREN, "Expect ')' after condition");
        ifState.addNode(")");
        consume(Token.Type.THEN, "Expect 'then' after condition");
        ifState.addNode("then");
        ifState.addNode(parseBlock());
        if (match(Token.Type.ELSE)) {
            ifState.addNode("else");
            ifState.addNode(parseBlock());
        }
        return ifState;
    }

    private Node parseDoWhile() {
        Node dowhile = new Node("DoWhile_Statement");
        dowhile.addNode("do");
        consume(Token.Type.LBRACE, "Expect '{' ");
        dowhile.addNode("{");
        dowhile.addNode(parseStatementList());
        consume(Token.Type.RBRACE, "Expect '}' ");
        dowhile.addNode("}");
        consume(Token.Type.WHILE, "Expect 'while' after 'do' block");
        dowhile.addNode("while");
        consume(Token.Type.LPAREN, "Expect '(' after 'while'");
        dowhile.addNode("(");
        dowhile.addNode(parseCondition());
        consume(Token.Type.RPAREN, "Expect ')' after condition");
        dowhile.addNode(")");
        consume(Token.Type.END_STATEMENT, "Expect ';' after do-while loop");
        dowhile.addNode(";");
        return dowhile;
    }

    private Node parseFor() {

        Node for_statement = new Node("for_statement");
        for_statement.addNode("for");

        consume(Token.Type.LPAREN, "Expect '(' after 'for'");
        for_statement.addNode("(");

        consume(Token.Type.IDENTIFIER, "Expect IDENTIFIER after '('");
        for_statement.addNode("id");
        for_statement.addNode(parseAssignment());

        consume(Token.Type.END_STATEMENT, "Expect ';' after initialization");
        for_statement.addNode(";");
        for_statement.addNode(parseCondition());

        consume(Token.Type.END_STATEMENT, "Expect ';' after condition");
        for_statement.addNode(";");

        consume(Token.Type.IDENTIFIER, "Expect IDENTIFIER ");
        for_statement.addNode("id");
        for_statement.addNode(parseAssignment());

        consume(Token.Type.RPAREN, "Expect ')' after for loop");
        for_statement.addNode(")");
        for_statement.addNode(parseBlock());
        return for_statement;
    }

    private Node parsePrint() {
        Node print = new Node("print_statement");
        print.addNode("print");

        consume(Token.Type.LPAREN, "Expect '(' after 'print'");
        print.addNode("(");

        print.addNode(parseExpression());
        consume(Token.Type.RPAREN, "Expect ')' after expression");
        print.addNode(")");
        return print;
    }

    private Node parseCondition() {
        Node condition = new Node("condition");
        condition.addNode(parseExpression());

        if (match(Token.Type.COMPARE)) {
            condition.addNode(previous().value);
            condition.addNode(parseExpression());
        } else {
             error("Expect comparison operator in condition");
        }
        return condition;
    }

    private Node parseExpression() {
        Node express = new Node("expression");
        express.addNode(parseTerm());
        while (match(Token.Type.OPERATOR)) {
            express.addNode(previous().value);
            express.addNode(parseTerm());
        }
        return express;
    }

    private Node parseTerm() {

        Node term = new Node("term");
        term.addNode(parseFactor());
        while (match(Token.Type.OPERATOR)) {
            term.addNode(previous().value);
            term.addNode(parseFactor());
        }
        return term;
    }

    private Node parseFactor() {

        Node factor = new Node("factor");

        if (match(Token.Type.NUMBER, Token.Type.BOOL, Token.Type.IDENTIFIER)) {
            // do nothing - it's a leaf
            factor.addNode(previous().value);
        } else if (match(Token.Type.LPAREN)) {
            factor.addNode("(");
            factor.addNode(parseExpression());
            factor.addNode(")");
            consume(Token.Type.RPAREN, "Expect ')' after expression");
        } else {
             error("Expect expression");
        }
        return factor;
    }

    public void printErorr() {
        if (erorrList.size() == 0) {
            System.out.println("không có lỗi cú pháp");
            return;
        }
        System.out.println("ERORR PARSER LIST:");
        for (String i : erorrList) {
            System.out.println(i);
        }
    }
} // end of Parser class

