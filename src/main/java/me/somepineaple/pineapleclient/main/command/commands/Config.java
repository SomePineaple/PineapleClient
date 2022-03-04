package me.somepineaple.pineapleclient.main.command.commands;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.command.PineapleclientCommand;
import me.somepineaple.pineapleclient.main.util.MessageUtil;

public class Config extends PineapleclientCommand {

    public Config() {
        super("config", "changes which config is loaded");
    }

    public boolean getMessage(String[] message) {

        if (message.length == 1) {
            MessageUtil.send_client_error_message("config needed");
            return true;
        } else if (message.length == 2) {
            String config = message[1];
            if (PineapleClient.get_config_manager().set_active_config_folder(config+"/")) {
                MessageUtil.send_client_message("new config folder set as " + config);
            } else {
                MessageUtil.send_client_error_message("cannot set folder to " + config);
            }
            return true;
        } else {
            MessageUtil.send_client_error_message("config path may only be one word");
            return true;
        }
    }

}
