package by.astakhau.matrixaddressing.matrix.operations;

import by.astakhau.matrixaddressing.matrix.Matrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class FieldAdditionTest {
    private static Matrix matrix;
    @BeforeEach
    public void setUp() {
        matrix = new Matrix();

        List<Boolean> fillNullList = new ArrayList<>();

        for (int i = 0; i < 16; i++) {
            fillNullList.add(false);
        }

        for (int i = 1; i < Matrix.WORDS_COUNT; i++) {
            matrix.setWord(i, fillNullList);
        }

        String testWord = "1110101001111101";
        List<Boolean> testWordList = new ArrayList<>();

        for (Character c : testWord.toCharArray()) {
            testWordList.add(c == '1');
        }

        matrix.setWord(1, testWordList);
    }

    @Test
    public void viewAllTest() {
        FieldAddition fieldAddition = new FieldAddition(List.of(true, true, true), matrix);
        var resultList = fieldAddition.getFoundWords().get(0);

        StringBuilder response = new StringBuilder();
        for (Boolean b : resultList) {
            response.append(b ? "1" : "0");
        }
        assertEquals("1110101001111101", response.toString());
    }

    @Test
    public void resultTest() {
        FieldAddition fieldAddition = new FieldAddition(List.of(true, true, true), matrix);
        var resultList = fieldAddition.getResult().get(0);

        StringBuilder response = new StringBuilder();
        for (Boolean b : resultList) {
            response.append(b ? "1" : "0");
        }
        assertEquals("1110101001101000", response.toString());
    }
}
