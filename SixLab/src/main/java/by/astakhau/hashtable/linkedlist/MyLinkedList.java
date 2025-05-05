package by.astakhau.hashtable.linkedlist;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinkedList<E> implements MyList<E> {
    private static class Node<E> {
        E data;
        Node<E> prev;
        Node<E> next;

        Node(Node<E> prev, E data, Node<E> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size = 0;

    @Override
    public void add(E element) {
        linkLast(element);
    }

    @Override
    public void add(int index, E element) {
        checkPositionIndex(index);
        if (index == size) {
            linkLast(element);
        } else {
            linkBefore(element, node(index));
        }
    }

    @Override
    public E get(int index) {
        checkElementIndex(index);
        return node(index).data;
    }

    @Override
    public E remove(int index) {
        checkElementIndex(index);
        return unlink(node(index));
    }

    @Override
    public E removeByObject(E element) {
        int index = 0;

        for (E e : this) {
            if (e.equals(element)) {
                break;
            }
            index++;
        }

        checkElementIndex(index);
        return unlink(node(index));
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private void linkLast(E element) {
        final Node<E> l = tail;
        final Node<E> newNode = new Node<>(l, element, null);
        tail = newNode;
        if (l == null)
            head = newNode;
        else
            l.next = newNode;
        size++;
    }

    private void linkBefore(E element, Node<E> succ) {
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, element, succ);
        succ.prev = newNode;
        if (pred == null)
            head = newNode;
        else
            pred.next = newNode;
        size++;
    }

    private E unlink(Node<E> x) {
        final E element = x.data;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.data = null;
        size--;
        return element;
    }


    private Node<E> node(int index) {
        if (index < (size >> 1)) {
            Node<E> x = head;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = tail;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    private void checkElementIndex(int index) {
        if (!(index >= 0 && index < size))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private void checkPositionIndex(int index) {
        if (!(index >= 0 && index <= size))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> next = head;
            private int expectedModCount = size;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public E next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                E data = next.data;
                next = next.next;
                return data;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Iterator<E> iterator = this.iterator();

        while (iterator.hasNext()) {
            builder.append(iterator.next().toString());
        }

        return builder.toString();
    }
}
