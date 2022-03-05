package me.somepineaple.pineapleclient.main.event;

import me.somepineaple.pineapleclient.main.manager.CommandManager;
import me.somepineaple.pineapleclient.main.manager.EventManager;
import net.minecraftforge.common.MinecraftForge;

public class EventRegister {
	public static void registerCommandManager(CommandManager manager) {
		MinecraftForge.EVENT_BUS.register(manager);
	}

	public static void registerModuleManager(EventManager manager) {
		MinecraftForge.EVENT_BUS.register(manager);
	}
}
