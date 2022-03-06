package me.somepineaple.pineapleclient.main.hacks;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.event.PineapleEventBus;
import me.somepineaple.pineapleclient.main.event.events.EventRender;
import me.somepineaple.pineapleclient.main.event.events.EventRenderEntityModel;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.util.MessageUtil;
import me.zero.alpine.fork.listener.Listenable;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hack implements Listenable {
	public Category category;

	public String name;
	public String tag;
	public String description;

	public int bind;

	public boolean moduleState;
	public boolean toggleMessage;
	public boolean widgetUsage;

	public static final Minecraft mc = Minecraft.getMinecraft();

	public Hack(Category category) {
		this.name           = "";
		this.tag            = "";
		this.description    = "";
		this.bind           = -1;
		this.toggleMessage  = true;
		this.widgetUsage    = false;
		this.category 		= category;
	}

	public void setBind(int key) {
		this.bind = (key);
	}

	public void setIfCanSendMessageToggle(boolean value) {
		this.toggleMessage = value;
	}

	public boolean isActive() {
		return this.moduleState;
	}

	public boolean usingWidget() {
		return this.widgetUsage;
	}

	public String getName() {
		return this.name;
	}

	public String getTag() {
		return this.tag;
	}

	public String getDescription() {
		return this.description;
	}

	public int getBind(int type) {
		return this.bind;
	}

	public String getBind(String type) {
		String converted_bind = "null";

		if (getBind(0) < 0) {
			converted_bind = "NONE";
		}

		if (!(converted_bind.equals("NONE"))) {
			String key     = Keyboard.getKeyName(getBind(0));
			converted_bind = Character.toUpperCase(key.charAt(0)) + (key.length() != 1 ? key.substring(1).toLowerCase() : "");
		} else {
			converted_bind = "None";
		}

		return converted_bind;
	}

	public Category getCategory() {
		return this.category;
	}

	public boolean canSendMessageWhenToggle() {
		return this.toggleMessage;
	}

	public void setDisable() {
		this.moduleState = false;
		PineapleEventBus.EVENT_BUS.unsubscribe(this);

		disable();
	}

	public void set_enable() {
		this.moduleState = true;

		enable();

		PineapleEventBus.EVENT_BUS.subscribe(this);
	}

	public void set_active(boolean value) {
		if (this.moduleState != value) {
			if (value) {
				set_enable();
			} else {
				setDisable();
			}
		}

		if (!(this.tag.equals("GUI") || this.tag.equals("HUD")) && this.toggleMessage) {
			MessageUtil.toggle_message(this);
		}
	}

	public void toggle() {
		set_active(!isActive());
	}

	protected Setting create(String name, String tag, int value, int min, int max) {
		PineapleClient.getSettingManager().register(new Setting(this, name, tag, value, min, max));

		return PineapleClient.getSettingManager().getSettingWithTag(this, tag);
	}

	protected Setting create(String name, String tag, double value, double min, double max) {
		PineapleClient.getSettingManager().register(new Setting(this, name, tag, value, min, max));

		return PineapleClient.getSettingManager().getSettingWithTag(this, tag);
	}

	protected Setting create(String name, String tag, boolean value) {
		PineapleClient.getSettingManager().register(new Setting(this, name, tag, value));

		return PineapleClient.getSettingManager().getSettingWithTag(this, tag);
	}

	protected Setting create(String name, String tag, String value) {
		PineapleClient.getSettingManager().register(new Setting(this, name, tag, value));

		return PineapleClient.getSettingManager().getSettingWithTag(this, tag);
	}

	protected Setting create(String name, String tag, String value, List<String> values) {
		PineapleClient.getSettingManager().register(new Setting(this, name, tag, values, value));

		return PineapleClient.getSettingManager().getSettingWithTag(this, tag);
	}

	protected List<String> combobox(String... item) {

		return new ArrayList<>(Arrays.asList(item));
	}

	public void render(EventRender event) {
		// 3d
	}

	public void render() {
		// 2d
	}

	public void update() {

	}

	public void event_widget() {

	}

	protected void disable() {

	}

	protected void enable() {

	}

	public String array_detail() {
		return null;
	}

	public void on_render_model(final EventRenderEntityModel event) {}
}
