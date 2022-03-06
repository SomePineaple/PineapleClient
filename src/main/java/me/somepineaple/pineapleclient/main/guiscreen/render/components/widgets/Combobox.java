package me.somepineaple.pineapleclient.main.guiscreen.render.components.widgets;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.Draw;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.AbstractWidget;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.Frame;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.ModuleButton;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;

import java.util.ArrayList;


public class Combobox extends AbstractWidget {
	private final ArrayList<String> values;

	private final Frame frame;
	private final ModuleButton master;
	private final Setting setting;

	private final String comboboxName;

	private int x;
	private int y;

	private int width;
	private int height;

	private int combobox_actual_value;

	private int save_y;

	private boolean can;

	public Combobox(Frame frame, ModuleButton master, String tag, int updatePostion) {
		this.values  = new ArrayList<>();
		this.frame   = frame;
		this.master  = master;
		this.setting = PineapleClient.getSettingManager().getSettingWithTag(master.getModule(), tag);

		this.x = master.get_x();
		this.y = updatePostion;

		this.save_y = this.y;

		this.width  = master.get_width();
		Draw font = new Draw(1);
		this.height = font.get_string_height();

		this.comboboxName = this.setting.get_name();

		this.can = true;

		this.values.addAll(this.setting.getValues());

		for (int i = 0; i >= this.values.size(); i++) {
			if (this.values.get(i).equals(this.setting.getCurrentValue())) {
				this.combobox_actual_value = i;
				break;
			}
		}
	}

	public Setting getSetting() {
		return this.setting;
	}

	@Override
	public void does_can(boolean value) {
		this.can = value;
	}

	@Override
	public void set_x(int x) {
		this.x = x;
	}

	@Override
	public void set_y(int y) {
		this.y = y;
	}

	@Override
	public void set_width(int width) {
		this.width = width;
	}

	@Override
	public void set_height(int height) {
		this.height = height;
	}

	@Override
	public int get_x() {
		return this.x;
	}

	@Override
	public int get_y() {
		return this.y;
	}

	@Override
	public int get_width() {
		return this.width;
	}

	@Override
	public int get_height() {
		return this.height;
	}

	public int get_save_y() {
		return this.save_y;
	}

	@Override
	public boolean motion_pass(int mx, int my) {
		return motion(mx, my);
	}

	public boolean motion(int mx, int my) {
		return mx >= get_x() && my >= get_save_y() && mx <= get_x() + get_width() && my <= get_save_y() + get_height();
	}

	public boolean can() {
		return this.can;
	}

	@Override
	public void mouse(int mx, int my, int mouse) {
		if (mouse == 0) {
			if (motion(mx, my) && this.master.is_open() && can()) {
				this.frame.doesCan(false);

				this.setting.set_current_value(this.values.get(this.combobox_actual_value));

				this.combobox_actual_value++;
			}
		}
	}

	@Override
	public void render(int master_y, int separe, int absolute_x, int absolute_y) {
		set_width(this.master.get_width() - separe);

		this.save_y = this.y + master_y;

		int ns_r = PineapleClient.clickGui.themeWidgetNameR;
		int ns_g = PineapleClient.clickGui.themeWidgetNameG;
		int ns_b = PineapleClient.clickGui.themeWidgetNameB;
		int ns_a = PineapleClient.clickGui.themeWidgetNameB;

		Draw.drawString(this.comboboxName + " " + this.setting.getCurrentValue(), this.x + 2, this.save_y, ns_r, ns_g, ns_b, ns_a);

		if (this.combobox_actual_value >= this.values.size()) {
			this.combobox_actual_value = 0;
		}
	}
}
