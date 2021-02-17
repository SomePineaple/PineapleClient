package me.somepineaple.pineapleclient.main.util;

public class Pair<T, S> {
    T key;
    S value;

    public Pair(T key, S value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public S getValue() {
        return value;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public void setValue(S value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair<?, ?> otherPair = (Pair<?, ?>) obj;
        return this.key.equals(otherPair.key) && this.value.equals(otherPair.value);
    }
}
