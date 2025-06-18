package src.three_address_code;

import src.parser_top_down.Node;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TAC {
    List<String> code;
    int tmp;
    int label;

    public TAC() {
        tmp = 0;
        label = 0;
        code = new ArrayList<>();
    }


    public String newTemp() {
        return "t" + tmp++;
    }

    public String newLabel() {
        return "L" + label++;
    }
    public void generator(Node node) {
        String name = node.name;
        switch (name) {
            case "program": {
                generator(node.child.get(1));
                break;
            }
            case "Body", "statement": {
                generator(node.child.get(0));
                break;
            }
            case "StatementList": {
                for (int i = 0; i < node.child.size(); i++) {
                    generator(node.child.get(i));
                }
                break;
            }
            case "declaration_statement": {
                code.add(node.child.get(1).child.get(0).name + " = " + evaluateExpression(node.child.get(3)));
                break;
            }
            case "assign_statement": {
                code.add(node.child.get(0).child.get(0).name + " = " + evaluateExpression(node.child.get(2)));
                break;
            }
            case "if_statement": {
                int size = node.child.size();
                if (size < 7) {
                    String condition = checkCondition(node.child.get(2));
                    String label = newLabel();
                    code.add("ifFalse " + condition + " goto " + label);
                    generator(node.child.get(5));
                    code.add(label + " :");
                } else {
                    String condition = checkCondition(node.child.get(2));
                    String label = newLabel();
                    code.add("ifFalse " + condition + " goto " + label);
                    generator(node.child.get(5));
                    String labelExit = newLabel();
                    code.add("goto " + labelExit);
                    code.add(label + " :");
                    generator(node.child.get(size - 1));
                    code.add(labelExit + " :");
                }


                break;
            }
            case "block": {
                for (int i = 1 ; i < node.child.size() - 1; i++) {
                    generator(node.child.get(i));
                }
                break;
            }
            case "for_statement": {
                // khoi tao
                generator(node.child.get(2));
                String labelLoop = newLabel();
                String labelExit = newLabel();
                code.add(labelLoop + " :");
                String condition = checkCondition(node.child.get(3));
                code.add("ifFalse " + condition + " goto" + labelExit);
                generator(node.child.get(7));
                generator(node.child.get(5));
                code.add("goto " + labelLoop);
                code.add(labelExit + " :");
                break;
            }
            case "DoWhile_Statement": {
                String label = newLabel();
                code.add(label + " :");
                generator(node.child.get(2));

                String condition = checkCondition(node.child.get(6));
                String labelExit = newLabel();
                code.add("ifFalse " + condition + " goto " + labelExit);
                code.add("goto " + label);
                code.add(labelExit + " :");
                break;
            }
            case "condition": {
                checkCondition(node);
                break;
            }
            case "print_statement": {
                String exp = evaluateExpression(node.child.get(2));
                code.add("PRINT " + exp);
                break;
            }
        }
    }

    public void print() throws Exception{
        try {
            PrintWriter out = new PrintWriter("output.txt");
            out.println("THREE ADDRESS CODE: ");
            for (int i = 0; i < code.size(); i++) {
                out.print(code.get(i));
                out.println();
            }

            out.close(); // quan trọng!
            System.out.println("In ra file thành công. ");
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }
    public String checkCondition(Node node) {
        String exp1 = evaluateExpression(node.child.get(0));
        String exp2 = evaluateExpression(node.child.get(2));
        String op = node.child.get(1).child.get(0).name;
        String temp = newTemp();
        code.add(temp + " = " + exp1 + " " + op + " " + exp2);
        return temp;
    }
    private String evaluateExpression(Node node) {
        switch (node.name) {
            case "expression":
                if (node.child.size() == 1) {
                    return evaluateExpression(node.child.get(0)); // term
                }
                String left = evaluateExpression(node.child.get(0)); // term đầu
                for (int i = 1; i < node.child.size(); i += 2) {
                    String op = node.child.get(i).child.get(0).name;
                    String right = evaluateExpression(node.child.get(i + 1));
                    String temp = newTemp();
                    code.add(temp + " = " + left + " " + op + " " + right);
                    left = temp;
                }
                return left;

            case "term":
                String tleft = evaluateExpression(node.child.get(0)); // factor đầu
                for (int i = 1; i < node.child.size(); i += 2) {
                    String op = node.child.get(i).child.get(0).name;
                    String right = evaluateExpression(node.child.get(i + 1));
                    String temp = newTemp();
                    code.add(temp + " = " + tleft + " " + op + " " + right);
                    tleft = temp;
                }
                return tleft;

            case "factor":
                if (node.child.size() == 1) return evaluateExpression(node.child.get(0));
                return evaluateExpression(node.child.get(1));

            case "IDENTIFIER", "NUMBER": return node.child.get(0).name;

            default:
                return evaluateExpression(node.child.get(0));
        }
    }

}
