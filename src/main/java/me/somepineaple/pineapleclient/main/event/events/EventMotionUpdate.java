package me.somepineaple.pineapleclient.main.event.events;

import me.somepineaple.pineapleclient.main.event.EventCancellable;

public class EventMotionUpdate extends EventCancellable {

    public int stage;

    public EventMotionUpdate(int stage) {
        super();
        this.stage = stage;
    }
}
