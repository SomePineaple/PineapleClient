package me.somepineaple.pineapleclient.main.command.commands;


import me.somepineaple.pineapleclient.main.command.PineapleclientCommand;
import me.somepineaple.pineapleclient.main.manager.CommandManager;
import me.somepineaple.pineapleclient.main.util.MessageUtil;


public class Prefix extends PineapleclientCommand {
	public Prefix() {
		super("prefix", "Change prefix.");
	}

	public boolean getMessage(String[] message) {
		String prefix = "null";

		if (message.length > 1) {
			prefix = message[1];
		}

		if (message.length > 2) {
			MessageUtil.send_client_error_message(currentPrefix() + "prefix <character>");

			return true;
		}

		if (prefix.equals("null")) {
			MessageUtil.send_client_error_message(currentPrefix() + "prefix <character>");

			return true;
		}

		CommandManager.setPrefix(prefix);

		MessageUtil.sendClientMessage("The new prefix is " + prefix);

		return true;
	}
}
