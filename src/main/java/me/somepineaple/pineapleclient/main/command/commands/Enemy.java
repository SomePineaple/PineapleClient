package me.somepineaple.pineapleclient.main.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.somepineaple.pineapleclient.main.command.PineapleclientCommand;
import me.somepineaple.pineapleclient.main.util.EnemyUtil;
import me.somepineaple.pineapleclient.main.util.MessageUtil;

public class Enemy extends PineapleclientCommand {

    public Enemy() {
        super("enemy", "To add enemy");
    }

    public static ChatFormatting red = ChatFormatting.GREEN;
    public static ChatFormatting green = ChatFormatting.RED;
    public static ChatFormatting bold = ChatFormatting.BOLD;
    public static ChatFormatting reset = ChatFormatting.RESET;

    public boolean getMessage(String[] message) {

        if (message.length == 1) {
            MessageUtil.sendClientMessage("Add - add enemy");
            MessageUtil.sendClientMessage("Del - delete enemy");
            MessageUtil.sendClientMessage("List - list enemies");

            return true;
        }

        if (message.length == 2) {
            if (message[1].equalsIgnoreCase("list")) {
                if (EnemyUtil.enemies.isEmpty()) {
                    MessageUtil.sendClientMessage("You appear to have " + red + bold + "no" + reset + " enemies :)");
                } else {
                    for (EnemyUtil.Enemy Enemy : EnemyUtil.enemies) {
                        MessageUtil.sendClientMessage("" + green + bold +  Enemy.getUsername());
                    }
                }
                return true;
            } else {
                if (EnemyUtil.isEnemy(message[1])) {
                    MessageUtil.sendClientMessage("Player " + green + bold + message[1] + reset + " is your Enemy D:");
                    return true;
                } else {
                    MessageUtil.send_client_error_message("Player " + red + bold + message[1] + reset + " is not your Enemy :)");
                    return true;
                }
            }
        }

        if (message.length >= 3) {
            if (message[1].equalsIgnoreCase("add")) {
                if (EnemyUtil.isEnemy(message[2])) {
                    MessageUtil.sendClientMessage("Player " + green + bold + message[2] + reset + " is already your Enemy D:");
                    return true;
                } else {
                    EnemyUtil.Enemy f = EnemyUtil.get_enemy_object(message[2]);
                    if (f == null) {
                        MessageUtil.send_client_error_message("Cannot find " + red + bold + "UUID" + reset + " for that player :(");
                        return true;
                    }
                    EnemyUtil.enemies.add(f);
                    MessageUtil.sendClientMessage("Player " + green + bold + message[2] + reset + " is now your Enemy D:");
                    return true;
                }
            } else if (message[1].equalsIgnoreCase("del") || message[1].equalsIgnoreCase("remove") || message[1].equalsIgnoreCase("delete")) {
                if (!EnemyUtil.isEnemy(message[2])) {
                    MessageUtil.sendClientMessage("Player " + red + bold + message[2] + reset + " is already not your Enemy :/");
                    return true;
                } else {
                    EnemyUtil.Enemy f = EnemyUtil.enemies.stream().filter(Enemy -> Enemy.getUsername().equalsIgnoreCase(message[2])).findFirst().get();
                    EnemyUtil.enemies.remove(f);
                    MessageUtil.sendClientMessage("Player " + red + bold + message[2]  + reset + " is now not your Enemy :)");
                    return true;
                }
            }
        }

        return true;
    }
}
