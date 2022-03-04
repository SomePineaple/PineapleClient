package me.somepineaple.pineapleclient.main.command.commands;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.command.PineapleclientCommand;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.MessageUtil;
import org.lwjgl.input.Keyboard;

public class Bind extends PineapleclientCommand {
	public Bind() {
		super("bind", "bind module to key");
	}

	public boolean getMessage(String[] message) {
		String module = "null;";
		String key = "null";

		if (message.length == 3) {
			module = message[1].toUpperCase();
			key = message[2].toUpperCase();
		} else if (message.length > 1) {
			MessageUtil.send_client_error_message(currentPrefix() + "bind <ModuleTag> <key>");

			return true;
		}

		if (module.equals("null") || key.equals("null")) {
			MessageUtil.send_client_error_message(currentPrefix() + "bind <ModuleTag> <key>");

			return true;
		}

		Hack module_requested = PineapleClient.get_hack_manager().getModuleWithTag(module);

		if (module_requested == null) {
			MessageUtil.send_client_error_message("Module does not exist.");

			return true;
		}

		if (key.equalsIgnoreCase("NONE")) {
			module_requested.set_bind(0);

			MessageUtil.send_client_message(module_requested.getTag() + " is bound to None.");

			return true;
		}

		int new_bind = Keyboard.getKeyIndex(key.toUpperCase());

		if (new_bind == 0) {
			MessageUtil.send_client_error_message("Key does not exist.");

			return true;
		}

		if (new_bind == module_requested.getBind(0)) {
			MessageUtil.send_client_error_message(module_requested.getTag() + " is already bound to this key");

			return true;
		}

		module_requested.set_bind(new_bind);

		MessageUtil.send_client_message(module_requested.getTag() +  " is bound to " + module_requested.getBind(""));

		return true;
	}
}
