package me.somepineaple.pineapleclient.mixins;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.event.PineapleEventBus;
import me.somepineaple.pineapleclient.main.event.events.EventGUIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// External.


@Mixin(value = Minecraft.class)
public class MixinMinecraft {
	@Inject(method = "displayGuiScreen", at = @At("HEAD"))
	private void displayGuiScreen(GuiScreen guiScreenIn, CallbackInfo info) {
		EventGUIScreen guiscreen = new EventGUIScreen(guiScreenIn);

		PineapleEventBus.EVENT_BUS.post(guiscreen);
	}

	@Inject(method = "shutdown", at = @At("HEAD"))
	private void shutdown(CallbackInfo info) {
		PineapleClient.get_config_manager().save_settings();
	}

}