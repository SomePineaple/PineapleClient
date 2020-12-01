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

    @Override
    protected void enable() {
        mc.gameSettings.gammaSetting = 10;
    }
}
