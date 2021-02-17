package me.somepineaple.pineapleclient.main.guiscreen.hud;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;
import me.somepineaple.pineapleclient.main.util.TimeUtil;

public class Time extends Pinnable {
    
    public Time() {
        super("Time", "Time", 1, 0, 0);
    }

    @Override
	public void render() {
		int nl_r = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorR").get_value(1);
		int nl_g = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorG").get_value(1);
		int nl_b = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorB").get_value(1);
		int nl_a = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorA").get_value(1);

		String line = "";

		line += TimeUtil.get_hour() < 10 ? "0" + TimeUtil.get_hour() : TimeUtil.get_hour();
		line += ":";
		line += TimeUtil.get_minuite() < 10 ? "0" + TimeUtil.get_minuite() : TimeUtil.get_minuite();
		line += ":";
		line += TimeUtil.get_second() < 10 ? "0" + TimeUtil.get_second() : TimeUtil.get_second();

		create_line(line, this.docking(1, line), 2, nl_r, nl_g, nl_b, nl_a);

		this.set_width(this.get(line, "width") + 2);
		this.set_height(this.get(line, "height") + 2);
	}
}
