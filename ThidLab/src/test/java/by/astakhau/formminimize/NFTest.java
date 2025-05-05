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

    @Test
    public void KarnaughTest() {
        String exp = "(a | b) & !c";
        TrueTable table = new TrueTable(exp);
        Forms forms = new Forms(table);

        KarnaughBuilder kb = new KarnaughBuilder(forms.getPDNF(), forms.getPCNF());

        assertEquals("(!c) & (a | b)", kb.getCNF());
        assertEquals("(b & !c) | (a & !c)", kb.getDNF());

        String exp1 = "(a | b) -> (c | !d) | !e";
        TrueTable table1 = new TrueTable(exp1);
        Forms forms1 = new Forms(table1);

        KarnaughBuilder kb1 = new KarnaughBuilder(forms1.getPDNF(), forms1.getPCNF());

        assertEquals("(!a | c | !d | !e) & (!b | c | !d | !e)", kb1.getCNF());
        assertEquals("(!e) | (!d) | (!a & !b) | (c)", kb1.getDNF());
    }

    @Test
    public void GluingCalcTest() {
        String exp = "(a | b) & !c";
        TrueTable table = new TrueTable(exp);
        Forms forms = new Forms(table);

        GluingCNFCalc calc = new GluingCNFCalc(forms.getPCNF());

        assertEquals("((!c) & (a | b))", calc.minimize());

        GluingDNFCalc calc1 = new GluingDNFCalc(forms.getPDNF());

        assertEquals("((a & !c) | (b & !c))", calc1.minimize());
    }
}
