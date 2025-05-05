package by.astakhau.schemesynthesis.nfbuild;

import java.util.*;

public class Forms {
    private TrueTable tableObj;
    private final ArrayList<ArrayList<String>> table;
//    private final String PDNF;
//    private final String PCNF;
//    private final String numericPDNF;
//    private final String numericPCNF;
    //private final String indexForm;
    private final int varCount;


    public Forms(TrueTable tableObj) {
        this.tableObj = tableObj;
        this.table = tableObj.getTable();
        varCount = tableObj.getVariables().size();

//        PDNF = createPDNF();
//        PCNF = createPCNF();
//        numericPCNF = createNumericPCNF();
//        numericPDNF = createNumericPDNF();
        //indexForm = createIndexForm();
    }

    private String createPDNF() {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < table.size(); i++) {
            if (table.get(i).get(table.get(i).size() - 1).equals("1")) {
                appendClause(sb, i, "1", "&", "|");
            }
        }

        return sb.toString();
    }

    private String createPCNF() {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < table.size(); i++) {
            if (table.get(i).get(table.get(i).size() - 1).equals("0")) {
                appendClause(sb, i, "0", "|", "&");
            }
        }

        return sb.toString();
    }

    public String createPCNF(int fromEnd) {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < table.size(); i++) {
            if (table.get(i).get(table.get(i).size() - fromEnd).equals("0")) {
                appendClause(sb, i, "0", "|", "&");
            }
        }

        return sb.toString();
    }

    public String createPDNF(int fromEnd) {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < table.size(); i++) {
            if (table.get(i).get(table.get(i).size() - fromEnd).equals("1")) {
                appendClause(sb, i, "0", "&", "|");
            }
        }

        return sb.toString();
    }

    private void appendClause(
            StringBuilder sb,
            int rowIndex,
            String expectedValue,
            String innerOperator,
            String outerOperator
    ) {
        sb.append(sb.isEmpty() ? "(" : outerOperator + " (");

        for (int j = 0; j < varCount; j++) {
            if (table.get(rowIndex).get(j).equals(expectedValue)) {
                sb.append(table.get(0).get(j));
            } else {
                sb.append("!").append(table.get(0).get(j));
            }

            sb.append(" ");
            if (j + 1 < varCount) {
                sb.append(innerOperator).append(" ");
            }
        }
        sb.append(") ");
    }


//    private String createNumericPCNF() {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("(");
//        for (int i = 1; i < table.size(); i++) {
//            if (table.get(i).get(table.get(i).size() - 1).equals("1")) {
//                sb.append(i - 1);
//                sb.append(i + 2 < table.size() ? ", " : ") &");
//            }
//        }
//
//        return sb.toString();
//    }

    private String createNumericPDNF() {
        StringBuilder sb = new StringBuilder();

        sb.append("(");
        for (int i = 1; i < table.size(); i++) {
            if (table.get(i).get(table.get(i).size() - 1).equals("0")) {
                sb.append(i - 1);
                sb.append(i + 2 < table.size() ? ", " : ") |");
            }
        }

        return sb.toString();
    }

    private String createIndexForm() {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < table.size(); i++) {
            sb.append(table.get(i).get(table.get(i).size() - 1));
        }

        String num = String.valueOf(Integer.parseInt(sb.toString(), 2));
        return num + " - "+ sb.toString();
    }


//    public String getPDNF() {
//        return PDNF;
//    }
//
//    public String getPCNF() {
//        return PCNF;
//    }

//    public String getNumericPDNF() {
//        return numericPDNF;
//    }
//
//    public String getNumericPCNF() {
//        return numericPCNF;
//    }

//    public String getIndexForm() {
//        return indexForm;
//    }

    public static class LogicalExpressionParser {

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
            return !op.equals("->") && !op.equals("!");
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
            Deque<String> stack = new ArrayDeque<>();
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
}
