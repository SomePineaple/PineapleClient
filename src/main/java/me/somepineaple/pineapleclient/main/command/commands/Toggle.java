package me.somepineaple.pineapleclient.main.command.commands;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.command.PineapleclientCommand;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.manager.CommandManager;
import me.somepineaple.pineapleclient.main.util.MessageUtil;

public class Toggle extends PineapleclientCommand {
	public Toggle() {
		super("t", "turn on and off stuffs");
	}

	public boolean getMessage(String[] message) {
		String module = "null";

		if (message.length > 1) {
			module = message[1];
		}

		if (message.length > 2) {
			MessageUtil.send_client_error_message(currentPrefix() + "t <ModuleNameNoSpace>");

			return true;
		}

		if (module.equals("null")) {
			MessageUtil.send_client_error_message(CommandManager.getPrefix() + "t <ModuleNameNoSpace>");

			return true;
		}

		Hack module_requested = PineapleClient.getModuleManager().getModuleWithTag(module);

		if (module_requested != null) {
			module_requested.toggle();

			MessageUtil.sendClientMessage("[" + module_requested.getTag() + "] - Toggled to " + module_requested.isActive() + ".");
		} else {
			MessageUtil.send_client_error_message("Module does not exist.");
		}

		return true;
	}
}
