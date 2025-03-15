package by.astakhau.binarycode;

public class BinaryOperations {

    public static BinaryNumber.FloatBinary divide(int dividendNum, int divisorNum) {
        int precision = 5;

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

        boolean[] extendedDivisor = new boolean[32];
        extendedDivisor[0] = false;
        for (int i = 0; i < 31; i++) {
            extendedDivisor[i + 1] = divisMag[i];
        }

        boolean[] remainder = new boolean[32];
        boolean[] quotient = new boolean[31];

        for (int i = 0; i < 31; i++) {
            remainder = leftShiftOneBit(remainder);
            remainder[31] = divdMag[i];

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

        boolean[] resultInteger = new boolean[32];
        resultInteger[0] = resultSign;

        for (int i = 30; i > 4; i--) {
            resultInteger[i - 4] = quotient[i];
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

        int expA = bitsToInt(a, 1, 8);
        int expB = bitsToInt(b, 1, 8);
        int E1 = expA - 127; // действительный порядок
        int E2 = expB - 127;

        int fracA = (1 << 23) | bitsToInt(a, 9, 23);
        int fracB = (1 << 23) | bitsToInt(b, 9, 23);

        int commonExp = Math.max(E1, E2);
        if (E1 < commonExp) {
            fracA = fracA >> (commonExp - E1);
        }
        if (E2 < commonExp) {
            fracB = fracB >> (commonExp - E2);
        }

        int sumMantissa = fracA + fracB;

        if (sumMantissa >= (1 << 24)) {
            sumMantissa = sumMantissa >> 1;
            commonExp++;
        }

        int resultExp = commonExp + 127;
        int resultFraction = sumMantissa & ((1 << 23) - 1);

        boolean[] result = new boolean[32];
        result[0] = false; // знак 0
        for (int i = 0; i < 8; i++) {
            result[i + 1] = ((resultExp >>> (7 - i)) & 1) == 1;
        }
        for (int i = 0; i < 23; i++) {
            result[i + 9] = ((resultFraction >>> (22 - i)) & 1) == 1;
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

        boolean[] result = new boolean[32];
        boolean carry = false;

        for (int i = 31; i >= 0; i--) {
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

        boolean[] result = new boolean[32];
        result[0] = resultSign;
        for (int i = 0; i < 31; i++) {
            result[i + 1] = productMag[i];
        }
        return result;
    }

    private static boolean[] extractMagnitude(boolean[] bits32) {
        boolean[] mag = new boolean[31];
        for (int i = 0; i < 31; i++) {
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
