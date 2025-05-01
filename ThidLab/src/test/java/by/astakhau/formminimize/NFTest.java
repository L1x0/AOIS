package by.astakhau.formminimize;

import by.astakhau.formminimize.nfbuild.Forms;
import by.astakhau.formminimize.nfbuild.TrueTable;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NFTest {
    @Test
    public void CNFTest() {
        String exp = "(a | b) & !c";
        TrueTable table = new TrueTable(exp);
        Forms forms = new Forms(table);

        assertEquals("((!c) & (a | b))", new GluingCNFCalc(forms.getPCNF()).minimize());

        String exp1 = "!(a->b) | (c & d) & e";
        TrueTable table1 = new TrueTable(exp1);
        Forms forms1 = new Forms(table1);

        assertEquals("((!b | e) & (!b | d) & (!b | c) & (a | e) & (a | d) & (a | c))", new GluingCNF(forms1.getPCNF()).minimize());
    }

    @Test
    public void DNFTest() {
        String exp = "(a | b) & !c";
        TrueTable table = new TrueTable(exp);
        Forms forms = new Forms(table);

        assertEquals("((b & !c) | (a & !c))", new GluingDNF(forms.getPDNF()).minimize());

        String exp1 = "!(a->b) | (c & d) & e";
        TrueTable table1 = new TrueTable(exp1);
        Forms forms1 = new Forms(table1);

        assertEquals("((c & d & e) | (a & !b))", new GluingDNF(forms1.getPDNF()).minimize());
    }
}
