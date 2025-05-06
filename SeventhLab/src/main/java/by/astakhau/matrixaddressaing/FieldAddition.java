package by.astakhau.matrixaddressaing;

import by.astakhau.matrixaddressaing.matrix.Matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldAddition {
    private final List<Integer> wordNums;
    private final List<Boolean> V;
    private final Matrix matrix;

    private static final int A_START_POS = 3;
    private static final int A_END_POS = 7;
    private static final int A_B_POS_DIFFERENCE = 4;

    public FieldAddition(List<Boolean> V, Matrix matrix) {
        wordNums = new ArrayList<>();
        this.V = V;
        this.matrix = matrix;

        findAllWorlds();
    }

    public List<List<Boolean>> getResult() {
        List<List<Boolean>> result = new ArrayList<>();

        for (Integer num : wordNums) {
            result.add(sum(matrix.getWord(num)));
        }

        return result;
    }

    private List<Boolean> sum(List<Boolean> list) {
        List<Boolean> A = new ArrayList<>(4);
        List<Boolean> B = new ArrayList<>(4);

        for (int i = A_START_POS; i < A_END_POS; i++) {
            A.add(list.get(i));
            B.add(list.get(i + A_B_POS_DIFFERENCE));
        }
        var ACopy = new ArrayList<>(A);

        ACopy.add(0, false);



        return Stream.of(V, A, B, addBinary(ACopy, B))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<List<Boolean>> getFoundWords() {
        List<List<Boolean>> result = new ArrayList<>();

        for (Integer num : wordNums) {
            result.add(matrix.getWord(num));
        }

        return result;
    }


    private void findAllWorlds() {
        for (int i = 1; i < Matrix.WORDS_COUNT; i++) {
            var tempWord = matrix.getWord(i);

            if (V.equals(List.of(tempWord.get(0), tempWord.get(1), tempWord.get(2)))) {
                wordNums.add(i);
            }
        }
    }

    private static List<Boolean> addBinary(List<Boolean> a, List<Boolean> b) {
        List<Boolean> result = new ArrayList<>();
        int i = a.size() - 1;
        int j = b.size() - 1;
        int carry = 0;

        while (i >= 0 || j >= 0 || carry != 0) {
            int bitA = (i >= 0 && a.get(i) ? 1 : 0);
            int bitB = (j >= 0 && b.get(j) ? 1 : 0);
            int sum = bitA + bitB + carry;

            result.add(sum % 2 == 1);
            carry = sum / 2;

            i--;
            j--;
        }

        Collections.reverse(result);
        return result;
    }
}
