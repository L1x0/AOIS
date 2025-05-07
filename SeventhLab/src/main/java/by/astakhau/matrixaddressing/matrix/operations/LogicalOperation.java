package by.astakhau.matrixaddressing.matrix.operations;

import java.util.ArrayList;
import java.util.List;

public class LogicalOperation {
    private List<Boolean> firstOperand;
    private List<Boolean> secondOperand;

    public LogicalOperation(List<Boolean> firstOperand, List<Boolean> secondOperand) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
    }

    public List<Boolean> firstExpression() {
        List<Boolean> result = new ArrayList<>();

        for (int i = 0; i < firstOperand.size(); i++) {
            result.add(firstOperand.get(i) & secondOperand.get(i));
        }
        return result;
    }

    public List<Boolean> secondExpression() {
        return firstOperand;
    }

    public List<Boolean> thirdExpression() {
        firstOperand.replaceAll(aBoolean -> !aBoolean);
        return firstOperand;
    }

    public List<Boolean> fourthExpression() {
        List<Boolean> result = new ArrayList<>();

        for (int i = 0; i < firstOperand.size(); i++) {
            result.add(firstOperand.get(i) & secondOperand.get(i));
        }

        result.replaceAll(aBoolean -> !aBoolean);
        return result;
    }
}
