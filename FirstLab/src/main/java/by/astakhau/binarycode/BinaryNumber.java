package by.astakhau.binarycode;


import lombok.Getter;

public class BinaryNumber {
    private static final int NUMBER_SIZE = 32;
    private static final int EXPONENT_SIZE = 8;
    private static final int FRACTION_SIZE = 23;
    private static final int EXPONENT_BIAS = 127;

    public static String convertDivisionToStringWithOriginal(FloatBinary fb) {
        StringBuilder result = new StringBuilder();
        result.append(convertDivisionToString(fb));
        result.append("\n");

        boolean sign = fb.integerPart[0];

        int integerMagnitude = 0;
        for (int i = 1; i < NUMBER_SIZE - 5; i++) {
            integerMagnitude = (integerMagnitude << 1) | (fb.integerPart[i] ? 1 : 0);
        }

        double fractionalValue = 0.0;
        for (int i = 0; i < 5; i++) {
            if (fb.fractionalPart[i]) {
                fractionalValue += 1.0 / (1 << (i + 1));
            }
        }

        double value = integerMagnitude + fractionalValue;
        return result.append(sign ? -value : value).toString();
    }

    public static String convertDirectToStringWithOriginal(boolean[] code) {
        StringBuilder result = new StringBuilder();
        result.append(getCode(code));
        result.append("\n");

        if (code.length != NUMBER_SIZE) {
            throw new IllegalArgumentException("Массив должен быть длины 32");
        }
        int value = 0;
        for (int i = 1; i < NUMBER_SIZE; i++) {
            if (code[i]) {
                value += (1 << (NUMBER_SIZE - 1 - i));
            }
        }
        result.append(code[0] ? -value : value);
        return result.toString();
    }

    public static class FloatBinary {
        public final boolean[] integerPart;
        public final boolean[] fractionalPart;

        public FloatBinary(boolean[] integerPart, boolean[] fractionalPart) {
            this.integerPart = integerPart;
            this.fractionalPart = fractionalPart;
        }
    }

    private long inDecimal;
    @Getter
    private boolean[] inDirect = new boolean[NUMBER_SIZE];
    @Getter
    private boolean[] inReverse = new boolean[NUMBER_SIZE];
    @Getter
    private boolean[] inAdditional = new boolean[NUMBER_SIZE];
    @Getter
    private boolean[] changedSignAdditional = new boolean[NUMBER_SIZE];

    public BinaryNumber(long inDecimal) {
        this.inDecimal = inDecimal;

        this.inAdditional = toAdditional();
        this.inReverse = toReverse();
        this.inDirect = toDirect();
    }

    public boolean[] toDirect() {
        boolean[] result = new boolean[NUMBER_SIZE];
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

        for (int i = 1; i < NUMBER_SIZE; i++) {
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

    public static String convertAdditionalToStringWithOriginal(boolean[] code) {
        StringBuilder result = new StringBuilder();
        result.append(getCode(code));
        result.append("\n");

        if (code.length != NUMBER_SIZE) {
            throw new IllegalArgumentException("Массив должен быть длины NUMBER_SIZE");
        }
        int value = 0;
        // Если старший бит (бит знака) равен 1, добавляем -2^(31)
        if (code[0]) {
            value = -(1 << NUMBER_SIZE - 1);
        }
        // Добавляем взвешенные значения остальных битов
        for (int i = 1; i < NUMBER_SIZE; i++) {
            if (code[i]) {
                value += (1 << (NUMBER_SIZE - 1 - i));
            }
        }

        result.append(value);

        return result.toString();
    }

    public void changeSign() {
        if (inAdditional[0]) {
            return;
        }

        changedSignAdditional = inAdditional.clone();
        changedSignAdditional[0] = true;
        for (int i = 1; i < NUMBER_SIZE; i++) {
            changedSignAdditional[i] = !changedSignAdditional[i];
        }
        changedSignAdditional = BinaryOperations.createAdditionalCode(changedSignAdditional);
    }

    public static String convertIEEEToStringWithOriginal(boolean[] code) {
        StringBuilder result = new StringBuilder();
        result.append(getCode(code));
        result.append("\n");

        if (code.length != NUMBER_SIZE) {
            throw new IllegalArgumentException("Массив должен быть длины 32");
        }
        boolean sign = code[0];

        int exp = 0;
        for (int i = 1; i <= 8; i++) {
            exp = (exp << 1) | (code[i] ? 1 : 0);
        }

        float fraction = 0.0f;
        float factor = 0.5f;
        for (int i = 9; i < NUMBER_SIZE; i++) {
            if (code[i]) {
                fraction += factor;
            }
            factor /= 2.0f;
        }

        float value;
        if (exp == 0) {
            value = fraction * (float) Math.pow(2, -126);
        } else if (exp == 0xFF) {
            value = Float.NaN;
        } else {
            value = (1.0f + fraction) * (float) Math.pow(2, exp - 127);
        }
        return result.append(sign ? -value : value).toString();
    }

    public static boolean[] floatToIEEE754(float f) {
        boolean[] bits = new boolean[NUMBER_SIZE];

        if (f == 0.0f) {
            return bits;
        }

        boolean sign = (f < 0);
        bits[0] = sign;
        float value = Math.abs(f);

        int exponent = 0;
        while (value >= 2.0f) {
            value /= 2.0f;
            exponent++;
        }
        while (value < 1.0f) {
            value *= 2.0f;
            exponent--;
        }

        float fraction = value - 1.0f;

        int fractionInt = (int) (fraction * (1 << FRACTION_SIZE));

        int exponentField = exponent + EXPONENT_BIAS;

        for (int i = 0; i < EXPONENT_SIZE; i++) {
            bits[i + 1] = ((exponentField >>> (EXPONENT_SIZE - 1 - i)) & 1) == 1;
        }

        for (int i = 0; i < FRACTION_SIZE; i++) {
            bits[i + 1 + EXPONENT_SIZE] = ((fractionInt >>> (FRACTION_SIZE - 1 - i)) & 1) == 1;
        }

        return bits;
    }

    public static String convertDivisionToString(FloatBinary result) {
        StringBuilder sb = new StringBuilder();

        sb.append(result.integerPart[0] ? "1" : "0");
        sb.append("  ");

        for (int i = 1; i < 27; i++) {
            sb.append(result.integerPart[i] ? "1" : "0");
        }

        sb.append(".");

        for (boolean b : result.fractionalPart) {
            sb.append(b ? "1" : "0");
        }

        return sb.toString();
    }
}
