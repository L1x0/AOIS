package by.astakhau.binarycode;

import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.Scanner;

@NoArgsConstructor
public class UI {
    private static final Scanner scanner = new Scanner(System.in);


    public static void doProgram() {
        while (true) {
            System.out.println("\nВыберите операцию:");
            System.out.println("1. Перевод числа в двоичные коды");
            System.out.println("2. Сложение в дополнительном коде");
            System.out.println("3. Вычитание в дополнительном коде");
            System.out.println("4. Умножение в прямом коде");
            System.out.println("5. Деление в прямом коде");
            System.out.println("6. Сложение чисел с плавающей точкой");
            System.out.println("0. Выход");

            int choice = scanner.nextInt();
            if (choice == 0) break;

            switch (choice) {
                case 1:
                    showBinaryCodes();
                    break;
                case 2:
                    performAddition();
                    break;
                case 3:
                    performSubtraction();
                    break;
                case 4:
                    performMultiplication();
                    break;
                case 5:
                    performDivision();
                    break;
                case 6:
                    performFloatAddition();
                    break;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
        scanner.close();
    }

    private static void showBinaryCodes() {
        clearConsole();
        System.out.print("Введите число: ");
        int number = scanner.nextInt();
        BinaryNumber bn = new BinaryNumber(number);
        
        System.out.println("Прямой код:         " + bn.getStraightCode());
        System.out.println("Обратный код:       " + bn.getReverseCode());
        System.out.println("Дополнительный код: " + bn.getAdditionalCode());
    }

    private static void performAddition() {
        clearConsole();
        System.out.print("Введите первое число: ");
        int a = scanner.nextInt();
        System.out.print("Введите второе число: ");
        int b = scanner.nextInt();

        boolean[] result = BinaryOperations.sum(a, b);
        System.out.println("Результат: " + 
            BinaryNumber.convertAdditionalToStringWithOriginal(result));
    }

    private static void performSubtraction() {
        clearConsole();
        System.out.print("Введите уменьшаемое: ");
        int a = scanner.nextInt();
        System.out.print("Введите вычитаемое: ");
        int b = scanner.nextInt();

        boolean[] result = BinaryOperations.subtraction(a, b);
        System.out.println("Результат: " + 
            BinaryNumber.convertAdditionalToStringWithOriginal(result));
    }

    private static void performMultiplication() {
        clearConsole();
        System.out.print("Введите первый множитель: ");
        int a = scanner.nextInt();
        System.out.print("Введите второй множитель: ");
        int b = scanner.nextInt();

        boolean[] result = BinaryOperations.multiplyDirect(a, b);
        System.out.println("Результат: " + 
            BinaryNumber.convertDirectToStringWithOriginal(result));
    }

    private static void performDivision() {
        clearConsole();
        System.out.print("Введите делимое: ");
        int a = scanner.nextInt();
        System.out.print("Введите делитель: ");
        int b = scanner.nextInt();

        try {
            BinaryNumber.FloatBinary result = BinaryOperations.divide(a, b);
            System.out.println("Результат: " + 
                BinaryNumber.convertDivisionToStringWithOriginal(result));
        } catch (ArithmeticException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void performFloatAddition() {
        clearConsole();
        scanner.useLocale(Locale.US);
        System.out.print("Введите первое число (Через точку!!!): ");
        float a = scanner.nextFloat();
        System.out.print("Введите второе число (Через точку!!!): ");
        float b = scanner.nextFloat();

        boolean[] result = BinaryOperations.addIEEE754(a, b);
        System.out.println("Результат: " +
                BinaryNumber.convertIEEEToStringWithOriginal(result));
    }

    private static void clearConsole() {
        System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.flush();
    }
}
