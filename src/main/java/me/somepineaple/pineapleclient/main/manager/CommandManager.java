package me.somepineaple.pineapleclient.main.manager;

import me.somepineaple.pineapleclient.main.command.PineapleclientCommands;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

 public class CommandManager {

	public static PineapleclientCommands command_list;

	public CommandManager() {
		command_list = new PineapleclientCommands(new Style().setColor(TextFormatting.BLUE));
	}

	public static void set_prefix(String new_prefix) {
		command_list.set_prefix(new_prefix);
	}

	public static String get_prefix() {
		return command_list.get_prefix();
	}
}
