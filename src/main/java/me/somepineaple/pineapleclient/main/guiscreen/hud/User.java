package me.somepineaple.pineapleclient.main.guiscreen.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.Draw;
import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;
import me.somepineaple.pineapleclient.main.util.TimeUtil;
import net.minecraft.util.math.MathHelper;

public class User extends Pinnable {
	public User() {
		super("User", "User", 1, 0, 0);
	}

	private int scaled_width;
	private int scaled_height;
	private int scale_factor;

	@Override
	public void render() {
		updateResolution();
		int nl_r = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorR").getValue(1);
		int nl_g = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorG").getValue(1);
		int nl_b = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorB").getValue(1);
		int nl_a = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorA").getValue(1);

		int time = TimeUtil.get_hour();
		String line;

		if (time >= 0 && time < 12) {
			line = "Morning, " + ChatFormatting.GOLD + ChatFormatting.BOLD + mc.player.getName() + ChatFormatting.RESET + " you smell good today :)";
		} else if (time >= 12 && time < 16) {
			line = "Afternoon, " + ChatFormatting.GOLD + ChatFormatting.BOLD +  mc.player.getName() + ChatFormatting.RESET + " you're looking good today :)";
		} else if (time >= 16 && time < 24) {
			line = "Evening, " + ChatFormatting.GOLD + ChatFormatting.BOLD +  mc.player.getName() + ChatFormatting.RESET + " you smell good today :)";
		} else {
			line = "Welcome, " + ChatFormatting.GOLD + ChatFormatting.BOLD +  mc.player.getName() + ChatFormatting.RESET + " you're looking fine today :)";
		}

		mc.fontRenderer.drawStringWithShadow(line, scaled_width / 2f - mc.fontRenderer.getStringWidth(line) / 2f, 20f, new Draw.TravisColor(nl_r,nl_g,nl_b,nl_a).hex());

		this.set_width(this.get(line, "width") + 2);
		this.set_height(this.get(line, "height") + 2);
	}

	public void updateResolution() {
		this.scaled_width = mc.displayWidth;
		this.scaled_height = mc.displayHeight;
		this.scale_factor = 1;
		final boolean flag = mc.isUnicode();
		int i = mc.gameSettings.guiScale;
		if (i == 0) {
			i = 1000;
		}
		while (this.scale_factor < i && this.scaled_width / (this.scale_factor + 1) >= 320 && this.scaled_height / (this.scale_factor + 1) >= 240) {
			++this.scale_factor;
		}
		if (flag && this.scale_factor % 2 != 0 && this.scale_factor != 1) {
			--this.scale_factor;
		}
		final double scaledWidthD = this.scaled_width / (double)this.scale_factor;
		final double scaledHeightD = this.scaled_height / (double)this.scale_factor;
		this.scaled_width = MathHelper.ceil(scaledWidthD);
		this.scaled_height = MathHelper.ceil(scaledHeightD);
	}
}
