package me.somepineaple.pineapleclient.main.guiscreen.hud;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;
import net.minecraft.client.Minecraft;

public class FPS extends Pinnable {
    
    public FPS() {
        super("Fps", "Fps", 1, 0, 0);
    }

    @Override
	public void render() {
		int nl_r = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorR").get_value(1);
		int nl_g = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorG").get_value(1);
		int nl_b = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorB").get_value(1);
		int nl_a = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorA").get_value(1);

		String line = "FPS: " + get_fps();

		create_line(line, this.docking(1, line), 2, nl_r, nl_g, nl_b, nl_a);

		this.set_width(this.get(line, "width") + 2);
		this.set_height(this.get(line, "height") + 2);
	}
	
    public String get_fps() {
		int fps = Minecraft.getDebugFPS();
		if (fps >= 60) {
			return "\u00A7a"+Integer.toString(fps);
		} else if (fps >= 30) {
			return "\u00A73"+Integer.toString(fps);
		} else {
			return "\u00A74"+Integer.toString(fps);
		}
    }
}
