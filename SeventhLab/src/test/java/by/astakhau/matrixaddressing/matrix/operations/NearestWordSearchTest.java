package by.astakhau.matrixaddressing.matrix.operations;

import by.astakhau.matrixaddressing.matrix.Matrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NearestWordSearchTest {
    private static Matrix matrix;


    @BeforeEach
    public void setUp() {
        matrix = new Matrix();

        List<Boolean> fillNullList = new ArrayList<>();

        for (int i = 0; i < Matrix.MATRIX_SIDE; i++) {
            fillNullList.add(false);
        }

        for (int i = 1; i < Matrix.WORDS_COUNT + 1; i++) {
            matrix.setWord(i, fillNullList);
        }

        String testWord = "1111111111111111";
        List<Boolean> testWordList = new ArrayList<>();

        for (Character c : testWord.toCharArray()) {
            testWordList.add(c == '1');
        }

        matrix.setWord(2, testWordList);
    }

    @Test
    public void testLowerNearestWordSearch() {
        NearestWordSearch nearestWordSearch = new NearestWordSearch(matrix, 2);

        System.out.println(matrix.toString());

        var result = nearestWordSearch.getNearestLowerWord();

        StringBuilder response = new StringBuilder();
        for (Boolean b : result) {
            response.append(b ? "1" : "0");
        }
        assertEquals("0000000000000000", response.toString());
    }
}
