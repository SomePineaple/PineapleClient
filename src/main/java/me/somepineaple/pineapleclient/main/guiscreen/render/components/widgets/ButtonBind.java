package me.somepineaple.pineapleclient.main.guiscreen.render.components.widgets;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.Draw;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.AbstractWidget;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.Frame;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.ModuleButton;

public class ButtonBind extends AbstractWidget {
	private Frame frame;
	private ModuleButton master;

	private String points;

	private int x;
	private int y;

	private int width;
	private int height;

	private int save_y;

	private float tick;

	private boolean can;
	private boolean waiting;

	private Draw font = new Draw(1);

	public ButtonBind(Frame frame, ModuleButton master, String tag, int update_postion) {
		this.frame   = frame;
		this.master  = master;

		this.x = master.get_x();
		this.y = update_postion;

		this.save_y = this.y;

		this.width  = master.get_width();
		this.height = font.get_string_height();

		this.can    = true;
		this.points = ".";
		this.tick   = 0;
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
	public boolean is_binding() {
		return this.waiting;
	}

	@Override
	public void bind(char char_, int key) {
		if (this.waiting) {
			switch (key) {
				case PineapleClient.KEY_GUI_ESCAPE: {
					this.waiting = false;

					break;
				}

				case PineapleClient.KEY_DELETE: {
					this.master.get_module().set_bind(0);

					this.waiting = false;

					break;
				}

				default : {
					this.master.get_module().set_bind(key);

					this.waiting = false;

					break;
				}
			}
		}
	}

	@Override
	public void mouse(int mx, int my, int mouse) {
		if (mouse == 0) {
			if (motion(mx, my) && this.master.is_open() && can()) {
				this.frame.doesCan(false);

				this.waiting = true;
			}
		}
	}

	@Override
	public void render(int master_y, int separe, int absolute_x, int absolute_y) {
		set_width(this.master.get_width() - separe);

		float[] tick_color = {
			(System.currentTimeMillis() % (360 * 32)) / (360f * 32)
		};

		if (this.waiting) {
			if (this.tick >= 15) {
				this.points = "..";
			}

			if (this.tick >= 30) {
				this.points = "...";
			}

			if (this.tick >= 45) {
				this.points = ".";
				this.tick   = 0.0f;
			}
		}

		this.save_y = this.y + master_y;

		int ns_r = PineapleClient.clickGui.themeWidgetNameR;
		int ns_g = PineapleClient.clickGui.themeWidgetNameG;
		int ns_b = PineapleClient.clickGui.themeWidgetNameB;
		int ns_a = PineapleClient.clickGui.themeWidgetNameA;

		int bg_r = PineapleClient.clickGui.themeWidgetBackgroundR;
		int bg_g = PineapleClient.clickGui.themeWidgetBackgroundG;
		int bg_b = PineapleClient.clickGui.themeWidgetBackgroundB;
		int bg_a = PineapleClient.clickGui.themeWidgetBackgroundA;

		if (this.waiting) {
			Draw.drawRect(get_x(), this.save_y, get_x() + this.width, this.save_y + this.height, bg_r, bg_g, bg_b, bg_a);

			this.tick += 0.5f;

			Draw.drawString("Listening " + this.points, this.x + 2, this.save_y, ns_r, ns_g, ns_b, ns_a);
		} else {
			Draw.drawString("Bind <" + this.master.get_module().getBind("string") + ">", this.x + 2, this.save_y, ns_r, ns_g, ns_b, ns_a);
		}

		tick_color[0] += 5;
	}
}
