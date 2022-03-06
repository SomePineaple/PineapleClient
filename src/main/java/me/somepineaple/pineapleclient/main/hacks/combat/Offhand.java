package me.somepineaple.pineapleclient.main.hacks.combat;

import me.somepineaple.pineapleclient.PineapleClient;
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

    Setting switchMode = create("Offhand", "OffhandOffhand", "Totem", combobox("Totem", "Crystal", "Gapple"));
    Setting totemSwitch = create("Totem HP", "OffhandTotemHP", 16, 0, 36);

    Setting gappleInHole = create("Gapple In Hole", "OffhandGapple", false);
    Setting gappleHoleHp = create("Gapple Hole HP", "OffhandGappleHP", 8, 0, 36);

    Setting delay = create("Delay", "OffhandDelay", false);

    private boolean switching = false;
    private int lastSlot;

    @Override
    public void update() {

        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory) {

            if (switching) {
                swapItems(lastSlot, 2);
                return;
            }

            float hp = mc.player.getHealth() + mc.player.getAbsorptionAmount();

            if (hp > totemSwitch.getValue(1)) {
                if (switchMode.in("Crystal") && (PineapleClient.getHackManager().getModuleWithTag("AutoCrystal").isActive() || PineapleClient.getHackManager().getModuleWithTag("AutoCrystalRW").isActive())) {
                    swapItems(get_item_slot(Items.END_CRYSTAL),0);
                    return;
                }
                if (gappleInHole.getValue(true) && hp > gappleHoleHp.getValue(1) && is_in_hole()) {
                    swapItems(get_item_slot(Items.GOLDEN_APPLE), delay.getValue(true) ? 1 : 0);
                    return;
                }
                if (switchMode.in("Totem")) {
                    swapItems(get_item_slot(Items.TOTEM_OF_UNDYING), delay.getValue(true) ? 1 : 0);
                    return;
                }
                if (switchMode.in("Gapple")) {
                    swapItems(get_item_slot(Items.GOLDEN_APPLE), delay.getValue(true) ? 1 : 0);
                    return;
                }
                if (switchMode.in("Crystal") && !PineapleClient.getHackManager().getModuleWithTag("AutoCrystal").isActive()) {
                    swapItems(get_item_slot(Items.TOTEM_OF_UNDYING),0);
                    return;
                }
            } else {
                swapItems(get_item_slot(Items.TOTEM_OF_UNDYING), delay.getValue(true) ? 1 : 0);
                return;
            }

            if (mc.player.getHeldItemOffhand().getItem() == Items.AIR) {
                swapItems(get_item_slot(Items.TOTEM_OF_UNDYING), delay.getValue(true) ? 1 : 0);
            }

        }

    }

    public void swapItems(int slot, int step) {
        if (slot == -1) return;
        if (step == 0) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        }
        if (step == 1) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            switching = true;
            lastSlot = slot;
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
