package by.astakhau.binarycode;


import lombok.Getter;

public class BinaryNumber {

    public static String convertDivisionToStringWithOriginal(FloatBinary fb) {
        StringBuilder result = new StringBuilder();
        result.append(convertDivisionToString(fb));
        result.append("\n");

        boolean sign = fb.integerPart[0];

        int integerMagnitude = 0;
        for (int i = 1; i < 27; i++) {
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

        if (code.length != 32) {
            throw new IllegalArgumentException("Массив должен быть длины 32");
        }
        int value = 0;
        for (int i = 1; i < 32; i++) {
            if (code[i]) {
                value += (1 << (31 - i));
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
    private boolean[] inDirect = new boolean[32];
    @Getter
    private boolean[] inReverse = new boolean[32];
    @Getter
    private boolean[] inAdditional = new boolean[32];
    @Getter
    private boolean[] changedSignAdditional = new boolean[32];

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

    public static String convertAdditionalToStringWithOriginal(boolean[] code) {
        StringBuilder result = new StringBuilder();
        result.append(getCode(code));
        result.append("\n");

        if (code.length != 32) {
            throw new IllegalArgumentException("Массив должен быть длины 32");
        }
        int value = 0;
        // Если старший бит (бит знака) равен 1, добавляем -2^(31)
        if (code[0]) {
            value = -(1 << 31);
        }
        // Добавляем взвешенные значения остальных битов
        for (int i = 1; i < 32; i++) {
            if (code[i]) {
                value += (1 << (31 - i));
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
        for (int i = 1; i < 32; i++) {
            changedSignAdditional[i] = !changedSignAdditional[i];
        }
        changedSignAdditional = BinaryOperations.createAdditionalCode(changedSignAdditional);
    }

    public static String convertIEEEToStringWithOriginal(boolean[] code) {
        StringBuilder result = new StringBuilder();
        result.append(getCode(code));
        result.append("\n");

        if (code.length != 32) {
            throw new IllegalArgumentException("Массив должен быть длины 32");
        }
        // 1. Знак
        boolean sign = code[0];
        // 2. Экспонента (8 бит)
        int exp = 0;
        for (int i = 1; i <= 8; i++) {
            exp = (exp << 1) | (code[i] ? 1 : 0);
        }
        // 3. Дробная часть мантиссы (23 бита)
        float fraction = 0.0f;
        float factor = 0.5f;
        for (int i = 9; i < 32; i++) {
            if (code[i]) {
                fraction += factor;
            }
            factor /= 2.0f;
        }

        float value;
        if (exp == 0) {
            // Денормализованное число
            value = fraction * (float)Math.pow(2, -126);
        } else if (exp == 0xFF) {
            // Специальные случаи (бесконечность, NaN) – не рассматриваем в данном примере
            value = Float.NaN;
        } else {
            // Нормализованное число: значение = (-1)^sign * (1 + fraction) * 2^(exp - 127)
            value = (1.0f + fraction) * (float)Math.pow(2, exp - 127);
        }
        return result.append(sign ? -value : value).toString();
    }

    public static boolean[] floatToIEEE754(float f) {
        boolean[] bits = new boolean[32];
        // Обработка нуля (±0)
        if (f == 0.0f) {
            // Все биты остаются false
            return bits;
        }
        // 1. Определяем знак
        boolean sign = (f < 0);
        bits[0] = sign;
        float v = Math.abs(f);

        // 2. Нормализация: найдем E, чтобы 1 <= v < 2.
        int E = 0;
        while (v >= 2.0f) {
            v /= 2.0f;
            E++;
        }
        while (v < 1.0f) {
            v *= 2.0f;
            E--;
        }
        float fPart = v - 1.0f;
        int fractionInt = (int) (fPart * (1 << 23));
        int exponentField = E + 127;

        for (int i = 0; i < 8; i++) {
            bits[i + 1] = ((exponentField >>> (7 - i)) & 1) == 1;
        }
        for (int i = 0; i < 23; i++) {
            bits[i + 9] = ((fractionInt >>> (22 - i)) & 1) == 1;
        }
        return bits;
    }

    public static String convertDivisionToString(FloatBinary result) {
        StringBuilder sb = new StringBuilder();

        // Знак
        sb.append(result.integerPart[0] ? "1" : "0");
        sb.append("  ");

        // Целая часть (26 бит)
        for (int i = 1; i < 27; i++) {
            sb.append(result.integerPart[i] ? "1" : "0");
        }

        // Точка
        sb.append(".");

        // Дробная часть (5 бит)
        for (boolean b : result.fractionalPart) {
            sb.append(b ? "1" : "0");
        }

        return sb.toString();
    }
}
