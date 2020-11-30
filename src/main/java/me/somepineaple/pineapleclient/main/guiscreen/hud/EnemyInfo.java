package me.somepineaple.pineapleclient.main.guiscreen.hud;

import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;
import me.somepineaple.pineapleclient.main.hacks.chat.Totempop;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Objects;

import static me.somepineaple.pineapleclient.main.util.TabUtil.section_sign;

public class EnemyInfo extends Pinnable {
    public EnemyInfo() {
        super("Enemy Info", "EnemyInfo", 1, 0, 0);

        this.set_height(80);
        this.set_width(150);
    }

    @Override
    public void render() {
        if (mc.world == null || mc.player == null) {
            return;
        }

        RenderHelper.enableGUIStandardItemLighting();

        EntityLivingBase target = mc.player;

        float lowest_distance = 999F;

        for (EntityLivingBase e : mc.world.playerEntities) {
            if (e.getDistance(mc.player) < lowest_distance && !e.getName().equals(mc.player.getName()) && e.getDistance(mc.player) != 0) {
                target = e;
                lowest_distance = e.getDistance(mc.player);
            }
        }

        create_rect(0, 0, this.get_width(), this.get_height(), 0, 0, 0, 69);

        float target_hp = target.getHealth() + target.getAbsorptionAmount();
        String ping_str = "Ping: ";
        try {
            final int response_time = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(target.getUniqueID()).getResponseTime();
            ping_str += response_time + "ms";
        } catch (Exception ignored) {}
        float distance_to_target = target.getDistance(mc.player);

        int hp_r = 16;
        int hp_g = 171;
        int hp_b = 11;

        if (target_hp < 10) {
            hp_r = 200;
            hp_g = 15;
            hp_b =15;
        } else if (target_hp < 15) {
            hp_r = 171;
            hp_g = 140;
            hp_b =15;
        } else if (target_hp > 20) {
            hp_r = 110;
            hp_g = 2;
            hp_b = 88;
        }

        String pop_str = "";

        try {
            pop_str += (Totempop.totem_pop_counter.get(target.getName()) == null ? section_sign() + "70" : section_sign() + "c " + Totempop.totem_pop_counter.get(target.getName()));
        } catch (Exception ignore) {}

        int str_height = this.get("00hpRRRta", "height") + 3;

        try {
            create_line(target.getName() + " HP: " + target_hp, 3, 3, hp_r, hp_g, hp_b, 255);
            create_line(ping_str, 3, str_height);
            create_line("Distance: " + (int) distance_to_target, 3, str_height * 2);
            create_line("Totems Poped: " + pop_str, 3, str_height * 3);

            ArrayList<Block> surroundblocks = get_surround_blocks(target);

            mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(surroundblocks.get(0)), this.get_x() + 75, this.get_y() - 20 + 40);
            mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(surroundblocks.get(1)), this.get_x() + 20 + 75, this.get_y() + 40);
            mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(surroundblocks.get(2)), this.get_x() + 75, this.get_y() + 20 + 40);
            mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(surroundblocks.get(3)), this.get_x() - 20 + 75, this.get_y() + 40);

            create_rect(0, this.get_height(), (int) (target_hp / 36 * this.get_width()), this.get_height() -10, hp_r, hp_g, hp_b, 255);

            GuiInventory.drawEntityOnScreen(this.get_x() + this.get_width() -20, this.get_y()
                    + this.get_height() - 10, 30, -target.rotationYaw, -target.rotationPitch, target);
        } catch (Exception ignored){}


        RenderHelper.disableStandardItemLighting();
    }

    private ArrayList<Block> get_surround_blocks(EntityLivingBase e) {
        ArrayList<Block> surroundblocks = new ArrayList<>();
        BlockPos entityblock = new BlockPos(Math.floor(e.posX), Math.floor(e.posY), Math.floor(e.posZ));
        surroundblocks.add(mc.world.getBlockState(entityblock.north()).getBlock());
        surroundblocks.add(mc.world.getBlockState(entityblock.east()).getBlock());
        surroundblocks.add(mc.world.getBlockState(entityblock.south()).getBlock());
        surroundblocks.add(mc.world.getBlockState(entityblock.west()).getBlock());
        return surroundblocks;
    }
}
