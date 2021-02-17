package me.somepineaple.pineapleclient.main.event.events;

import me.somepineaple.pineapleclient.main.event.EventCancellable;

public class EventSetupFog extends EventCancellable {
    
    public int start_coords;
    public float partial_ticks;

    public EventSetupFog(int coords, float ticks) {
        start_coords = coords;
        partial_ticks = ticks;        
    }
}
