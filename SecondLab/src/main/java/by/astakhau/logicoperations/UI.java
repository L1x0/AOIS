package by.astakhau.logicoperations;

import java.util.Scanner;

public class UI {
    private static final Scanner scanner = new Scanner(System.in);

    public static void doProgram() {
        System.out.println("Введите выражение: ");
        String exp = scanner.nextLine();

        TrueTable table = new TrueTable(exp);
        System.out.println(table.toString());
    }
}
