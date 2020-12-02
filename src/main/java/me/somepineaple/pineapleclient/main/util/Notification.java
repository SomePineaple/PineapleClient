package me.somepineaple.pineapleclient.main.util;

public class Notification {
    private final long timeCreated;
    private final String message;

    public Notification (String message) {
        timeCreated = System.currentTimeMillis();
        this.message = message;
    }

    public long getTimeCreated () {
        return timeCreated;
    }

    public String getMessage () {
        return message;
    }
}
