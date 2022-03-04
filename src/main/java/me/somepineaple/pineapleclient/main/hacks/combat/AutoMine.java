package me.somepineaple.pineapleclient.main.hacks.combat;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.BreakUtil;
import me.somepineaple.pineapleclient.main.util.EntityUtil;
import me.somepineaple.pineapleclient.main.util.MessageUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.math.BlockPos;

public class AutoMine extends Hack {

    public AutoMine() {
        super(Category.COMBAT);

        this.name        = "Auto Mine";
        this.tag         = "AutoMine";
        this.description = "jumpy is now never going to use the client again";
    }

    Setting end_crystal = create("End Crystal", "MineEndCrystal", false);
    Setting range = create("Range", "MineRange", 4, 0, 6);
    Setting ray_trace = create("Ray Trace", "MineRayTrace", false);
    Setting swap = create("Swap to Pick","MineSwap", true);
    
    private BlockPos target_block = null;

    @Override
    protected void enable() {
        target_block = null;

        for (EntityPlayer player : mc.world.playerEntities) {
            if (mc.player.getDistance(player) > range.getValue(1)) continue;

            BlockPos p = EntityUtil.is_cityable(player, end_crystal.getValue(true));

            if (p != null) {
                target_block = p;
            }
        }

        if (target_block == null) {
            MessageUtil.send_client_message("cannot find block");
            this.set_active(false);
            return;
        }

        int pickSlot = findPickaxe();
        if (swap.getValue(true) && pickSlot != -1) {
            mc.player.inventory.currentItem = pickSlot;
        }

        BreakUtil.set_current_block(target_block);
    }

    @Override
    public void update() {
        BreakUtil.update(range.getValue(1), ray_trace.getValue(true));
        if(mc.world.getBlockState(target_block).getBlock() instanceof BlockAir) {
            this.set_active(false);
        }
    }

    @Override
    protected void disable() {
        BreakUtil.set_current_block(null);
    }

    private int findPickaxe() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemPickaxe) {
                return i;
            }
        }

        return -1;
    }
}
