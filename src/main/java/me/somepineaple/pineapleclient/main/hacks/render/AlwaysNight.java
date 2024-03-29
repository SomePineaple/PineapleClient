package me.somepineaple.pineapleclient.main.hacks.render;

import me.somepineaple.pineapleclient.main.event.events.EventPacket;
import me.somepineaple.pineapleclient.main.event.events.EventRender;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.server.SPacketTimeUpdate;

public class AlwaysNight extends Hack {

    public AlwaysNight() {
        super(Category.RENDER);

        this.name = "Always Night";
        this.tag = "AlwaysNight";
        this.description = "see even less";
    }

    @EventHandler
    private Listener<EventRender> on_render = new Listener<>(event -> {
        if (mc.world == null) return;
        mc.world.setWorldTime(18000);
    });

    @Override
    public void update() {
        if (mc.world == null) return;
        mc.world.setWorldTime(18000);
    }
    
    @EventHandler
    private Listener<EventPacket.ReceivePacket> recieve_packet = new Listener<>(event -> {
    	if (event.getPacket() instanceof SPacketTimeUpdate) {
    		event.cancel();
    	}
    });
}
