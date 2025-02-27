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
        BinaryOperations.DivisionResult result = BinaryOperations.divide(10, 2);
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
}
