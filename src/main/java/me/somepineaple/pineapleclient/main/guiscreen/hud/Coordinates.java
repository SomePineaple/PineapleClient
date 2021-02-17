package me.somepineaple.pineapleclient.main.guiscreen.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;

public class Coordinates extends Pinnable {
	ChatFormatting dg = ChatFormatting.DARK_GRAY;
	ChatFormatting db = ChatFormatting.DARK_BLUE;
	ChatFormatting dr = ChatFormatting.DARK_RED;

	public Coordinates() {
		super("Coordinates", "Coordinates", 1, 0, 0);
	}

	@Override
	public void render() {
		int nl_r = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorR").get_value(1);
		int nl_g = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorG").get_value(1);
		int nl_b = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorB").get_value(1);
		int nl_a = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorA").get_value(1);

		String x = PineapleClient.g + "[" + PineapleClient.r + Integer.toString((int) (mc.player.posX)) + PineapleClient.g + "]" + PineapleClient.r;
		String y = PineapleClient.g + "[" + PineapleClient.r + Integer.toString((int) (mc.player.posY)) + PineapleClient.g + "]" + PineapleClient.r;
		String z = PineapleClient.g + "[" + PineapleClient.r + Integer.toString((int) (mc.player.posZ)) + PineapleClient.g + "]" + PineapleClient.r;

		String x_nether = PineapleClient.g + "[" + PineapleClient.r + Long.toString(Math.round(mc.player.dimension != -1 ? (mc.player.posX / 8) : (mc.player.posX * 8))) + PineapleClient.g + "]" + PineapleClient.r;
		String z_nether = PineapleClient.g + "[" + PineapleClient.r + Long.toString(Math.round(mc.player.dimension != -1 ? (mc.player.posZ / 8) : (mc.player.posZ * 8))) + PineapleClient.g + "]" + PineapleClient.r;

		String line = "XYZ " + x + y + z + " XZ " + x_nether + z_nether;

		create_line(line, this.docking(1, line), 2, nl_r, nl_g, nl_b, nl_a);

		this.set_width(this.get(line, "width"));
		this.set_height(this.get(line, "height") + 2);
	}
}
