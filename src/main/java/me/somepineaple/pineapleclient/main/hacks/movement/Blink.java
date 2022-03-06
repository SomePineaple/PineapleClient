package me.somepineaple.pineapleclient.main.hacks.movement;

import me.somepineaple.pineapleclient.main.event.events.EventPacket;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.LinkedList;
import java.util.Queue;

public class Blink extends Hack {
    public Blink() {
        super(Category.MOVEMENT);
        name = "Blink";
        tag = "Blink";
        description = "Fake lag";
    }

    private final Queue<Packet<?>> packetQueue = new LinkedList<>();

    @EventHandler
    public Listener<EventPacket.SendPacket> sendPacketListener = new Listener<>(event -> {
        if (!(event.getPacket() instanceof CPacketPlayer)) return;
        packetQueue.add(event.getPacket());
        event.cancel();
    });

    @Override
    protected void disable() {
        while (!packetQueue.isEmpty())
            mc.player.connection.sendPacket(packetQueue.poll());
    }
}
