package me.somepineaple.pineapleclient.main.util;

import java.util.ArrayList;
import java.util.List;

public class DrawnUtil {
    
    public static List<String> hidden_tags = new ArrayList<>();

    public static void add_remove_item(String s) {
        s = s.toLowerCase();
        if (hidden_tags.contains(s)) {
            MessageUtil.sendClientMessage("Added " + s);
            hidden_tags.remove(s);
        } else {
            MessageUtil.sendClientMessage("Removed " + s);
            hidden_tags.add(s);
        }
    }
}
