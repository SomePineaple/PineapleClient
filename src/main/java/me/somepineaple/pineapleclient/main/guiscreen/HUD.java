package me.somepineaple.pineapleclient.main.guiscreen;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Frame;
import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.PinnableButton;
import net.minecraft.client.gui.GuiScreen;

public class HUD extends GuiScreen {
	private final Frame frame;

	public boolean on_gui;
	public boolean back;

	public HUD() {
		this.frame  = new Frame("PineapleClient HUD", "HUD", 40, 40);
		this.back   = false;
		this.on_gui = false;
	}

	public Frame get_frame_hud() {
		return this.frame;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void initGui() {
		this.on_gui = true;

		Frame.nc_r = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUINameFrameR").get_value(1);
		Frame.nc_g = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUINameFrameG").get_value(1);
		Frame.nc_b = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUINameFrameB").get_value(1);
		
		Frame.bg_r = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBackgroundFrameR").get_value(1);
		Frame.bg_g = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBackgroundFrameG").get_value(1);
		Frame.bg_b = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBackgroundFrameB").get_value(1);
		Frame.bg_a = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBackgroundFrameA").get_value(1);
		
		Frame.bd_r = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBorderFrameR").get_value(1);
		Frame.bd_g = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBorderFrameG").get_value(1);
		Frame.bd_b = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBorderFrameB").get_value(1);
		Frame.bd_a = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBorderFrameA").get_value(1);
		
		Frame.bdw_r = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBorderWidgetR").get_value(1);
		Frame.bdw_g = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBorderWidgetG").get_value(1);
		Frame.bdw_b = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBorderWidgetB").get_value(1);
		Frame.bdw_a = 255;

		PinnableButton.nc_r = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUINameWidgetR").get_value(1);
		PinnableButton.nc_g = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUINameWidgetG").get_value(1);
		PinnableButton.nc_b = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUINameWidgetB").get_value(1);
	
		PinnableButton.bg_r = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBackgroundWidgetR").get_value(1);
		PinnableButton.bg_g = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBackgroundWidgetG").get_value(1);
		PinnableButton.bg_b = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBackgroundWidgetB").get_value(1);
		PinnableButton.bg_a = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBackgroundWidgetA").get_value(1);
	
		PinnableButton.bd_r = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBorderWidgetR").get_value(1);
		PinnableButton.bd_g = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBorderWidgetG").get_value(1);
		PinnableButton.bd_b = PineapleClient.get_setting_manager().get_setting_with_tag("GUI", "ClickGUIBorderWidgetB").get_value(1);
	}

	@Override
	public void onGuiClosed() {
		if (this.back) {
			PineapleClient.get_hack_manager().get_module_with_tag("GUI").set_active(true);
			PineapleClient.get_hack_manager().get_module_with_tag("HUD").set_active(false);
		} else {
			PineapleClient.get_hack_manager().get_module_with_tag("HUD").set_active(false);
			PineapleClient.get_hack_manager().get_module_with_tag("GUI").set_active(false);
		}

		this.on_gui = false;

		PineapleClient.get_config_manager().save_settings();
	}

	@Override
	protected void mouseClicked(int mx, int my, int mouse) {
		this.frame.mouse(mx, my, mouse);

		if (mouse == 0) {
			if (this.frame.motion(mx, my) && this.frame.can()) {
				this.frame.set_move(true);

				this.frame.set_move_x(mx - this.frame.get_x());
				this.frame.set_move_y(my - this.frame.get_y());
			}
		}
	}

	@Override
	protected void mouseReleased(int mx, int my, int state) {
		this.frame.release(mx, my, state);

		this.frame.set_move(false);
	}

	@Override
	public void drawScreen(int mx, int my, float tick) {
		this.drawDefaultBackground();

		this.frame.render(mx, my, 2);
	}
}
