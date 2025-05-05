package by.astakhau.hashtable.linkedlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ListTest {
    private static MyList<String> list;

    @BeforeEach
    public void setUp() {
        list = new MyLinkedList<>();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
    }

    @Test
    public void simpleTest() {
        assertEquals("ABCD", list.toString());
    }

    @Test
    public void addTest() {
        list.add("1");

        assertEquals("ABCD1", list.toString());
    }

    @Test
    public void addOnIndexTest() {
        list.add(2,"1");

        assertEquals("AB1CD", list.toString());
    }

    @Test
    public void removeTest() {
        list.remove(1);

        assertEquals("ACD", list.toString());
    }

    @Test
    public void removeByObjectTest() {
        list.removeByObject("B");

        assertEquals("ACD", list.toString());
    }

    @Test
    public void getTest() {
        assertEquals("C", list.get(2));
    }

    @Test
    public void listAnalysisTest() {
        assertEquals(4, list.size());
        assertFalse(list.isEmpty());
    }
}
