package src.parser_bottom_up;

import java.util.List;

public class Rule {
    String lhs;
    List<String> rhs;

    public Rule(String lhs, List<String> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public String toString() {
        return lhs + " → " + String.join(" ", rhs);
    }
}