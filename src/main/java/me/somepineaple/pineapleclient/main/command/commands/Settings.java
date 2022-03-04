package me.somepineaple.pineapleclient.main.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.command.PineapleclientCommand;
import me.somepineaple.pineapleclient.main.util.MessageUtil;

public class Settings extends PineapleclientCommand {
	public Settings() {
		super("settings", "To save/load settings.");
	}

	public boolean getMessage(String[] message) {
		String msg = "null";

		if (message.length > 1) {
			msg = message[1];
		}

		if (msg.equals("null")) {
			MessageUtil.send_client_error_message(currentPrefix() + "settings <save/load>");

			return true;
		}

		ChatFormatting c = ChatFormatting.GRAY;

		if (msg.equalsIgnoreCase("save")) {
			PineapleClient.get_config_manager().save_settings();

			MessageUtil.send_client_message(ChatFormatting.GREEN + "Successfully " + c + "saved!");
		} else if (msg.equalsIgnoreCase("load")) {
			PineapleClient.get_config_manager().load_settings();

			MessageUtil.send_client_message(ChatFormatting.GREEN + "Successfully " + c + "loaded!");
		} else {
			MessageUtil.send_client_error_message(currentPrefix() + "settings <save/load>");

			return true;
		}

		return true;
	}
}
