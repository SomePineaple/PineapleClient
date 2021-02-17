package me.somepineaple.pineapleclient.main.hacks;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;

public class ClickHUD extends Hack {

	public ClickHUD() {
		super(Category.GUI);

		this.name        = "HUD";
		this.tag         = "HUD";
		this.description = "gui for pinnables";
	}

	Setting frame_view = create("info", "HUDStringsList", "Strings");

	Setting strings_r = create("Color R", "HUDStringsColorR", 255, 0, 255);
	Setting strings_g = create("Color G", "HUDStringsColorG", 255, 0, 255);
	Setting strings_b = create("Color B", "HUDStringsColorB", 255, 0, 255);
	Setting strings_a = create("Alpha", "HUDStringsColorA", 230, 0, 255);
	Setting compass_scale = create("Compass Scale", "HUDCompassScale", 16, 1, 60);
	Setting arraylist_mode = create("PineapleArrayList", "HUDArrayList", "Free", combobox("Free", "Top R", "Top L", "Bottom R", "Bottom L"));
	Setting show_all_pots = create("All Potions", "HUDAllPotions", false);
	Setting max_player_list = create("Max Players", "HUDMaxPlayers", 24, 1, 64);

	Setting notification_pop = create("Notify on Totem", "notificationtotem", true);
	Setting notificaiton_enable = create("Notify on Enable", "notificationenable", true);
	Setting notification_armor = create("Armor Notifications", "notificationarmor", true);
	Setting max_notifications = create("Max notifications", "maxnotifications", 3, 1, 10);

	@Override
	public void enable() {
		if (mc.world != null && mc.player != null) {
			PineapleClient.get_hack_manager().get_module_with_tag("GUI").set_active(false);
				
			PineapleClient.click_hud.back = false;

			mc.displayGuiScreen(PineapleClient.click_hud);
		}
	}
}
