package me.somepineaple.pineapleclient.main.hacks.combat;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;

public class AutoGapple extends Hack {

    public AutoGapple() {
        super(Category.COMBAT);

        this.name        = "Auto Gapple";
        this.tag         = "AutoGapple";
        this.description = "put gapple in offhand";
    }

    Setting delay = create("Delay", "GappleDelay", false);

    private boolean switching = false;
    private int last_slot;

    @Override
    public void update() {

        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory) {
            if (switching) {
                swap_items(last_slot, 2);
                return;
            }

            swap_items(get_item_slot(), delay.get_value(true) ? 1 : 0);
        }

    }

    private int get_item_slot() {
        if (Items.GOLDEN_APPLE == mc.player.getHeldItemOffhand().getItem()) return -1;
        for(int i = 36; i >= 0; i--) {
            final Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if(item == Items.GOLDEN_APPLE) {
                if (i < 9) {
                    return -1;
                }
                return i;
            }
        }
        return -1;
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
}
