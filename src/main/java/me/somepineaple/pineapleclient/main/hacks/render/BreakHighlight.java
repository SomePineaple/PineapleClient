package me.somepineaple.pineapleclient.main.hacks.render;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.turok.draw.RenderHelp;
import me.somepineaple.pineapleclient.main.event.events.EventRender;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;

public class BreakHighlight extends Hack {

    private final ArrayList<BlockPos> BlocksBeingBroken = new ArrayList<>();

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
	Setting range = create("Range", "BreakRange", 20, 0, 100);
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

    @Override
    public void render(EventRender event) {
        if (mc.player != null && mc.world != null) {
            float[] tick_color = {
				(System.currentTimeMillis() % (360 * 32)) / (360f * 32)
			};

            int color_rgb = Color.HSBtoRGB(tick_color[0], 1, 1);

			if (rgb.getValue(true)) {
				color_r = ((color_rgb >> 16) & 0xFF);
				color_g = ((color_rgb >> 8) & 0xFF);
				color_b = (color_rgb & 0xFF);

				r.setValue(color_r);
				g.setValue(color_g);
				b.setValue(color_b);
			} else {
				color_r = r.getValue(1);
				color_g = g.getValue(2);
				color_b = b.getValue(3);
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

			mc.renderGlobal.damagedBlocks.forEach((integer, destroyBlockProgress) -> {
				if (destroyBlockProgress != null) {

					BlockPos blockPos = destroyBlockProgress.getPosition();

					if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR) {
						return;
					}

					if (blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= range.getValue(1)) {
						if (solid) {
							RenderHelp.prepare("quads");
							RenderHelp.draw_cube(RenderHelp.get_buffer_build(),
									blockPos.getX(), blockPos.getY(), blockPos.getZ(),
									1, 1, 1,
									r.getValue(1), g.getValue(1), b.getValue(1), a.getValue(1),
									"all"
							);
							RenderHelp.release();
						}
						if (outline) {
							RenderHelp.prepare("lines");
							RenderHelp.draw_cube_line(RenderHelp.get_buffer_build(),
									blockPos.getX(), blockPos.getY(), blockPos.getZ(),
									1, 1, 1,
									r.getValue(1), g.getValue(1), b.getValue(1), l_a.getValue(1),
									"all"
							);
							RenderHelp.release();
						}
					}
				}
			});
        }
    }
}
