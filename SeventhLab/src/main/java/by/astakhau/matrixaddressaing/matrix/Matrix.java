package by.astakhau.matrixaddressaing.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Matrix {
    private static final int WORD_FULL_LENGTH = 17;
    private static final int MATRIX_SIDE = 16;
    List<List<Boolean>> matrix;

    public Matrix(List<List<Boolean>> matrix) {
        this.matrix = matrix;
    }

    public Matrix() {
        matrix = new ArrayList<>();

        for (int i = 0; i < 16; i++) {
            matrix.add(new ArrayList<>());
        }
    }

    public List<Boolean> getWord(int num) {
        if (!sizeCheck()) {
            throw new IllegalStateException("your matrix is not 16x16");
        }

        if (num >= MATRIX_SIDE || num < 0) {
            throw new IllegalArgumentException("число выходит за границы матрицы");
        }

        num--;
        List<Boolean> result = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            int row = (num * WORD_FULL_LENGTH + 1 + i) % MATRIX_SIDE;
            int col = (num * WORD_FULL_LENGTH + 1 + i) / MATRIX_SIDE;

            result.add(matrix.get(row).get(col));
        }

        return result;
    }

    public void setWord(int num, List<Boolean> word) {
        if (!sizeCheck()) {
            throw new IllegalStateException("your matrix is not 16x16");
        }

        if (num >= MATRIX_SIDE || num < 0) {
            throw new IllegalArgumentException("число выходит за границы матрицы");
        }

        if (word.size() != MATRIX_SIDE) {
            throw new IllegalArgumentException("Длина слова не равна 16");
        }

        num--;
        for (int i = 0; i < 16; i++) {
            int row = (num * WORD_FULL_LENGTH + 1 + i) % MATRIX_SIDE;
            int col = (num * WORD_FULL_LENGTH + 1 + i) / MATRIX_SIDE;

            matrix.get(row).set(col, word.get(i));
        }
    }

    public void generateMatrix() {
        Random r = new Random();

        clearMatrix();

        for (int i = 0; i < MATRIX_SIDE; i++) {
            for (int j = 0; j < MATRIX_SIDE; j++) {
                matrix.get(i).add(r.nextBoolean());
            }
        }
    }

    private boolean sizeCheck() {
        for (List<Boolean> booleans : matrix) {
            if (booleans.size() != MATRIX_SIDE) {
                return false;
            }
        }

        return true;
    }

    private void clearMatrix() {
        for (int i = 0; i < MATRIX_SIDE; i++) {
            for (int j = 0; j < MATRIX_SIDE; j++) {
                matrix.get(i).removeAll(matrix.get(j));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (List<Boolean> booleans : matrix) {
            for (boolean b : booleans) {
                result.append(b ? "1" : "0").append(" ");
            }
            result.append("\n");
        }

        return result.toString();
    }
}
