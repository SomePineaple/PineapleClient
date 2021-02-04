package me.somepineaple.pineapleclient.main.command.commands;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.command.PineapleclientCommand;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.DrawnUtil;
import me.somepineaple.pineapleclient.main.util.MessageUtil;

import java.util.List;

public class Drawn extends PineapleclientCommand {
    
    public Drawn() {
        super("drawn", "Hide elements of the array list");
    }

    public boolean get_message(String[] message) {

        if (message.length == 1) {
            MessageUtil.send_client_error_message("module name needed");

            return true;
        }

        if (message.length == 2) {

            if (is_module(message[1])) {
                DrawnUtil.add_remove_item(message[1]);
                PineapleClient.get_config_manager().save_settings();
            } else {
                MessageUtil.send_client_error_message("cannot find module by name: " + message[1]);
            }
            return true;

        }

        return false;
    
    }

    public boolean is_module(String s) {

        List<Hack> modules = PineapleClient.get_hack_manager().get_array_hacks();

        for (Hack module : modules) {
            if (module.get_tag().equalsIgnoreCase(s)) {
                return true;
            }
        }

        return false;

    }

}