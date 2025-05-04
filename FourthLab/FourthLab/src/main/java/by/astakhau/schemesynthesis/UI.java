package by.astakhau.schemesynthesis;

import java.util.Scanner;

public class UI {
    private static final Scanner scanner = new Scanner(System.in);

    public static void doProgram() {
        Adder adder = new Adder();
        Converter converter = new Converter();

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

        System.out.println("\n____________________________________");

        System.out.println("\nCКНФ первой цифры: ");
        System.out.print(reviewOfResult(new GluingCNF(converter.getFirstCNF()).minimize()));

        System.out.println("\nCКНФ второй цифры: ");
        System.out.print(reviewOfResult(new GluingCNF(converter.getSecondCNF()).minimize()));

        System.out.println("\nCКНФ третей цифры: ");
        System.out.print(reviewOfResult(new GluingCNF(converter.getThirdCNF()).minimize()));

        System.out.println("\nCКНФ четвёртой цифры: ");
        System.out.print(reviewOfResult(converter.getFourthCNF()));

        System.out.println();
    }

    private static String reviewOfResult(String result) {
        result = result.replaceAll("a", "x3");
        result = result.replaceAll("b", "x2");
        result = result.replaceAll("c", "x1");
        result = result.replaceAll("d", "x0");

        return result;
    }
}
