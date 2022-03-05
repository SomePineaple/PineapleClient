package me.somepineaple.pineapleclient.main.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.command.PineapleclientCommand;
import me.somepineaple.pineapleclient.main.util.EzMessageUtil;
import me.somepineaple.pineapleclient.main.util.MessageUtil;

public class EzMessage extends PineapleclientCommand {

    public EzMessage() {
        super("ezmessage", "Set ez mode");
    }

    public boolean getMessage(String[] message) {

        if (message.length == 1) {
            MessageUtil.send_client_error_message("message needed");
            return true;
        }

        if (message.length >= 2) {
            StringBuilder ez = new StringBuilder();
            boolean flag = true;
            for (String word : message) {
                if (flag) {
                    flag = false;
                    continue;
                }
                ez.append(word).append(" ");
            }
            EzMessageUtil.set_message(ez.toString());
            MessageUtil.sendClientMessage("ez message changed to " + ChatFormatting.BOLD + ez.toString());
            PineapleClient.getConfigManager().saveSettings();
            return true;
        }

        return false;
    }
}
