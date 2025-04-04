package src.Lexer_dfa;

import java.util.*;

public class LexerDFA {

    private static int index;
    private static int length;
    private static int line = 0;
    private static boolean checkComment = false;
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "begin", "end", "int", "bool", "if", "then", "else", "do", "while", "for", "print"
    ));
    private static final Set<Character> OPERATORS = new HashSet<>(Arrays.asList('+', '*', '-', '/'));

    private static final Set<Character> COMPARE = new HashSet<>(Arrays.asList('>', '<', '='));
    private static final Set<Character> SEPARATORS = new HashSet<>(Arrays.asList('(', ')', '{', '}', ';', ','));
    public static void tokenize(String input, List<Token> tokens) {
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

                } tokens.add(new Token(Token.Type.OPERATOR, input.substring(index, ++index)));
            } else if (COMPARE.contains(input.charAt(index))) {
                int start = index;
                if (index + 1 < length) {
                    if (input.charAt(index + 1) == '=') {
                        index+=2;
                    } else {
                        index++;
                    }
                }
                tokens.add(new Token(Token.Type.COMPARE, input.substring(start, index)));
            } else if (SEPARATORS.contains(input.charAt(index))) {
                tokens.add(new Token(Token.Type.SEPARATOR, Character.toString(input.charAt(index))));
                index++;
            }

            else {
                System.out.println("ERROR:" + input.charAt(index) + "in line " + line);
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
        return new Token(Token.Type.NUMBER, s);
    }
    /*******///////////

    private static Token readIdOrKeyword(String input) {
        int start = index;
        while (index < length && Character.isLetter(input.charAt(index))) index++;
        while (index < length && Character.isDigit(input.charAt(index))) index++;

        String s = input.substring(start, index);
        if (KEYWORDS.contains(s)) {
            return new Token(Token.Type.KEYWORD, s);
        } else return new Token(Token.Type.IDENTIFIER, s);
    }

    private static void skipWhiteSpace(String input) {
        while (index < length
                && (input.charAt(index) == ' '
                || input.charAt(index) == '\t')) {
            index++;
        }
    }
}