package me.somepineaple.pineapleclient.main.manager;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Hack;

import java.util.ArrayList;

public class SettingManager {

	public ArrayList<Setting> array_setting;

	public SettingManager() {
		this.array_setting = new ArrayList<>();
	}

	public void register(Setting setting) {
		this.array_setting.add(setting);
	}

	public ArrayList<Setting> get_array_settings() {
		return this.array_setting;
	}

	public Setting getSettingWithTag(Hack module, String tag) {
		Setting setting_requested = null;

		for (Setting settings : get_array_settings()) {
			if (settings.get_master().equals(module) && settings.get_tag().equalsIgnoreCase(tag)) {
				setting_requested = settings;
			}
		}

		return setting_requested;
	}

	public Setting getSettingWithTag(String tag, String tag_) {
		Setting setting_requested = null;

		for (Setting settings : get_array_settings()) {
			if (settings.get_master().getTag().equalsIgnoreCase(tag) && settings.get_tag().equalsIgnoreCase(tag_)) {
				setting_requested = settings;
				break;
			}
		}

		return setting_requested;
	}

	public ArrayList<Setting> get_settings_with_hack(Hack module) {
		ArrayList<Setting> setting_requesteds = new ArrayList<>();

		for (Setting settings : get_array_settings()) {
			if (settings.get_master().equals(module)) {
				setting_requesteds.add(settings);
			}
		}

		return setting_requesteds;
	}
}
