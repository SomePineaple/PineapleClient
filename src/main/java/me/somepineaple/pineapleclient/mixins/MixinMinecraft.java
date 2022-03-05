package me.somepineaple.pineapleclient.mixins;

import me.somepineaple.pineapleclient.PineapleClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// External.

@Mixin(value = Minecraft.class)
public class MixinMinecraft {
	@Inject(method = "shutdown", at = @At("HEAD"))
	private void shutdown(CallbackInfo info) {
		PineapleClient.getConfigManager().saveSettings();
	}
}
