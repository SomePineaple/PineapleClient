package me.somepineaple.pineapleclient.main.hacks.chat;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.event.events.EventPacket;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.Random;

// Zero alpine manager.

public class ChatSuffix extends Hack {
	public ChatSuffix() {
		super(Category.CHAT);

		this.name        = "Chat Suffix";
		this.tag         = "ChatSuffix";
		this.description = "show off how cool u are";
	}

	Setting ignore = create("Ignore", "ChatSuffixIgnore", true);
	Setting type   = create("Type", "ChatSuffixType", "Default", combobox("Default", "Random"));

	boolean accept_suffix;
	boolean suffix_default;
	boolean suffix_random;

	StringBuilder suffix;

	String[] random_client_name = {
		"chad",
		"tabott",
		"travis",
		"unco",
		"kiwi",
		"xdolf",
		"eightsixfour",
		"biggus",
		"naughty",
		"jumpy",
		"chae",
		"wurst",
		"buttfuhk"
	};

	String[] random_client_finish = {
		" plus",
		" epic",
		"god",
		" sex",
		" blue",
		" brown",
		" gay",
		"plus",
		""
	};

	@EventHandler
	private Listener<EventPacket.SendPacket> listener = new Listener<>(event -> {
		// If not be the CPacketChatMessage return.
		if (!(event.getPacket() instanceof CPacketChatMessage)) {
			return;
		}

		// Start event suffix.
		accept_suffix = true;

		// Get value.
		boolean ignore_prefix = ignore.getValue(true);

		String message = ((CPacketChatMessage) event.getPacket()).getMessage();

		// If is with some caracther.
		if (message.startsWith("/")  && ignore_prefix) accept_suffix = false;
		if (message.startsWith("\\") && ignore_prefix) accept_suffix = false;
		if (message.startsWith("!")  && ignore_prefix) accept_suffix = false;
		if (message.startsWith(":")  && ignore_prefix) accept_suffix = false;
		if (message.startsWith(";")  && ignore_prefix) accept_suffix = false;
		if (message.startsWith(".")  && ignore_prefix) accept_suffix = false;
		if (message.startsWith(",")  && ignore_prefix) accept_suffix = false;
		if (message.startsWith("@")  && ignore_prefix) accept_suffix = false;
		if (message.startsWith("&")  && ignore_prefix) accept_suffix = false;
		if (message.startsWith("*")  && ignore_prefix) accept_suffix = false;
		if (message.startsWith("$")  && ignore_prefix) accept_suffix = false;
		if (message.startsWith("#")  && ignore_prefix) accept_suffix = false;
		if (message.startsWith("(")  && ignore_prefix) accept_suffix = false;
		if (message.startsWith(")")  && ignore_prefix) accept_suffix = false;

		// Compare the values type.
		if (type.in("Default")) {
			suffix_default = true;
			suffix_random  = false;
		}

		if (type.in("Random")) {
			suffix_default = false;
			suffix_random  = true;
		}

		// If accept.
		if (accept_suffix) {
			if (suffix_default) {
				// Just default.
				message += PineapleClient.SIGN + convert_base("pineapleclient");
			}

			if (suffix_random) {
				// Create first the string builder.
				StringBuilder suffix_with_randoms = new StringBuilder();

				// Convert the base using the TravisFont.
				suffix_with_randoms.append(convert_base(random_string(random_client_name)));
				suffix_with_randoms.append(convert_base(random_string(random_client_finish)));

				message += PineapleClient.SIGN + suffix_with_randoms.toString();
			}

			// If message 256 string length substring.
			if (message.length() >= 256) {
				message.substring(0, 256);
			}
		}

		// Send the message.
		((CPacketChatMessage) event.getPacket()).message = message;
	});

	// Get the random values string.
	public String random_string(String[] list) {
		return list[new Random().nextInt(list.length)];
	}

	// Convert the base using the TravisFont.
	public String convert_base(String base) {
		return PineapleClient.smoth(base);
	}

	@Override
	public String array_detail() {
		// Update the detail.
		return this.type.getCurrentValue();
	}
}
