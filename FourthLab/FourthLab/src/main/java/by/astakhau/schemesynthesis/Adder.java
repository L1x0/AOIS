package by.astakhau.schemesynthesis;

import by.astakhau.schemesynthesis.nfbuild.Forms;
import by.astakhau.schemesynthesis.nfbuild.TrueTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Adder {
    private TrueTable customTable;

    public Adder() {
        init();
    }

    private void init() {
        customTable = new TrueTable();

        ArrayList<ArrayList<String>> table = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            table.add(new ArrayList<>());
        }

        table.get(0).addAll(Arrays.asList("a", "b", "c", "p", "s"));
        table.get(1).addAll(Arrays.asList("0", "0", "0", "0", "0"));
        table.get(2).addAll(Arrays.asList("0", "0", "1", "0", "1"));
        table.get(3).addAll(Arrays.asList("0", "1", "0", "0", "1"));
        table.get(4).addAll(Arrays.asList("0", "1", "1", "1", "0"));
        table.get(5).addAll(Arrays.asList("1", "0", "0", "0", "1"));
        table.get(6).addAll(Arrays.asList("1", "0", "1", "1", "0"));
        table.get(7).addAll(Arrays.asList("1", "1", "0", "1", "0"));
        table.get(8).addAll(Arrays.asList("1", "1", "1", "1", "1"));

        customTable.setTable(table);
        customTable.setVariables(List.of("a", "b", "c"));
    }

    public String getP_DNF() {
        Forms forms = new Forms(customTable);

        return forms.createPDNF(2);
    }

    public String getS_DNF() {
        Forms forms = new Forms(customTable);

        return forms.createPDNF(1);
    }

    public String getP_CNF() {
        Forms forms = new Forms(customTable);

        return forms.createPCNF(2);
    }

    public String getS_CNF() {
        Forms forms = new Forms(customTable);

        return forms.createPCNF(1);
    }
}
