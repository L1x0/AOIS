package by.astakhau.formminimize.nfbuild;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class TrueTable {
    private final String expression;
    private final List<String> prefixExp;
    private final List<String> variables;
    private final List<String> subexpressions;
    private final List<List<String>> table;

    public TrueTable(String expression) {
        this.expression = expression;
        this.prefixExp = Forms.LogicalExpressionParser.infixToRPN(expression);
        this.variables = Forms.LogicalExpressionParser.getVariables(expression);
        this.subexpressions = buildSubexpressions(prefixExp);
        this.table = buildTruthTable();
    }


    public List<List<String>> getTable() {
        return table;
    }

    public List<String> getVariables() {
        return new ArrayList<>(variables);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (List<String> row : table) {
            for (String cell : row) {
                sb.append(String.format("%-8s", cell));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private List<String> buildSubexpressions(List<String> rpn) {
        Stack<String> stack = new Stack<>();
        List<String> subs = new ArrayList<>();
        for (String tok : rpn) {
            if (Forms.LogicalExpressionParser.isVariable(tok)) {
                stack.push(tok);
            } else if (tok.equals("!")) {
                String a = stack.pop();
                String sub = "!" + (a.length() > 1 ? "(" + a + ")" : a);
                stack.push(sub);
                subs.add(sub);
            } else {
                String b = stack.pop();
                String a = stack.pop();
                String sub = (a.length() > 1 ? "(" + a + ")" : a)
                        + tok
                        + (b.length() > 1 ? "(" + b + ")" : b);
                stack.push(sub);
                subs.add(sub);
            }
        }
        return subs;
    }

    private List<List<String>> buildTruthTable() {
        List<List<String>> result = new ArrayList<>();
        List<String> header = new ArrayList<>();
        header.addAll(variables);
        header.addAll(subexpressions);
        result.add(header);

        int n = variables.size();
        int rows = 1 << n;
        for (int mask = 0; mask < rows; mask++) {
            List<String> row = new ArrayList<>();
            boolean[] vals = new boolean[n];
            for (int i = 0; i < n; i++) {
                boolean bit = ((mask >> (n - 1 - i)) & 1) == 1;
                vals[i] = bit;
                row.add(bit ? "1" : "0");
            }

            Stack<Boolean> evalStack = new Stack<>();
            List<String> subResults = new ArrayList<>();
            for (String tok : prefixExp) {
                if (Forms.LogicalExpressionParser.isVariable(tok)) {
                    int idx = variables.indexOf(tok);
                    evalStack.push(vals[idx]);
                } else if (tok.equals("!")) {
                    boolean v = evalStack.pop();
                    boolean res = !v;
                    evalStack.push(res);
                    subResults.add(res ? "1" : "0");
                } else {
                    boolean b = evalStack.pop();
                    boolean a = evalStack.pop();
                    boolean res;
                    switch (tok) {
                        case "&": res = a && b; break;
                        case "|": res = a || b; break;
                        case "^": res = a ^ b; break;
                        case "~": res = (a == b); break;
                        case "->": res = !a || b; break;
                        default: throw new IllegalStateException("Unknown op: " + tok);
                    }
                    evalStack.push(res);
                    subResults.add(res ? "1" : "0");
                }
            }
            row.addAll(subResults);
            result.add(row);
        }
        return result;
    }
}