package by.astakhau.matrixaddressing.matrix.operations;

import by.astakhau.matrixaddressing.matrix.Matrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class LogicalOperationTest {
    private static List<Boolean> firstOperand;
    private static List<Boolean> secondOperand;
    private static Matrix matrix;
    private static LogicalOperation logicalOperation;

    @BeforeEach
    public void setUp() {
        matrix = new Matrix();
        firstOperand = matrix.getWord(1);
        secondOperand = matrix.getWord(2);
        logicalOperation = new LogicalOperation(firstOperand, secondOperand);
    }

    @Test
    public void firstOperationTest() {
        List<Boolean> result = new ArrayList<>();

        for (int i = 0; i < firstOperand.size(); i++) {
            result.add(firstOperand.get(i) & secondOperand.get(i));
        }

        assertEquals(result, logicalOperation.firstExpression());
    }

    @Test
    public void secondOperationTest() {
        assertEquals(firstOperand, logicalOperation.secondExpression());
    }

    @Test
    public void fourthOperationTest() {
        List<Boolean> result = new ArrayList<>();

        for (int i = 0; i < firstOperand.size(); i++) {
            result.add(firstOperand.get(i) & secondOperand.get(i));
        }

        result.replaceAll(aBoolean -> !aBoolean);
        assertEquals(result, logicalOperation.fourthExpression());
    }

    @Test
    public void thirdOperationTest() {
        firstOperand.replaceAll(aBoolean -> !aBoolean);
        assertEquals(firstOperand, logicalOperation.thirdExpression());
    }
}
