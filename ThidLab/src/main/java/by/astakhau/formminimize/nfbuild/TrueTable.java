package by.astakhau.formminimize.nfbuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrueTable {
    String exp;
    List<String> prefixExp;
    List<String> variables;
    List<ArrayList<String>> table;

    Map<Integer, String> operationIndex = new HashMap<>();
    Map<Integer, Boolean> employedIndexes = new HashMap<>();
    Map<Integer, String> operationResultByIndex = new HashMap<>();

    public TrueTable(String exp) {
        this.exp = exp;
        prefixExp = Forms.LogicalExpressionParser.infixToRPN(exp);
        variables = Forms.LogicalExpressionParser.getVariables(exp);
        table = createTable();
    }

    public ArrayList<ArrayList<String>> getTable() {
        return (ArrayList<ArrayList<String>>) table;
    }

    private ArrayList<ArrayList<String>> createTable() {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        result.get(0).addAll(variables);

        for (int i = 0; i < prefixExp.size(); i++) {
            if (!Forms.LogicalExpressionParser.isVariable(prefixExp.get(i))
                    && !prefixExp.get(i).equals("!")
                    && !prefixExp.get(i).equals(">")) {
                if (!prefixExp.get(i - 1).equals("!") && !prefixExp.get(i - 2).equals("!")) {
                    result = processBinaryOperator(i, result);
                } else {
                    result = processOperatorWithNOT(i, result);
                }
            }
            else if (prefixExp.get(i).equals("!")) {
                processNegation(i, result);
            }
        }

        result = fillTable(result);
        result = evaluateAllExpressions(result);
        return result;
    }

    private void processNegation(int i, ArrayList<ArrayList<String>> result) {
        StringBuilder subExp = new StringBuilder();
        subExp.append(prefixExp.get(i));
        if (operationIndex.containsKey(i - 1)) {
            subExp.append("(").append(operationIndex.get(i - 1)).append(") ");
        } else {
            subExp.append(prefixExp.get(i - 1));
        }
        result.get(0).add(subExp.toString());
        operationIndex.put(i, subExp.toString());
        employedIndexes.put(i - 1, true);
        if (variables.contains(prefixExp.get(i - 1))) {
            employedIndexes.put(i, true);
        }
    }

    private ArrayList<ArrayList<String>> processBinaryOperator(int i, ArrayList<ArrayList<String>> result) {
        StringBuilder subExp = new StringBuilder();

        subExp.append(getOperandRepresentation(i, 2));
        subExp.append(prefixExp.get(i));
        if (prefixExp.get(i).equals("-")) {
            employedIndexes.put(i + 1, true);
            subExp.append(">");
        }
        subExp.append(" ");
        subExp.append(getOperandRepresentation(i, 1));
        result.get(0).add(subExp.toString());
        if (prefixExp.get(i).equals("-")) {
            operationIndex.put(i + 1, subExp.toString());
        } else {
            operationIndex.put(i, subExp.toString());
        }
        return result;
    }

    private ArrayList<ArrayList<String>> processOperatorWithNOT(int i, ArrayList<ArrayList<String>> result) {
        StringBuilder subExp = new StringBuilder();
        boolean isNotSecond = prefixExp.get(i - 1).equals("!");

        if (!isNotSecond) {
            subExp.append(getLeftOperandForNot(i, 2));
        } else {
            subExp.append(getLeftOperandForNot(i, 3));
        }
        subExp.append(prefixExp.get(i));
        if (prefixExp.get(i).equals("-")) {
            subExp.append(">");
        }
        subExp.append(" ");
        subExp.append(getRightOperandForNot(i));
        result.get(0).add(subExp.toString());
        if (prefixExp.get(i).equals("-")) {
            operationIndex.put(i + 1, subExp.toString());
            employedIndexes.put(i + 1, true);
        } else {
            operationIndex.put(i, subExp.toString());
        }
        return result;
    }

    private String getLeftOperandForNot(int i, int startOffset) {
        int j;
        for (j = startOffset; employedIndexes.containsKey(i - j); j++) {}
        String operand;
        if (operationIndex.containsKey(i - j)) {
            if (prefixExp.get(i - j).equals("!")) {
                operand = operationIndex.get(i - j) + " ";
            } else {
                operand = "(" + operationIndex.get(i - j) + ") ";
            }
        } else {
            operand = prefixExp.get(i - j) + " ";
            employedIndexes.put(i - j, true);
        }
        return operand;
    }

    private String getOperandRepresentation(int i, int startOffset) {
        int j;
        for (j = startOffset; employedIndexes.containsKey(i - j); j++) {}
        String operand;
        if (operationIndex.containsKey(i - j)) {
            operand = "(" + operationIndex.get(i - j) + ") ";
        } else {
            operand = prefixExp.get(i - j) + " ";
            employedIndexes.put(i - j, true);
        }
        return operand;
    }

    private String getRightOperandForNot(int i) {
        String operand;
        if (operationIndex.containsKey(i - 1)) {
            if (prefixExp.get(i - 1).equals("!")) {
                operand = operationIndex.get(i - 1) + " ";
            } else {
                operand = "(" + operationIndex.get(i - 1) + ") ";
            }
        } else {
            int j;
            for (j = 1; employedIndexes.containsKey(i - j); j++) {}
            operand = prefixExp.get(i - j);
            employedIndexes.put(i - j, true);
        }
        return operand;
    }

    private boolean solveSubexpression(String firstValStr, String secondValStr, String operation) {
        boolean firstVal = firstValStr.equals("1");
        boolean secondVal = secondValStr.equals("1");
        return switch (operation) {
            case "&" -> firstVal && secondVal;
            case "|" -> firstVal || secondVal;
            case "^" -> firstVal ^ secondVal;
            case "~" -> firstVal == secondVal;
            case "->" -> !(firstVal && !secondVal);
            case "!" -> !secondVal;
            default -> throw new IllegalStateException("Unexpected value: " + operation);
        };
    }

    private ArrayList<ArrayList<String>> evaluateAllExpressions(ArrayList<ArrayList<String>> table) {
        for (int i = 1; i < table.size(); i++) {
            ArrayList<String> tempPrefix = new ArrayList<>(prefixExp);
            for (int j = 0; j < variables.size(); j++) {
                int finalJ = j;
                int finalI = i;
                tempPrefix.replaceAll(s -> s.equals(table.get(0).get(finalJ))
                        ? table.get(finalI).get(finalJ) : s);
            }
            table.get(i).addAll(evaluatePrefix(tempPrefix));
        }
        return table;
    }

    private ArrayList<String> evaluatePrefix(List<String> prefix) {
        ArrayList<String> result = new ArrayList<>();
        employedIndexes = new HashMap<>();
        for (int i = 0; i < prefix.size(); i++) {
            if (Forms.LogicalExpressionParser.isOperator(prefix.get(i)) && !prefix.get(i).equals("!")) {
                String second = getEvaluationOperand(prefix, i, 1);
                String first = getEvaluationOperand(prefix, i, 2);
                String opResult = solveSubexpression(first, second, prefix.get(i)) ? "1" : "0";
                result.add(opResult);
                if (prefix.get(i).equals("-")) {
                    employedIndexes.put(i, true);
                    operationResultByIndex.put(i + 1, opResult);
                } else {
                    operationResultByIndex.put(i, opResult);
                }
            } else if (prefix.get(i).equals("!")) {
                String operand = getEvaluationOperand(prefix, i, 1);
                String opResult = solveSubexpression(operand, operand, prefix.get(i)) ? "1" : "0";
                result.add(opResult);
                operationResultByIndex.put(i, opResult);
            }
        }
        return result;
    }

    private String getEvaluationOperand(List<String> prefix, int i, int startOffset) {
        int j;
        for (j = startOffset; employedIndexes.containsKey(i - j); j++) {
            // ищем первый неиспользованный индекс
        }
        String operand;
        if (operationResultByIndex.containsKey(i - j)) {
            operand = operationResultByIndex.get(i - j);
        } else {
            operand = prefix.get(i - j);
        }
        employedIndexes.put(i - j, true);
        return operand;
    }

    private ArrayList<ArrayList<String>> fillTable(ArrayList<ArrayList<String>> table) {
        if (table == null || table.isEmpty()) {
            throw new IllegalArgumentException("Таблица должна содержать хотя бы одну строку с именами переменных.");
        }
        int numVars = Forms.LogicalExpressionParser.getVariables(exp).size();
        int totalRows = (int) Math.pow(2, numVars);
        for (int i = 0; i < totalRows; i++) {
            ArrayList<String> row = new ArrayList<>();
            for (int j = 0; j < numVars; j++) {
                int bit = (i >> (numVars - j - 1)) & 1;
                row.add(bit == 1 ? "1" : "0");
            }
            table.add(row);
        }
        return table;
    }

    public List<String> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        if (table == null || table.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (List<String> row : table) {
            for (String cell : row) {
                sb.append(String.format("%-20s", cell));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
