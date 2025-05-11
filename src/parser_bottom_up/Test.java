package src.parser_bottom_up;

import src.Lexer_dfa.LexerDFA;
import src.Lexer_dfa.Token;
import src.parser_top_down.Symbol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String args[]) {
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

            Parser p = new Parser(tokens);
            p.parse();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
//a + b * (c + a) + d * f