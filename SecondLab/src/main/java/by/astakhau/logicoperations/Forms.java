package by.astakhau.logicoperations;

import java.util.ArrayList;

public class Forms {
    private TrueTable tableObj;
    private final ArrayList<ArrayList<String>> table;
    private final String PDNF;
    private final String PCNF;
    private final String numericPDNF;
    private final String numericPCNF;
    private final String indexForm;
    private final int varCount;


    public Forms(TrueTable tableObj) {
        this.tableObj = tableObj;
        this.table = tableObj.getTable();
        varCount = tableObj.getVariables().size();

        PDNF = createPDNF();
        PCNF = createPCNF();
        numericPCNF = createNumericPCNF();
        numericPDNF = createNumericPDNF();
        indexForm = createIndexForm();
    }

    private String createPDNF() {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < table.size(); i++) {
            if (table.get(i).get(table.get(i).size() - 1).equals("1")) {
                sb.append(sb.isEmpty() ? "(" : "| (");

                for (int j = 0; j < varCount; j++) {
                    if (table.get(i).get(j).equals("1")) {
                        sb.append(table.get(0).get(j));
                    } else {
                        sb.append("!").append(table.get(0).get(j));
                    }

                    sb.append(" ");

                    if (j + 1 < varCount) {
                        sb.append("& ");
                    }
                }
                sb.append(") ");
            }
        }

        return sb.toString();
    }

    private String createPCNF() {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < table.size(); i++) {
            if (table.get(i).get(table.get(i).size() - 1).equals("0")) {
                sb.append(sb.isEmpty() ? "(" : "& (");

                for (int j = 0; j < varCount; j++) {
                    if (table.get(i).get(j).equals("0")) {
                        sb.append(table.get(0).get(j));
                    } else {
                        sb.append("!").append(table.get(0).get(j));
                    }

                    sb.append(" ");

                    if (j + 1 < varCount) {
                        sb.append("| ");
                    }
                }
                sb.append(") ");
            }
        }

        return sb.toString();
    }

    private String createNumericPCNF() {
        StringBuilder sb = new StringBuilder();

        sb.append("(");
        for (int i = 1; i < table.size(); i++) {
            if (table.get(i).get(table.get(i).size() - 1).equals("1")) {
                sb.append(i - 1);
                sb.append(i + 2 < table.size() ? ", " : ") &");
            }
        }

        return sb.toString();
    }

    private String createNumericPDNF() {
        StringBuilder sb = new StringBuilder();

        sb.append("(");
        for (int i = 1; i < table.size(); i++) {
            if (table.get(i).get(table.get(i).size() - 1).equals("0")) {
                sb.append(i - 1);
                sb.append(i + 2 < table.size() ? ", " : ") |");
            }
        }

        return sb.toString();
    }

    private String createIndexForm() {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < table.size(); i++) {
            sb.append(table.get(i).get(table.get(i).size() - 1));
        }

        String num = String.valueOf(Integer.parseInt(sb.toString(), 2));
        return num + " - "+ sb.toString();
    }


    public String getPDNF() {
        return PDNF;
    }

    public String getPCNF() {
        return PCNF;
    }

    public String getNumericPDNF() {
        return numericPDNF;
    }

    public String getNumericPCNF() {
        return numericPCNF;
    }

    public String getIndexForm() {
        return indexForm;
    }
}
