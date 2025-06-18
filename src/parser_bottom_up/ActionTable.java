package src.parser_bottom_up;

import java.util.*;

class LR0Item {
    String lhs;
    List<String> rhs;
    int dotPos;

    LR0Item(String lhs, List<String> rhs, int dotPos) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.dotPos = dotPos;
    }

    boolean isComplete() {
        return dotPos >= rhs.size();
    }

    String getNextSymbol() {
        return isComplete() ? null : rhs.get(dotPos);
    }

    LR0Item advanceDot() {
        return new LR0Item(lhs, rhs, dotPos + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LR0Item)) return false;
        LR0Item other = (LR0Item) o;
        return lhs.equals(other.lhs) && rhs.equals(other.rhs) && dotPos == other.dotPos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lhs, rhs, dotPos);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(lhs + " → ");
        for (int i = 0; i < rhs.size(); i++) {
            if (i == dotPos) sb.append("• ");
            sb.append(rhs.get(i)).append(" ");
        }
        if (dotPos == rhs.size()) sb.append("•");
        return sb.toString().trim();
    }
}

public class ActionTable {
    static Map<String, List<List<String>>> grammar = new HashMap<>();
    static Set<String> terminals = new HashSet<>(Arrays.asList("+", "*", "id", "$"));
    static Set<String> nonTerminals = new HashSet<>(Arrays.asList("E", "T", "F"));

    static {
        grammar.put("E", List.of(List.of("E", "+", "T")));
        grammar.put("T", List.of(List.of("T", "*", "F")));
        grammar.put("F", List.of(List.of("id")));
    }

    static Set<LR0Item> closure(Set<LR0Item> items) {
        Set<LR0Item> result = new HashSet<>(items);
        boolean changed;
        do {
            changed = false;
            Set<LR0Item> newItems = new HashSet<>();
            for (LR0Item item : result) {
                String symbol = item.getNextSymbol();
                if (symbol != null && grammar.containsKey(symbol)) {
                    for (List<String> prod : grammar.get(symbol)) {
                        LR0Item newItem = new LR0Item(symbol, prod, 0);
                        if (!result.contains(newItem)) {
                            newItems.add(newItem);
                            changed = true;
                        }
                    }
                }
            }
            result.addAll(newItems);
        } while (changed);
        return result;
    }

    static Set<LR0Item> goTo(Set<LR0Item> items, String symbol) {
        Set<LR0Item> moved = new HashSet<>();
        for (LR0Item item : items) {
            if (symbol.equals(item.getNextSymbol())) {
                moved.add(item.advanceDot());
            }
        }
        return closure(moved);
    }

    public static void main(String[] args) {
        List<Set<LR0Item>> states = new ArrayList<>();
        Map<Integer, Map<String, Integer>> transitions = new HashMap<>();

        Set<LR0Item> start = closure(Set.of(new LR0Item("E", List.of("S"), 0)));
        states.add(start);

        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);

        while (!queue.isEmpty()) {
            int stateIndex = queue.poll();
            Set<LR0Item> state = states.get(stateIndex);

            for (String symbol : union(terminals, nonTerminals)) {
                Set<LR0Item> next = goTo(state, symbol);
                if (!next.isEmpty()) {
                    int nextIndex = findState(states, next);
                    if (nextIndex == -1) {
                        nextIndex = states.size();
                        states.add(next);
                        queue.add(nextIndex);
                    }

                    transitions
                            .computeIfAbsent(stateIndex, k -> new HashMap<>())
                            .put(symbol, nextIndex);
                }
            }
        }

        // Print states
        for (int i = 0; i < states.size(); i++) {
            System.out.println("State " + i + ":");
            for (LR0Item item : states.get(i)) {
                System.out.println("  " + item);
            }
        }

        // Print transitions (ACTION + GOTO)
        System.out.println("\nTransitions (ACTION + GOTO):");
        for (var entry : transitions.entrySet()) {
            int from = entry.getKey();
            for (var trans : entry.getValue().entrySet()) {
                String symbol = trans.getKey();
                int to = trans.getValue();
                String type = terminals.contains(symbol) ? "ACTION" : "GOTO";
                System.out.printf("%s[%d, %s] = %d%n", type, from, symbol, to);
            }
        }
    }

    static int findState(List<Set<LR0Item>> states, Set<LR0Item> target) {
        for (int i = 0; i < states.size(); i++) {
            if (states.get(i).equals(target)) return i;
        }
        return -1;
    }

    static Set<String> union(Set<String> a, Set<String> b) {
        Set<String> result = new HashSet<>(a);
        result.addAll(b);
        return result;
    }
}

