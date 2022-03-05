package me.somepineaple.pineapleclient.main.guiscreen.hud;

import com.google.common.collect.Lists;
import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.Draw;
import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.DrawnUtil;
import net.minecraft.util.math.MathHelper;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PineapleArrayList extends Pinnable {
	public PineapleArrayList() {
		super("Array List", "PineapleArrayList", 1, 0, 0);
	}

	boolean flag = true;

	private int scaled_width;
	private int scaled_height;
	private int scale_factor;

	@Override
	public void render() {
		updateResolution();
		int position_update_y = 2;

		int nl_r = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorR").getValue(1);
		int nl_g = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorG").getValue(1);
		int nl_b = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorB").getValue(1);
		int nl_a = PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDStringsColorA").getValue(1);

		List<Hack> pretty_modules = PineapleClient.getHackManager().getArrayActiveHacks().stream()
			.sorted(Comparator.comparing(modules -> get(modules.array_detail() == null ? modules.getTag() : modules.getTag() + PineapleClient.g + " [" + PineapleClient.r + modules.array_detail() + PineapleClient.g + "]" + PineapleClient.r, "width")))
			.collect(Collectors.toList());

		int count = 0;

		if (PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDArrayList").in("Top R") || PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDArrayList").in("Top L") ) {
			pretty_modules = Lists.reverse(pretty_modules);
		}

		for (Hack modules : pretty_modules) {

			flag = true;

			if (modules.getCategory().get_tag().equals("GUI")) {
				continue;
			}

			for (String s : DrawnUtil.hidden_tags) {
				if (modules.getTag().equalsIgnoreCase(s)) {
					flag = false;
					break;
				}
				if (!flag) break;
			}
			
			if (flag) {
				String module_name = (
					modules.array_detail() == null ? modules.getTag() :
					modules.getTag() + PineapleClient.g + " [" + PineapleClient.r + modules.array_detail() + PineapleClient.g + "]" + PineapleClient.r
				);

				if (PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDArrayList").in("Free")) {
					create_line(module_name, this.docking(2, module_name), position_update_y, nl_r, nl_g, nl_b, nl_a);

					position_update_y += get(module_name, "height") + 2;

					if (get(module_name, "width") > this.get_width()) {
						this.set_width(get(module_name, "width") + 2);
					}

					this.set_height(position_update_y);
				} else {
					if (PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDArrayList").in("Top R")) {
						mc.fontRenderer.drawStringWithShadow(module_name, scaled_width - 2 - mc.fontRenderer.getStringWidth(module_name), 3 + count * 10, new Draw.TravisColor(nl_r,nl_g,nl_b,nl_a).hex());
						count++;
					}
					if (PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDArrayList").in("Top L")) {
						mc.fontRenderer.drawStringWithShadow(module_name, 2, 3 + count * 10, new Draw.TravisColor(nl_r,nl_g,nl_b,nl_a).hex());
						count++;
					}
					if (PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDArrayList").in("Bottom R")) {
						mc.fontRenderer.drawStringWithShadow(module_name, scaled_width - 2 - mc.fontRenderer.getStringWidth(module_name), scaled_height - (count * 10), new Draw.TravisColor(nl_r,nl_g,nl_b,nl_a).hex());
						count++;
					}
					if (PineapleClient.getSettingManager().getSettingWithTag("HUD", "HUDArrayList").in("Bottom L")) {
						mc.fontRenderer.drawStringWithShadow(module_name, 2, scaled_height - (count * 10), new Draw.TravisColor(nl_r,nl_g,nl_b,nl_a).hex());
						count++;
					}
				}
			}			
		}
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
