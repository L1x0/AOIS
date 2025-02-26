package by.astakhau.binarycode;


import lombok.Getter;

public class BinaryNumber {
    private long inDecimal;
    private boolean[] inDirect = new boolean[32];
    private boolean[] inReverse = new boolean[32];
    @Getter private boolean[] inAdditional = new boolean[32];
    @Getter private boolean[] changedSignAdditional = new boolean[32];

    public BinaryNumber(long inDecimal) {
        this.inDecimal = inDecimal;

        this.inAdditional = toAdditional();
        this.inReverse = toReverse();
        this.inDirect = toDirect();
    }

    public boolean[] toDirect() {
        boolean[] result = new boolean[32];
        var temp = inDecimal;

        if (temp < 0) {
            result[0] = true;
            temp = Math.abs(temp);
        }

        for (int i = 31; i >= 0; i--) {
            if (temp == 1) {
                result[i] = true;
                break;
            }

            result[i] = temp % 2 == 1;

            temp /= 2;
        }

        return result;
    }

    public boolean[] toReverse() {
        boolean[] result = toDirect();

        if (!result[0]) {
            return result;
        }

        for (int i = 31; i > 0; i--) {
            result[i] = !result[i];
        }

        return result;
    }

    public boolean[] toAdditional() {
        return inDecimal < 0 ? BinaryOperations.createAdditionalCode(toReverse()) : toReverse();
    }

    public String getStraightCode() {
        return getCode(inDirect);
    }

    public String getReverseCode() {
        return getCode(inReverse);
    }

    public String getAdditionalCode() {
        return getCode(inAdditional);
    }

    private static String getCode(boolean[] code) {
        StringBuilder result = new StringBuilder();

        result.append(code[0] ? 1 : 0);
        result.append("  ");

        for (int i = 1; i < 32; i++) {
            result.append(code[i] ? "1" : "0");
        }

        return result.toString();
    }

    public void setInDecimal(long inDecimal) {
        this.inDecimal = inDecimal;

        this.inAdditional = toAdditional();
        this.inReverse = toReverse();
        this.inDirect = toDirect();
    }

    public static String convertToString(boolean[] code) {
        return getCode(code);
    }

    public void changeSign() {
        if (inAdditional[0]) {
            return;
        }

        changedSignAdditional = inAdditional.clone();
        changedSignAdditional[0] = true;
        for (int i = 1; i < 32; i++) {
            changedSignAdditional[i] = !changedSignAdditional[i];
        }
        changedSignAdditional = BinaryOperations.createAdditionalCode(changedSignAdditional);
    }
}
