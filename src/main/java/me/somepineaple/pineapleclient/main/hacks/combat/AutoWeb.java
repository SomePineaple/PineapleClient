package me.somepineaple.pineapleclient.main.hacks.combat;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoWeb extends Hack {

    public AutoWeb() {
        super(Category.COMBAT);

        this.name        = "Auto Self Web";
        this.tag         = "AutoSelfWeb";
        this.description = "places fuckin webs at ur feet";
    }

    Setting always_on = create("Always On", "AlwaysOn", true);
    Setting rotate = create("Rotate", "AutoWebRotate", true);
    Setting range = create("Enemy Range", "AutoWebRange", 4, 0, 8);

    int new_slot = -1;

    boolean sneak = false;

    @Override
    public void enable() {

        if (mc.player != null) {

            new_slot = find_in_hotbar();

            if (new_slot == -1) {
                MessageUtil.send_client_error_message("cannot find webs in hotbar");
                set_active(false);
            }

        }

    }

    @Override
    public void disable() {
        if (mc.player != null) {
            if (sneak) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                sneak = false;
            }
        }
    }

    @Override
    public void update() {

        if (mc.player == null) return;

        if (always_on.getValue(true)) {

            EntityPlayer target = find_closest_target();
            if (target == null) return;

            if (mc.player.getDistance(target) < range.getValue(1) && is_surround()) {
                int last_slot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = new_slot;
                mc.playerController.updateController();
                place_blocks(PlayerUtil.GetLocalPlayerPosFloored());
                mc.player.inventory.currentItem = last_slot;
            }

        } else {
            int last_slot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = new_slot;
            mc.playerController.updateController();
            place_blocks(PlayerUtil.GetLocalPlayerPosFloored());
            mc.player.inventory.currentItem = last_slot;
            this.setDisable();
        }

    }

    public EntityPlayer find_closest_target()  {

        if (mc.world.playerEntities.isEmpty())
            return null;

        EntityPlayer closestTarget = null;

        for (final EntityPlayer target : mc.world.playerEntities)
        {
            if (target == mc.player)
                continue;

            if (FriendUtil.isFriend(target.getName()))
                continue;

            if (!EntityUtil.isLiving(target))
                continue;

            if (target.getHealth() <= 0.0f)
                continue;

            if (closestTarget != null)
                if (mc.player.getDistance(target) > mc.player.getDistance(closestTarget))
                    continue;

            closestTarget = target;
        }

        return closestTarget;
    }

    private int find_in_hotbar() {

        for (int i = 0; i < 9; ++i) {

            final ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack.getItem() == Item.getItemById(30)) {
                return i;
            }

        }
        return -1;
    }

    private boolean is_surround() {

        BlockPos player_block = PlayerUtil.GetLocalPlayerPosFloored();
        return mc.world.getBlockState(player_block.east()).getBlock() != Blocks.AIR
            && mc.world.getBlockState(player_block.west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.north()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(player_block.south()).getBlock() != Blocks.AIR
                        && mc.world.getBlockState(player_block).getBlock() == Blocks.AIR;

    }

    private void place_blocks(BlockPos pos) {

        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return;
        }

        if (!BlockInteractHelper.checkForNeighbours(pos)) {
            return;
        }

        for (EnumFacing side : EnumFacing.values()) {

            BlockPos neighbor = pos.offset(side);

            EnumFacing side2 = side.getOpposite();

            if (!BlockInteractHelper.canBeClicked(neighbor)) continue;

            if (BlockInteractHelper.blackList.contains(mc.world.getBlockState(neighbor).getBlock())) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                sneak = true;
            }

            Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));

            if (rotate.getValue(true)) {
                BlockInteractHelper.faceVectorPacketInstant(hitVec);
            }

            mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);

            return;
        }
    }
}
