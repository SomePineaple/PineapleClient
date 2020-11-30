package me.somepineaple.pineapleclient.main.hacks.combat;

import me.somepineaple.pineapleclient.Pineapleclient;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.PlayerUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

public class Offhand extends Hack {

    public Offhand() {
        super(Category.COMBAT);

        this.name        = "Offhand";
        this.tag         = "Offhand";
        this.description = "Switches shit to ur offhand";
    }

    Setting switch_mode = create("Offhand", "OffhandOffhand", "Totem", combobox("Totem", "Crystal", "Gapple"));
    Setting totem_switch = create("Totem HP", "OffhandTotemHP", 16, 0, 36);

    Setting gapple_in_hole = create("Gapple In Hole", "OffhandGapple", false);
    Setting gapple_hole_hp = create("Gapple Hole HP", "OffhandGappleHP", 8, 0, 36);

    Setting delay = create("Delay", "OffhandDelay", false);

    private boolean switching = false;
    private int last_slot;

    @Override
    public void update() {

        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory) {

            if (switching) {
                swap_items(last_slot, 2);
                return;
            }

            float hp = mc.player.getHealth() + mc.player.getAbsorptionAmount();

            if (hp > totem_switch.get_value(1)) {
                if (switch_mode.in("Crystal") && Pineapleclient.get_hack_manager().get_module_with_tag("AutoCrystal").is_active()) {
                    swap_items(get_item_slot(Items.END_CRYSTAL),0);
                    return;
                }
                if (gapple_in_hole.get_value(true) && hp > gapple_hole_hp.get_value(1) && is_in_hole()) {
                    swap_items(get_item_slot(Items.GOLDEN_APPLE), delay.get_value(true) ? 1 : 0);
                    return;
                }
                if (switch_mode.in("Totem")) {
                    swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), delay.get_value(true) ? 1 : 0);
                    return;
                }
                if (switch_mode.in("Gapple")) {
                    swap_items(get_item_slot(Items.GOLDEN_APPLE), delay.get_value(true) ? 1 : 0);
                    return;
                }
                if (switch_mode.in("Crystal") && !Pineapleclient.get_hack_manager().get_module_with_tag("AutoCrystal").is_active()) {
                    swap_items(get_item_slot(Items.TOTEM_OF_UNDYING),0);
                    return;
                }
            } else {
                swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), delay.get_value(true) ? 1 : 0);
                return;
            }

            if (mc.player.getHeldItemOffhand().getItem() == Items.AIR) {
                swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), delay.get_value(true) ? 1 : 0);
            }

        }

    }

    public void swap_items(int slot, int step) {
        if (slot == -1) return;
        if (step == 0) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        }
        if (step == 1) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            switching = true;
            last_slot = slot;
        }
        if (step == 2) {
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            switching = false;
        }

        mc.playerController.updateController();
    }

    private boolean is_in_hole() {

        BlockPos player_block = PlayerUtil.GetLocalPlayerPosFloored();

        return mc.world.getBlockState(player_block.east()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.north()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.south()).getBlock() != Blocks.AIR;
    }


    private int get_item_slot(Item input) {
        if (input == mc.player.getHeldItemOffhand().getItem()) return -1;
        for(int i = 36; i >= 0; i--) {
            final Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if(item == input) {
                if (i < 9) {
                    if (input == Items.GOLDEN_APPLE) {
                        return -1;
                    }
                    i += 36;
                }
                return i;
            }
        }
        return -1;
    }

}
