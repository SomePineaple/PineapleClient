package me.somepineaple.pineapleclient.main.hacks.combat;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Surround extends Hack {

	public Surround() {
		super(Category.COMBAT);

		this.name        = "Surround";
		this.tag         = "Surround";
		this.description = "surround urself with obi and such";
	}

	Setting rotate = create("Rotate", "SurroundSmoth", true);
	Setting hybrid = create("Hybrid", "SurroundHybrid", true);
	Setting triggerable = create("Toggle", "SurroundToggle", true);
	Setting center = create("Center", "SurroundCenter", false);
	Setting block_head = create("Block Face", "SurroundBlockFace", false);
	Setting tick_for_place = create("Blocks per tick","SurroundTickToPlace", 2, 1, 8);
	Setting tick_timeout = create("Ticks til timeout","SurroundTicks", 20, 10,50);
	Setting swing = create("Swing", "SurroundSwing", "Mainhand", combobox("Mainhand", "Offhand", "Both", "None"));


	private int y_level = 0;
	private int tick_runs = 0;
	private int offset_step = 0;

	private Vec3d center_block = Vec3d.ZERO;

	Vec3d[] surround_targets = {
		new Vec3d(  1,   0,   0),
		new Vec3d(  0,   0,   1),
		new Vec3d(- 1,   0,   0),
		new Vec3d(  0,   0, - 1),
		new Vec3d(  1, - 1,   0),
		new Vec3d(  0, - 1,   1),
		new Vec3d(- 1, - 1,   0),
		new Vec3d(  0, - 1, - 1),
		new Vec3d(  0, - 1,   0)
	};

	Vec3d[] surround_targets_face = {
			new Vec3d(  1,   1,   0),
			new Vec3d(  0,   1,   1),
			new Vec3d(- 1,   1,   0),
			new Vec3d(  0,   1, - 1),
			new Vec3d(  1,   0,   0),
			new Vec3d(  0,   0,   1),
			new Vec3d(- 1,   0,   0),
			new Vec3d(  0,   0, - 1),
			new Vec3d(  1, - 1,   0),
			new Vec3d(  0, - 1,   1),
			new Vec3d(- 1, - 1,   0),
			new Vec3d(  0, - 1, - 1),
			new Vec3d(  0, - 1,   0)
	};

	@Override
	public void enable() {
		if (find_in_hotbar() == -1) {
			this.setDisable();
			return;
		}

		if (mc.player != null) {

			y_level = (int) Math.round(mc.player.posY);

			center_block = get_center(mc.player.posX, mc.player.posY, mc.player.posZ);

			if (center.getValue(true)) {
				mc.player.motionX = 0;
				mc.player.motionZ = 0;
			}
		}
	}

	@Override
	public void update() {

		if (mc.player != null) {

			if (center_block != Vec3d.ZERO && center.getValue(true)) {

				double x_diff = Math.abs(center_block.x - mc.player.posX);
				double z_diff = Math.abs(center_block.z - mc.player.posZ);

				if (x_diff <= 0.1 && z_diff <= 0.1) {
					center_block = Vec3d.ZERO;
				} else {
					double motion_x = center_block.x - mc.player.posX;
					double motion_z = center_block.z - mc.player.posZ;

					mc.player.motionX = motion_x / 2;
					mc.player.motionZ = motion_z / 2;
				}
			}

			if ((int) Math.round(mc.player.posY) != y_level && this.hybrid.getValue(true)) {
				this.setDisable();
				return;
			}

			if (!this.triggerable.getValue(true) && this.tick_runs >= this.tick_timeout.getValue(1)) { // timeout time
				this.tick_runs = 0;
				this.setDisable();
				return;
			}

			int blocks_placed = 0;

			while (blocks_placed < this.tick_for_place.getValue(1)) {

				if (this.offset_step >= (block_head.getValue(true) ? this.surround_targets_face.length : this.surround_targets.length)) {
					this.offset_step = 0;
					break;
				}

				BlockPos offsetPos = new BlockPos(block_head.getValue(true) ? this.surround_targets_face[offset_step] : this.surround_targets[offset_step]);
				BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

				boolean try_to_place = true;

				if (!mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
					try_to_place = false;
				}

				for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))) {
					if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
					try_to_place = false;
					break;
				}

				if (try_to_place && BlockUtil.placeBlock(targetPos, find_in_hotbar(), rotate.getValue(true), rotate.getValue(true), true, swing)) {
					blocks_placed++;
				}

				offset_step++;
			}

			this.tick_runs++;
		}
	}

	private int find_in_hotbar() {
        for (int i = 0; i < 9; ++i) {

            final ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {

                final Block block = ((ItemBlock) stack.getItem()).getBlock();

                if (block instanceof BlockEnderChest)
                    return i;

                else if (block instanceof BlockObsidian)
                    return i;
            }
        }
        return -1;
	}

	public Vec3d get_center(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D ;

        return new Vec3d(x, y, z);
    }

	public boolean areBlocksLeftToSurrond () {
		if (!this.isActive()) {
			return false;
		}

		boolean areAllPlaced = true;

		for (Vec3d target : surround_targets) {
			BlockPos offsetPos = new BlockPos(target);
			BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
			areAllPlaced = areAllPlaced && !mc.world.getBlockState(targetPos).getMaterial().isReplaceable();
		}

		return !areAllPlaced;
	}
}
