package by.astakhau.formminimize;

import by.astakhau.formminimize.nfbuild.Forms;
import by.astakhau.formminimize.nfbuild.TrueTable;

import java.util.Scanner;

public class UI {
    private static final Scanner scanner = new Scanner(System.in);

    public static void doProgram() {
        System.out.println("Введите выражение: ");
        String exp = scanner.nextLine();

        TrueTable table = new TrueTable(exp);
        System.out.println(table.toString());

        Forms forms = new Forms(table);
        System.out.println("СДНФ: ");
        System.out.print(forms.getPDNF());

        System.out.println("\nСКНФ: ");
        System.out.print(forms.getPCNF());

        // c ∨ ¬d ∨ ¬e ∨ ¬a¬b
        // (a | b) -> (c | !d) | !e

        System.out.println("\nРезультат стадии склеивания КНФ:");
        System.out.print(new GluingCNF(forms.getPCNF()).minimize());

        System.out.println("\nРезультат стадии склеивания ДНФ:");
        System.out.print(new GluingDNF(forms.getPDNF()).minimize());

        System.out.println("\nРезультат расчётно-табличного КНФ:");
        System.out.print(new GluingCNFCalc(forms.getPCNF()).minimize());

        System.out.println("\nРезультат расчётно-табличного ДНФ:");
        System.out.print(new GluingDNFCalc(forms.getPDNF()).minimize());

        System.out.println("\nРезультат метода Карно:");
        KarnaughBuilder kb = new KarnaughBuilder(forms.getPDNF(), forms.getPCNF());

        System.out.println("\nКарта Карно:");
        kb.printKMap();
        System.out.println("\nКНФ:");
        System.out.print(kb.getCNF());

        System.out.println("\nДНФ:");
        System.out.print(kb.getDNF());

        System.out.println();
    }
}
