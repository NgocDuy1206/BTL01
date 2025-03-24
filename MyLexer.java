import java.util.*;
import java.util.regex.*;

public class MyLexer {
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "(?<COMMENT>/\\*|//)|" +
                    "(?<KEYWORD>\\b(begin|end|int|bool|if|then|else|do|while|for|print)\\b)|" +
                    "(?<OPERATOR>>=|>|==|\\+|\\*)|" +
                    "(?<ID>[a-zA-Z]+[0-9]*)|" +
                    "(?<NUMBER>\\b\\d+\\b)|" +
                    "(?<SYMBOL>[{}();=])|"

    );
    public static boolean checkMultiComments = false;
    public static void tokenize(String input, List<Token> tokens) {
        if (checkMultiComments == true) {
            if (input.indexOf("*/") != -1) {
                checkMultiComments = false;
            }
            return;
        }
        Matcher matcher = TOKEN_PATTERN.matcher(input);

        while (matcher.find()) {
            if (matcher.group("COMMENT") != null) {
                if (matcher.group("COMMENT").equals("/*")) {
                    checkMultiComments = true;
                    tokens.add(new Token(TokenType.COMMENT, "multi comments"));
                    if (input.indexOf("*/") != -1) {
                        checkMultiComments = false;
                    } return;
                } else {
                    tokens.add(new Token(TokenType.COMMENT, "one comment"));
                    return;
                }
            } else if (matcher.group("KEYWORD") != null) {
                tokens.add(new Token(TokenType.KEYWORD, matcher.group("KEYWORD")));
            } else if (matcher.group("OPERATOR") != null) {
                tokens.add(new Token(TokenType.OPERATOR, matcher.group("OPERATOR")));
            } else if (matcher.group("ID") != null) {
                tokens.add(new Token(TokenType.ID, matcher.group("ID")));
            } else if (matcher.group("NUMBER") != null) {
                tokens.add(new Token(TokenType.NUMBER, matcher.group("NUMBER")));
            } else if (matcher.group("SYMBOL") != null) {
                tokens.add(new Token(TokenType.SYMBOL, matcher.group("SYMBOL")));
            }
        }
    }
}

enum TokenType {
    COMMENT, KEYWORD, OPERATOR, ID, NUMBER, SYMBOL;
}

class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "TOKEN <" + type + ", \"" + value + "\">";
    }
}
