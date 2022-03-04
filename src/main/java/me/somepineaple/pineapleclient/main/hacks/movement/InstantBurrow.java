package me.somepineaple.pineapleclient.main.hacks.movement;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.BlockUtil;
import me.somepineaple.pineapleclient.main.util.MessageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class InstantBurrow extends Hack {
    Setting rotate = create("Rotate", "InstantBurrowRotate", false);
    Setting offset = create("Offset", "InstantBurrowOffset", 7.0, -20.0, 20.0);
    Setting hand = create("Swing", "InstantBurrowHand", "Mainhand", combobox("Mainhand", "Offhand", "Both"));

    private BlockPos originalPos;

    public InstantBurrow() {
        super(Category.MOVEMENT);
        name = "Instant Burrow";
        tag = "InstantBurrow";
        description = "Be annoying lolz";
    }

    @Override
    protected void enable() {
        originalPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock().equals(Blocks.OBSIDIAN) ||
                intersectsWithEntity(this.originalPos)) {
            toggle();
        }
    }

    @Override
    public void update() {
        if (findHotbarSlot() == -1) {
            MessageUtil.clientMessage("Instant burrow cannot find blocks in your hotbar");
            this.toggle();
            return;
        }

        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, true));

        BlockUtil.placeBlock(originalPos, findHotbarSlot(), rotate.getValue(true), rotate.getValue(true), false, hand);

        // Rubberband
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset.getValue(1.0), mc.player.posZ, false));

        this.toggle();
    }

    private int findHotbarSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
                return i;
            }
        }

        return -1;
    }

    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : mc.world.loadedEntityList) {
            if (entity.equals(mc.player)) continue;
            if (entity instanceof EntityItem) continue;
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) return true;
        }
        return false;
    }
}
