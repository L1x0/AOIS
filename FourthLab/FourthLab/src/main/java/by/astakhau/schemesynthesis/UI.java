package by.astakhau.schemesynthesis;

import java.util.Scanner;

public class UI {
    private static final Scanner scanner = new Scanner(System.in);

    public static void doProgram() {
        Adder adder = new Adder();

        System.out.println("CДНФ переноса: ");
        System.out.print(adder.getP_DNF());

        System.out.println("\nСКНФ переноса: ");
        System.out.print(adder.getP_CNF());

        System.out.println("\nCДНФ суммы: ");
        System.out.print(adder.getS_DNF());

        System.out.println("\nСКНФ суммы: ");
        System.out.print(adder.getS_CNF());

        System.out.println("\nРезультат стадии склеивания ДНФ переноса:");
        System.out.print(new GluingDNF(adder.getP_DNF()).minimize());

        System.out.println("\nРезультат стадии склеивания ДНФ суммы:");
        System.out.print(new GluingDNF(adder.getS_DNF()).minimize());

        System.out.println();
    }
}
