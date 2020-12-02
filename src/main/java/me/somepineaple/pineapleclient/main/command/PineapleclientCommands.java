package me.somepineaple.pineapleclient.main.command;

import me.somepineaple.turok.values.TurokString;
import me.somepineaple.pineapleclient.main.command.commands.*;
import net.minecraft.util.text.Style;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class PineapleclientCommands {
	public static ArrayList<PineapleclientCommand> command_list = new ArrayList<>();
	static HashMap<java.lang.String, PineapleclientCommand> list_command  = new HashMap<>();

	public static final TurokString prefix = new TurokString("Prefix", "Prefix", ".");

	public final Style style;

	public PineapleclientCommands(Style style_) {
		style = style_;

		add_command(new Bind());
		add_command(new Prefix());
		add_command(new Settings());
		add_command(new Toggle());
		add_command(new Alert());
		add_command(new Help());
		add_command(new Friend());
		add_command(new Drawn());
		add_command(new EzMessage());
		add_command(new Enemy());
		add_command(new Config());
		add_command(new NotificationTest());

		command_list.sort(Comparator.comparing(PineapleclientCommand::get_name));
	}

	public static void add_command(PineapleclientCommand command) {
		command_list.add(command);

		list_command.put(command.get_name().toLowerCase(), command);
	}

	public java.lang.String[] get_message(java.lang.String message) {
		java.lang.String[] arguments = {};

		if (has_prefix(message)) {
			arguments = message.replaceFirst(prefix.get_value(), "").split(" ");
		}

		return arguments;
	}

	public boolean has_prefix(java.lang.String message) {
		return message.startsWith(prefix.get_value());
	}

	public void set_prefix(java.lang.String new_prefix) {
		prefix.set_value(new_prefix);
	}

	public java.lang.String get_prefix() {
		return prefix.get_value();
	}

	public static ArrayList<PineapleclientCommand> get_pure_command_list() {
		return command_list;
	}

	public static PineapleclientCommand get_command_with_name(java.lang.String name) {
		return list_command.get(name.toLowerCase());
	}
}