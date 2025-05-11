package src.parser_top_down;

import src.Lexer_dfa.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Node {
    public String name;
    public int line;
    public List<Node> child = new ArrayList<>();

    public Node(String name) {
        this.name = name;
    }
    public Node(String name, int line) {
        this.name = name;
        this.line = line;
    }

    public void addNode(Node x) {
        child.add(x);
    }

    public void addNode(String x) {
        child.add(new Node(x));
    }

    public void printNode(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + name);
        for (int i = 0; i < child.size(); i++) {
            boolean last = (i == child.size() - 1);
            child.get(i).printNode(prefix + (isTail ? "    " : "│   "), last);
        }
    }
}
