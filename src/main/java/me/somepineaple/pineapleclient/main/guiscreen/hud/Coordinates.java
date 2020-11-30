package me.somepineaple.pineapleclient.main.guiscreen.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.somepineaple.pineapleclient.Pineapleclient;
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
		int nl_r = Pineapleclient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorR").get_value(1);
		int nl_g = Pineapleclient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorG").get_value(1);
		int nl_b = Pineapleclient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorB").get_value(1);
		int nl_a = Pineapleclient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorA").get_value(1);

		String x = Pineapleclient.g + "[" + Pineapleclient.r + Integer.toString((int) (mc.player.posX)) + Pineapleclient.g + "]" + Pineapleclient.r;
		String y = Pineapleclient.g + "[" + Pineapleclient.r + Integer.toString((int) (mc.player.posY)) + Pineapleclient.g + "]" + Pineapleclient.r;
		String z = Pineapleclient.g + "[" + Pineapleclient.r + Integer.toString((int) (mc.player.posZ)) + Pineapleclient.g + "]" + Pineapleclient.r;

		String x_nether = Pineapleclient.g + "[" + Pineapleclient.r + Long.toString(Math.round(mc.player.dimension != -1 ? (mc.player.posX / 8) : (mc.player.posX * 8))) + Pineapleclient.g + "]" + Pineapleclient.r;
		String z_nether = Pineapleclient.g + "[" + Pineapleclient.r + Long.toString(Math.round(mc.player.dimension != -1 ? (mc.player.posZ / 8) : (mc.player.posZ * 8))) + Pineapleclient.g + "]" + Pineapleclient.r;

		String line = "XYZ " + x + y + z + " XZ " + x_nether + z_nether;

		create_line(line, this.docking(1, line), 2, nl_r, nl_g, nl_b, nl_a);

		this.set_width(this.get(line, "width"));
		this.set_height(this.get(line, "height") + 2);
	}
}