package me.somepineaple.pineapleclient.main.event.events;

import me.somepineaple.pineapleclient.main.event.EventCancellable;
import net.minecraft.util.EnumHand;

public class EventSwing extends EventCancellable {
    
    public EnumHand hand;

    public EventSwing(EnumHand hand) {
        super();
        this.hand = hand;
    }
}
