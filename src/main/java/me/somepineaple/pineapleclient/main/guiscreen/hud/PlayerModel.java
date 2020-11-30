package me.somepineaple.pineapleclient.main.guiscreen.hud;

import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class PlayerModel extends Pinnable {
    public PlayerModel() {
        super("Player Model", "PlayerModel", 1, 0, 0);
        this.set_width(40);
        this.set_height(100);
    }

    @Override
    public void render() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        EntityLivingBase player = mc.player;

        float yaw = interpolateAndWrap(player.prevRotationYaw, player.rotationYaw);
        float pitch = interpolateAndWrap(player.prevRotationPitch, player.rotationPitch);
        GuiInventory.drawEntityOnScreen(this.get_x() + 20, this.get_y() + 80, 40, -yaw, -pitch, player);
    }

    private float interpolateAndWrap( Float prev, Float current) {
        return MathHelper.wrapDegrees(prev + (current - prev) * mc.getRenderPartialTicks());
    }
}
