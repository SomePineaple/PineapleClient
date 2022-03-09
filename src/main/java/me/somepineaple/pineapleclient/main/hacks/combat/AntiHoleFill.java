package me.somepineaple.pineapleclient.main.hacks.combat;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.BlockInteractHelper;
import me.somepineaple.pineapleclient.main.util.BlockInteractHelper.ValidResult;
import me.somepineaple.pineapleclient.main.util.BlockUtil;
import me.somepineaple.pineapleclient.main.util.MessageUtil;
import me.somepineaple.pineapleclient.main.util.PlayerUtil;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class AntiHoleFill extends Hack {
    public AntiHoleFill() {
		super(Category.COMBAT);

		this.name        = "Anti Hole Fill";
		this.tag         = "AntiHoleFill";
		this.description = "Fill up holes w/ pressure plates";
    }

    Setting holeToggle = create("Toggle", "HoleFillToggle", true);
    Setting holeRotate = create("Rotate", "HoleFillRotate", true);
    Setting holeRange = create("Range", "HoleFillRange", 4, 1, 6);
    Setting swing = create("Swing", "HoleFillSwing", "Mainhand", combobox("Mainhand", "Offhand", "Both", "None"));

    private final ArrayList<BlockPos> holes = new ArrayList<>();

    @Override
	public void enable() {
		if (findInHotbar() == -1) {
		    this.setDisable();
        }
        findNewHoles();
	}

	@Override
	public void disable() {
        holes.clear();
    }
    
    @Override
	public void update() {

        if (findInHotbar() == -1) {
            this.disable();
            return;
        }

        if (holes.isEmpty()) {
            if (!holeToggle.getValue(true)) {
                this.setDisable();
                MessageUtil.toggle_message(this);
                return;
            } else {
                findNewHoles();
            }
        }

        BlockPos posToFill = null;

        for (BlockPos pos : new ArrayList<>(holes)) {

            if (pos == null) continue;

            ValidResult result = BlockInteractHelper.valid(pos);

            if (result != ValidResult.Ok) {
                holes.remove(pos);
                continue;
            }
            posToFill = pos;
            break;
        }

        if (findInHotbar() == -1) {
            this.disable();
            return;
        }

        if (posToFill != null) {
            if (BlockUtil.placeBlock(posToFill, findInHotbar(), holeRotate.getValue(true), holeRotate.getValue(true), true, swing)) {
                holes.remove(posToFill);
            }
        }

    }

    public void findNewHoles() {
        holes.clear();

        for (BlockPos pos : BlockInteractHelper.getSphere(PlayerUtil.GetLocalPlayerPosFloored(), holeRange.getValue(1), holeRange.getValue(1), false, true, 0)) {

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

    private int findInHotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();

                if (block instanceof BlockPressurePlate || block instanceof BlockPressurePlateWeighted)
                    return i;
            }
        }
        return -1;
    }
}
