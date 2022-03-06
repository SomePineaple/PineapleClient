package me.somepineaple.pineapleclient.main.util;

public class Timer {
    private long time;

    public Timer() {
        this.time = -1L;
    }

    public boolean passed(final long ms) {
        return this.getTime(System.nanoTime() - this.time) >= ms;
    }

    public void reset() {
        this.time = System.nanoTime();
    }

    public void setTimePassed(final long ms) {
        time = System.nanoTime() - (ms * 1000000L);
    }

    public long getTime(final long time) {
        return time / 1000000L;
    }
}
