package me.somepineaple.pineapleclient.main.command.commands;

import me.somepineaple.pineapleclient.main.command.PineapleclientCommand;
import me.somepineaple.pineapleclient.main.util.Notification;
import me.somepineaple.pineapleclient.main.util.NotificationUtil;

public class NotificationTest extends PineapleclientCommand {
    public NotificationTest() {
        super("notificationtest", "send urself notification");
    }

    public boolean get_message (String[] message) {
        NotificationUtil.send_notification(new Notification("Test notification"));
        return true;
    }
}
