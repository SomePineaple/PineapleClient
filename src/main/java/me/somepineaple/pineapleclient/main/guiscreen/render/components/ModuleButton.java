package me.somepineaple.pineapleclient.main.guiscreen.render.components;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.Draw;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.widgets.*;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Hack;

import java.util.ArrayList;

// Hacks.

public class ModuleButton {
	private Hack module;
	private Frame master;

	private ArrayList<AbstractWidget> widget;

	private String module_name;

	private boolean opened;

	private int x;
	private int y;

	private int width;
	private int height;

	private int opened_height;

	private int save_y;

	private Draw font = new Draw(1);

	private int border_a    = 200;
	private int border_size = 1;

	private int master_height_cache;

	public int settings_height;

	private int count;

	public ModuleButton(Hack module, Frame master) {
		/**
		 * A value to save the y. When move the frame the save_y does the work.
		 * @param save_y;
		 **/

		this.module = module;
		this.master = master;

		this.widget = new ArrayList<>();

		this.module_name = module.getName();

		this.x = 0;
		this.y = 0;

		this.width  = font.get_string_width(module.getName()) + 5;
		this.height = font.get_string_height();

		this.opened_height = this.height;

		this.save_y = 0;

		this.opened = false;

		this.master_height_cache = master.getHeight();

		this.settings_height = this.y + 10;

		this.count = 0;

		for (Setting settings : PineapleClient.getSettingManager().get_settings_with_hack(module)) {
			if (settings.getType().equals("button")) {
				this.widget.add(new Button(master, this, settings.get_tag(), this.settings_height));

				this.settings_height += 10;

				this.count++;
			}

			if (settings.getType().equals("combobox")) {
				this.widget.add(new Combobox(master, this, settings.get_tag(), this.settings_height));

				this.settings_height += 10;

				this.count++;
			}

			if (settings.getType().equals("label")) {
				this.widget.add(new Label(master, this, settings.get_tag(), this.settings_height));

				this.settings_height += 10;

				this.count++;
			}

			if (settings.getType().equals("doubleslider") || settings.getType().equals("integerslider")) {
				this.widget.add(new Slider(master, this, settings.get_tag(), this.settings_height));

				this.settings_height += 10;

				this.count++;
			}
		}

		int size = PineapleClient.getSettingManager().get_settings_with_hack(module).size();

		if (this.count >= size) {
			this.widget.add(new ButtonBind(master, this, "bind", this.settings_height));

			this.settings_height += 10;
		}
	}

	public Hack getModule() {
		return this.module;
	}

	public Frame get_master() {
		return this.master;
	}

	public void set_pressed(boolean value) {
		this.module.set_active(value);
	}

	public void set_width(int width) {
		this.width = width;
	}

	public void set_height(int height) {
		this.height = height;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void set_y(int y) {
		this.y = y;
	}

	public void set_open(boolean value) {
		this.opened = value;
	}

	public boolean get_state() {
		return this.module.isActive();
	}

	public int get_settings_height() {
		return this.settings_height;
	}

	public int get_cache_height() {
		return this.master_height_cache;
	}

	public int get_width() {
		return this.width;
	}

	public int get_height() {
		return this.height;
	}

	public int get_x() {
		return this.x;
	}

	public int get_y() {
		return this.y;
	}

	public int get_save_y() {
		return this.save_y;
	}

	public boolean is_open() {
		return this.opened;
	}

	public boolean is_binding() {
		boolean value_requested = false;

		for (AbstractWidget widgets : this.widget) {
			if (widgets.is_binding()) {
				value_requested = true;
			}
		}

		return value_requested;
	}

	public boolean motion(int mx, int my) {
		if (mx >= get_x() && my >= get_save_y() && mx <= get_x() + get_width() && my <= get_save_y() + get_height()) {
			return true;
		}

		return false;
	}

	public void does_widgets_can(boolean can) {
		for (AbstractWidget widgets : this.widget) {
			widgets.does_can(can);
		}
	}

	public void bind(char char_, int key) {
		for (AbstractWidget widgets : this.widget) {
			widgets.bind(char_, key);
		}
	}

	public void mouse(int mx, int my, int mouse) {
		for (AbstractWidget widgets : this.widget) {
			widgets.mouse(mx, my, mouse);
		}

		if (mouse == 0) {
			if (motion(mx, my)) {
				this.master.doesCan(false);

				set_pressed(!get_state());
			}
		}

		if (mouse == 1) {
			if (motion(mx, my)) {
				this.master.doesCan(false);

				set_open(!is_open());

				this.master.refreshFrame(this, 0);
			}
		}
	}

	public void button_release(int mx, int my, int mouse) {
		for (AbstractWidget widgets : this.widget) {
			widgets.release(mx, my, mouse);
		}

		this.master.doesCan(true);
	}

	public void render(int mx, int my, int separe) {
		set_width(this.master.getWidth() - separe);

		this.save_y = this.y + this.master.getY() - 10;

		int nm_r = PineapleClient.clickGui.themeWidgetNameR;
		int nm_g = PineapleClient.clickGui.themeWidgetNameG;
		int nm_b = PineapleClient.clickGui.themeWidgetNameB;
		int nm_a = PineapleClient.clickGui.themeWidgetNameA;

		int bg_r = PineapleClient.clickGui.themeWidgetBackgroundR;
		int bg_g = PineapleClient.clickGui.themeWidgetBackgroundG;
		int bg_b = PineapleClient.clickGui.themeWidgetBackgroundB;
		int bg_a = PineapleClient.clickGui.themeWidgetBackgroundA;

		int bd_r = PineapleClient.clickGui.themeWidgetBorderR;
		int bd_g = PineapleClient.clickGui.themeWidgetBorderG;
		int bd_b = PineapleClient.clickGui.themeWidgetBorderB;

		if (this.module.isActive()) {
			Draw.drawRect(this.x, this.save_y, this.x + this.width - separe, this.save_y + this.height, bg_r, bg_g, bg_b, bg_a);

			Draw.drawString(this.module_name, this.x + separe, this.save_y, nm_r, nm_g, nm_b, nm_a);
		} else {
			Draw.drawString(this.module_name, this.x + separe, this.save_y, nm_r, nm_g, nm_b, nm_a);
		}

		for (AbstractWidget widgets : this.widget) {
			widgets.set_x(get_x());

			boolean is_passing_in_widget = this.opened ? widgets.motion_pass(mx, my) : false;

			if (motion(mx, my) || is_passing_in_widget) {
				Draw.drawRect(this.master.getX() - 1, this.save_y, this.master.getWidth() + 1, this.opened_height, bd_r, bd_g, bd_b, border_a, this.border_size, "right-left");
			}

			if (this.opened) {
				this.opened_height = this.height + this.settings_height - 10;

				widgets.render(get_save_y(), separe, mx, my);
			} else {
				this.opened_height = this.height;
			}
		}
	}
}
