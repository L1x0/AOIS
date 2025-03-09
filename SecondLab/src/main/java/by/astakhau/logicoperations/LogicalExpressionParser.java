package by.astakhau.logicoperations;


import java.util.*;

public class LogicalExpressionParser {
    // Определяем приоритеты операторов.
    private static final Map<String, Integer> precedence = new HashMap<>();
    static {
        // Приоритет: ! (NOT) — самый высокий; затем & (AND); затем | (OR); затем -> и ~ (наименьший приоритет)
        precedence.put("!", 4);
        precedence.put("&", 3);
        precedence.put("|", 2);
        precedence.put("->", 1);
        precedence.put("~", 1);
    }

    // Проверка, является ли токен оператором
    public static boolean isOperator(String token) {
        return precedence.containsKey(token);
    }

    // Проверка, является ли токен переменной (a, b, c, d, e)
    public static boolean isVariable(String token) {
        return token.matches("[a-e]");
    }

    // Для упрощения: считаем, что все бинарные операторы (кроме "->" и унарного "!") являются левоассоциативными.
    private static boolean isLeftAssociative(String op) {
        if (op.equals("->") || op.equals("!")) {
            return false; // "->" и "!" считаются правоассоциативными
        }
        return true;
    }

    // Простой токенизатор для логического выражения.
    // Обрабатывает символы, пробелы, скобки и мультисимвольный оператор "->".
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
            } else if (ch == '-') { // Обработка оператора "->"
                if (i + 1 < expression.length() && expression.charAt(i + 1) == '>') {
                    tokens.add("->");
                    i += 2;
                } else {
                    // Если встретился один символ '-', пропускаем его
                    i++;
                }
            } else if (Character.isLetter(ch)) {
                tokens.add(String.valueOf(ch));
                i++;
            } else {
                // Если встретился неизвестный символ, пропускаем его
                i++;
            }
        }
        return tokens;
    }

    // Функция для преобразования инфиксного выражения в обратную польскую запись (ОПЗ)
    public static List<String> infixToRPN(String expression) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        List<String> tokens = tokenize(expression);

        for (String token : tokens) {
            if (isVariable(token)) {
                // При встрече переменной выводим её и добавляем в выходной список.
                System.out.println("Переменная обнаружена: " + token);
                output.add(token);
            } else if (isOperator(token)) {
                // Обработка операторов.
                while (!stack.isEmpty() && isOperator(stack.peek()) &&
                        ((isLeftAssociative(token) && precedence.get(token) <= precedence.get(stack.peek())) ||
                                (!isLeftAssociative(token) && precedence.get(token) < precedence.get(stack.peek())))) {
                    output.add(stack.pop());
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                // Извлекаем операторы до открывающей скобки.
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (!stack.isEmpty() && stack.peek().equals("(")) {
                    stack.pop(); // удаляем открывающую скобку
                } else {
                    System.out.println("Ошибка: несогласованные скобки");
                }
            }
        }

        // Выталкиваем оставшиеся операторы из стека
        while (!stack.isEmpty()) {
            String op = stack.pop();
            if (op.equals("(") || op.equals(")")) {
                System.out.println("Ошибка: несогласованные скобки");
            } else {
                output.add(op);
            }
        }

        return output;
    }

    public static List<String> getVariables(String expression) {
        List<String> result = new ArrayList<>();

        for (String token : tokenize(expression)) {
            if (isVariable(token)) {
                result.add(token);
            }
        }

        return result;
    }
}
