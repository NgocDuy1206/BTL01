package src.parser_top_down;

import src.Lexer_dfa.Token;

import java.util.*;

public class ManageSymbol {
    private HashMap<String, Symbol> currentTable;
    private Stack<HashMap<String, Symbol>> processThread = new Stack<>();
    public ManageSymbol() {
        this.addStack();
    }
    public List<String> listErr = new ArrayList<>();
    public HashMap<String, Symbol> peek() {
        return currentTable;
    }
    protected void addStack() {
        currentTable = new HashMap<>();
        processThread.add(currentTable);
    }

    protected void popStack() {
        processThread.pop();
        currentTable = processThread.peek();
    }
    protected boolean checkExist(String name) {
        if (lookup(name) == null) return false;
        else  return true;
    }

    protected boolean checkHaveValue(String name) {
        for (int i = processThread.size() - 1; i >= 0; i--) {
            HashMap<String, Symbol> table = processThread.get(i);
            if (table.containsKey(name)) {
                return table.get(name).initial;
            }
        }
        return false;
    }

    protected void addErr(String mes) {
        listErr.add("ERROR SEMATIC: " + mes );
    }
    protected void addSymbol(Token.Type type, String name, boolean initial) {
        currentTable.put(name, new Symbol(type, name, initial));
    }


    protected Symbol lookup(String name) {
        // Duyệt từ trên đỉnh stack xuống
        for (int i = processThread.size() - 1; i >= 0; i--) {
            Map<String, Symbol> scope = processThread.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null; // Không tìm thấy
    }

    protected void printTable() {
        System.out.println();
        for (Map.Entry<String, Symbol> entry: currentTable.entrySet()) {
            System.out.println(entry.getValue().toString());
        }
    }
    protected void printErr() {
        if (listErr.size() != 0) {
            System.out.println("Erorr in Sematic");
            for(String i : listErr) {
                System.out.println(i);
            }
        } else {
            System.out.println("không có lỗi ngữ nghĩa");
        }
    }

    protected boolean checkNull(String name) {
        Symbol x;
        if ((x = lookup(name)) == null) return true;
        if (x.initial == false) return true;
        return false;
    }

    public boolean checkType(String varName, Token.Type expectType) {
        if (expectType == null) return true;
        Symbol x = lookup(varName);
        return expectType == x.type;
    }
}
