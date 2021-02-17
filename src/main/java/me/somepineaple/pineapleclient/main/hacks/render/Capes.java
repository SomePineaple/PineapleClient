package me.somepineaple.pineapleclient.main.hacks.render;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;

public class Capes extends Hack {

    public Capes() {
        super(Category.RENDER);

        this.name = "Capes";
        this.tag = "Capes";
        this.description = "see epic capes behind epic dudes";
    }

    Setting cape = create("Cape", "CapeCape", "New", combobox("New", "OG"));
}
