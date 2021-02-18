package me.somepineaple.pineapleclient.main.hacks.movement;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.BlockUtil;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class Scaffold extends Hack {
    public Scaffold() {
        super(Category.MOVEMENT);
        this.name = "Scaffold";
        this.tag = "Scaffold";
        this.description = "Places block beneath your feet";
    }
    
    Setting rotate = create("Rotate", "ScaffoldRotate", true);
    Setting arm = create("Swing:", "ScaffoldHand", "Mainhand", combobox("Mainhand", "Offhand"));
	Setting blocks_per_tick = create("Blocks Per Tick", "ScaffoldBlocksPerTick", 1, 1, 8);

    @Override
    public void update() {
        BlockPos belowPlayer = new BlockPos(mc.player);
        belowPlayer = belowPlayer.add(0, -1, 0);
		int blocksPlaced = 0;
		while (blocksPlaced < blocks_per_tick.get_value(1)) {
			int firstBlock = findBlockInHotbar();
			if (mc.world.getBlockState(belowPlayer).getMaterial().isReplaceable() && firstBlock != -1) {
				mc.player.inventory.currentItem = firstBlock;
				BlockPos bestBlock = getBestBlock(belowPlayer);
				if (bestBlock != null) {
					BlockUtil.placeBlock(bestBlock, firstBlock, rotate.get_value(true), rotate.get_value(true), arm);
					blocksPlaced++;
				} else break;
			} else break;
		}
    }
    
    private int findBlockInHotbar() {
    	for (int i = 0; i < 9; i++) {
    		if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
    			return i;
    		}
    	}
    	
    	return -1;
    }
    
    private Vec3i[] nextTo = {
    	new Vec3i(1, 0, 0),
    	new Vec3i(0, 0, 1),
    	new Vec3i(-1, 0, 0),
    	new Vec3i(0, 0, -1),
    	new Vec3i(0, -1, 0)
    };
    
	BlockPos nextToBlocks[] = new BlockPos[6];
    private BlockPos getBestBlock(BlockPos belowPlayer) {
    	if (doesBlockHaveNeighbors(belowPlayer)) {
    		return belowPlayer;
    	}

		int i = 0;
    	
    	for (Vec3i direction : nextTo) {
    		BlockPos block = belowPlayer.add(direction);
    		if (doesBlockHaveNeighbors(block)) {
    			return block;
    		}
			nextToBlocks[i] = block;
			i++;
    	}

		for (BlockPos block : nextToBlocks) {
			for (Vec3i direction : nextTo) {
				BlockPos block2 = block.add(direction);
				if (doesBlockHaveNeighbors(block2)) {
					return block2;
				}
			}
		}
    	
    	return null;
    }
    
    private boolean doesBlockHaveNeighbors(BlockPos block) {
    	for (Vec3i direction : nextTo) {
    		if (mc.world.getBlockState(block.add(direction)).isFullBlock()) {
    			return true;
    		}
    	}
    	
    	return false;
    }
}
