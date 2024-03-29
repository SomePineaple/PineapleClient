package me.somepineaple.pineapleclient.main.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.somepineaple.pineapleclient.main.command.PineapleclientCommand;
import me.somepineaple.pineapleclient.main.util.FriendUtil;
import me.somepineaple.pineapleclient.main.util.MessageUtil;

public class Friend extends PineapleclientCommand {
    
    public Friend() {
        super("friend", "To add friends");
    }

    public static ChatFormatting red = ChatFormatting.RED;
    public static ChatFormatting green = ChatFormatting.GREEN;
    public static ChatFormatting bold = ChatFormatting.BOLD;
    public static ChatFormatting reset = ChatFormatting.RESET;

    public boolean getMessage(String[] message) {

        if (message.length == 1) {
            MessageUtil.sendClientMessage("Add - add friend");
            MessageUtil.sendClientMessage("Del - delete friend");
            MessageUtil.sendClientMessage("List - list friends");

            return true;
        }

        if (message.length == 2) {
            if (message[1].equalsIgnoreCase("list")) {
                if (FriendUtil.friends.isEmpty()) {
                    MessageUtil.sendClientMessage("You appear to have " + red + bold + "no" + reset + " friends :(");
                } else {
                    for (FriendUtil.Friend friend : FriendUtil.friends) {
                        MessageUtil.sendClientMessage("" + green + bold +  friend.getUsername());
                    }
                }
                return true;
            } else {
                if (FriendUtil.isFriend(message[1])) {
                    MessageUtil.sendClientMessage("Player " + green + bold + message[1] + reset + " is your friend :D");
                    return true;
                } else {
                    MessageUtil.send_client_error_message("Player " + red + bold + message[1] + reset + " is not your friend :(");
                    return true;
                }
            }
        }

        if (message.length >= 3) {
            if (message[1].equalsIgnoreCase("add")) {
                if (FriendUtil.isFriend(message[2])) {
                    MessageUtil.sendClientMessage("Player " + green + bold + message[2] + reset + " is already your friend :D");
                    return true;
                } else {
                    FriendUtil.Friend f = FriendUtil.get_friend_object(message[2]);
                    if (f == null) {
                        MessageUtil.send_client_error_message("Cannot find " + red + bold + "UUID" + reset + " for that player :(");
                        return true;
                    }
                    FriendUtil.friends.add(f);
                    MessageUtil.sendClientMessage("Player " + green + bold + message[2] + reset + " is now your friend :D");
                    return true;
                }
            } else if (message[1].equalsIgnoreCase("del") || message[1].equalsIgnoreCase("remove") || message[1].equalsIgnoreCase("delete")) {
                if (!FriendUtil.isFriend(message[2])) {
                    MessageUtil.sendClientMessage("Player " + red + bold + message[2] + reset + " is already not your friend :/");
                    return true;
                } else {
                    FriendUtil.Friend f = FriendUtil.friends.stream().filter(friend -> friend.getUsername().equalsIgnoreCase(message[2])).findFirst().get();
                    FriendUtil.friends.remove(f);
                    MessageUtil.sendClientMessage("Player " + red + bold + message[2]  + reset + " is now not your friend :(");
                    return true;
                }
            }
        }

        return true;
    }
}
