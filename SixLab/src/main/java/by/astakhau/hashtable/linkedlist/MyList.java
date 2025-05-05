package by.astakhau.hashtable.linkedlist;

public interface MyList<E> extends Iterable<E> {
    void add(E element);
    void add(int index, E element);
    E get(int index);
    E remove(int index);
    E removeByObject(E object);
    int size();
    boolean isEmpty();
}
