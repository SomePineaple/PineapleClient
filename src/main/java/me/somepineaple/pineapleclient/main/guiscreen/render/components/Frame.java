package me.somepineaple.pineapleclient.main.guiscreen.render.components;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.Draw;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class Frame {
	private final Category category;

	private final ArrayList<ModuleButton> moduleButton;

	private int x;
	private int y;

	private int width;
	private int height;

	private String frameName;
	private final String frameTag;

	private final Draw font = new Draw(1);

	private boolean move;

	private int moveX;
	private int moveY;
	
	private boolean can;

	private final Minecraft mc = Minecraft.getMinecraft();

	public Frame(Category category) {
		this.x = 10;
		this.y = 10;

		this.width  = 100;
		this.height = 27;

		this.category = category;

		this.moduleButton = new ArrayList<>();

		this.frameName = category.get_name();
		this.frameTag = category.get_tag();

		this.moveX = 0;
		this.moveY = 0;

		int size  = PineapleClient.getHackManager().getModulesWithCategory(category).size();
		int count = 0;

		for (Hack modules : PineapleClient.getHackManager().getModulesWithCategory(category)) {
			ModuleButton buttons = new ModuleButton(modules, this);

			buttons.set_y(this.height);

			this.moduleButton.add(buttons);

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

	public void refreshFrame(ModuleButton button, int combo_height) {

		this.height = 25;

		int size  = PineapleClient.getHackManager().getModulesWithCategory(this.category).size();
		int count = 0;

		for (ModuleButton buttons : this.moduleButton) {
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

	public void doesCan(boolean value) {
		this.can = value;
	}

	public void setMove(boolean value) {
		this.move = value;
	}

	public void setMoveX(int x) {
		this.moveX = x;
	}

	public void setMoveY(int y) {
		this.moveY = y;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getName() {
		return this.frameName;
	}

	public String getTag() {
		return this.frameTag;
	}

	public boolean isMoving() {
		return this.move;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public boolean can() {
		return this.can;
	}

	public boolean motion(int mx, int my) {
		return mx >= getX() && my >= getY() && mx <= getX() + getWidth() && my <= getY() + getHeight();
	}

	public boolean motion(String tag, int mx, int my) {
		return mx >= getX() && my >= getY() && mx <= getX() + getWidth() && my <= getY() + font.get_string_height();
	}

	public void crush(int mx, int my) {

		int screen_x = (mc.displayWidth / 2);
		int screen_y = (mc.displayHeight / 2);

		setX(mx - this.moveX);
		setY(my - this.moveY);

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

	public boolean isBinding() {
		boolean value_requested = false;

		for (ModuleButton buttons : this.moduleButton) {
			if (buttons.is_binding()) {
				value_requested = true;
			}
		}

		return value_requested;
	}

	public void doesButtonForDoWidgetsCan(boolean can) {
		for (ModuleButton buttons : this.moduleButton) {
			buttons.does_widgets_can(can);
		}
	}

	public void bind(char char_, int key) {
		for (ModuleButton buttons : this.moduleButton) {
			buttons.bind(char_, key);
		}
	}

	public void mouse(int mx, int my, int mouse) {
		for (ModuleButton buttons : this.moduleButton) {
			buttons.mouse(mx, my, mouse);
		}
	}

	public void mouse_release(int mx, int my, int mouse) {
		for (ModuleButton buttons : this.moduleButton) {
			buttons.button_release(mx, my, mouse);
		}
	}

	public void render(int mx, int my) {
		float[] tick_color = {
			(System.currentTimeMillis() % (360 * 32)) / (360f * 32)
		};

		int nc_r = PineapleClient.clickGui.themeFrameNameR;
		int nc_g = PineapleClient.clickGui.themeFrameNameG;
		int nc_b = PineapleClient.clickGui.themeFrameNameB;
		int nc_a = PineapleClient.clickGui.themeFrameNameA;

		int bg_r = PineapleClient.clickGui.themeFrameBackgroundR;
		int bg_g = PineapleClient.clickGui.themeFrameBackgroundG;
		int bg_b = PineapleClient.clickGui.themeFrameBackgroundB;
		int bg_a = PineapleClient.clickGui.themeFrameBackgroundA;

		int bd_r = PineapleClient.clickGui.themeFrameBorderR;
		int bd_g = PineapleClient.clickGui.themeFrameBorderG;
		int bd_b = PineapleClient.clickGui.themeFrameBorderB;
		int bd_a = PineapleClient.clickGui.themeFrameBorderA;

		this.frameName = this.category.get_name();

		Draw.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, bg_r, bg_g, bg_b, bg_a);
		int border_size = 1;
		Draw.drawRect(this.x - 1, this.y, this.width + 1, this.height, bd_r, bd_g, bd_b, bd_a, border_size, "left-right");
		Draw.drawRect(this.x, this.y, this.x + this.width, this.y + 14, bd_r, bd_g, bd_b, bd_a);
		
		Draw.drawString(this.frameName, this.x + 4, this.y + 4, nc_r, nc_g, nc_b, nc_a);

		if (isMoving()) {
			crush(mx, my);
		}

		for (ModuleButton buttons : this.moduleButton) {
			buttons.setX(this.x + 2);

			buttons.render(mx, my, 2);
		}

		tick_color[0] += 1;
	}
}
