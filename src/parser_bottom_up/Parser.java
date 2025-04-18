package src.parser_bottom_up;

import src.Lexer_dfa.Token;

import java.util.*;

public class Parser {
    private Stack<Integer> stateStack = new Stack<>();
    private Stack<String> symbolStack = new Stack<>();

    private Map<Pair<Integer, String>, String> actionTable = new HashMap<>();
    private Map<Pair<Integer, String>, Integer> gotoTable = new HashMap<>();

    private List<Rule> rules = new ArrayList<>();
    private List<Token> tokens;
    private int pos = 0;
    public Parser(List<Token> input) {
        this.tokens = input;
        createTable();
        createRule();
    }

    private void createTable() {

        // bảng action
        actionTable.put(new Pair<>(0, "id"), "s5");
        actionTable.put(new Pair<>(0, "("), "s4");

        actionTable.put(new Pair<>(1, "+"), "s6");
        actionTable.put(new Pair<>(1, "$"), "acc");

        actionTable.put(new Pair<>(2, "+"), "r2");
        actionTable.put(new Pair<>(2, "*"), "s7");
        actionTable.put(new Pair<>(2, ")"), "r2");
        actionTable.put(new Pair<>(2, "$"), "r2");

        actionTable.put(new Pair<>(3, "+"), "r4");
        actionTable.put(new Pair<>(3, "*"), "r4");
        actionTable.put(new Pair<>(3, ")"), "r4");
        actionTable.put(new Pair<>(3, "$"), "r4");

        actionTable.put(new Pair<>(4, "id"), "s5");
        actionTable.put(new Pair<>(4, "("), "s4");

        actionTable.put(new Pair<>(5, "+"), "r6");
        actionTable.put(new Pair<>(5, "*"), "r6");
        actionTable.put(new Pair<>(5, ")"), "r6");
        actionTable.put(new Pair<>(5, "$"), "r6");

        actionTable.put(new Pair<>(6, "id"), "s5");
        actionTable.put(new Pair<>(6, "("), "s4");

        actionTable.put(new Pair<>(7, "id"), "s5");
        actionTable.put(new Pair<>(7, "("), "s4");

        actionTable.put(new Pair<>(8, "+"), "s6");
        actionTable.put(new Pair<>(8, ")"), "s11");


        actionTable.put(new Pair<>(9, "+"), "r1");
        actionTable.put(new Pair<>(9, "*"), "s7");
        actionTable.put(new Pair<>(9, ")"), "r1");
        actionTable.put(new Pair<>(9, "$"), "r1");

        actionTable.put(new Pair<>(10, "+"), "r3");
        actionTable.put(new Pair<>(10, "*"), "r3");
        actionTable.put(new Pair<>(10, ")"), "r3");
        actionTable.put(new Pair<>(10, "$"), "r3");

        actionTable.put(new Pair<>(11, "+"), "r5");
        actionTable.put(new Pair<>(11, "*"), "r5");
        actionTable.put(new Pair<>(11, ")"), "r5");
        actionTable.put(new Pair<>(11, "$"), "r5");

        // bảng goto

        gotoTable.put(new Pair<>(0, "E"), 1);
        gotoTable.put(new Pair<>(0, "T"), 2);
        gotoTable.put(new Pair<>(0, "F"), 3);

        gotoTable.put(new Pair<>(4, "E"), 8);
        gotoTable.put(new Pair<>(4, "T"), 2);
        gotoTable.put(new Pair<>(4, "F"), 3);


        gotoTable.put(new Pair<>(6, "T"), 9);
        gotoTable.put(new Pair<>(6, "F"), 3);

        gotoTable.put(new Pair<>(7, "F"), 10);



    }
    private void createRule() {
        rules.add(new Rule("E", Arrays.asList("E", "+", "T")));
        rules.add(new Rule("E", Arrays.asList("T")));
        rules.add(new Rule("T", Arrays.asList("T", "*", "F")));
        rules.add(new Rule("T", Arrays.asList("F")));
        rules.add(new Rule("F", Arrays.asList("(", "E", ")")));
        rules.add(new Rule("F", Arrays.asList("id")));
    }


    public void parse() throws Exception {
        stateStack.push(0); // bắt đầu từ state 0
        int index = 0;
        System.out.printf("%-30s", "Stack");
        System.out.printf("%-30s", "Symbol");
        System.out.printf("%-30s", "Input");
        System.out.printf("%-30s", "Action");
        System.out.println();
        while (true) {
            printLineTable(index);
            int currentState = stateStack.peek();
            String currentToken = getTerminal(tokens.get(index));

            String action = actionTable.get(new Pair<>(currentState, currentToken));

            if (action == null) {
                System.out.println("Lỗi cú pháp tại token: " + currentToken +" " + tokens.get(index).value);
                break;
            }

            if (action.startsWith("s")) { // Shift
                int nextState = Integer.parseInt(action.substring(1));
                System.out.printf("%-30s", "shift " + nextState);
                symbolStack.push(currentToken);
                stateStack.push(nextState);
                index++;
            } else if (action.startsWith("r")) { // Reduce
                int ruleNum = Integer.parseInt(action.substring(1)) - 1;
                Rule rule = rules.get(ruleNum); // A → β

                for (int i = 0; i < rule.rhs.size(); i++) {
                    symbolStack.pop();
                    stateStack.pop();
                }

                symbolStack.push(rule.lhs);
                int gotoState = gotoTable.get(new Pair<>(stateStack.peek(), rule.lhs));
                stateStack.push(gotoState);

                System.out.printf("Reduce %-30s", rule);
            } else if (action.equals("acc")) {
                System.out.println("Phân tích thành công!");
                break;
            }
            System.out.println();
        }
    }

    private String getTerminal(Token x) throws Exception {
        if (x.type == Token.Type.IDENTIFIER) {
            return "id";
        } else if (x.type == Token.Type.LPAREN) {
            return "(";
        } else if (x.type == Token.Type.RPAREN) {
            return ")";
        } else if (x.type == Token.Type.END_OF_FILE) {
            return "$";
        } else if (x.type == Token.Type.OPERATOR) {
            if (x.value.equals("+")) {
                return "+";
            } else return "*";
        } throw new Exception("Lỗi token không hợp lệ");
    }

    private void printLineTable(int pos) {
        String s = "";
        for(Integer i : stateStack) {
            s += Integer.toString(i);
        }
       System.out.printf("%-30s", s);
        s = "";
        for(String i : symbolStack) {
            s += i;
        }
        System.out.printf("%-30s", s);
        s = "";
        for (int j = pos; j < tokens.size(); j++) {
            if (tokens.get(j).type == Token.Type.IDENTIFIER) {
                s += "id"; continue;
            } else if (tokens.get(j).type == Token.Type.END_OF_FILE) {
                s += "$"; continue;
            }
                s += tokens.get(j).value;
        }
        System.out.printf("%-30s", s);
    }
}

