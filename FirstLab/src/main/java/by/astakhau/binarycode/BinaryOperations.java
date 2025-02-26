package by.astakhau.binarycode;

public class BinaryOperations {

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
}
