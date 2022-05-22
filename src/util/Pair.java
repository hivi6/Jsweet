package util;

public class Pair<K, V> {
    public K first;
    public V second;

    public Pair() {
        first = null;
        second = null;
    }

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }
}
