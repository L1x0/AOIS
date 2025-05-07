package by.astakhau.matrixaddressing.matrix.operations;


import by.astakhau.matrixaddressing.matrix.Matrix;

import java.util.ArrayList;
import java.util.List;

public class NearestWordSearch {
    private List<List<Boolean>> words;
    private final Matrix matrix;
    private final int index;
    private int targetIndex;

    public NearestWordSearch(Matrix matrix, int index) {
        this.matrix = matrix;
        this.index = index;
        words = new ArrayList<>();
        this.sort();
    }

    public List<Boolean> getNearestGreaterWord() {
        setTargetIndex();

        if (targetIndex == words.size() - 1) {
            return null;
        }

        return words.get(targetIndex + 1);
    }

    public List<Boolean> getNearestLowerWord() {
        setTargetIndex();

        if (targetIndex == 0) {
            return null;
        }

        return words.get(targetIndex - 1);
    }

    private void setTargetIndex() {
        for (int i = 1; i < Matrix.WORDS_COUNT; i++) {
            if (matrix.getWord(index).equals(words.get(i))) {
                targetIndex = i;
                break;
            }
        }
    }



    private void sort() {
        words.clear();
        for (int i = 1; i <= Matrix.WORDS_COUNT; i++) {
            words.add(matrix.getWord(i));
        }

        words.sort((word1, word2) -> {
            for (int i = 0; i < word1.size(); i++) {
                boolean b1 = word1.get(i);
                boolean b2 = word2.get(i);
                if (b1 != b2) {
                    return b1 ? 1 : -1;
                }
            }
            return 0;
        });
    }

}
