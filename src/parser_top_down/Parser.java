package src.parser_top_down;

import src.Lexer_dfa.Token;

import javax.swing.tree.TreeNode;
import java.util.*;

public class Parser {
    private List<Token> tokens;
    private int current = 0;

    private ManageSymbol manageSymbol = new ManageSymbol();
    
    private List<String> erorrList = new ArrayList<>();


    private Set<Token.Type> startStatement = Set.of(
            Token.Type.IF,
            Token.Type.FOR,
            Token.Type.DO,
            Token.Type.PRINT,
            Token.Type.DATA_TYPE,
            Token.Type.WHILE,
            Token.Type.IDENTIFIER
    );

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }


    private Token peek() {
        if (current >=0 && current < tokens.size())
        return tokens.get(current);
        return tokens.get(tokens.size() - 1);
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
                return true;
            }
        }
        return false;
    }

    private boolean check(Token.Type type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }
    private void consume(Node parent, Token.Type type, String message) {
        if (check(type)) {
            Node x = new Node(type.name(), peek().line);
            x.addNode(peek().value);
            parent.addNode(x);
            advance();
            return;
        } else {
            error(message);
            int t = current;
            while (t < current + 5) {
                t++;
                if (t >= tokens.size()) break;
                if (tokens.get(t).type == type) {
                    current = t;
                    advance();
                    return;
                }
            }
        }
    }

    private void error(String message) {
        erorrList.add("[Parser Error]: " + message + " after token: " + previous());
    }

    // Entry point
    // program -> "BEGIN" BLOCK "END"
    public Node parseProgram() {
        Node program = new Node("program");

        consume(program, Token.Type.BEGIN, "Expect 'begin'");


        program.addNode(parseBody());


        consume(program, Token.Type.END, "Expect 'end'");
        return program;
    }

    private Node parseBody() {

        Node body = new Node("Body");
        if (match(Token.Type.LBRACE)) {


            consume(body, Token.Type.LBRACE, "Expect '}' ");
            body.addNode(parseStatementList());
            consume(body, Token.Type.RBRACE, "Expect '}' ");


        } else {
            body.addNode(parseStatementList());
        }

        return body;
    }
    private Node parseStatementList() {
        Node statementList = new Node("StatementList");
        while (startStatement.contains(peek().type)) {
            statementList.addNode(parseStatement());
//            while (!startStatement.contains(peek().type) && peek().type != Token.Type.END) {
//                advance();
//            }
        }

        return statementList;

    }
    private Node parseBlock() {

        Node block = new Node("block");
        if (match(Token.Type.LBRACE)) {  // nếu có dấu {

            consume(block, Token.Type.LBRACE, "Expect '}' after block");
            while (!check(Token.Type.RBRACE) && !isAtEnd()) {
                block.addNode(parseStatement());
            }
            consume(block, Token.Type.RBRACE, "Expect '}' after block");

        } else {
                block.addNode(parseStatement());
        }

        return block;
    }


    private Node parseStatement() {
        Node statement = new Node("statement");
        if (match(Token.Type.DATA_TYPE)) {
            statement.addNode(parseDeclaration());
        } else if (match(Token.Type.IDENTIFIER)) {
            statement.addNode(parseAssignment(true));
        } else if (match(Token.Type.IF)) {
            statement.addNode(parseIf());
        } else if (match(Token.Type.DO)) {
            statement.addNode(parseDoWhile());
        } else if (match(Token.Type.FOR)) {
            statement.addNode(parseFor());
        } else if (match(Token.Type.PRINT)) {
            statement.addNode(parsePrint());
        } else {
             error("Unknown statement.");
        }
        return statement;
    }

    private Node parseDeclaration() {
        Node declaration = new Node("declaration_statement");
        Token x = peek();

        Token.Type type = x.type;
        consume(declaration, Token.Type.DATA_TYPE, "Expect type of declaration");

        String name = peek().value;
        consume(declaration, Token.Type.IDENTIFIER, "Expect variable name");


        if (match(Token.Type.ASSIGN)) {
            consume(declaration, Token.Type.ASSIGN, "Expect '=' ");
            if (x.value.equals("bool")) {

                if (match(Token.Type.LPAREN)) {

                    consume(declaration, Token.Type.LPAREN, "Expect ')' " );
                    declaration.addNode(parseCondition());
                    consume(declaration, Token.Type.RPAREN, "Expect ')' " );

                } else if (match(Token.Type.BOOLEAN)) {
                    consume(declaration, Token.Type.BOOLEAN, "");
                } else declaration.addNode(parseCondition());
            } else declaration.addNode(parseExpression());
        }
        consume(declaration, Token.Type.END_STATEMENT, "Expect ';' after declaration");

        return declaration;
    }

    private Node parseAssignment(boolean checkEnd) {
        Node assign = new Node("assign_statement");

        consume(assign, Token.Type.IDENTIFIER, "Expect 'id' ");
        consume(assign, Token.Type.ASSIGN, "Expect '=' ");

        assign.addNode(parseExpression());
        if (checkEnd) consume(assign, Token.Type.END_STATEMENT, "Expect ';' ");
        return assign;

    }

    private Node parseIf() {
        Node ifState = new Node("if_statement");
        consume(ifState, Token.Type.IF, "Expect 'if' ");
        consume(ifState, Token.Type.LPAREN, "Expect '(' ");

        ifState.addNode(parseCondition());

        consume(ifState, Token.Type.RPAREN, "Expect ')' ");

        consume(ifState, Token.Type.THEN, "Expect 'then' ");

        ifState.addNode(parseBlock());
        if (match(Token.Type.ELSE)) {

            consume(ifState, Token.Type.ELSE, "Expect 'else' ");
            ifState.addNode(parseBlock());
        }
        return ifState;
    }

    private Node parseDoWhile() {
        Node dowhile = new Node("DoWhile_Statement");

        consume(dowhile, Token.Type.DO, "Expect 'do' ");
        consume(dowhile, Token.Type.LBRACE, "Expect '{' ");

        dowhile.addNode(parseStatementList());
        consume(dowhile, Token.Type.RBRACE, "Expect '}' ");

        consume(dowhile, Token.Type.WHILE, "Expect 'while' after 'do' block");

        consume(dowhile, Token.Type.LPAREN, "Expect '(' after 'while'");

        dowhile.addNode(parseCondition());
        consume(dowhile, Token.Type.RPAREN, "Expect ')' after condition");

        consume(dowhile, Token.Type.END_STATEMENT, "Expect ';' after do-while loop");

        return dowhile;
    }

    private Node parseFor() {

        Node for_statement = new Node("for_statement");

        consume(for_statement, Token.Type.FOR, "Expect 'for' ");

        consume(for_statement, Token.Type.LPAREN, "Expect '(' ");

        for_statement.addNode(parseAssignment(true));


        for_statement.addNode(parseCondition());

        consume(for_statement, Token.Type.END_STATEMENT, "Expect ';'");


        for_statement.addNode(parseAssignment(false));

        consume(for_statement, Token.Type.RPAREN, "Expect ')' ");

        for_statement.addNode(parseBlock());
        return for_statement;
    }

    private Node parsePrint() {
        Node print = new Node("print_statement");
        consume(print, Token.Type.PRINT, "Expect 'print' ");

        consume(print, Token.Type.LPAREN, "Expect '(' ");


        print.addNode(parseExpression());
        consume(print, Token.Type.RPAREN, "Expect ')' ");
        consume(print, Token.Type.END_STATEMENT, "Expect ';' ");
        return print;
    }

    private Node parseCondition() {
        Node condition = new Node("condition");
        condition.addNode(parseExpression());

        if (match(Token.Type.COMPARE)) {
            consume(condition, peek().type, "Expect compare operation");
            condition.addNode(parseExpression());
        } else {
             error("Expect comparison operator in condition");
        }
        return condition;
    }

    private Node parseExpression() {

        Node express = new Node("expression");
        express.addNode(parseTerm());

        while (match(Token.Type.OPERATOR) && peek().value.equals("+")) {

            consume(express, peek().type, "Expect operation ");
            express.addNode(parseTerm());

        }

        return express;
    }

    private Node parseTerm() {

        Node term = new Node("term");
        term.addNode(parseFactor());

        while (match(Token.Type.OPERATOR) && peek().value.equals("*")) {

            consume(term, peek().type, "Expect operation ");
            term.addNode(parseFactor());

        }

        return term;
    }

    private Node parseFactor() {

        Node factor = new Node("factor");

        if (match(Token.Type.NUMBER, Token.Type.BOOLEAN, Token.Type.IDENTIFIER)) {
            // do nothing - it's a leaf


            consume(factor, peek().type, "");

        } else if (match(Token.Type.LPAREN)) {

            consume(factor, Token.Type.LPAREN, "");
            factor.addNode(parseExpression());

            consume(factor, Token.Type.RPAREN, "Expect ')' after expression");
        } else {
             error("Expect expression");
        }
        return factor;
    }

    public void printErorr(Node root) {

        if (erorrList.size() == 0) {

            System.out.println("không có lỗi cú pháp");
            System.out.println("Cây cú pháp: ");
            root.printNode("", true);
            return;

        }

        System.out.println("ERORR PARSER LIST:");

        for (String i : erorrList) {

            System.out.println(i);

        }
    }
}

