package me.somepineaple.pineapleclient.main.hacks.combat;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.BlockInteractHelper;
import me.somepineaple.pineapleclient.main.util.BlockInteractHelper.ValidResult;
import me.somepineaple.pineapleclient.main.util.BlockUtil;
import me.somepineaple.pineapleclient.main.util.MessageUtil;
import me.somepineaple.pineapleclient.main.util.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class HoleFill extends Hack {
    
    public HoleFill() {
		super(Category.COMBAT);

		this.name        = "Hole Fill"; 
		this.tag         = "HoleFill";
		this.description = "Turn holes into floors";
    }
    
    Setting hole_toggle = create("Toggle", "HoleFillToggle", true);
    Setting hole_rotate = create("Rotate", "HoleFillRotate", true);
    Setting hole_range = create("Range", "HoleFillRange", 4, 1, 6);
    Setting swing = create("Swing", "HoleFillSwing", "Mainhand", combobox("Mainhand", "Offhand", "Both", "None"));


    private final ArrayList<BlockPos> holes = new ArrayList<>();

    @Override
	public void enable() {
		if (find_in_hotbar() == -1) {
		    this.set_disable();
        }
        find_new_holes();
	}

	@Override
	public void disable() {
        holes.clear();
    }
    
    @Override
	public void update() {

        if (find_in_hotbar() == -1) {
            this.disable();
            return;
        }

        if (holes.isEmpty()) {
            if (!hole_toggle.get_value(true)) {
                this.set_disable();
                MessageUtil.toggle_message(this);
                return;

            } else {
                find_new_holes();
            }
        }

        BlockPos pos_to_fill = null;

        for (BlockPos pos : new ArrayList<>(holes)) {

            if (pos == null) continue;

            BlockInteractHelper.ValidResult result = BlockInteractHelper.valid(pos);

            if (result != ValidResult.Ok) {
                holes.remove(pos);
                continue;
            }
            pos_to_fill = pos;
            break;
        }

        if (find_in_hotbar() == -1) {
            this.disable();
            return;
        }

        if (pos_to_fill != null) {
            if (BlockUtil.placeBlock(pos_to_fill, find_in_hotbar(), hole_rotate.get_value(true), hole_rotate.get_value(true), swing)) {
                holes.remove(pos_to_fill);
            }
        }

    }

    public void find_new_holes() {

        holes.clear();

        for (BlockPos pos : BlockInteractHelper.getSphere(PlayerUtil.GetLocalPlayerPosFloored(), hole_range.get_value(1), (int) hole_range.get_value(1), false, true, 0)) {

            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            boolean possible = true;

            for (BlockPos seems_blocks : new BlockPos[] {
            new BlockPos( 0, -1,  0),
            new BlockPos( 0,  0, -1),
            new BlockPos( 1,  0,  0),
            new BlockPos( 0,  0,  1),
            new BlockPos(-1,  0,  0)
            }) {
                Block block = mc.world.getBlockState(pos.add(seems_blocks)).getBlock();

                if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                    possible = false;
                    break;
                }
            }

            if (possible) {
                holes.add(pos);
            }
        }
    }

    private int find_in_hotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();

                if (block instanceof BlockEnderChest) {
                    return i;
                }

                if (block instanceof BlockObsidian) {
                    return i;
                }
            }
        }
        return -1;
    }
}
