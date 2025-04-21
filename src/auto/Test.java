package src.auto;

import java.io.*;
import java_cup.runtime.*;
public class Test {
    public static void main(String[] args) throws Exception {
        Lexer lexer = new Lexer(new FileReader("input.txt"));
        parser p = new parser(lexer);
        try {
            p.parse();
        } catch (Exception e) {
            System.out.println("Parser gặp lỗi và đã thoát.");
        }

        if (!p.errorList.isEmpty()) {
            System.out.println("\n--- Danh sách lỗi ---");
            for (String error : p.errorList) {
                System.out.println(error);
            }
        } else {
            System.out.println("Phân tích thành công không có lỗi cú pháp.");
        }



    }
}

