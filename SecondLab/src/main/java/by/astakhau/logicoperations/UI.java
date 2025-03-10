package by.astakhau.logicoperations;

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

        System.out.println("\nЧисловая форма СДНФ:");
        System.out.print(forms.getNumericPDNF());

        System.out.println("\nЧисловая форма СКНФ:");
        System.out.print(forms.getNumericPCNF());

        System.out.println("\nИндексная форма: ");
        System.out.print(forms.getIndexForm());
    }
}
