package by.astakhau.matrixaddressaing;

import by.astakhau.matrixaddressaing.matrix.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UI {

    private static final Scanner scanner = new Scanner(System.in);

    public static void doProgram() {
        Matrix matrix = new Matrix();
        matrix.generateMatrix();

        while (true) {
            System.out.println(matrix);
            displayMenu();

            int choice = readInt("Выберите операцию: ");

            switch (choice) {
                case 1:
                    displayWord(matrix);
                    System.out.println("\n\n\n");
                    break;
                case 2:
                    matrix.generateMatrix();
                    System.out.println("\n\n\n");
                    break;
                case 3:
                    inputWord(matrix);
                    System.out.println("\n\n\n");
                    break;
                case 4:
                    performLogicalOperations(matrix);
                    System.out.println("\n\n\n");
                    break;
                case 5:
                    performFieldAddition(matrix);
                    System.out.println("\n\n\n");
                    break;
                case 6:
                    findNearestWords(matrix);
                    System.out.println("\n\n\n");
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println();
        System.out.println("1) Вывести слово по его номеру");
        System.out.println("2) Перегенерировать матрицу");
        System.out.println("3) Ввести слово по его номеру");
        System.out.println("4) Провести логические выражения над словами");
        System.out.println("5) Сложение полей Aj и Bj в словах Sj, у которых Vj совпадает с заданным V=000-111");
        System.out.println("6) Поиск ближайших сверху/снизу");
    }

    private static void displayWord(Matrix matrix) {
        int index = readInt("Введите номер слова: ");
        try {
            List<Boolean> word = matrix.getWord(index);
            printList(word);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
    }

    private static void inputWord(Matrix matrix) {
        System.out.println("Введите слово (строку из 16 бит, например, 1010101010101010): ");
        String input = scanner.nextLine();
        List<Boolean> word = new ArrayList<>();
        for (char c : input.toCharArray()) {
            word.add(c == '1');
        }

        int index = readInt("Введите его позицию: ");
        try {
            matrix.setWord(index, word);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void performLogicalOperations(Matrix matrix) {
        int index1 = readInt("Введите номер первого слова: ");
        int index2 = readInt("Введите номер второго слова: ");

        try {
            List<Boolean> firstOperand = matrix.getWord(index1);
            List<Boolean> secondOperand = matrix.getWord(index2);

            System.out.print("Первый операнд: ");
            printList(firstOperand);
            System.out.print("\nВторой операнд: ");
            printList(secondOperand);

            LogicalOperation logicExp = new LogicalOperation(firstOperand, secondOperand);

            System.out.print("\nf1: ");
            printList(logicExp.firstExpression());

            System.out.print("\nf3: ");
            printList(logicExp.secondExpression());

            System.out.print("\nf12: ");
            printList(logicExp.thirdExpression());

            System.out.print("\nf14: ");
            printList(logicExp.fourthExpression());

            System.out.println();
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void performFieldAddition(Matrix matrix) {
        System.out.println("Введите 3-битное значение V (например, 101): ");
        String input = scanner.nextLine();
        List<Boolean> V = new ArrayList<>();
        for (char c : input.toCharArray()) {
            V.add(c == '1');
        }

        FieldAddition fieldAddition = new FieldAddition(V, matrix);

        System.out.println("Найденные слова:");
        for (List<Boolean> word : fieldAddition.getFoundWords()) {
            printList(word);
            System.out.println();
        }

        System.out.println("Результат сложения полей Aj и Bj:");
        for (List<Boolean> word : fieldAddition.getResult()) {
            printList(word);
            System.out.println();
        }
    }

    private static void findNearestWords(Matrix matrix) {
        int index = readInt("Введите номер слова: ");
        NearestWordSearch nearestWordSearch = new NearestWordSearch(matrix, index);

        System.out.println("Ближайшее сверху: ");

        var greater = nearestWordSearch.getNearestGreaterWord();
        if (greater == null) {
            System.out.println("Данное слово является максимальным");
        } else {
            printList(nearestWordSearch.getNearestGreaterWord());
        }

        System.out.println("Ближайшее снизу: ");
        var lower = nearestWordSearch.getNearestLowerWord();
        if (lower == null) {
            System.out.println("Данное слово является минимальным");
        } else {
            printList(nearestWordSearch.getNearestLowerWord());
        }
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Пожалуйста, введите целое число: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume the remaining newline
        return value;
    }

    private static void printList(List<Boolean> list) {
        for (Boolean bit : list) {
            System.out.print(bit ? "1 " : "0 ");
        }
        System.out.println();
    }
}
