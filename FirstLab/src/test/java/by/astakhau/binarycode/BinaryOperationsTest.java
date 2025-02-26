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
}
