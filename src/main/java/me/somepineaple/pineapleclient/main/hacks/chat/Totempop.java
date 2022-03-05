package me.somepineaple.pineapleclient.main.hacks.chat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.event.events.EventPacket;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.FriendUtil;
import me.somepineaple.pineapleclient.main.util.MessageUtil;
import me.somepineaple.pineapleclient.main.util.Notification;
import me.somepineaple.pineapleclient.main.util.NotificationUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

import java.util.HashMap;

public class Totempop extends Hack {
    
    public Totempop() {
		super(Category.CHAT);

		this.name        = "Totem Pop Counter";
		this.tag         = "TotemPopCounter";
		this.description = "tells u when someone pops a totem";
    }

    private boolean should_notify;

    public static final HashMap<String, Integer> totem_pop_counter = new HashMap<String, Integer>();
    
    public static ChatFormatting red = ChatFormatting.RED;
    public static ChatFormatting green = ChatFormatting.GREEN;
    public static ChatFormatting gold = ChatFormatting.GOLD;
    public static ChatFormatting grey = ChatFormatting.GRAY;
    public static ChatFormatting bold = ChatFormatting.BOLD;
    public static ChatFormatting reset = ChatFormatting.RESET;

    @EventHandler
    private final Listener<EventPacket.ReceivePacket> packet_event = new Listener<>(event -> {

        if (event.get_packet() instanceof SPacketEntityStatus) {

            SPacketEntityStatus packet = (SPacketEntityStatus) event.get_packet();

            if (packet.getOpCode() == 35) {

                Entity entity = packet.getEntity(mc.world);

                int count = 1;

                if (totem_pop_counter.containsKey(entity.getName())) {
                    count = totem_pop_counter.get(entity.getName());
                    totem_pop_counter.put(entity.getName(), ++count);
                } else {
                    totem_pop_counter.put(entity.getName(), count);
                }

                if (entity == mc.player) return;

                if (FriendUtil.isFriend(entity.getName())) {
                    MessageUtil.sendClientMessage( red + "" + bold + " TotemPop " + reset + grey + " > " + reset + "dude, " + bold + green + entity.getName() + reset + " has popped " + bold + count + reset + " totems. you should go help them");
                    if (should_notify) NotificationUtil.send_notification(new Notification(entity.getName() + " has poped " + count + " totems, go help"));
                } else {
                    MessageUtil.sendClientMessage( red + "" + bold + " TotemPop " + reset + grey + " > " + reset + "dude, " + bold + red + entity.getName() + reset + " has popped " + bold + count + reset + " totems. what a loser");
                    if (should_notify) NotificationUtil.send_notification(new Notification(entity.getName() + " has poped " + count + " totems"));
                }

            }

        }

    });

    @Override
	public void update() {
        should_notify = PineapleClient.getSettingManager().getSettingWithTag("HUD", "notificationtotem").getValue(true);
        for (EntityPlayer player : mc.world.playerEntities) {

            if (!totem_pop_counter.containsKey(player.getName())) continue;

            if (player.isDead || player.getHealth() <= 0) {

                int count = totem_pop_counter.get(player.getName());

                totem_pop_counter.remove(player.getName());

                if (player == mc.player) continue;

                if (FriendUtil.isFriend(player.getName())) {
                    MessageUtil.sendClientMessage( red + "" + bold + " TotemPop " + reset + grey + " > " + reset + "dude, " + bold + green + player.getName() + reset + " just fucking DIED after popping " + bold + count + reset + " totems. RIP :pray:");
                    if (should_notify) NotificationUtil.send_notification(new Notification(player.getName() + " died after poping " + count + " totems"));
                } else {
                    MessageUtil.sendClientMessage( red + "" + bold + " TotemPop " + reset + grey + " > " + reset + "dude, " + bold + red + player.getName() + reset + " just fucking DIED after popping " + bold + count + reset + " totems");
                    if (should_notify) NotificationUtil.send_notification(new Notification(player.getName() + " died after poping " + count + " totems"));
                }

            }

        }

	}

    @Override
    protected void enable() {
        totem_pop_counter.clear();
    }

    @Override
    protected void disable() {
        totem_pop_counter.clear();
    }
}
