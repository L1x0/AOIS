package by.astakhau.binarycode;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BinaryNumberTest {

    @Test
    public void toDirectCodeTest() {
        BinaryNumber num = new BinaryNumber(5);

        assertEquals("0  0000000000000000000000000000101", num.getStraightCode());

        num.setInDecimal(-5);
        assertEquals("1  0000000000000000000000000000101", num.getStraightCode());

        num.setInDecimal(127);
        assertEquals("0  0000000000000000000000001111111", num.getStraightCode());

        num.setInDecimal(-127);
        assertEquals("1  0000000000000000000000001111111", num.getStraightCode());

        num.setInDecimal(256);
        assertEquals("0  0000000000000000000000100000000", num.getStraightCode());

        num.setInDecimal(-256);
        assertEquals("1  0000000000000000000000100000000", num.getStraightCode());

        num.setInDecimal(1023);
        assertEquals("0  0000000000000000000001111111111", num.getStraightCode());

        num.setInDecimal(-1023);
        assertEquals("1  0000000000000000000001111111111", num.getStraightCode());

        num.setInDecimal(2048);
        assertEquals("0  0000000000000000000100000000000", num.getStraightCode());

        num.setInDecimal(-2048);
        assertEquals("1  0000000000000000000100000000000", num.getStraightCode());
    }

    @Test
    public void toReverseCodeTest() {
        BinaryNumber num = new BinaryNumber(5);
        assertEquals("0  0000000000000000000000000000101", num.getReverseCode());

        num.setInDecimal(127);
        assertEquals("0  0000000000000000000000001111111", num.getReverseCode());

        num.setInDecimal(256);
        assertEquals("0  0000000000000000000000100000000", num.getReverseCode());

        num.setInDecimal(1023);
        assertEquals("0  0000000000000000000001111111111", num.getReverseCode());

        num.setInDecimal(2048);
        assertEquals("0  0000000000000000000100000000000", num.getReverseCode());

        num.setInDecimal(-5);
        assertEquals("1  1111111111111111111111111111010", num.getReverseCode());

        num.setInDecimal(-127);
        assertEquals("1  1111111111111111111111110000000", num.getReverseCode());

        num.setInDecimal(-256);
        assertEquals("1  1111111111111111111111011111111", num.getReverseCode());

        num.setInDecimal(-1023);
        assertEquals("1  1111111111111111111110000000000", num.getReverseCode());

        num.setInDecimal(-2048);
        assertEquals("1  1111111111111111111011111111111", num.getReverseCode());
    }

    @Test
    public void toAdditionalCodeTest() {
        BinaryNumber num = new BinaryNumber(5);
        assertEquals("0  0000000000000000000000000000101", num.getAdditionalCode());

        num.setInDecimal(127);
        assertEquals("0  0000000000000000000000001111111", num.getAdditionalCode());

        num.setInDecimal(256);
        assertEquals("0  0000000000000000000000100000000", num.getAdditionalCode());

        num.setInDecimal(1023);
        assertEquals("0  0000000000000000000001111111111", num.getAdditionalCode());

        num.setInDecimal(2048);
        assertEquals("0  0000000000000000000100000000000", num.getAdditionalCode());

        num.setInDecimal(-5);
        assertEquals("1  1111111111111111111111111111011", num.getAdditionalCode());

        num.setInDecimal(-127);
        assertEquals("1  1111111111111111111111110000001", num.getAdditionalCode());

        num.setInDecimal(-256);
        assertEquals("1  1111111111111111111111100000000", num.getAdditionalCode());

        num.setInDecimal(-1023);
        assertEquals("1  1111111111111111111110000000001", num.getAdditionalCode());

        num.setInDecimal(-2048);
        assertEquals("1  1111111111111111111100000000000", num.getAdditionalCode());
    }
}
