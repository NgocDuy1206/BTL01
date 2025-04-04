package test;
import src.Lexer_dfa.Token;
import src.Lexer_dfa.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;


public class Test {
    public static void main(String srgc[]) {
        try {
            FileReader f = new FileReader("input.txt");
            BufferedReader bf = new BufferedReader(f);
            ArrayList<Token> tokens = new ArrayList<>();
            String line;
            while ((line = bf.readLine()) != null) {
                LexerDFA.tokenize(line, tokens);
            }
            FileWriter fw = new FileWriter("output_dfa.txt");
            BufferedWriter bfw = new BufferedWriter(fw);
            for (Token x : tokens) {
                bfw.write(x.toString());
                bfw.newLine();
            }
            bf.close();
            bfw.close();

        } catch (Exception e) {

        }

    }



    private static void test_regex() {

    }

    private static void test_dfa() {
    }

    private static void test_jflex() {
    }
}