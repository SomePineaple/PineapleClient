package me.somepineaple.pineapleclient.main.hacks;

public enum Category {
	CHAT("Chat", "Chat", false),
	COMBAT("Combat", "Combat", false),
	MOVEMENT("Movement", "Movement", false),
	RENDER("Render", "Render", false),
	EXPLOIT("Exploit", "Exploit", false),
	MISC("Misc", "Misc", false),
	GUI("GUI", "GUI", false),
	BETA("Beta", "Beta", false),
	HIDDEN("Hidden", "Hidden", true);

	final String name;
	final String tag;
	final boolean hidden;

	Category(String name, String tag, boolean hidden) {
		this.name   = name;
		this.tag    = tag;
		this.hidden = hidden;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public String get_name() {
		return this.name;
	}

	public String get_tag() {
		return this.tag;
	}
}
