package me.somepineaple.pineapleclient.main.hacks.render;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.turok.draw.RenderHelp;
import me.somepineaple.pineapleclient.main.event.events.EventRender;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.CrystalUtil;
import me.somepineaple.pineapleclient.main.util.EntityUtil;
import me.somepineaple.pineapleclient.main.util.FriendUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class FuckedDetector extends Hack {
    
    public FuckedDetector() {
        super(Category.RENDER);

        this.name = "Fucked Detector";
        this.tag = "FuckedDetector";
        this.description = "see if people are hecked";
    }

    Setting draw_own = create("Draw Own", "FuckedDrawOwn", false);
    Setting draw_friends = create("Draw Friends", "FuckedDrawFriends", false);

    Setting render_mode = create("Render Mode", "FuckedRenderMode", "Pretty", combobox("Pretty", "Solid", "Outline"));
    Setting r = create("R", "FuckedR", 255, 0, 255);
	Setting g = create("G", "FuckedG", 255, 0, 255);
	Setting b = create("B", "FuckedB", 255, 0, 255);
    Setting a = create("A", "FuckedA", 100, 0, 255);

    private boolean solid;
    private boolean outline;

    public Set<BlockPos> fucked_players = new HashSet<BlockPos>();

    @Override
    protected void enable() {
        fucked_players.clear();
    }

    @Override
    public void update() {
        if (mc.world == null) return;
        set_fucked_players();
    }

    public void set_fucked_players() {

        fucked_players.clear();

        for (EntityPlayer player : mc.world.playerEntities) {

            if (!EntityUtil.isLiving(player) || player.getHealth() <= 0) continue;
            // if (!player.onGround) continue;

            if (is_fucked(player)) {

                if (FriendUtil.isFriend(player.getName()) && !draw_friends.getValue(true)) continue;
                if (player == mc.player && !draw_own.getValue(true)) continue;

                fucked_players.add(new BlockPos(player.posX, player.posY, player.posZ));

            }

        }

    }

    public boolean is_fucked(EntityPlayer player) {

        BlockPos pos = new BlockPos(player.posX, player.posY - 1, player.posZ);

        if (CrystalUtil.canPlaceCrystal(pos.south()) || (CrystalUtil.canPlaceCrystal(pos.south().south()) && mc.world.getBlockState(pos.add(0, 1, 1)).getBlock() == Blocks.AIR)) {
            return true;
        } 
            
        if (CrystalUtil.canPlaceCrystal(pos.east()) || (CrystalUtil.canPlaceCrystal(pos.east().east()) && mc.world.getBlockState(pos.add(1, 1, 0)).getBlock() == Blocks.AIR)) {
            return true;
        } 
            
        if (CrystalUtil.canPlaceCrystal(pos.west()) || (CrystalUtil.canPlaceCrystal(pos.west().west()) && mc.world.getBlockState(pos.add(-1, 1, 0)).getBlock() == Blocks.AIR)) {
            return true;
        } 
            
        if (CrystalUtil.canPlaceCrystal(pos.north()) || (CrystalUtil.canPlaceCrystal(pos.north().north()) && mc.world.getBlockState(pos.add(0, 1, -1)).getBlock() == Blocks.AIR)) {
            return true;
        }


        return false;

    }

    @Override
	public void render(EventRender event) {

        if (render_mode.in("Pretty")) {
            outline = true;
            solid = true;
        }

        if (render_mode.in("Solid")) {
            outline = false;
            solid = true;
        }

        if (render_mode.in("Outline")) {
            outline = true;
            solid = false;
        }

        for (BlockPos render_block : fucked_players) {

            if (render_block == null) return;

            if (solid) {
                RenderHelp.prepare("quads");
                RenderHelp.draw_cube(RenderHelp.get_buffer_build(),
                        render_block.getX(), render_block.getY(), render_block.getZ(),
                        1, 1, 1,
                        r.getValue(1), g.getValue(1), b.getValue(1), a.getValue(1),
                        "all"
                );
                RenderHelp.release();
            }        
    
            if (outline) {
                RenderHelp.prepare("lines");
                RenderHelp.draw_cube_line(RenderHelp.get_buffer_build(),
                        render_block.getX(), render_block.getY(), render_block.getZ(),
                        1, 1, 1,
                        r.getValue(1), g.getValue(1), b.getValue(1), a.getValue(1),
                        "all"
                );
                RenderHelp.release();
            }
        }
    }
}
