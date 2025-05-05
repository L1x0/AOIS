package by.astakhau.hashtable;

import by.astakhau.hashtable.linkedlist.MyLinkedList;
import by.astakhau.hashtable.linkedlist.MyList;

import java.util.ArrayList;
import java.util.List;

public class Table implements MyHashtable<String, String> {
    private List<Record> records;

    private static final int H = 20;
    private static final int B = 0;

    public Table() {
        records = new ArrayList<>(20);
    }

    private int hash(String key) {
        int v = getCode(key);
        int mod = Math.floorMod(v, H);
        return mod + B;
    }

    @Override
    public void put(String key, String value) {
        int hash = hash(key);

        if (hash >= records.size()) {
            while (records.size() <= hash) {
                records.add(new Record());
            }
        }

        records.get(hash).put(key, value);
    }

    @Override
    public String get(String key) {
        int hash = hash(key);

        if (records.size() <= hash) {
            return null;
        }


        return records.get(hash).get(key);
    }

    @Override
    public void remove(String key) {
        int hash = hash(key);

        if (records.size() <= hash) {
            return;
        }


        records.get(hash).remove(key);
    }

    @Override
    public void update(String key, String newValue) {
        int hash = hash(key);

        if (hash >= records.size()) {
            while (records.size() <= hash) {
                records.add(new Record());
            }
        }

        records.get(hash).update(key, newValue);
    }

    private int getCode(String key) {
        key = key.toLowerCase();
        if ((int) key.charAt(0) >= 224)
            return ((int) key.charAt(0) - 223) * 33 + ((int) key.charAt(1) - 223);
        else
            return ((int) key.charAt(0) - 96) * 26 + ((int) key.charAt(1) - 96);
    }

    public int size() {
        int count = 0;

        for (Record record : records) {
            count += record.list.size();
        }

        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < records.size(); i++) {
            sb.append(i);
            sb.append(records.get(i).toString()).append("\n");
        }

        return sb.toString();
    }

    private static class Record {
        private MyList<Node> list;

        public Record() {
            this.list = new MyLinkedList<>();
        }

        public void put(String key, String value) {
            if (indexOf(key) != -1)
                throw new IllegalArgumentException("Key " + key + " already exists");

            list.add(new Node(key, value));
        }

        public String get(String key) {
            if (indexOf(key) == -1)
                return null;

            return list.get(indexOf(key)).getValue();
        }

        public void remove(String key) {
            if (indexOf(key) != -1) {
                list.remove(indexOf(key));
            }
        }

        public void update(String key, String newValue) {
            if (indexOf(key) != -1) {
                list.remove(indexOf(key));
            }

            list.add(new Node(key, newValue));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");

            for (int i = 0; i < list.size(); i++) {
                sb.append("Элемент списка №: ").append(i).append("    ");
                sb.append(list.get(i).toString()).append("\n");
            }

            return sb.toString();
        }

        private int indexOf(String key) {
            var iterator = list.iterator();
            int index = 0;

            while (iterator.hasNext()) {
                if (iterator.next().getKey().equals(key)) {
                    return index;
                }
                index++;
            }

            return -1;
        }
    }

    private static class Node {
        private String key;
        private String value;

        public Node(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("key : ").append(key).append(" | ");
            sb.append("value : ").append(value);

            return sb.toString();
        }
    }
}
