package by.astakhau.binarycode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BinaryOperationsTest {

    @Test
    public void sumTest() {
        assertEquals(
                "0  0000000000000000000000000000111",
                BinaryNumber.convertToString(BinaryOperations.sum(2, 5))
        );

        assertEquals(
                "1  1111111111111111111111111111110",
                BinaryNumber.convertToString(BinaryOperations.sum(-5, 3))
        );

        assertEquals(
                "1  1111111111111111111111111101111",
                BinaryNumber.convertToString(BinaryOperations.sum(-7, -10))
        );

        assertEquals(
                "0  0000000000000000000101111111111",
                BinaryNumber.convertToString(BinaryOperations.sum(1023, 2048))
        );

        assertEquals(
                "1  1111111111111111111010000000001",
                BinaryNumber.convertToString(BinaryOperations.sum(-2048, -1023))
        );
    }

    @Test
    public void subtractionTest() {

        assertEquals(
                "1  1111111111111111111111111111110",
                BinaryNumber.convertToString(BinaryOperations.subtraction(3, 5))
        );

        assertEquals(
                "1  1111111111111111111111111101111",
                BinaryNumber.convertToString(BinaryOperations.subtraction(-7, 10))
        );

        assertEquals(
                "1  1111111111111111111010000000001",
                BinaryNumber.convertToString(BinaryOperations.subtraction(-2048, 1023))
        );
    }

    @Test
    public void multiplicationTest() {
        assertEquals(
                "0  0000000000000000000000000001010",
                BinaryNumber.convertToString(BinaryOperations.multiplyDirect(2, 5))
        );

        assertEquals(
                "1  0000000000000000000000000001100",
                BinaryNumber.convertToString(BinaryOperations.multiplyDirect(-3, 4))
        );

        assertEquals(
                "0  0000000000000000000000001000110",
                BinaryNumber.convertToString(BinaryOperations.multiplyDirect(-7, -10))
        );

        assertEquals(
                "0  0000000000000000000000011100001",
                BinaryNumber.convertToString(BinaryOperations.multiplyDirect(15, 15))
        );

        assertEquals(
                "0  0000000000000000000011111111110",
                BinaryNumber.convertToString(BinaryOperations.multiplyDirect(1023, 2))
        );
    }

    @Test
    public void divisionTest() {
        // Целочисленное деление
        BinaryNumber.FloatBinary result = BinaryOperations.divide(10, 2);
        assertEquals(
            "0  00000000000000000000000101.00000",
            BinaryNumber.convertDivisionToString(result)
        );

        // Деление с дробной частью
        result = BinaryOperations.divide(5, 2);
        assertEquals(
            "0  00000000000000000000000010.10000",
            BinaryNumber.convertDivisionToString(result)
        );

        // Отрицательные числа
        result = BinaryOperations.divide(-10, 3);
        assertEquals(
            "1  00000000000000000000000011.01010",
            BinaryNumber.convertDivisionToString(result)
        );

        // Деление на отрицательное число
        result = BinaryOperations.divide(7, -2);
        assertEquals(
            "1  00000000000000000000000011.10000",
            BinaryNumber.convertDivisionToString(result)
        );

        // Периодическая дробь
        result = BinaryOperations.divide(1, 3);
        assertEquals(
            "0  00000000000000000000000000.01010",
            BinaryNumber.convertDivisionToString(result)
        );

        // Большие числа
        result = BinaryOperations.divide(1023, 2);
        assertEquals(
            "0  00000000000000000111111111.10000",
            BinaryNumber.convertDivisionToString(result)
        );

        // Проверка на деление на ноль
        assertThrows(ArithmeticException.class, () -> {
            BinaryOperations.divide(5, 0);
        });
    }
    
    @Test
    public void IEEE_test() {
        boolean[] sumIEEE = BinaryOperations.addIEEE754(1.5f, 2.25f);
        String expected1 = "0  1000000011100000000000000000000";
        String actual1 = BinaryNumber.convertToString(sumIEEE);
        assertEquals(expected1, actual1, "Test 1 (1.5 + 2.25) failed");

        // Тест 2: 2.0 + 3.0 = 5.0
        // 5.0 = 1.25 * 2^2, поэтому:
        //   знак: 0
        //   экспонента: 10000001 (2+127 = 129 → 10000001)
        //   мантисса: 01000000000000000000000 (0.25 = 0.010 в двоичном виде)
        sumIEEE = BinaryOperations.addIEEE754(2.0f, 3.0f);
        String expected2 = "0  1000000101000000000000000000000";
        String actual2 = BinaryNumber.convertToString(sumIEEE);
        assertEquals(expected2, actual2, "Test 2 (2.0 + 3.0) failed");

        // Тест 3: 0.75 + 0.125 = 0.875
        // 0.875 = 1.75/2, нормализованное представление: 1.75 = 1.75, но для дробного числа:
        // Ожидаем: знак: 0, экспонента: 01111111 (127), мантисса: 11000000000000000000000 
        // (1.75 - 1 = 0.75, 0.75 * 2^23 примерно даёт 0.11... → 110...0)
        sumIEEE = BinaryOperations.addIEEE754(0.75f, 0.125f);
        String expected3 = "0  0111111011000000000000000000000";
        String actual3 = BinaryNumber.convertToString(sumIEEE);
        assertEquals(expected3, actual3, "Test 3 (0.75 + 0.125) failed");
    }

    @Test
    public void interfaceTest() {
        assertEquals(
                "0  0000000000000000000000000001010\n10",
                BinaryNumber.convertDirectToStringWithOriginal(BinaryOperations.multiplyDirect(2, 5))
        );

        boolean[] sumIEEE = BinaryOperations.addIEEE754(1.5f, 2.25f);
        String expected1 = "0  1000000011100000000000000000000\n3.75";
        String actual1 = BinaryNumber.convertIEEEToStringWithOriginal(sumIEEE);
        assertEquals(expected1, actual1);

        BinaryNumber.FloatBinary result = BinaryOperations.divide(10, 2);
        assertEquals(
                "0  00000000000000000000000101.00000\n5.0",
                BinaryNumber.convertDivisionToStringWithOriginal(result)
        );

        assertEquals(
                "1  1111111111111111111111111111110\n-2",
                BinaryNumber.convertAdditionalToStringWithOriginal(BinaryOperations.subtraction(3, 5))
        );

        assertEquals(
                "0  0000000000000000000000000000111\n7",
                BinaryNumber.convertAdditionalToStringWithOriginal(BinaryOperations.sum(2, 5))
        );

        result = BinaryOperations.divide(7, -2);

        assertEquals(
                "1  00000000000000000000000011.10000\n-3.5",
                BinaryNumber.convertDivisionToStringWithOriginal(result)
        );
    }
}
