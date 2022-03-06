package me.somepineaple.pineapleclient.main.hacks.movement;

import me.somepineaple.pineapleclient.main.event.events.EventPacket;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.ArrayList;

public class Blink extends Hack {
    public Blink() {
        super(Category.MOVEMENT);
        name = "Blink";
        tag = "Blink";
        description = "Fake lag";
    }

    private final ArrayList<Packet<?>> packets = new ArrayList<>();

    @EventHandler
    public Listener<EventPacket.SendPacket> sendPacketListener = new Listener<>(event -> {
        if (!(event.getPacket() instanceof CPacketPlayer)) return;
        packets.add(event.getPacket());
        event.cancel();
    });

    @Override
    protected void disable() {
        for (Packet<?> p : packets) {
            mc.player.connection.sendPacket(p);
        }
        packets.clear();
    }
}
