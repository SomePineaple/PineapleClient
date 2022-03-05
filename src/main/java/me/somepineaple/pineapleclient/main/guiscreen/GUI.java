package me.somepineaple.pineapleclient.main.guiscreen;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.components.Frame;
import me.somepineaple.pineapleclient.main.hacks.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

// Hacks.

public class GUI extends GuiScreen {
	private final ArrayList<Frame> frame;

	private Frame current;

	// Frame.
	public int themeFrameNameR = 0;
	public int themeFrameNameG = 0;
	public int themeFrameNameB = 0;
	public int themeFrameNameA = 0;

	public int themeFrameBackgroundR = 0;
	public int themeFrameBackgroundG = 0;
	public int themeFrameBackgroundB = 0;
	public int themeFrameBackgroundA = 0;

	public int themeFrameBorderR = 0;
	public int themeFrameBorderG = 0;
	public int themeFrameBorderB = 0;
	public int themeFrameBorderA = 0;

	// Module.
	public int themeWidgetNameR = 0;
	public int themeWidgetNameG = 0;
	public int themeWidgetNameB = 0;
	public int themeWidgetNameA = 0;

	public int themeWidgetBackgroundR = 0;
	public int themeWidgetBackgroundG = 0;
	public int themeWidgetBackgroundB = 0;
	public int themeWidgetBackgroundA = 0;

	public int themeWidgetBorderR = 0;
	public int themeWidgetBorderG = 0;
	public int themeWidgetBorderB = 0;

	private final Minecraft mc = Minecraft.getMinecraft();

	public GUI() {
		this.frame = new ArrayList<>();
		int frameX = 10;

		for (Category categorys : Category.values()) {
			if (categorys.isHidden()) {
				continue;
			}

			Frame frames = new Frame(categorys);

			frames.setX(frameX);

			this.frame.add(frames);

			frameX += frames.getWidth() + 5;

			this.current = frames;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}


	@Override
	public void onGuiClosed() {
		PineapleClient.getHackManager().getModuleWithTag("GUI").set_active(false);

		PineapleClient.getConfigManager().saveSettings();
	}

	@Override
	protected void keyTyped(char char_, int key) {
		for (Frame frame : this.frame) {
			frame.bind(char_, key);

			if (key == PineapleClient.KEY_GUI_ESCAPE && !frame.isBinding()) {
				mc.displayGuiScreen(null);
			}

			if (key == Keyboard.KEY_DOWN || key == 200) {
				frame.setY(frame.getY()-3);
			}

			if (key == Keyboard.KEY_UP || key == 208) {
				frame.setY(frame.getY()+3);
			}
		}
	}

	@Override
	protected void mouseClicked(int mx, int my, int mouse) {
		for (Frame frames : this.frame) {
			frames.mouse(mx, my, mouse);

			// If left click.
			if (mouse == 0) {
				if (frames.motion(mx, my) && frames.can()) {
					frames.doesButtonForDoWidgetsCan(false);

					this.current = frames;

					this.current.setMove(true);

					this.current.setMoveX(mx - this.current.getX());
					this.current.setMoveY(my - this.current.getY());
				}
			}
		}
	}

	@Override
	protected void mouseReleased(int mx, int my, int state) {
		for (Frame frames : this.frame) {
			frames.doesButtonForDoWidgetsCan(true);
			frames.mouse_release(mx, my, state);
			frames.setMove(false);
		}

		set_current(this.current);
	}

	@Override
	public void drawScreen(int mx, int my, float tick) {
		this.drawDefaultBackground();

		for (Frame frames : this.frame) {
			frames.render(mx, my);
		}
	}

	public void set_current(Frame current) {
		this.frame.remove(current);
		this.frame.add(current);
	}

	public Frame get_current() {
		return this.current;
	}

	public ArrayList<Frame> get_array_frames() {
		return this.frame;
	}

	public Frame get_frame_with_tag(String tag) {
		Frame frame_requested = null;

		for (Frame frames : get_array_frames()) {
			if (frames.getTag().equals(tag)) {
				frame_requested = frames;
			}
		}

		return frame_requested;
	}
}
