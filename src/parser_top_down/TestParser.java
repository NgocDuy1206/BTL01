package src.parser_top_down;

import src.Lexer_dfa.Token;
import src.three_address_code.TAC;

import java.util.*;

public class TestParser {
    public static void main(String[] args) throws Exception {
        // Tạo danh sách token ví dụ: "begin print(3 + 5 * 2); end"
        List<Token> tokens = Test.test();

        // Tạo Parser và phân tích cú pháp
        Parser parser = new Parser(tokens);
        ParserTree tree = new ParserTree(parser.parseProgram());
        parser.printErorr(tree.root);
        ManageSymbol manage = new ManageSymbol();
        tree.checkSematic(tree.root, manage);
        if (manage.listErr.size() != 0) {
            manage.printErr();
        } else {
            TAC x = new TAC();
            x.generator(tree.root);
            x.print();
        }
    }
}