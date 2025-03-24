import java.io.*;
import java.util.*;

public class Main {
    public static  List<Token> tokens = new ArrayList<Token>();
    public static void main(String[] args) {
        try {
            readFile("input.txt");

            // Test lexer without JFlex

            writeFile("output_without_jflex.txt", tokens);

            System.out.println("Lexical analysis completed. Check output_without_jflex.txt.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        testJflex();
    }

    private static String readFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) {
            MyLexer.tokenize(line, tokens);
        }
        reader.close();
        return content.toString();
    }

    private static void writeFile(String filename, List<?> tokens) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (Object token : tokens) {
            writer.write(token.toString());
            writer.newLine();
        }
        writer.close();
    }
    private static void testJflex() {
        try {
            // Đọc từ file input.txt
            Reader reader = new FileReader("input.txt");
            Lexer lexer = new Lexer(reader);
            BufferedWriter writer = new BufferedWriter(new FileWriter("output_with_jflex.txt"));

            String token;
            while ((token = lexer.yylex()) != null) {
                writer.write("TOKEN <" + token + ">" + "\n");
            }

            // Đóng file
            writer.close();
            reader.close();

            System.out.println("Token list has been written to output_with_jflex.txt");
        } catch (IOException e) {
            System.err.println("Error reading/writing file: " + e.getMessage());
        }
    }
}
