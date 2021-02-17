package me.somepineaple.pineapleclient.mixins;

import me.somepineaple.pineapleclient.main.event.PineapleEventBus;
import me.somepineaple.pineapleclient.main.event.events.EventRenderName;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderPlayer.class)
public class MixinRenderPlayer {

    @Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
	public void renderLivingLabel(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo info) {

		EventRenderName event_packet = new EventRenderName(entityIn, x, y, z, name, distanceSq);

        PineapleEventBus.EVENT_BUS.post(event_packet);
        
        if (event_packet.isCancelled()) {
            info.cancel();
        }
	}
}
