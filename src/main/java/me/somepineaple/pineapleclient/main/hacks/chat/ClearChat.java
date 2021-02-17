package me.somepineaple.pineapleclient.main.hacks.chat;


import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;

public class ClearChat extends Hack {
    public ClearChat() {
        super(Category.CHAT);

        this.name = "Clear Chatbox";
        this.tag = "ClearChatbox";
        this.description = "Removes the default minecraft chat outline.";
    }
}
