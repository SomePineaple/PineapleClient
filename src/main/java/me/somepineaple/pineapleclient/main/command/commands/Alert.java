package me.somepineaple.pineapleclient.main.command.commands;


import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.command.PineapleclientCommand;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.MessageUtil;


public class Alert extends PineapleclientCommand {
	public Alert() {
		super("alert", "if the module should spam chat or not");
	}

	public boolean getMessage(String[] message) {
		String module = "null";
		String state  = "null";

		if (message.length > 1) {
			module = message[1];
		}

		if (message.length > 2) {
			state = message[2];
		}

		if (message.length > 3) {
			MessageUtil.send_client_error_message(currentPrefix() + "t <ModuleName> <True/On/False/Off>");

			return true;
		}

		if (module.equals("null")) {
			MessageUtil.send_client_error_message(currentPrefix() + "t <ModuleName> <True/On/False/Off>");

			return true;
		}

		if (state.equals("null")) {
			MessageUtil.send_client_error_message(currentPrefix() + "t <ModuleName> <True/On/False/Off>");

			return true;
		}

		module = module.toLowerCase();
		state  = state.toLowerCase();

		Hack module_requested = PineapleClient.getHackManager().getModuleWithTag(module);

		if (module_requested == null) {
			MessageUtil.send_client_error_message("This module does not exist.");

			return true;
		}

		boolean value = true;

		if (state.equals("true") || state.equals("on")) {
			value = true;
		} else if (state.equals("false") || state.equals("off")) {
			value = false;
		} else {
			MessageUtil.send_client_error_message("This value does not exist. <True/On/False/Off>");

			return true;
		}

		module_requested.set_if_can_send_message_toggle(value);

		MessageUtil.send_client_message("The actual value of " + module_requested.get_name() +  " is " + Boolean.toString(module_requested.can_send_message_when_toggle()) + ".");

		return true;
	}
}
