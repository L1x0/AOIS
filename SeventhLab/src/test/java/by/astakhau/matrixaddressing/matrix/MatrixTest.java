package by.astakhau.matrixaddressing.matrix;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatrixTest {
    private static Matrix matrix;
    @BeforeEach
    public void setUp() {
        matrix = new Matrix();
    }

    @Test
    public void getWordTest() {
        assertEquals(16, matrix.getWord(1).size());
    }

    @Test
    public void setWordTest() {
        List<Boolean> arg = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            arg.add(true);
        }
        matrix.setWord(1, arg);

        StringBuilder response = new StringBuilder();
        for (Boolean b : arg) {
            response.append(b ? "1" : "0");
        }
        assertEquals("1111111111111111", response.toString());
    }

}
