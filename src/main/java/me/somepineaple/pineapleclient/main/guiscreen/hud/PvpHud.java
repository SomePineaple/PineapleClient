package me.somepineaple.pineapleclient.main.guiscreen.hud;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PvpHud extends Pinnable {
    
    public PvpHud() {
        super("PVP Hud", "pvphud", 1, 0, 0);
    }

    @Override
	public void render() {
		int nl_r = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorR").getValue(1);
		int nl_g = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorG").getValue(1);
		int nl_b = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorB").getValue(1);
        int nl_a = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorA").getValue(1);

        String totem = "Totems: " + get_totems();
        String trap = "Trap: " + trap_enabled();
        String aura = "Aura: " + aura_enabled();
        String surround = "Surround: " + surround_enabled();
        String holefill = "Holefill: " + holefill_enabled();
        String socks = "Socks: " + socks_enabled();
        String selftrap = "Self Trap: " + selftrap_enabled();

        create_line(totem, this.docking(1, totem), 2, nl_r, nl_g, nl_b, nl_a);
        create_line(aura, this.docking(1, aura), 13, nl_r, nl_g, nl_b, nl_a);
        create_line(trap, this.docking(1, trap), 24, nl_r, nl_g, nl_b, nl_a);
        create_line(surround, this.docking(1, surround), 34, nl_r, nl_g, nl_b, nl_a);
        create_line(holefill, this.docking(1, holefill), 45, nl_r, nl_g, nl_b, nl_a);
        create_line(socks, this.docking(1, socks), 56, nl_r, nl_g, nl_b, nl_a);
        create_line(selftrap, this.docking(1, selftrap), 67, nl_r, nl_g, nl_b, nl_a);

		this.set_width(this.get(surround, "width") + 2);
		this.set_height(this.get(surround, "height") * 5);
    }

    public String selftrap_enabled() {
        try {
            if (PineapleClient.getHackManager().getModuleWithTag("SelfTrap").isActive()) {
                return "\u00A7a 1";
            }
            return "\u00A74 0";
        } catch (Exception e) {
            return "0";
        }
    }

    public String trap_enabled() {
        try {
            if (PineapleClient.getHackManager().getModuleWithTag("Trap").isActive()) {
                return "\u00A7a 1";
            }
            return "\u00A74 0";
        } catch (Exception e) {
            return "0";
        }
    }

    public String aura_enabled() {

        try {
            if (PineapleClient.getHackManager().getModuleWithTag("AutoCrystal").isActive()) {
                return "\u00A7a 1";
            }
            return "\u00A74 0";
        } catch (Exception e) {
            return "0";
        }
    }

    public String socks_enabled() {
        try {
            if (PineapleClient.getHackManager().getModuleWithTag("Socks").isActive()) {
                return "\u00A7a 1";
            }
            return "\u00A74 0";
        } catch (Exception e) {
            return "0";
        }
    }

    public String surround_enabled() {

        try {
            if (PineapleClient.getHackManager().getModuleWithTag("Surround").isActive()) {
                return "\u00A7a 1";
            }
            return "\u00A74 0";
        } catch (Exception e) {
            return "0";
        }
    }

    public String holefill_enabled() {

        try {
            if (PineapleClient.getHackManager().getModuleWithTag("HoleFill").isActive()) {
                return "\u00A7a 1";
            }
            return "\u00A74 0";
        } catch (Exception e) {
            return "0";
        }
    }

    public String get_totems() {

        try {

            int totems = offhand() + mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();

            if (totems > 1) {
                return "\u00A7a "+totems;
            } else {
                return "\u00A74 "+totems;
            }

        } catch (Exception e) {
            return "0";
        }
    }

    public int offhand() {
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            return 1;
        }
        return 0;
    }
}
