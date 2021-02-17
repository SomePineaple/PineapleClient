package me.somepineaple.pineapleclient.mixins;

import me.somepineaple.pineapleclient.main.event.PineapleEventBus;
import me.somepineaple.pineapleclient.main.event.events.EventSetupFog;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// External.

@Mixin(value = EntityRenderer.class)
public class MixinEntityRenderer {
    
    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    public void setupFog(int startCoords, float partialTicks, CallbackInfo p_Info)
    {
        EventSetupFog event = new EventSetupFog(startCoords, partialTicks);
        PineapleEventBus.EVENT_BUS.post(event);
        
        if (event.isCancelled()) {
			return;
        }
    }
}
