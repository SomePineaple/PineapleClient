package me.somepineaple.pineapleclient.main.hacks.movement;

import me.somepineaple.pineapleclient.main.event.events.EventMotionUpdate;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Anchor extends Hack {

    // Written by NathanW, thanks to my friend Ian for some hole shit.

    public Anchor() {
        super(Category.MOVEMENT);

        this.name        = "Anchor";
        this.tag         = "Anchor";
        this.description = "Stops all movement if player is above a hole";
    }

    Setting Pitch = create("Pitch", "AnchorPitch", 60, 0, 90);
    Setting Pull = create("Pull", "AnchorPull", true);
    int holeblocks;


    public static boolean AnchorING;
    public boolean isBlockHole(BlockPos blockpos) {
        holeblocks = 0;
        if (mc.world.getBlockState(blockpos.add(0, 3, 0)).getBlock() == Blocks.AIR) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 2, 0)).getBlock() == Blocks.AIR) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 1, 0)).getBlock() == Blocks.AIR) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 0, 0)).getBlock() == Blocks.AIR) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN ||mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN ||mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN ||mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN ||mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (holeblocks >= 9) return true;
        else return false;
    }
    private Vec3d Center = Vec3d.ZERO;

    public Vec3d GetCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D ;

        return new Vec3d(x, y, z);
    }

    @EventHandler
    private Listener<EventMotionUpdate> OnClientTick = new Listener<>(event -> {
        if (mc.player.rotationPitch >= Pitch.get_value(60)) {

            if (isBlockHole(getPlayerPos().down(1)) || isBlockHole(getPlayerPos().down(2)) ||
                    isBlockHole(getPlayerPos().down(3)) || isBlockHole(getPlayerPos().down(4))) {
                AnchorING = true;

                if (!Pull.get_value(true)) {
                    mc.player.motionX = 0.0;
                    mc.player.motionZ = 0.0;
                } else {
                    Center = GetCenter(mc.player.posX, mc.player.posY, mc.player.posZ);
                    double XDiff = Math.abs(Center.x - mc.player.posX);
                    double ZDiff = Math.abs(Center.z - mc.player.posZ);

                    if (XDiff <= 0.1 && ZDiff <= 0.1) {
                        Center = Vec3d.ZERO;
                    }
                    else {
                        double MotionX = Center.x-mc.player.posX;
                        double MotionZ = Center.z-mc.player.posZ;

                        mc.player.motionX = MotionX/2;
                        mc.player.motionZ = MotionZ/2;
                    }
                }
            } else AnchorING = false;
        }
    });

    public void onDisable() {
        AnchorING = false;
        holeblocks = 0;
    }

    public BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

}