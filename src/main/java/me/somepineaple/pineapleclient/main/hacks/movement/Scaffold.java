package me.somepineaple.pineapleclient.main.hacks.movement;

import java.util.concurrent.ConcurrentLinkedQueue;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.BlockUtil;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class Scaffold extends Hack implements Runnable {
	private boolean shouldJoin;
	private Thread thread;

	public Scaffold() {
		super(Category.MOVEMENT);
		this.name = "Scaffold";
		this.tag = "Scaffold";
		this.description = "Places block beneath your feet";
	}

	Setting rotate = create("Rotate", "ScaffoldRotate", false);
	Setting arm = create("Swing:", "ScaffoldHand", "Mainhand", combobox("Mainhand", "Offhand"));
	Setting delay = create("Delay (MS)", "ScaffoldDelay", 20, 0, 100);
	Setting mode = create("Mode:", "ScaffoldMode", "Expand", combobox("Normal", "Expand"));
	Setting sprint = create("Allow Sprint", "ScaffoldSprint", true);

	private long lastBlockPlaced = System.currentTimeMillis();

	@Override
	public void enable() {
		shouldJoin = false;
		thread = new Thread(this, "scaffoldthread");
		thread.start();
	}

	@Override
	protected void disable() {
		shouldJoin = true;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
			if (block == null) continue;
			for (Vec3i direction : nextTo) {
				BlockPos block2 = block.add(direction);
				if (doesBlockHaveNeighbors(block2)) {
					return block2;
				}
			}
		}
    	
    	return null;
    }

	private BlockPos getBestExtendsBlock(BlockPos belowPlayer) {
		switch(mc.player.getHorizontalFacing()) {
			case EAST :
				return belowPlayer.east();
			case DOWN:
				break;
			case NORTH:
				return belowPlayer.north();
			case SOUTH:
				return belowPlayer.south();
			case UP:
				break;
			case WEST:
				return belowPlayer.west();
			default:
				break;
				
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

	private boolean isBlockBelowPlayerEmpty = false;
	ConcurrentLinkedQueue<BlockPos> blocksToBePlacedQueue = new ConcurrentLinkedQueue<>();

	@Override
	public void update() {
		for (BlockPos block : blocksToBePlacedQueue) {
			BlockUtil.placeBlock(block, findBlockInHotbar(), rotate.getValue(true), rotate.getValue(true), false, arm);
			lastBlockPlaced = System.currentTimeMillis();
		}
		BlockPos belowPlayer = new BlockPos(mc.player).add(0, -1, 0);
		isBlockBelowPlayerEmpty = mc.world.getBlockState(belowPlayer).getMaterial().isReplaceable();
		blocksToBePlacedQueue.clear();
	}

	@Override
	public void run() {
		while (!shouldJoin) {
			if (!sprint.getValue(true)) mc.player.setSprinting(false);
			BlockPos belowPlayer = new BlockPos(mc.player).add(0, -1, 0);
			if (System.currentTimeMillis() - lastBlockPlaced > delay.getValue(1)) {
				int firstBlock = findBlockInHotbar();
				if (isBlockBelowPlayerEmpty && firstBlock != -1) {
					mc.player.inventory.currentItem = firstBlock;
					BlockPos bestBlock = getBestBlock(belowPlayer);
					if (bestBlock != null) {
						blocksToBePlacedQueue.add(bestBlock);
					}
				} else if (mode.in("Expand") && firstBlock != -1) {
					mc.player.inventory.currentItem = firstBlock;
					BlockPos bestBlock = getBestExtendsBlock(belowPlayer);
					if (bestBlock != null) {
						blocksToBePlacedQueue.add(bestBlock);
					}
				}
			}
		}
	}
}
