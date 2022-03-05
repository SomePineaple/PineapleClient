package me.somepineaple.pineapleclient.mixins;

//Core

import me.somepineaple.pineapleclient.PineapleClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//Data
//randospongethingo

@Mixin({ GuiNewChat.class })
public class MixinGuiNewChat {

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0))
    private void overrideChatBackgroundColour(int left, int top, int right, int bottom, int color) {

        if (!PineapleClient.getHackManager().getModuleWithTag("ClearChatbox").isActive()) {

            Gui.drawRect(left, top, right, bottom, color);

        }
    }
}
