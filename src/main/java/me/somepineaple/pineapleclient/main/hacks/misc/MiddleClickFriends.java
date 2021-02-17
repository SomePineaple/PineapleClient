package me.somepineaple.pineapleclient.main.hacks.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.FriendUtil;
import me.somepineaple.pineapleclient.main.util.MessageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Mouse;

public class MiddleClickFriends extends Hack {
    
    public MiddleClickFriends() {
        super(Category.MISC);

		this.name        = "Middleclick Friends";
		this.tag         = "MiddleclickFriends";
		this.description = "you press button and the world becomes a better place :D";
    }

    private boolean clicked = false;

    public static ChatFormatting red = ChatFormatting.RED;
    public static ChatFormatting green = ChatFormatting.GREEN;
    public static ChatFormatting bold = ChatFormatting.BOLD;
    public static ChatFormatting reset = ChatFormatting.RESET;

    @Override
	public void update() {
        
        if (mc.currentScreen != null) {
            return;
        }

        if (!Mouse.isButtonDown(2)) {
            clicked = false;
            return;
        }

        if (!clicked) {

            clicked = true;

            final RayTraceResult result = mc.objectMouseOver;

            if (result == null || result.typeOfHit != RayTraceResult.Type.ENTITY) {
                return;
            }

            if (!(result.entityHit instanceof EntityPlayer)) return;

            Entity player = result.entityHit;

            if (FriendUtil.isFriend(player.getName())) {

                FriendUtil.Friend f = FriendUtil.friends.stream().filter(friend -> friend.getUsername().equalsIgnoreCase(player.getName())).findFirst().get();
                FriendUtil.friends.remove(f);
                MessageUtil.send_client_message("Player " + red + bold + player.getName() + reset + " is now not your friend :(");
                            
            } else {
                FriendUtil.Friend f = FriendUtil.get_friend_object(player.getName());
                FriendUtil.friends.add(f);
                MessageUtil.send_client_message("Player " + green + bold + player.getName() + reset + " is now your friend :D");
            }
        }
	}
}
