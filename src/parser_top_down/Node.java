package src.parser_top_down;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public String value;
    public List<Node> child = new ArrayList<>();


    public Node(String value) {
        this.value = value;
    }

    public void addNode(Node x) {
        child.add(x);
    }

    public void addNode(String x) {
        child.add(new Node(x));
    }

    public void print(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + value);
        for (int i = 0; i < child.size(); i++) {
            boolean last = (i == child.size() - 1);
            child.get(i).print(prefix + (isTail ? "    " : "│   "), last);
        }
    }
}
