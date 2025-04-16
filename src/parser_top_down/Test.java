package src.parser_top_down;
import src.Lexer_dfa.Token;
import src.Lexer_dfa.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


public class Test {
    public static List<Token> test() {
        try {
            FileReader f = new FileReader("input.txt");
            BufferedReader bf = new BufferedReader(f);
            ArrayList<Token> tokens = new ArrayList<>();
            String line;
            int n = 0;
            while ((line = bf.readLine()) != null) {
                LexerDFA.tokenize(line, tokens);
                n++;
            }
            tokens.add(new Token(n, Token.Type.END_OF_FILE, null));
            FileWriter fw = new FileWriter("output_dfa.txt");
            BufferedWriter bfw = new BufferedWriter(fw);
            for (Token x : tokens) {
                bfw.write(x.toString());
                bfw.newLine();
            }

            bf.close();
            bfw.close();
            return tokens;

        } catch (Exception e) {

        }

        return null;
    }



    private static void test_regex() {

    }

    private static void test_dfa() {
    }

    private static void test_jflex() {
    }
}