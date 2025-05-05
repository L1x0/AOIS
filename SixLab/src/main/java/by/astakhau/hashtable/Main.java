package by.astakhau.hashtable;

public class Main {
    public static void main(String[] args) {
        Table table = new Table();

        table.put("Сара Коннор", "Герой серии фильмов 'Терминатор'");
        table.put("Георгафия", "Глобус");
        table.put("I love", "Java");
        table.put("Альберт Эйнштейн", "Создатель теории относительности");
        table.put("Пифагор", "Древнегреческий математик и философ");
        table.put("пифагор", "Учёный");
        table.put("JavaScript", "Язык программирования для веб-страниц");
        table.put("Привет, мир!", "Классическая первая программа");
        table.put("Deep Blue", "Компьютер, победивший чемпиона мира по шахматам");
        table.put("Синий кит", "Крупнейшее животное на Земле");
        table.put("Шекспир", "Великий английский драматург и поэт");

        System.out.println(table.toString());
    }
}