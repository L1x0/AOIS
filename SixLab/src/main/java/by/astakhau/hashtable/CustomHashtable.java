package by.astakhau.hashtable;

public interface CustomHashtable<E extends String, T extends String> {
    void put(E key, T value);
    T get(E key);
    void remove(E key);
    void update(E key, T newValue);
}
