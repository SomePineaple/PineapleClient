package me.somepineaple.pineapleclient.main.command;

import me.somepineaple.pineapleclient.main.manager.CommandManager;

public class PineapleclientCommand {
	String name;
	String description;

	public PineapleclientCommand(String name, String description) {
		this.name        = name;
		this.description = description;
	}

	public boolean getMessage(String[] message) {
		return false;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public String currentPrefix() {
		return CommandManager.getPrefix();
	}
}
