package me.somepineaple.pineapleclient.main.guiscreen.hud;

import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;
import me.somepineaple.pineapleclient.main.util.Notification;
import me.somepineaple.pineapleclient.main.util.NotificationUtil;

import java.util.ArrayList;

public class Notifications extends Pinnable {
    public Notifications() {
        super("Notifications", "notifications", 1, 0, 0);

        this.set_width(125);
        this.set_height(42);
    }

    @Override
    public void render() {
        ArrayList<Notification> notifications;
        NotificationUtil.update();

        notifications = NotificationUtil.get_notifications();

        int renderY = 0;
        for (Notification n : notifications) {
            int messageWidth = get(n.getMessage(), "width") + 25;
            int nWidth = Math.max(messageWidth, 125);
            create_rect(this.get_width() - nWidth, renderY, nWidth, renderY + 40, 0, 0, 0, 69);
            create_rect(this.get_width() - nWidth, renderY, this.get_width() - nWidth + 5, renderY + 40, n.getR(), n.getG(), n.getB(), 255);
            create_line(n.getMessage(), this.get_width() - nWidth + 10, renderY + (42 - (get(n.getMessage(), "height"))) / 2);
            renderY += 42;
        }
    }
}
