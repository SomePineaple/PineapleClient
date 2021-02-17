package me.somepineaple.pineapleclient.main.command;

import me.somepineaple.pineapleclient.main.manager.CommandManager;

public class PineapleclientCommand {
	String name;
	String description;

	public PineapleclientCommand(String name, String description) {
		this.name        = name;
		this.description = description;
	}

	public boolean get_message(String[] message) {
		return false;
	}

	public String get_name() {
		return this.name;
	}

	public String get_description() {
		return this.description;
	}

	public String current_prefix() {
		return CommandManager.get_prefix();
	}
}
