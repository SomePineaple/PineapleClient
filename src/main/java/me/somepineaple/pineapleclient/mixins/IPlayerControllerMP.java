package me.somepineaple.pineapleclient.mixins;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerControllerMP.class)
public interface IPlayerControllerMP {
    @Invoker("syncCurrentPlayItem")
    void hookSyncCurrentPlayItem();
}
