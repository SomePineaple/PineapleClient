package me.somepineaple.pineapleclient.main.command.commands;


import me.somepineaple.pineapleclient.main.command.PineapleclientCommand;
import me.somepineaple.pineapleclient.main.command.PineapleclientCommands;
import me.somepineaple.pineapleclient.main.util.MessageUtil;


public class Help extends PineapleclientCommand {
	public Help() {
		super("help", "helps people");
	}

	public boolean get_message(String[] message) {
		String type = "null";

		if (message.length == 1) {
			type = "list";
		}

		if (message.length > 1) {
			type = message[1];
		}

		if (message.length > 2) {
			MessageUtil.send_client_error_message(current_prefix() + "help <List/NameCommand>");

			return true;
		}

		if (type.equals("null")) {
			MessageUtil.send_client_error_message(current_prefix() + "help <List/NameCommand>");

			return true;
		}

		if (type.equalsIgnoreCase("list")) {

			for (PineapleclientCommand commands : PineapleclientCommands.get_pure_command_list()) {
				MessageUtil.send_client_message(commands.get_name());

			}

			return true;
		}

		PineapleclientCommand command_requested = PineapleclientCommands.get_command_with_name(type);

		if (command_requested == null) {
			MessageUtil.send_client_error_message("This command does not exist.");

			return true;
		}

		MessageUtil.send_client_message(command_requested.get_name() + " - " + command_requested.get_description());

		return true;
	}
}