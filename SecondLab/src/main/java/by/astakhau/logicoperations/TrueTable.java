package by.astakhau.logicoperations;

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

    TrueTable(String exp) {
        this.exp = exp;

        prefixExp = LogicalExpressionParser.infixToRPN(exp);
        variables = LogicalExpressionParser.getVariables(exp);
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
            if (!LogicalExpressionParser.isVariable(prefixExp.get(i))
                    && !prefixExp.get(i).equals("!")
                    && !prefixExp.get(i).equals(">")) {

                if (!prefixExp.get(i - 1).equals("!") && !prefixExp.get(i - 2).equals("!")) {
                    result = onlyBinary(i, result);
                } else {
                    result = withNOT(i, result);
                }

            } else if (prefixExp.get(i).equals("!")) {

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
        }



        result = fillingTable(result);
        result = solveAllExpression(result);

        return result;
    }

    private ArrayList<ArrayList<String>> onlyBinary(int i, ArrayList<ArrayList<String>> result) {

        StringBuilder subExp = new StringBuilder();
        int j;
        for (j = 2; employedIndexes.containsKey(i - j); j++) {}

        if (operationIndex.containsKey(i - j)) {
            subExp.append("(").append(operationIndex.get(i - j)).append(") ");
        } else {
            subExp.append(prefixExp.get(i - j)).append(" ");

            employedIndexes.put(i - j, true);
        }

        subExp.append(prefixExp.get(i));
        if (prefixExp.get(i).equals("-")) {
            employedIndexes.put(i + 1, true);
            subExp.append(">");
        }
        subExp.append(" ");



        if (operationIndex.containsKey(i - 1)) {
            subExp.append("(").append(operationIndex.get(i - 1)).append(") ");
        } else {
            for (j = 1; employedIndexes.containsKey(i - j); j++) {}

            subExp.append(prefixExp.get(i - j));
            employedIndexes.put(i - j, true);
        }

        result.get(0).add(subExp.toString());

        if (prefixExp.get(i).equals("-")) {
            operationIndex.put(i + 1, subExp.toString());
        } else {
            operationIndex.put(i, subExp.toString());
        }

        return result;
    }

    private ArrayList<ArrayList<String>> withNOT(int i, ArrayList<ArrayList<String>> result) {

        StringBuilder subExp = new StringBuilder();

        boolean isItAtSecond = prefixExp.get(i - 1).equals("!");

        if (!isItAtSecond) {
            if (operationIndex.containsKey(i - 2)) {
                if (prefixExp.get(i - 2).equals("!")) {
                    subExp.append(operationIndex.get(i - 2)).append(" ");
                } else {
                    subExp.append(" ").append(operationIndex.get(i - 2)).append(" ");
                }
            } else {
                int j;
                for (j = 2; employedIndexes.containsKey(i - j); j++) {}
                subExp.append(prefixExp.get(i - j)).append(" ");

                employedIndexes.put(i - j, true);
            }

        } else {
            if (operationIndex.containsKey(i - 3)) {
                if (prefixExp.get(i).equals("!")) {
                    subExp.append(operationIndex.get(i - 3)).append(" ");
                } else {
                    subExp.append("(").append(operationIndex.get(i - 3)).append(") ");
                }
            } else {
                int j;
                for (j = 3; employedIndexes.containsKey(i - j); j++) {}
                subExp.append(prefixExp.get(i - j)).append(" ");
                employedIndexes.put(i - j, true);
            }

        }
        subExp.append(prefixExp.get(i));
        if (prefixExp.get(i).equals("-")) subExp.append(">");
        subExp.append(" ");



        if (operationIndex.containsKey(i - 1)) {
            if (prefixExp.get(i - 1).equals("!")) {
                subExp.append(operationIndex.get(i - 1)).append(" ");
            } else {
                subExp.append("(").append(operationIndex.get(i - 1)).append(") ");
            }
        } else {
            int j;
            for (j = 1; employedIndexes.containsKey(i - j); j++) {}

            subExp.append(prefixExp.get(i - j));
            employedIndexes.put(i - j, true);
        }


        result.get(0).add(subExp.toString());

        if (prefixExp.get(i).equals("-")) {
            operationIndex.put(i + 1, subExp.toString());
            employedIndexes.put(i + 1, true);
        } else {
            operationIndex.put(i, subExp.toString());
        }


        return result;
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

    private ArrayList<ArrayList<String>> solveAllExpression(ArrayList<ArrayList<String>> table) {
        ArrayList<ArrayList<String>> result = table;

        for (int i = 1; i < result.size(); i++) {
            ArrayList<String> tempPrefix = new ArrayList<>(prefixExp);

            for (int j = 0; j < variables.size(); j++) {

                int finalJ = j;
                int finalI = i;

                tempPrefix.replaceAll(s -> s.equals(result.get(0).get(finalJ)) ? result.get(finalI).get(finalJ) : s);
            }

            result.get(i).addAll(solveOnePrefix(tempPrefix));
        }


        return result;
    }

    private ArrayList<String> solveOnePrefix(List<String> prefix) {
        ArrayList<String> result = new ArrayList<>();
        employedIndexes = new HashMap<>();

        for (int i = 0; i < prefix.size(); i++) {
            String first, second;
            if (LogicalExpressionParser.isOperator(prefix.get(i)) && !prefix.get(i).equals("!")) {
                int j;
                for (j = 1; employedIndexes.containsKey(i - j); j++) {}

                if (operationResultByIndex.containsKey(i - j)) {
                    second = operationResultByIndex.get(i - j);

                    employedIndexes.put(i - j, true);
                } else {
                    second = prefix.get(i - j);

                    employedIndexes.put(i - j, true);
                }

                for (j = 2; employedIndexes.containsKey(i - j); j++) {}

                if (operationResultByIndex.containsKey(i - j)) {
                    first = operationResultByIndex.get(i - j);

                    employedIndexes.put(i - j, true);
                } else {
                    first = prefix.get(i - j);

                    employedIndexes.put(i - j, true);
                }

                result.add(solveSubexpression(first, second, prefix.get(i)) ? "1" : "0");
                if (prefix.get(i).equals("-")) {
                    employedIndexes.put(i, true);
                    operationResultByIndex.put(i + 1, solveSubexpression(first, second, prefix.get(i)) ? "1" : "0");
                } else {
                    operationResultByIndex.put(i, solveSubexpression(first, second, prefix.get(i)) ? "1" : "0");
                }

            } if (prefix.get(i).equals("!")) {
                int j;
                for (j = 1; employedIndexes.containsKey(i - j); j++) {}

                if (operationResultByIndex.containsKey(i - j)) {
                    second = operationResultByIndex.get(i - j);

                    employedIndexes.put(i - j, true);
                } else {
                    second = prefix.get(i - j);

                    employedIndexes.put(i - j, true);
                }

                result.add(solveSubexpression(second, second, prefix.get(i)) ? "1" : "0");
                operationResultByIndex.put(i, solveSubexpression(second, second, prefix.get(i)) ? "1" : "0");
            }
        }

        return result;
    }

    private ArrayList<ArrayList<String>> fillingTable(ArrayList<ArrayList<String>> table) {
        if (table == null || table.isEmpty()) {
            throw new IllegalArgumentException("Таблица должна содержать хотя бы одну строку с именами переменных.");
        }

        ArrayList<String> header = table.get(0);
        int numVars = LogicalExpressionParser.getVariables(exp).size(); // Количество переменных

        // Вычисляем число строк для комбинаций: 2^numVars
        int totalRows = (int) Math.pow(2, numVars);

        // Для каждой комбинации от 0 до 2^n - 1
        for (int i = 0; i < totalRows; i++) {
            ArrayList<String> row = new ArrayList<>();
            // Заполняем строку значениями переменных.
            // Самый левый столбец соответствует старшему биту
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
                // Каждая ячейка имеет фиксированную ширину 30 символов, выравнивание по левому краю
                sb.append(String.format("%-20s", cell));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
