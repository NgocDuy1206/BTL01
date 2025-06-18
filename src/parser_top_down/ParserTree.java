package src.parser_top_down;

import src.Lexer_dfa.Token;

import java.util.List;

public class ParserTree {
    public Node root;
    public ManageSymbol manageSymbol;
    public ParserTree(Node x) {
        this.root = x;
        manageSymbol = new ManageSymbol();
    }

    public void printTree() {
        root.printNode("", true);
    }



    public void printTableSymbol() {
        manageSymbol.printTable();
    }

    public boolean checkSematic(Node node, ManageSymbol manage) {
        String name = node.name;
        switch (name) {
            case "program", "block":
                return  checkSematic(node.child.get(1), manage);

            case "Body", "statement":
                return checkSematic(node.child.get(0), manage);
            case "StatementList":
                manage.addStack();
                boolean j = true;
                for (int i = 0; i < node.child.size(); i++) {
                    if (!checkSematic(node.child.get(i), manage)) j = false;
                }
                manage.popStack();
                return j;
            case "declaration_statement": {
                Token.Type type = convert(node.child.get(0).child.get(0).name);
                String nameVar = node.child.get(1).child.get(0).name;
                if (manage.checkExist(nameVar)) {
                    manage.addErr("variable " + nameVar + " has exist in " + node.child.get(0).line);
                    return false;
                }
                if (node.child.size() > 3) {
                    int i = 3;
                    if (node.child.get(3).name.equals("LPAREN")) i =  4;
                    if (!checkExpress(node.child.get(i), manage, type)) {
                        manage.addErr("Biểu thức không hợp lệ");
                        manage.addSymbol(type, nameVar, false);
                    } else {
                        manage.addSymbol(type, nameVar, true);
                    }
                } else {
                    manage.addSymbol(type, nameVar, false);
                }
                break;
            }
            case "assign_statement": {
                String varName = node.child.get(0).child.get(0).name;
                Token.Type type = Token.Type.INT;
                if (!manage.checkExist(varName)) {
                    manage.addErr("chưa khai báo biến " + varName + " in " + node.child.get(0).line);
                    type = null;
                }
                if (type != null) type = manage.lookup(varName).type;
                if (!checkExpress(node.child.get(2), manage, type)) {
                    manage.addErr("phép gán không hợp lệ in line " + node.child.get(0).line);
                }
                break;
            }
            case "if_statement":
                checkExpress(node.child.get(2), manage, null);
                if (node.child.size() > 4) {
                    checkExpress(node.child.get(5), manage, null);
                }
                if (node.child.size() > 6) {
                    checkSematic(node.child.get(7), manage);
                }
                break;
            case "for_statement":
                checkSematic(node.child.get(2), manage);
                checkExpress(node.child.get(3), manage, null);
                checkSematic(node.child.get(5), manage);
                checkSematic(node.child.get(7), manage);
                break;
            case "print_statement":
                checkExpress(node.child.get(2), manage, null);
                break;
            case "DoWhile_Statement":
                checkSematic(node.child.get(2), manage);
                checkExpress(node.child.get(6), manage, null);
                break;
        }
        return true;
    }

    public boolean checkExpress(Node node, ManageSymbol manage, Token.Type expectType) {
        switch (node.name) {
            case "expression", "term":
                boolean check = true;
                for (int i = 0; i < node.child.size(); i+= 2) {
                    if (!checkExpress(node.child.get(i), manage, expectType)) check = false;
                }
                return check;
            case "factor":
                if (node.child.size() == 1) {
                    String name = node.child.get(0).name;
                    switch (name) {
                        case "NUMBER":
                            return expectType == Token.Type.INT;
                        case "BOOLEAN":
                            return expectType == Token.Type.BOOL;
                        case "IDENTIFIER":
                            String varName = node.child.get(0).child.get(0).name;
                            if (!manage.checkExist(varName)) {
                                manage.addErr("chưa khai báo biến " + varName + " in line " + node.child.get(0).line);
                                return false;
                            } else if (!manage.checkType(varName, expectType)) {
                                manage.addErr("kiểu của biến " + varName + " không hợp lệ" + " in line " + node.child.get(0).line);
                                return false;
                            } else if (manage.checkNull(varName)) {
                                manage.addErr("biến " + varName + " thì null" + " in line " + node.child.get(0).line);
                                return false;
                            } else return true;
                    }
                } else {
                    return checkExpress(node.child.get(1), manage, expectType);
                }
                break;
            case "condition":
                if (node.child.size() == 1) {
                    switch (node.child.get(0).name) {
                        case "IDENTIFIER": {
                            String name = node.child.get(0).child.get(0).name;
                            if (!manage.checkType(name, Token.Type.BOOL)) {
                                manage.addErr("kiểu của biến " + name + " không hợp lệ");
                                return false;
                            }
                        }
                        case "BOOLEAN":
                            return true;
                        default: return true;
                    }
                }
                boolean a = checkExpress(node.child.get(0), manage, Token.Type.INT);
                boolean b = checkExpress(node.child.get(2), manage, Token.Type.INT);
                if (a != b) {
                    manage.addErr("so sanh khong hop le in line " + node.child.get(1).line);
                    return false;
                } else return true;
        }
        return true;
    }
    public Token.Type convert(String s) {
        if (s.equalsIgnoreCase("int")) return Token.Type.INT;
        else return Token.Type.BOOL;
    }
}
