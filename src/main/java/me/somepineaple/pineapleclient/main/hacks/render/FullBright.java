package me.somepineaple.pineapleclient.main.hacks.render;

import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;

public class FullBright extends Hack {
    public FullBright() {
        super(Category.RENDER);

        this.name = "Full Bright";
        this.tag = "FullBright";
        this.description = "Makes everything bright";
    }

    float prev_gamma = 1F;

    @Override
    protected void enable() {
        prev_gamma = mc.gameSettings.gammaSetting;
        mc.gameSettings.gammaSetting = 10;
    }

    @Override
    protected void disable() {
        mc.gameSettings.gammaSetting = prev_gamma;
    }
}
