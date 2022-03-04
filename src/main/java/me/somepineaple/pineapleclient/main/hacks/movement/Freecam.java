package me.somepineaple.pineapleclient.main.hacks.movement;

import me.somepineaple.pineapleclient.main.event.EventCancellable;
import me.somepineaple.pineapleclient.main.event.events.EventPacket;
import me.somepineaple.pineapleclient.main.event.events.EventPlayerTravel;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.MathUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.world.World;

public class Freecam extends Hack {

    private double posX;
    private double posY;
    private double posZ;
    private float pitch;
    private float yaw;
    private EntityOtherPlayerMP clonedPlayer;
    private boolean isRidingEntity;
    private Entity ridingEntity;

    public Freecam (){
        super(Category.MOVEMENT);

        this.name = "Free Cam";
        this.tag = "Freecam";
        this.description = "Move around out of your body, copied from Xulu";
    }

    Setting cancelpackets = create("Cancel Packets", "freecamcancelpackets", true);
    Setting speed = create("Speed", "freecamspeed", 0.5, 0.1, 5.0);

    @Override
    public void update() {
        mc.player.noClip = true;
        mc.player.setVelocity(0, 0, 0);
        mc.player.jumpMovementFactor = (float) speed.getValue(1.0);
        double[] dir = MathUtil.directionSpeed(speed.getValue(1.0));
        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        mc.player.setSprinting(false);

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY += speed.getValue(1.0);
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY -= speed.getValue(1.0);
        }
    }

    @Override
    protected void enable() {
        if (mc.player != null) {
            this.isRidingEntity = (mc.player.getRidingEntity() != null);

            if (mc.player.getRidingEntity() == null) {
                this.posX = mc.player.posX;
                this.posY = mc.player.posY;
                this.posZ = mc.player.posZ;
            } else {
                this.ridingEntity = mc.player.getRidingEntity();
                mc.player.dismountRidingEntity();
            }

            this.pitch = mc.player.rotationPitch;
            this.yaw = mc.player.rotationYaw;

            (this.clonedPlayer = new EntityOtherPlayerMP((World)mc.world, mc.getSession().getProfile())).copyLocationAndAnglesFrom((Entity)mc.player);
            this.clonedPlayer.rotationYawHead = mc.player.rotationYawHead;

            mc.world.addEntityToWorld(-100, (Entity)this.clonedPlayer);
            mc.player.noClip = true;
        }
    }

    @Override
    protected void disable() {
        if (mc.player != null) {
            mc.player.setPositionAndRotation(this.posX, this.posY, this.posZ, this.yaw, this.pitch);
            mc.world.removeEntityFromWorld(-100);
            this.clonedPlayer = null;

            this.posX = 0d;
            this.posY = 0d;
            this.posZ = 0d;
            this.yaw = 0f;
            this.pitch = 0f;

            mc.player.capabilities.isFlying = false;
            mc.player.capabilities.setFlySpeed(0.05f);
            mc.player.noClip = false;

            final EntityPlayerSP player = mc.player;
            final EntityPlayerSP player2 = mc.player;
            final EntityPlayerSP player3 = mc.player;
            player3.motionZ = 0d;
            player2.motionY = 0d;
            player.motionX = 0d;

            if (isRidingEntity) mc.player.startRiding(ridingEntity, true);
        }

        mc.renderGlobal.loadRenderers();
    }

    @EventHandler
    private final Listener<EventPacket.SendPacket> sendPacketListener = new Listener<>(event -> {
        if (cancelpackets.getValue(true) && (event.get_packet() instanceof CPacketPlayer || event.get_packet() instanceof CPacketInput)) {
            event.cancel();
        }
    });

    @EventHandler
    private final Listener<EventPlayerTravel> travelListener = new Listener<>(event -> {
        if (event.get_era() != EventCancellable.Era.EVENT_PRE) {
            return;
        }
        mc.player.noClip = true;
    });
}
