package by.astakhau.matrixaddressaing;

import by.astakhau.matrixaddressaing.matrix.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UI {
    public static void doProgram() {
        Matrix matrix = new Matrix();
        matrix.generateMatrix();

        while (true) {
            System.out.println(matrix);

            System.out.println();
            System.out.println("Выберите операцию:");
            System.out.println("1) Вывести слово по его номеру");
            System.out.println("2) Перегенерировать матрицу");
            System.out.println("3) Ввести слово по его номеру");
            System.out.println("4) Провести логические выражение над словами");
            System.out.println("1) Вывести слово по его номеру");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine();
            int index;

            switch (choice) {
                case 1:
                    System.out.println("какое слово вывести");
                    index = scanner.nextInt();

                    List<Boolean> list;

                    try {
                        list = matrix.getWord(index);
                    } catch (IllegalStateException | IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    printList(list);
                    System.out.println("\n\n\n");
                    break;

                case 2:
                    matrix.generateMatrix();
                    break;

                case 3:
                    System.out.println();
                    System.out.println("Введите слово: ");
                    var str = scanner.nextLine();

                    List<Boolean> word = new ArrayList<>();

                    for (char c : str.toCharArray()) {
                        word.add(c == '1');
                    }

                    System.out.println("Введите его позицию");
                    index = scanner.nextInt();

                    try {
                        matrix.setWord(index, word);
                    } catch (IllegalStateException | IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    break;
                case 4:
                    System.out.println("номер первого слова");
                    index = scanner.nextInt();

                    List<Boolean> firstOperand;


                    try {
                        firstOperand = matrix.getWord(index);
                    } catch (IllegalStateException | IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    System.out.println("\nномер второго слова");
                    index = scanner.nextInt();

                    List<Boolean> secondOperand;

                    try {
                        secondOperand = matrix.getWord(index);
                    } catch (IllegalStateException | IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    System.out.print("Первый операнд: ");
                    printList(firstOperand);

                    System.out.print("\nВторой операнд: ");
                    printList(secondOperand);

                    var logicExp = new LogicalOperation(firstOperand, secondOperand);

                    System.out.print("\nf1: ");

                    var temp = logicExp.firstExpression();
                    printList(temp);

                    System.out.print("\nf3: ");

                    temp = logicExp.secondExpression();
                    printList(temp);

                    System.out.print("\nf12: ");

                    temp = logicExp.thirdExpression();
                    printList(temp);

                    System.out.print("\nf14: ");

                    temp = logicExp.fourthExpression();
                    printList(temp);

                    System.out.println("\n\n\n");
            }
        }

    }

    private static void printList(List<Boolean> temp) {
        for (Boolean bool : temp) {
            System.out.print(bool ? "1 " : "0 ");
        }
    }

}
