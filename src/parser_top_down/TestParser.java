package src.parser_top_down;

import src.Lexer_dfa.Token;

import java.util.*;

public class TestParser {
    public static void main(String[] args) {
        // Tạo danh sách token ví dụ: "begin print(3 + 5 * 2); end"
        List<Token> tokens = Test.test();

        // Tạo Parser và phân tích cú pháp
        Parser parser = new Parser(tokens);
        Node program = parser.parseProgram();
        parser.printErorr(program);
    }


}