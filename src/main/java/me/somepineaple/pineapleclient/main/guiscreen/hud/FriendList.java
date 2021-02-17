package me.somepineaple.pineapleclient.main.guiscreen.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;
import me.somepineaple.pineapleclient.main.util.OnlineFriends;
import net.minecraft.entity.Entity;

public class FriendList extends Pinnable {
    
    public FriendList() {
        super("Friends", "Friends", 1, 0, 0);
    }

    int passes;

    public static ChatFormatting bold = ChatFormatting.BOLD;

    @Override
	public void render() {
		int nl_r = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorR").get_value(1);
		int nl_g = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorG").get_value(1);
        int nl_b = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorB").get_value(1);
        int nl_a = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorA").get_value(1);

        String line1 = bold + "the_fellas: ";
        
        passes = 0;

        create_line(line1, this.docking(1, line1), 2, nl_r, nl_g, nl_b, nl_a);
        
        if (!OnlineFriends.getFriends().isEmpty()) {
            for (Entity e : OnlineFriends.getFriends()) {
                passes++;
                create_line(e.getName(), this.docking(1, e.getName()), this.get(line1, "height")*passes, nl_r, nl_g, nl_b, nl_a);
            }
        }

		this.set_width(this.get(line1, "width") + 2);
		this.set_height(this.get(line1, "height") + 2);
	}
}
