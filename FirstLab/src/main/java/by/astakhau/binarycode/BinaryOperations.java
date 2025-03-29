package by.astakhau.binarycode;

public class BinaryOperations {
    // Константы для размеров массивов
    private static final int BIT_LENGTH = 32;
    private static final int BIT_LENGTH_WITHOUT_SIGN = 31;
    
    // Константы для деления
    private static final int DEFAULT_PRECISION = 5;
    private static final int SHIFT_INDEX = 4;
    
    // Константы для IEEE 754
    private static final int IEEE_BIAS = 127;
    private static final int IEEE_EXPONENT_BITS = 8;
    private static final int IEEE_MANTISSA_BITS = 23;
    private static final int IEEE_MANTISSA_START_INDEX = 9;
    private static final int IEEE_MANTISSA_OVERFLOW = 1 << 24;
    private static final int IEEE_MANTISSA_MASK = (1 << IEEE_MANTISSA_BITS) - 1;
    private static final int IEEE_MANTISSA_IMPLICIT_BIT = 1 << IEEE_MANTISSA_BITS;
    private static final int IEEE_MANTISSA_LAST_BIT_INDEX = 22;

    public static BinaryNumber.FloatBinary divide(int dividendNum, int divisorNum) {
        int precision = DEFAULT_PRECISION;

        if (divisorNum == 0) {
            throw new ArithmeticException("Division by zero");
        }

        boolean[] dividend = new BinaryNumber(dividendNum).getInDirect();
        boolean[] divisor = new BinaryNumber(divisorNum).getInDirect();

        boolean signDividend = dividend[0];
        boolean signDivisor = divisor[0];
        boolean resultSign = signDividend ^ signDivisor;

        boolean[] divdMag = extractMagnitude(dividend);
        boolean[] divisMag = extractMagnitude(divisor);

        boolean[] extendedDivisor = new boolean[BIT_LENGTH];
        extendedDivisor[0] = false;
        for (int i = 0; i < BIT_LENGTH_WITHOUT_SIGN; i++) {
            extendedDivisor[i + 1] = divisMag[i];
        }

        boolean[] remainder = new boolean[BIT_LENGTH];
        boolean[] quotient = new boolean[BIT_LENGTH_WITHOUT_SIGN];

        for (int i = 0; i < BIT_LENGTH_WITHOUT_SIGN; i++) {
            remainder = leftShiftOneBit(remainder);
            remainder[BIT_LENGTH - 1] = divdMag[i];

            if (compare(remainder, extendedDivisor) >= 0) {
                remainder = subtract(remainder, extendedDivisor);
                quotient[i] = true;
            } else {
                quotient[i] = false;
            }
        }
        boolean[] fraction = new boolean[precision];

        for (int i = 0; i < precision; i++) {
            remainder = leftShiftOneBit(remainder);
            if (compare(remainder, extendedDivisor) >= 0) {
                remainder = subtract(remainder, extendedDivisor);
                fraction[i] = true;
            } else {
                fraction[i] = false;
            }
        }

        boolean[] resultInteger = new boolean[BIT_LENGTH];
        resultInteger[0] = resultSign;

        for (int i = BIT_LENGTH_WITHOUT_SIGN - 1; i > SHIFT_INDEX; i--) {
            resultInteger[i - SHIFT_INDEX] = quotient[i];
        }

        return new BinaryNumber.FloatBinary(resultInteger, fraction);
    }

    private static boolean[] leftShiftOneBit(boolean[] bits) {
        boolean[] shifted = new boolean[bits.length];
        for (int i = 0; i < bits.length - 1; i++) {
            shifted[i] = bits[i + 1];
        }
        shifted[bits.length - 1] = false;
        return shifted;
    }

    private static boolean[] subtract(boolean[] A, boolean[] B) {
        int len = A.length;
        boolean[] diff = new boolean[len];
        boolean borrow = false;
        for (int i = len - 1; i >= 0; i--) {
            // Представляем биты как 0 или 1:
            int aVal = A[i] ? 1 : 0;
            int bVal = B[i] ? 1 : 0;
            int sub = aVal - bVal - (borrow ? 1 : 0);
            if (sub < 0) {
                sub += 2;
                borrow = true;
            } else {
                borrow = false;
            }
            diff[i] = (sub == 1);
        }
        return diff;
    }

    private static int compare(boolean[] A, boolean[] B) {
        int len = A.length;
        for (int i = 0; i < len; i++) {
            if (A[i] != B[i]) {
                return A[i] ? 1 : -1;
            }
        }
        return 0;
    }


    public static boolean[] sum(int a, int b) {
        BinaryNumber aBin = new BinaryNumber(a);
        BinaryNumber bBin = new BinaryNumber(b);

        var A = aBin.getInAdditional();
        var B = bBin.getInAdditional();

        boolean[] result = new boolean[32];
        boolean carry = false;

        for (int i = 31; i >= 0; i--) {
            boolean sumBit = (A[i] ^ B[i]) ^ carry;
            result[i] = sumBit;
            carry = (A[i] && B[i]) || (A[i] && carry) || (B[i] && carry);
        }

        return result;
    }

    public static boolean[] addIEEE754(float aNum, float bNum) {
        boolean[] a = BinaryNumber.floatToIEEE754(aNum);
        boolean[] b = BinaryNumber.floatToIEEE754(bNum);

        int expA = bitsToInt(a, 1, IEEE_EXPONENT_BITS);
        int expB = bitsToInt(b, 1, IEEE_EXPONENT_BITS);
        int E1 = expA - IEEE_BIAS; // действительный порядок
        int E2 = expB - IEEE_BIAS;

        int fracA = IEEE_MANTISSA_IMPLICIT_BIT | bitsToInt(a, IEEE_MANTISSA_START_INDEX, IEEE_MANTISSA_BITS);
        int fracB = IEEE_MANTISSA_IMPLICIT_BIT | bitsToInt(b, IEEE_MANTISSA_START_INDEX, IEEE_MANTISSA_BITS);

        int commonExp = Math.max(E1, E2);
        if (E1 < commonExp) {
            fracA = fracA >> (commonExp - E1);
        }
        if (E2 < commonExp) {
            fracB = fracB >> (commonExp - E2);
        }

        int sumMantissa = fracA + fracB;

        if (sumMantissa >= IEEE_MANTISSA_OVERFLOW) {
            sumMantissa = sumMantissa >> 1;
            commonExp++;
        }

        int resultExp = commonExp + IEEE_BIAS;
        int resultFraction = sumMantissa & IEEE_MANTISSA_MASK;

        boolean[] result = new boolean[BIT_LENGTH];
        result[0] = false; // знак 0
        for (int i = 0; i < IEEE_EXPONENT_BITS; i++) {
            result[i + 1] = ((resultExp >>> (IEEE_EXPONENT_BITS - 1 - i)) & 1) == 1;
        }
        for (int i = 0; i < IEEE_MANTISSA_BITS; i++) {
            result[i + IEEE_MANTISSA_START_INDEX] = ((resultFraction >>> (IEEE_MANTISSA_LAST_BIT_INDEX - i)) & 1) == 1;
        }
        return result;
    }

    private static int bitsToInt(boolean[] bits, int start, int len) {
        int value = 0;
        for (int i = start; i < start + len; i++) {
            value = (value << 1) | (bits[i] ? 1 : 0);
        }
        return value;
    }

    public static boolean[] sum(boolean[] A, boolean[] B) {
        boolean[] result = new boolean[BIT_LENGTH];
        boolean carry = false;

        for (int i = BIT_LENGTH - 1; i >= 0; i--) {
            boolean sumBit = (A[i] ^ B[i]) ^ carry;
            result[i] = sumBit;
            carry = (A[i] && B[i]) || (A[i] && carry) || (B[i] && carry);
        }

        return result;
    }

    public static boolean[] createAdditionalCode(boolean[] code) {

        boolean[] unit = new boolean[32];
        unit[31] = true;

        boolean[] result = new boolean[32];
        boolean carry = false;

        for (int i = 31; i >= 0; i--) {
            boolean sumBit = (code[i] ^ unit[i]) ^ carry;
            result[i] = sumBit;
            carry = (code[i] && unit[i]) || (code[i] && carry) || (unit[i] && carry);
        }

        return result;
    }

    public static boolean[] subtraction(int minuend, int subtrahend) {
        BinaryNumber minuendBin = new BinaryNumber(minuend);
        BinaryNumber subtrahendBin = new BinaryNumber(subtrahend);
        subtrahendBin.changeSign();

        return sum(minuendBin.getInAdditional(), subtrahendBin.getChangedSignAdditional());
    }

    public static boolean[] multiplyDirect(int a, int b) {
        BinaryNumber aBin = new BinaryNumber(a);
        BinaryNumber bBin = new BinaryNumber(b);


        boolean resultSign = aBin.getInDirect()[0] ^ bBin.getInDirect()[0];

        boolean[] magA = extractMagnitude(aBin.getInDirect());
        boolean[] magB = extractMagnitude(bBin.getInDirect());

        boolean[] productMag = multiplyMagnitudes(magA, magB);

        boolean[] result = new boolean[BIT_LENGTH];
        result[0] = resultSign;
        for (int i = 0; i < BIT_LENGTH_WITHOUT_SIGN; i++) {
            result[i + 1] = productMag[i];
        }
        return result;
    }

    private static boolean[] extractMagnitude(boolean[] bits32) {
        boolean[] mag = new boolean[BIT_LENGTH_WITHOUT_SIGN];
        for (int i = 0; i < BIT_LENGTH_WITHOUT_SIGN; i++) {
            mag[i] = bits32[i + 1];
        }
        return mag;
    }

    private static boolean[] multiplyMagnitudes(boolean[] X, boolean[] Y) {
        boolean[] result = new boolean[31];

        for (int i = 30; i >= 0; i--) {
            int shift = 30 - i;
            if (Y[i]) {
                boolean[] temp = leftShift(X, shift);
                result = addMagnitudes(result, temp);
            }
        }
        return result;
    }

    private static boolean[] addMagnitudes(boolean[] X, boolean[] Y) {
        boolean[] sum = new boolean[31];
        boolean carry = false;

        for (int i = 30; i >= 0; i--) {
            boolean bitSum = (X[i] ^ Y[i]) ^ carry;
            sum[i] = bitSum;
            carry = (X[i] && Y[i]) || (X[i] && carry) || (Y[i] && carry);
        }
        return sum;
    }

    private static boolean[] leftShift(boolean[] bits, int shift) {
        boolean[] shifted = new boolean[31];
        for (int i = 0; i < 31; i++) {
            int j = i + shift;
            shifted[i] = (j < 31) ? bits[j] : false;
        }
        return shifted;
    }

}
