package me.somepineaple.pineapleclient.main.guiscreen.render.components.widgets;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.turok.draw.RenderHelp;
import me.somepineaple.pineapleclient.main.guiscreen.render.Draw;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.AbstractWidget;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.Frame;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.ModuleButton;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

// Travis.


public class Label extends AbstractWidget {
	private Frame frame;
	private ModuleButton master;
	private Setting setting;

	private String label_name;

	private int x;
	private int y;

	private int width;
	private int height;

	private int save_y;

	private boolean can;
	private boolean info;

	private final Draw font = new Draw(1);

	private int border_size = 0;

	public Label(Frame frame, ModuleButton master, String tag, int update_postion) {
		this.frame   = frame;
		this.master  = master;
		this.setting = PineapleClient.get_setting_manager().get_setting_with_tag(master.get_module(), tag);

		this.x = master.get_x();
		this.y = update_postion;

		this.save_y = this.y;

		this.width  = master.get_width();
		this.height = font.get_string_height();

		this.label_name = this.setting.get_name();

		if (this.setting.get_name().equalsIgnoreCase("info")) {
			this.info = true;
		}

		this.can  = true;
	}

	public Setting get_setting() {
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
		if (mx >= get_x() && my >= get_save_y() && mx <= get_x() + get_width() && my <= get_save_y() + get_height()) {
			return true;
		}

		return false;
	}

	public boolean can() {
		return this.can;
	}

	@Override
	public void mouse(int mx, int my, int mouse) {
		if (mouse == 0) {
			if (motion(mx, my) && this.master.is_open() && can()) {
				this.frame.does_can(false);
			}
		}
	}

	@Override
	public void render(int master_y, int separe, int absolute_x, int absolute_y) {
		set_width(this.master.get_width() - separe);

		String s = "me";

		this.save_y = this.y + master_y;

		int ns_r = PineapleClient.click_gui.theme_widget_name_r;
		int ns_g = PineapleClient.click_gui.theme_widget_name_g;
		int ns_b = PineapleClient.click_gui.theme_widget_name_b;
		int ns_a = PineapleClient.click_gui.theme_widget_name_a;

		int bg_r = PineapleClient.click_gui.theme_widget_background_r;
		int bg_g = PineapleClient.click_gui.theme_widget_background_g;
		int bg_b = PineapleClient.click_gui.theme_widget_background_b;
		int bg_a = PineapleClient.click_gui.theme_widget_background_a;

		int bd_r = PineapleClient.click_gui.theme_widget_border_r;
		int bd_g = PineapleClient.click_gui.theme_widget_border_g;
		int bd_b = PineapleClient.click_gui.theme_widget_border_b;
		int bd_a = 100;

		if (motion(absolute_x, absolute_y)) {
			if (this.setting.get_master().using_widget()) {
				this.setting.get_master().event_widget();

				GL11.glPushMatrix();

				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_BLEND);

				GlStateManager.enableBlend();

				GL11.glPopMatrix();

				RenderHelp.release_gl();
			}
		}

		if (this.info) {
			Draw.draw_string(this.setting.get_value(s), this.x + 2, this.save_y, ns_r, ns_g, ns_b, ns_a);
		} else {
			Draw.draw_string(this.label_name + " \"" + this.setting.get_value(s) + "\"", this.x + 2, this.save_y, ns_r, ns_g, ns_b, ns_a);
		}
	}
}