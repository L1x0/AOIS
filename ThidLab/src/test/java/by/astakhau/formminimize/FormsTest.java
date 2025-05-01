package by.astakhau.formminimize;

import by.astakhau.formminimize.nfbuild.Forms;
import by.astakhau.formminimize.nfbuild.TrueTable;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FormsTest {
    @Test
    public void PDNFTest() {
        String exp = "(a | b) & !c";
        TrueTable table = new TrueTable(exp);
        Forms forms = new Forms(table);

        assertEquals("(!a & b & !c ) | (a & !b & !c ) | (a & b & !c ) ", forms.getPDNF());
    }

    @Test
    public void PCNFTest() {
        String exp = "(a | b) & !c";
        TrueTable table = new TrueTable(exp);
        Forms forms = new Forms(table);

        assertEquals("(a | b | c ) & (a | b | !c ) & (a | !b | !c ) & (!a | b | !c ) & (!a | !b | !c ) ", forms.getPCNF());
    }

    @Test
    public void numericPDNFTest() {
        String exp = "(a | b) & !c";
        TrueTable table = new TrueTable(exp);
        Forms forms = new Forms(table);

        assertEquals("(0, 1, 3, 5, 7) |", forms.getNumericPDNF());
    }

    @Test
    public void numericPCNFTest() {
        String exp = "(a | b) & !c";
        TrueTable table = new TrueTable(exp);
        Forms forms = new Forms(table);

        assertEquals("(2, 4, 6) &", forms.getNumericPCNF());
    }

//    @Test
//    public void indexFormTest() {
//        String exp = "(a | b) & !c";
//        TrueTable table = new TrueTable(exp);
//        Forms forms = new Forms(table);
//
//        assertEquals("42 - 00101010", forms.getIndexForm());
//    }
}
