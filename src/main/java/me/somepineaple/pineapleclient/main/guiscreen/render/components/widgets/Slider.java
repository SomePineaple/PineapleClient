package me.somepineaple.pineapleclient.main.guiscreen.render.components.widgets;


import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.turok.values.TurokDouble;
import me.somepineaple.pineapleclient.main.guiscreen.render.Draw;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.AbstractWidget;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.Frame;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.ModuleButton;

// Travis.


public class Slider extends AbstractWidget {
	private Frame frame;
	private ModuleButton master;
	private Setting setting;

	private String slider_name;

	private double double_;
	private int    intenger;

	private int x;
	private int y;

	private int width;
	private int height;

	private int save_y;

	private boolean can;
	private boolean compare;
	private boolean click;

	private Draw font = new Draw(1);

	public Slider(Frame frame, ModuleButton master, String tag, int update_postion) {
		this.frame   = frame;
		this.master  = master;
		this.setting = PineapleClient.getSettingManager().getSettingWithTag(master.getModule(), tag);

		this.x = master.get_x();
		this.y = update_postion;

		this.save_y = this.y;

		this.width  = master.get_width();
		this.height = font.get_string_height();

		this.slider_name = this.setting.get_name();

		this.can = true;

		this.double_  = 8192;
		this.intenger = 8192;

		if (this.setting.getType().equals("doubleslider")) {
			this.double_ = this.setting.getValue(1.0);
		} else if (this.setting.getType().equals("integerslider")) {
			this.intenger = this.setting.getValue(1);
		}
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
				this.frame.doesCan(false);

				this.click = true;
			}
		}
	}

	@Override
	public void release(int mx, int my, int mouse) {
		this.click = false;
	}

	@Override
	public void render(int master_y, int separe, int absolute_x, int absolute_y) {
		set_width(this.master.get_width() - separe);

		this.save_y = this.y + master_y;

		int ns_r = PineapleClient.clickGui.themeWidgetNameR;
		int ns_g = PineapleClient.clickGui.themeWidgetNameG;
		int ns_b = PineapleClient.clickGui.themeWidgetNameB;
		int ns_a = PineapleClient.clickGui.themeWidgetNameB;

		int bg_r = PineapleClient.clickGui.themeWidgetBackgroundR;
		int bg_g = PineapleClient.clickGui.themeWidgetBackgroundG;
		int bg_b = PineapleClient.clickGui.themeWidgetBackgroundB;
		int bg_a = PineapleClient.clickGui.themeWidgetBackgroundA;

		if (this.double_ != 8192 && this.intenger == 8192) {
			this.compare = false;
		}

		if (this.double_ == 8192 && this.intenger != 8192) {
			this.compare = true;
		}

		double mouse = Math.min(this.width, Math.max(0, absolute_x - get_x()));

		if (this.click) {
			if (mouse != 0) {
				this.setting.setValue(TurokDouble.round(((mouse / this.width) * (this.setting.getMax(1.0) - this.setting.getMin(1.0)) + this.setting.getMin(1.0))));
			} else {
				this.setting.setValue(this.setting.getMin(1.0));
			}
		}

		String slider_value = !this.compare ? java.lang.Double.toString(this.setting.getValue(this.double_)) : Integer.toString(this.setting.getValue(this.intenger));

		Draw.drawRect(this.x, this.save_y, this.x + (int)((this.width) * (this.setting.getValue(1.0) - this.setting.getMin(1.0)) / (this.setting.getMax(1.0) - this.setting.getMin(1.0))), this.save_y + this.height, bg_r, bg_g, bg_b, bg_a);

		Draw.drawString(this.slider_name, this.x + 2, this.save_y, ns_r, ns_g, ns_b, ns_a);
		Draw.drawString(slider_value, this.x + this.width - separe - font.get_string_width(slider_value) + 2, this.save_y, ns_r, ns_g, ns_b, ns_a);
	}
}
