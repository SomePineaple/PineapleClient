package me.somepineaple.pineapleclient.mixins;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.event.PineapleEventBus;
import me.somepineaple.pineapleclient.main.event.events.EventRenderEntity;
import me.somepineaple.pineapleclient.main.event.events.EventRenderEntityModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T> {

    protected MixinRenderLivingBase(final RenderManager renderManagerIn, final ModelBase modelBaseIn, final float shadowSizeIn) {
        super(renderManagerIn);
    }

    @Redirect(method = { "renderModel" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void renderModelHook(final ModelBase modelBase, final Entity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        EventRenderEntity.Head eventRenderEntity = new EventRenderEntity.Head(entityIn, EventRenderEntity.Type.COLOR);
        PineapleEventBus.EVENT_BUS.post(eventRenderEntity);
        if (PineapleClient.get_hack_manager().getModuleWithTag("ESP").isActive()) {
            final EventRenderEntityModel event = new EventRenderEntityModel(0, modelBase, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            PineapleClient.get_hack_manager().getModuleWithTag("ESP").on_render_model(event);
            if (event.isCancelled()) {
                return;
            }
        }
        if (eventRenderEntity.isCancelled()) return;
        modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        EventRenderEntity.Return eventRenderEntity1 = new EventRenderEntity.Return(entityIn, EventRenderEntity.Type.COLOR);
        PineapleEventBus.EVENT_BUS.post(eventRenderEntity1);
    }
}
