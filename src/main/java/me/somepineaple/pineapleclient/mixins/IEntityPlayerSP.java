package me.somepineaple.pineapleclient.mixins;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPlayerSP.class)
public interface IEntityPlayerSP {
    @Accessor("lastReportedYaw")
    float getLastReportedYaw();

    @Accessor("lastReportedPitch")
    float getLastReportedPitch();
}
