package me.somepineaple.pineapleclient.main.guiscreen.hud;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.event.EventHandler;
import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;

public class TPS extends Pinnable {

    public TPS() {
        super("TPS", "TPS", 1, 0, 0);
    }

    @Override
	public void render() {
		int nl_r = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorR").getValue(1);
		int nl_g = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorG").getValue(1);
		int nl_b = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorB").getValue(1);
		int nl_a = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorA").getValue(1);

		String line = "TPS: " + getTPS();

		create_line(line, this.docking(1, line), 2, nl_r, nl_g, nl_b, nl_a);

		this.set_width(this.get(line, "width") + 2);
		this.set_height(this.get(line, "height") + 2);
    }

    public String getTPS() {
        try {
            int tps = Math.round(EventHandler.INSTANCE.getTickRate());
        if (tps >= 16) {
            return "\u00A7a"+Integer.toString(tps);
        } else if (tps >= 10) {
            return "\u00A73"+Integer.toString(tps);
        } else {
            return "\u00A74"+Integer.toString(tps);
        }
        } catch (Exception e) {
            return "oh no " +e;
        }
    }
    
}
