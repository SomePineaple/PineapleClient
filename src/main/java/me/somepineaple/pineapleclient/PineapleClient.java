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

import java.util.Collections;

@Mod(modid = "pineapleclient", version = PineapleClient.VERSION)
public class PineapleClient {

	@Mod.Instance
	private static PineapleClient MASTER;

	public static final String NAME = "PineapleClient";
	public static final String VERSION = "2.0";
	public static final String SIGN = " - ";

	public static final int KEY_GUI = Keyboard.KEY_RSHIFT;
	public static final int KEY_DELETE = Keyboard.KEY_DELETE;
	public static final int KEY_GUI_ESCAPE = Keyboard.KEY_ESCAPE;

	public static Logger register_log;

	private static SettingManager settingManager;
	private static ConfigManager configManager;
	private static ModuleManager moduleManager;
	private static HUDManager hudManager;

	public static GUI clickGui;
	public static HUD clickHud;

	public static Turok turok;

	public static ChatFormatting g = ChatFormatting.DARK_GRAY;
	public static ChatFormatting r = ChatFormatting.RESET;

	@Mod.EventHandler
	public void Starting(FMLInitializationEvent event) {
		initLog(NAME);

		EventHandler.INSTANCE = new EventHandler();

		sendMinecraftLog("initialising managers");

		settingManager = new SettingManager();
		configManager = new ConfigManager();
		moduleManager = new ModuleManager();
		hudManager = new HUDManager();

		EventManager eventManager = new EventManager();
		CommandManager commandManager = new CommandManager(); // hack

		sendMinecraftLog("done");

		sendMinecraftLog("initialising guis");

		Display.setTitle("PineapleClient");
		clickGui = new GUI();
		clickHud = new HUD();

		sendMinecraftLog("done");

		sendMinecraftLog("initialising skidded framework");

		turok = new Turok("Turok");

		sendMinecraftLog("done");

		sendMinecraftLog("initialising commands and events");

		// Register event modules and manager.
		EventRegister.registerCommandManager(commandManager);
		EventRegister.registerModuleManager(eventManager);

		sendMinecraftLog("done");

		sendMinecraftLog("loading settings");

		configManager.loadSettings();

		sendMinecraftLog("done");

		if (moduleManager.getModuleWithTag("GUI").isActive()) {
			moduleManager.getModuleWithTag("GUI").set_active(false);
		}

		if (moduleManager.getModuleWithTag("HUD").isActive()) {
			moduleManager.getModuleWithTag("HUD").set_active(false);
		}

		sendMinecraftLog("client started");
		sendMinecraftLog("we gaming");

	}

	public void initLog(String name) {
		register_log = LogManager.getLogger(name);

		sendMinecraftLog("starting PineapleClient");
	}

	public static void sendMinecraftLog(String log) {
		register_log.info(log);
	}

	public static String getName() {
		return NAME;
	}

	public static String getVersion() {
		return VERSION;
	}

	public static String getActualUser() {
		return Minecraft.getMinecraft().getSession().getUsername();
	}

	public static ConfigManager getConfigManager() {
		return configManager;
	}

	public static ModuleManager getHackManager() {
		return moduleManager;
	}

	public static SettingManager getSettingManager() {
		return settingManager;
	}

	public static HUDManager getHudManager() {
		return hudManager;
	}

	public static ModuleManager getModuleManager() { return moduleManager; }

	public static EventHandler getEventHandler() {
		return EventHandler.INSTANCE;
	}

	public static String smoth(String base) {
		return Font.smoth(base);
	}
}
