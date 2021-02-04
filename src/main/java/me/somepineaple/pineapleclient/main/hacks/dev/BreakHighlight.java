package me.somepineaple.pineapleclient.main.hacks.dev;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.event.events.EventPacket;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.util.RenderUtil;
import me.somepineaple.turok.draw.RenderHelp;
import me.somepineaple.pineapleclient.main.event.events.EventRender;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static me.somepineaple.pineapleclient.main.util.MessageUtil.client_message_simple;

public class BreakHighlight extends Hack {

    private final ArrayList<BlockPos> BlocksBeingBroken = new ArrayList<>();
    private final HashMap<BlockPos, Integer> BlocksHashMap = new HashMap<>();

    public BreakHighlight() {
        super(Category.RENDER);
        this.name = "Break Highlight";
        this.tag = "BreakHighlight";
        this.description = "Highlight blocks being broken & warns u when someone is mining your feet";
    }

    Setting mode = create("Mode", "HighlightMode", "Pretty", combobox("Pretty", "Solid", "Outline"));

    Setting rgb = create("RGB Effect", "HighlightRGBEffect", true);

    Setting r = create("R", "BreakR", 255, 0, 255);
	Setting g = create("G", "BreakG", 255, 0, 255);
	Setting b = create("B", "BreakB", 255, 0, 255);
	Setting a = create("A", "BreakA", 100, 0, 255);

	Setting l_a = create("Outline A", "BreakLineA", 255, 0, 255);

	int color_r;
	int color_g;
	int color_b;

	boolean outline = false;
	boolean solid   = false;

    @Override
    protected void disable() {
        outline = false;
        solid = false;
    }

    private boolean WasJustMined = false;

    @Override
    public void update() {
        String line = "Warning >>> Ur feet r being mined rn";
        if (IsFootBeingMined()) {
            WasJustMined = true;
            client_message_simple(ChatFormatting.GOLD + PineapleClient.NAME + ChatFormatting.GRAY + " > " + ChatFormatting.RED + line);
        } else {
            if (WasJustMined) {
                client_message_simple(ChatFormatting.GOLD + PineapleClient.NAME + ChatFormatting.GRAY + " > " + ChatFormatting.DARK_AQUA + "Your feet r no longer being mined out!");
                WasJustMined = false;
            }
        }

        try {
            BlocksBeingBroken.removeIf(block -> mc.world.getBlockState(block).getBlock() instanceof BlockAir);
        } catch (Exception ignored) {}
    }

    @Override
    public void render(EventRender event) {
        if (mc.player != null && mc.world != null) {
            float[] tick_color = {
				(System.currentTimeMillis() % (360 * 32)) / (360f * 32)
			};

            int color_rgb = Color.HSBtoRGB(tick_color[0], 1, 1);

			if (rgb.get_value(true)) {
				color_r = ((color_rgb >> 16) & 0xFF);
				color_g = ((color_rgb >> 8) & 0xFF);
				color_b = (color_rgb & 0xFF);

				r.set_value(color_r);
				g.set_value(color_g);
				b.set_value(color_b);
			} else {
				color_r = r.get_value(1);
				color_g = g.get_value(2);
				color_b = b.get_value(3);
			}

			if (mode.in("Pretty")) {
				outline = true;
				solid   = true;
			}

			if (mode.in("Solid")) {
				outline = false;
				solid   = true;
			}

			if (mode.in("Outline")) {
				outline = true;
				solid   = false;
			}

			for (BlockPos block : BlocksBeingBroken) {
			    // Solid.
                if (solid) {
                    RenderHelp.prepare("quads");
                    RenderHelp.draw_cube(block, color_r, color_g, color_b, a.get_value(1), "all");
                    RenderHelp.release();
                }

                // Outline.
                if (outline) {
                    RenderHelp.prepare("lines");
                    RenderHelp.draw_cube_line(block, color_r, color_g, color_b, l_a.get_value(1), "all");
                    RenderHelp.release();
                }

                RenderUtil.drawText(block, String.valueOf(BlocksHashMap.get(block)));
            }
        }
    }

    private boolean IsFootBeingMined () {
        ArrayList<BlockPos> surroundingBlocks = new ArrayList<>();
        surroundingBlocks.add(new BlockPos((int)mc.player.posX + 1, (int)mc.player.posY, (int)mc.player.posZ));
        surroundingBlocks.add(new BlockPos((int)mc.player.posX - 1, (int)mc.player.posY, (int)mc.player.posZ));
        surroundingBlocks.add(new BlockPos((int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ + 1));
        surroundingBlocks.add(new BlockPos((int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ - 1));

        for (BlockPos block : BlocksBeingBroken) {
            if (surroundingBlocks.contains(block)) {
                return true;
            }
        }

        return false;
    }

    @EventHandler
    private final Listener<EventPacket.ReceivePacket> packet_event = new Listener<>(event -> {

        if (event.get_packet() instanceof SPacketBlockBreakAnim)
        {
            SPacketBlockBreakAnim l_Packet = (SPacketBlockBreakAnim)event.get_packet();

            try {
                if (!BlocksBeingBroken.contains(l_Packet.getPosition()) && (l_Packet.getProgress() > 0 && l_Packet.getProgress() <= 10)) {
                    BlocksBeingBroken.add(l_Packet.getPosition());
                    BlocksHashMap.remove(l_Packet.getPosition());
                    BlocksHashMap.put(l_Packet.getPosition(), l_Packet.getProgress());
                } else if (l_Packet.getProgress() <= 0 || l_Packet.getProgress() > 10) {
                    BlocksBeingBroken.remove(l_Packet.getPosition());
                    BlocksHashMap.remove(l_Packet.getPosition());
                }
            } catch (Exception ignored){}
        }
    });
}
