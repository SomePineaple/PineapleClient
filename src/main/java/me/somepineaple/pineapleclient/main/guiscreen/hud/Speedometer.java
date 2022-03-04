package me.somepineaple.pineapleclient.main.guiscreen.hud;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;

public class Speedometer extends Pinnable {

    public Speedometer() {
        super("Speedometer", "Speedometer", 1, 0, 0);
    }

    @Override
    public void render() {

        int nl_r = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorR").getValue(1);
        int nl_g = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorG").getValue(1);
        int nl_b = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorB").getValue(1);
        int nl_a = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorA").getValue(1);

        final double x = mc.player.posX - mc.player.prevPosX;
        final double z = mc.player.posZ - mc.player.prevPosZ;
        final float tr = (mc.timer.tickLength / 1000.0f);

        String bps = "M/s: " + new DecimalFormat("#.#").format(MathHelper.sqrt(x * x + z * z) / tr);

        create_line(bps, this.docking(1, bps), 2, nl_r, nl_g, nl_b, nl_a);

        this.set_width(this.get(bps, "width") + 2);
        this.set_height(this.get(bps, "height")  + 2);
    }
}
