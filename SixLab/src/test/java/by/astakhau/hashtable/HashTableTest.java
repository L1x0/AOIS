package by.astakhau.hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HashTableTest {
    private static Table table;

    @BeforeEach
    public void setUp() {
        table = new Table();

        table.put("Сара Коннор", "Герой серии фильмов 'Терминатор'");
        table.put("Георгафия", "Глобус");
        table.put("I love", "Java");
        table.put("Альберт Эйнштейн", "Создатель теории относительности");
        table.put("Пифагор", "Древнегреческий математик и философ");
        table.put("JavaScript", "Язык программирования для веб-страниц");
        table.put("Привет, мир!", "Классическая первая программа");
        table.put("Deep Blue", "Компьютер, победивший чемпиона мира по шахматам");
        table.put("Синий кит", "Крупнейшее животное на Земле");
        table.put("Шекспир", "Великий английский драматург и поэт");
    }

    @Test
    public void tableInfoTest() {
        assertEquals(10, table.size());
        table.remove("Шекспир");
        assertEquals(9, table.size());
    }

    @Test
    public void getTest() {
        assertEquals("Создатель теории относительности", table.get("Альберт Эйнштейн"));
        assertEquals("Язык программирования для веб-страниц", table.get("JavaScript"));
    }

    @Test
    public void putTest() {
        table.put("biba", "boba");
        assertEquals(11, table.size());
        assertEquals("boba", table.get("biba"));
        assertThrowsExactly(IllegalArgumentException.class, () -> table.put("biba", "boba"));
    }

    @Test
    public void removeTest() {
        table.remove("География");
        assertNull(table.get("География"));
    }

    @Test
    public void collisionTest() {
        table.put("пифагор", "Учёный");
        assertEquals(11, table.size());
        assertEquals("Учёный", table.get("пифагор"));
    }

    @Test
    public void updateTest() {
        table.update("Пифагор", "Учёный");

        assertEquals(10, table.size());
        assertEquals("Учёный", table.get("Пифагор"));

        table.update("biba", "boba");
        assertEquals(11, table.size());
        assertEquals("boba", table.get("biba"));
    }
}
