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

	public boolean state_module;
	public boolean toggle_message;
	public boolean widget_usage;

	public static final Minecraft mc = Minecraft.getMinecraft();

	public Hack(Category category) {
		this.name           = "";
		this.tag            = "";
		this.description    = "";
		this.bind           = -1;
		this.toggle_message = true;
		this.widget_usage   = false;
		this.category 		= category;
	}

	public void set_bind(int key) {
		this.bind = (key);
	}

	public void set_if_can_send_message_toggle(boolean value) {
		this.toggle_message = value;
	}

	public boolean is_active() {
		return this.state_module;
	}

	public boolean using_widget() {
		return this.widget_usage;
	}

	public String get_name() {
		return this.name;
	}

	public String get_tag() {
		return this.tag;
	}

	public String get_description() {
		return this.description;
	}

	public int get_bind(int type) {
		return this.bind;
	}

	public String get_bind(String type) {
		String converted_bind = "null";

		if (get_bind(0) < 0) {
			converted_bind = "NONE";
		}

		if (!(converted_bind.equals("NONE"))) {
			String key     = Keyboard.getKeyName(get_bind(0));
			converted_bind = Character.toUpperCase(key.charAt(0)) + (key.length() != 1 ? key.substring(1).toLowerCase() : "");
		} else {
			converted_bind = "None";
		}

		return converted_bind;
	}

	public Category get_category() {
		return this.category;
	}

	public boolean can_send_message_when_toggle() {
		return this.toggle_message;
	}

	public void set_disable() {
		this.state_module = false;

		disable();

		PineapleEventBus.EVENT_BUS.unsubscribe(this);
	}

	public void set_enable() {
		this.state_module = true;

		enable();

		PineapleEventBus.EVENT_BUS.subscribe(this);
	}

	public void set_active(boolean value) {
		if (this.state_module != value) {
			if (value) {
				set_enable();
			} else {
				set_disable();
			}
		}

		if (!(this.tag.equals("GUI") || this.tag.equals("HUD")) && this.toggle_message) {
			MessageUtil.toggle_message(this);
		}
	}

	public void toggle() {
		set_active(!is_active());
	}

	protected Setting create(String name, String tag, int value, int min, int max) {
		PineapleClient.get_setting_manager().register(new Setting(this, name, tag, value, min, max));

		return PineapleClient.get_setting_manager().get_setting_with_tag(this, tag);
	}

	protected Setting create(String name, String tag, double value, double min, double max) {
		PineapleClient.get_setting_manager().register(new Setting(this, name, tag, value, min, max));

		return PineapleClient.get_setting_manager().get_setting_with_tag(this, tag);
	}

	protected Setting create(String name, String tag, boolean value) {
		PineapleClient.get_setting_manager().register(new Setting(this, name, tag, value));

		return PineapleClient.get_setting_manager().get_setting_with_tag(this, tag);
	}

	protected Setting create(String name, String tag, String value) {
		PineapleClient.get_setting_manager().register(new Setting(this, name, tag, value));

		return PineapleClient.get_setting_manager().get_setting_with_tag(this, tag);
	}

	protected Setting create(String name, String tag, String value, List<String> values) {
		PineapleClient.get_setting_manager().register(new Setting(this, name, tag, values, value));

		return PineapleClient.get_setting_manager().get_setting_with_tag(this, tag);
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