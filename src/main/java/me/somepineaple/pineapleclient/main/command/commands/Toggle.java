package me.somepineaple.pineapleclient.main.command.commands;

import me.somepineaple.pineapleclient.Pineapleclient;
import me.somepineaple.pineapleclient.main.command.PineapleclientCommand;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.manager.CommandManager;
import me.somepineaple.pineapleclient.main.util.MessageUtil;

public class Toggle extends PineapleclientCommand {
	public Toggle() {
		super("t", "turn on and off stuffs");
	}

	public boolean get_message(String[] message) {
		String module = "null";

		if (message.length > 1) {
			module = message[1];
		}

		if (message.length > 2) {
			MessageUtil.send_client_error_message(current_prefix() + "t <ModuleNameNoSpace>");

			return true;
		}

		if (module.equals("null")) {
			MessageUtil.send_client_error_message(CommandManager.get_prefix() + "t <ModuleNameNoSpace>");

			return true;
		}

		Hack module_requested = Pineapleclient.get_module_manager().get_module_with_tag(module);

		if (module_requested != null) {
			module_requested.toggle();

			MessageUtil.send_client_message("[" + module_requested.get_tag() + "] - Toggled to " + module_requested.is_active() + ".");
		} else {
			MessageUtil.send_client_error_message("Module does not exist.");
		}

		return true;
	}
}