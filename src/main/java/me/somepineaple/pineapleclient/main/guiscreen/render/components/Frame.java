package me.somepineaple.pineapleclient.main.guiscreen.render.components;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.Draw;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;


public class Frame {
	private final Category category;

	private final ArrayList<ModuleButton> module_button;

	private int x;
	private int y;

	private int width;
	private int height;

	private String frame_name;
	private final String frame_tag;

	private final Draw font = new Draw(1);

	private boolean move;

	private int move_x;
	private int move_y;
	
	private boolean can;

	private final Minecraft mc = Minecraft.getMinecraft();

	public Frame(Category category) {
		this.x = 10;
		this.y = 10;

		this.width  = 100;
		this.height = 27;

		this.category = category;

		this.module_button = new ArrayList<>();

		this.frame_name = category.get_name();
		this.frame_tag  = category.get_tag();

		this.move_x = 0;
		this.move_y = 0;

		int size  = PineapleClient.get_hack_manager().get_modules_with_category(category).size();
		int count = 0;

		for (Hack modules : PineapleClient.get_hack_manager().get_modules_with_category(category)) {
			ModuleButton buttons = new ModuleButton(modules, this);

			buttons.set_y(this.height);

			this.module_button.add(buttons);

			count++;

			if (count >= size) {
				this.height += 10;
			} else {
				this.height += 17;
			}
		}

		this.move = false;
		this.can  = true;
	}

	public void refresh_frame(ModuleButton button, int combo_height) {

		this.height = 25;

		int size  = PineapleClient.get_hack_manager().get_modules_with_category(this.category).size();
		int count = 0;

		for (ModuleButton buttons : this.module_button) {
			buttons.set_y(this.height);

			count++;

			int compare;
			if (count >= size) {
				compare = 10;
			} else {
				compare = 17;
			}

			if (buttons.is_open()) {
				if (compare == 10) {
					this.height += buttons.get_settings_height() - compare;
				} else {
					this.height += buttons.get_settings_height();
				}
			} else {
				this.height += compare;
			}
		}
	}

	public void does_can(boolean value) {
		this.can = value;
	}

	public void set_move(boolean value) {
		this.move = value;
	}

	public void set_move_x(int x) {
		this.move_x = x;
	}

	public void set_move_y(int y) {
		this.move_y = y;
	}

	public void set_width(int width) {
		this.width = width;
	}

	public void set_height(int height) {
		this.height = height;
	}

	public void set_x(int x) {
		this.x = x;
	}

	public void set_y(int y) {
		this.y = y;
	}

	public String get_name() {
		return this.frame_name;
	}

	public String get_tag() {
		return this.frame_tag;
	}

	public boolean is_moving() {
		return this.move;
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

	public boolean can() {
		return this.can;
	}

	public boolean motion(int mx, int my) {
		return mx >= get_x() && my >= get_y() && mx <= get_x() + get_width() && my <= get_y() + get_height();
	}

	public boolean motion(String tag, int mx, int my) {
		return mx >= get_x() && my >= get_y() && mx <= get_x() + get_width() && my <= get_y() + font.get_string_height();
	}

	public void crush(int mx, int my) {

		int screen_x = (mc.displayWidth / 2);
		int screen_y = (mc.displayHeight / 2);

		set_x(mx - this.move_x);
		set_y(my - this.move_y);

		if (this.x + this.width >= screen_x) {
			this.x = screen_x - this.width - 1;
		}

		if (this.x <= 0) {
			this.x = 1;
		}

		if (this.y + this.height >= screen_y) {
			this.y = screen_y - this.height - 1;
		}

		if (this.y <= 0) {
			this.y = 1;
		}

		if (this.x % 2 != 0) {
			this.x += this.x % 2;
		}

		if (this.y % 2 != 0) {
			this.y += this.y % 2;
		}
	}

	public boolean is_binding() {
		boolean value_requested = false;

		for (ModuleButton buttons : this.module_button) {
			if (buttons.is_binding()) {
				value_requested = true;
			}
		}

		return value_requested;
	}

	public void does_button_for_do_widgets_can(boolean can) {
		for (ModuleButton buttons : this.module_button) {
			buttons.does_widgets_can(can);
		}
	}

	public void bind(char char_, int key) {
		for (ModuleButton buttons : this.module_button) {
			buttons.bind(char_, key);
		}
	}

	public void mouse(int mx, int my, int mouse) {
		for (ModuleButton buttons : this.module_button) {
			buttons.mouse(mx, my, mouse);
		}
	}

	public void mouse_release(int mx, int my, int mouse) {
		for (ModuleButton buttons : this.module_button) {
			buttons.button_release(mx, my, mouse);
		}
	}

	public void render(int mx, int my) {
		float[] tick_color = {
			(System.currentTimeMillis() % (360 * 32)) / (360f * 32)
		};

		int nc_r = PineapleClient.click_gui.theme_frame_name_r;
		int nc_g = PineapleClient.click_gui.theme_frame_name_g;
		int nc_b = PineapleClient.click_gui.theme_frame_name_b;
		int nc_a = PineapleClient.click_gui.theme_frame_name_a;

		int bg_r = PineapleClient.click_gui.theme_frame_background_r;
		int bg_g = PineapleClient.click_gui.theme_frame_background_g;
		int bg_b = PineapleClient.click_gui.theme_frame_background_b;
		int bg_a = PineapleClient.click_gui.theme_frame_background_a;

		int bd_r = PineapleClient.click_gui.theme_frame_border_r;
		int bd_g = PineapleClient.click_gui.theme_frame_border_g;
		int bd_b = PineapleClient.click_gui.theme_frame_border_b;
		int bd_a = PineapleClient.click_gui.theme_frame_border_a;

		this.frame_name = this.category.get_name();

		Draw.draw_rect(this.x, this.y, this.x + this.width, this.y + this.height, bg_r, bg_g, bg_b, bg_a);
		int border_size = 1;
		Draw.draw_rect(this.x - 1, this.y, this.width + 1, this.height, bd_r, bd_g, bd_b, bd_a, border_size, "left-right");
		Draw.draw_rect(this.x, this.y, this.x + this.width, this.y + 14, bd_r, bd_g, bd_b, bd_a);
		
		Draw.draw_string(this.frame_name, this.x + 4, this.y + 4, nc_r, nc_g, nc_b, nc_a);

		if (is_moving()) {
			crush(mx, my);
		}

		for (ModuleButton buttons : this.module_button) {
			buttons.set_x(this.x + 2);

			buttons.render(mx, my, 2);
		}

		tick_color[0] += 1;
	}
}