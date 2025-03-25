package by.astakhau.logicoperations;


import java.util.*;

public class LogicalExpressionParser {

    private static final Map<String, Integer> precedence = new HashMap<>();
    static {
        precedence.put("!", 4);
        precedence.put("&", 3);
        precedence.put("|", 2);
        precedence.put("->", 1);
        precedence.put("~", 1);
    }

    public static boolean isOperator(String token) {
        return precedence.containsKey(token);
    }


    public static boolean isVariable(String token) {
        return token.matches("[a-e]");
    }


    private static boolean isLeftAssociative(String op) {
        if (op.equals("->") || op.equals("!")) {
            return false;
        }
        return true;
    }

    private static List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < expression.length();) {
            char ch = expression.charAt(i);
            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }
            if (ch == '(' || ch == ')') {
                tokens.add(String.valueOf(ch));
                i++;
            } else if (ch == '!' || ch == '&' || ch == '|' || ch == '~') {
                tokens.add(String.valueOf(ch));
                i++;
            } else if (ch == '-') {
                if (i + 1 < expression.length() && expression.charAt(i + 1) == '>') {
                    tokens.add("->");
                    i += 2;
                } else {
                    i++;
                }
            } else if (Character.isLetter(ch)) {
                tokens.add(String.valueOf(ch));
                i++;
            } else {
                i++;
            }
        }
        return tokens;
    }

    public static List<String> infixToRPN(String expression) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        List<String> tokens = tokenize(expression);

        for (String token : tokens) {
            if (isVariable(token)) {
                output.add(token);
            } else if (isOperator(token)) {
                while (!stack.isEmpty() && isOperator(stack.peek()) &&
                        ((isLeftAssociative(token) && precedence.get(token) <= precedence.get(stack.peek())) ||
                                (!isLeftAssociative(token) && precedence.get(token) < precedence.get(stack.peek())))) {
                    output.add(stack.pop());
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (!stack.isEmpty() && stack.peek().equals("(")) {
                    stack.pop();
                } else {
                    throw new IllegalArgumentException("несогласованные скобки");
                }
            }
        }

        while (!stack.isEmpty()) {
            String op = stack.pop();
            if (op.equals("(") || op.equals(")")) {
                throw new IllegalArgumentException("несогласованные скобки");
            } else {
                output.add(op);
            }
        }

        return output;
    }

    public static List<String> getVariables(String expression) {
        List<String> result = new ArrayList<>();

        for (String token : tokenize(expression)) {
            if (isVariable(token) && !result.contains(token)) {
                result.add(token);
            }
        }

        return result;
    }
}
