package me.somepineaple.pineapleclient.mixins;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.event.PineapleEventBus;
import me.somepineaple.pineapleclient.main.event.events.EventMotionUpdate;
import me.somepineaple.pineapleclient.main.event.events.EventMove;
import me.somepineaple.pineapleclient.main.event.events.EventSwing;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// External.

@Mixin(value = EntityPlayerSP.class)
public class MixinEntitySP extends MixinEntity {

	@Inject(method = "move", at = @At("HEAD"), cancellable = true)
	private void move(MoverType type, double x, double y, double z, CallbackInfo info) {
		EventMove event = new EventMove(type, x, y, z);
		PineapleEventBus.EVENT_BUS.post(event);

		if (event.isCancelled()) {
            super.move(type, event.get_x(), event.get_y(), event.get_z());
			info.cancel();
		}
	}

	@Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void OnPreUpdateWalkingPlayer(CallbackInfo p_Info) {
        EventMotionUpdate l_Event = new EventMotionUpdate(0);
        PineapleEventBus.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            p_Info.cancel();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"), cancellable = true)
    public void OnPostUpdateWalkingPlayer(CallbackInfo p_Info) {

        EventMotionUpdate l_Event = new EventMotionUpdate(1);
        PineapleEventBus.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            p_Info.cancel();
    }

    @Inject(method = "swingArm", at = @At("RETURN"), cancellable = true)
    public void swingArm(EnumHand p_Hand, CallbackInfo p_Info) {
        EventSwing l_Event = new EventSwing(p_Hand);
        PineapleEventBus.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            p_Info.cancel();

    }

    @Inject(method={"pushOutOfBlocks"}, at={@At(value="HEAD")}, cancellable=true)
    private void pushOutOfBlocksHook(double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        if (PineapleClient.get_hack_manager().get_module_with_tag("Freecam").is_active()) {
            info.setReturnValue(false);
        }
    }
}
