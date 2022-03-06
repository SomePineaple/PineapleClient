package me.somepineaple.pineapleclient.mixins;

import net.minecraft.network.play.client.CPacketUseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketUseEntity.class)
public interface ICPacketUseEntity {

    @Accessor("entityID")
    void setID(int id);

    @Accessor("action")
    void setAction(CPacketUseEntity.Action action);
}
