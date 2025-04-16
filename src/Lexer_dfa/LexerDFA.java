package src.Lexer_dfa;

import java.util.*;

public class LexerDFA {

    private static int index;
    private static int length;
    private static int line = 0;
    private static boolean checkComment = false;
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "begin", "end", "int", "bool", "if", "then", "else", "do", "while", "for", "print", "true", "false"
    ));
    private static final Set<Character> OPERATORS = new HashSet<>(Arrays.asList('+', '*', '-', '/'));

    private static final Set<Character> COMPARE = new HashSet<>(Arrays.asList('>', '<', '='));
    private static final Set<Character> SEPARATORS = new HashSet<>(Arrays.asList('(', ')', '{', '}', ';', ','));
    public static void tokenize(String input, List<Token> tokens) throws Exception {
        line++;
        index = 0;
        length = input.length();
        while (index < length) {
            skipWhiteSpace(input);
            if (input.charAt(index) != '*' && checkComment) {
                index++; continue;
            }
            if (Character.isLetter(input.charAt(index))) {
                tokens.add(readIdOrKeyword(input));
            } else if (Character.isDigit(input.charAt(index))) {
                tokens.add(readNumber(input));
            } else if (OPERATORS.contains(input.charAt(index))) {
                if (input.charAt(index) == '/') {
                    if (index + 1 < length) {
                        if (input.charAt(index + 1) == '/') {
                            return;
                        } else if (input.charAt(index + 1) == '*') {
                            checkComment = true;
                            index+=2;
                            continue;
                        }
                    }
                } else if (input.charAt(index) == '*') {
                    char a = input.charAt(index);

                    if (index + 1 < length && input.charAt(index + 1) == '/') {
                        checkComment = false;
                        index+=2;
                        continue;
                    }
                    else if (checkComment) {
                        index++;
                        continue;
                    }

                }
//                tokens.add(readOperator(input.substring(index, ++index)));
                tokens.add(new Token(line, Token.Type.OPERATOR, input.substring(index, ++index)));
            } else if (COMPARE.contains(input.charAt(index))) {
                int start = index;
                if (index + 1 < length) {
                    if (input.charAt(index + 1) == '=') {
                        index+=2;
                    } else {
                        if (input.charAt(index) == '=') {
                            tokens.add(new Token(line, Token.Type.ASSIGN, input.substring(start, index + 1)));
                            index++;
                            continue;
                        }
                        index++;
                    }
                }
                tokens.add(new Token(line, Token.Type.COMPARE, input.substring(start, index)));
            } else if (SEPARATORS.contains(input.charAt(index))) {
                tokens.add(readSeperator(input.charAt(index)));
                index++;
            }

            else {
                System.out.println("ERROR LEXER : CHARACTER " + input.charAt(index) + " in line " + line);
                index++;

            }
        }

    }

    private static Token readNumber(String input) {
        int start = index;
        while (index < length
                && Character.isDigit(input.charAt(index))) {
            index++;
        }
        String s = input.substring(start, index);
        return new Token(line, Token.Type.NUMBER, s);
    }
    /*******///////////

    private static Token readIdOrKeyword(String input) {
        int start = index;
        while (index < length && Character.isLetter(input.charAt(index))) index++;
        while (index < length && Character.isDigit(input.charAt(index))) index++;

        String s = input.substring(start, index);
        if (KEYWORDS.contains(s)) {
            switch (s) {
                case "if":      return new Token(line, Token.Type.IF, s);
                case "else":    return new Token(line, Token.Type.ELSE, s);
                case "then":    return new Token(line, Token.Type.THEN, s);
                case "do":      return new Token(line, Token.Type.DO, s);
                case "while":   return new Token(line, Token.Type.WHILE, s);
                case "for":     return new Token(line, Token.Type.FOR, s);
                case "begin":   return new Token(line, Token.Type.BEGIN, s);
                case "end":     return new Token(line, Token.Type.END, s);
                case "int":     return new Token(line, Token.Type.INT, s);
                case "bool":    return new Token(line, Token.Type.BOOL, s);
                case "print":   return new Token(line, Token.Type.PRINT, s);
                case "true":    return new Token(line, Token.Type.BOOLEAN, s);
                case "false":   return new Token(line, Token.Type.BOOLEAN, s);
                default:        return new Token(line, Token.Type.KEYWORD, s);
            }
        } else return new Token(line, Token.Type.IDENTIFIER, s);
    }

    private static void skipWhiteSpace(String input) {
        while (index < length
                && (input.charAt(index) == ' '
                || input.charAt(index) == '\t')) {
            index++;
        }
    }

    private static Token readOperator(String oper) throws Exception {
        if (oper.equals("+")) {
            return new Token(line, Token.Type.PLUS, oper);
        } else if (oper.equals("*")) {
            return new Token(line, Token.Type.MUL, oper);
        } else throw new Exception("operation khong hop le");
    }

    private static Token readSeperator(char s) {
        String value = Character.toString(s);
        switch (s) {
            case '(': return new Token(line, Token.Type.LPAREN, value);
            case ')': return new Token(line, Token.Type.RPAREN, value);
            case '{': return new Token(line, Token.Type.LBRACE, value);
            case '}': return new Token(line, Token.Type.RBRACE, value);
            case ';': return new Token(line, Token.Type.END_STATEMENT, value);
            default: return new Token(line, Token.Type.SEPARATOR, value);
        }
    }
}