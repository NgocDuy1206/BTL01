package src.tree_parser;

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
}
