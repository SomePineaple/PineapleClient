package me.somepineaple.pineapleclient.main.event.events;

import me.somepineaple.pineapleclient.main.event.EventCancellable;
import net.minecraft.client.gui.ScaledResolution;

public class EventGameOverlay extends EventCancellable {

    public float partial_ticks;
    private ScaledResolution scaled_resolution;

    public EventGameOverlay(float partial_ticks, ScaledResolution scaled_resolution) {
        
        this.partial_ticks = partial_ticks;
        this.scaled_resolution = scaled_resolution;

    }

    public ScaledResolution get_scaled_resoltion() {
        return scaled_resolution;
    }
    
}