package me.somepineaple.pineapleclient;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.somepineaple.pineapleclient.main.guiscreen.GUI;
import me.somepineaple.pineapleclient.main.guiscreen.HUD;
import me.somepineaple.turok.Turok;
import me.somepineaple.turok.task.Font;
import me.somepineaple.pineapleclient.main.event.EventHandler;
import me.somepineaple.pineapleclient.main.event.EventRegister;
import me.somepineaple.pineapleclient.main.manager.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

@Mod(modid = "pineapleclient", version = PineapleClient.VERSION)
public class PineapleClient {

	@Mod.Instance
	private static PineapleClient MASTER;

	public static final String NAME = "PineapleClient";
	public static final String VERSION = "1.8";
	public static final String SIGN = " - ";

	public static final int KEY_GUI = Keyboard.KEY_RSHIFT;
	public static final int KEY_DELETE = Keyboard.KEY_DELETE;
	public static final int KEY_GUI_ESCAPE = Keyboard.KEY_ESCAPE;

	public static Logger register_log;

	private static SettingManager setting_manager;
	private static ConfigManager config_manager;
	private static ModuleManager module_manager;
	private static HUDManager hud_manager;

	public static GUI click_gui;
	public static HUD click_hud;

	public static Turok turok;

	public static ChatFormatting g = ChatFormatting.DARK_GRAY;
	public static ChatFormatting r = ChatFormatting.RESET;

	@Mod.EventHandler
	public void Starting(FMLInitializationEvent event) {

		init_log(NAME);

		EventHandler.INSTANCE = new EventHandler();

		send_minecraft_log("initialising managers");

		setting_manager = new SettingManager();
		config_manager = new ConfigManager();
		module_manager = new ModuleManager();
		hud_manager = new HUDManager();

		EventManager event_manager = new EventManager();
		CommandManager command_manager = new CommandManager(); // hack

		send_minecraft_log("done");

		send_minecraft_log("initialising guis");

		Display.setTitle("PineapleClient");
		click_gui = new GUI();
		click_hud = new HUD();

		send_minecraft_log("done");

		send_minecraft_log("initialising skidded framework");

		turok = new Turok("Turok");

		send_minecraft_log("done");

		send_minecraft_log("initialising commands and events");

		// Register event modules and manager.
		EventRegister.register_command_manager(command_manager);
		EventRegister.register_module_manager(event_manager);

		send_minecraft_log("done");

		send_minecraft_log("loading settings");

		config_manager.load_settings();

		send_minecraft_log("done");

		if (module_manager.get_module_with_tag("GUI").is_active()) {
			module_manager.get_module_with_tag("GUI").set_active(false);
		}

		if (module_manager.get_module_with_tag("HUD").is_active()) {
			module_manager.get_module_with_tag("HUD").set_active(false);
		}

		send_minecraft_log("client started");
		send_minecraft_log("we gaming");

	}

	public void init_log(String name) {
		register_log = LogManager.getLogger(name);

		send_minecraft_log("starting PineapleClient");
	}

	public static void send_minecraft_log(String log) {
		register_log.info(log);
	}

	public static String get_name() {
		return NAME;
	}

	public static String get_version() {
		return VERSION;
	}

	public static String get_actual_user() {
		return Minecraft.getMinecraft().getSession().getUsername();
	}

	public static ConfigManager get_config_manager() {
		return config_manager;
	}

	public static ModuleManager get_hack_manager() {
		return module_manager;
	}

	public static SettingManager get_setting_manager() {
		return setting_manager;
	}

	public static HUDManager get_hud_manager() {
		return hud_manager;
	}

	public static ModuleManager get_module_manager() { return module_manager; }

	public static EventHandler get_event_handler() {
		return EventHandler.INSTANCE;
	}

	public static String smoth(String base) {
		return Font.smoth(base);
	}
}
