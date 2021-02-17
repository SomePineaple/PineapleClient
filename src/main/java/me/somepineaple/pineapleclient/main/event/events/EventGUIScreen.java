package me.somepineaple.pineapleclient.main.event.events;

import me.somepineaple.pineapleclient.main.event.EventCancellable;
import net.minecraft.client.gui.GuiScreen;

// External.


public class EventGUIScreen extends EventCancellable {
	private final GuiScreen guiscreen;

	public EventGUIScreen(GuiScreen screen) {
		super();
		guiscreen = screen;
	}

	public GuiScreen get_guiscreen() {
		return guiscreen;
	}
}
