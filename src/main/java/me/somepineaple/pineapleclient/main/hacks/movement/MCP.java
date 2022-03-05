package me.somepineaple.pineapleclient.main.hacks.movement;

import org.lwjgl.input.Mouse;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult.Type;

public class MCP extends Hack {
    public MCP () {
        super(Category.MOVEMENT);
        this.name = "MCP";
        this.tag = "MCP";
        this.description = "Throws pearls when you press middle mouse";
    }

    Setting mode = create("Mode", "mcpmode", "Midclick", combobox("Midclick", "Toggle"));
    Setting antiFriend = create("Ignore MCF", "mcpantimcf", false);

    private boolean clicked = false;

    @Override
    public void enable () {
        if (mode.in("Toggle")) {
            throwPearl();
            set_active(false);
        }
    }

    @Override
    public void update() {
        if (mc.currentScreen != null) return;
        if (mode.in("Midclick")) {
            if (Mouse.isButtonDown(2)) {
                if (!clicked) {
                    throwPearl();
                }
                clicked = true;
            } else {
                clicked = false;
            }
        }
    }

    private void throwPearl() {
        if (!antiFriend.getValue(true) && mc.objectMouseOver != null &&
            mc.objectMouseOver.typeOfHit == Type.ENTITY && 
            PineapleClient.getHackManager().getModuleWithTag("MiddleclickFriends").isActive()) {
            return;
        }
        int pearlSlot = findPearlSlot();
        if (pearlSlot == -1) {
            return;
        }
        int prevSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = pearlSlot;
        mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
        mc.player.inventory.currentItem = prevSlot;
    }

    private int findPearlSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ENDER_PEARL) {
                return i;
            }
        }
        return -1;
    }
}
