package me.somepineaple.pineapleclient.mixins;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.event.PineapleEventBus;
import me.somepineaple.pineapleclient.main.event.events.EventBlock;
import me.somepineaple.pineapleclient.main.event.events.EventDamageBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerControllerMP.class)
public class MixinPlayerControllerMP {

	// Player damage fix the hit.
	@Redirect(method = "onPlayerDamageBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getPlayerRelativeBlockHardness(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)F"))
	private float onPlayerDamageBlockSpeed(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
		return state.getPlayerRelativeBlockHardness(player, world, pos) * (PineapleClient.getEventHandler().getTickRate() / 20f);
	}

	@Inject(method = "onPlayerDamageBlock", at = @At("HEAD"), cancellable = true)
	public void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> info) {

        EventDamageBlock event_packet = new EventDamageBlock(posBlock, directionFacing);

		PineapleEventBus.EVENT_BUS.post(event_packet);

		if (event_packet.isCancelled()) {
			info.setReturnValue(false);
			info.cancel();
		}

		final EventBlock event = new EventBlock(4, posBlock, directionFacing);
		PineapleEventBus.EVENT_BUS.post(event);
	}

	@Inject(method = { "clickBlock" }, at = { @At("HEAD") }, cancellable = true)
	private void clickBlockHook(final BlockPos pos, final EnumFacing face, final CallbackInfoReturnable<Boolean> info) {
		final EventBlock event = new EventBlock(3, pos, face);
		PineapleEventBus.EVENT_BUS.post(event);
	}
}
