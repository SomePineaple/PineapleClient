package me.somepineaple.turok.draw;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

/**
* @author 086
*
* Update by me.
* 08/04/20.
*
*/
public class RenderHelp extends Tessellator {
    public static RenderHelp INSTANCE = new RenderHelp();

    public RenderHelp() {
        super(0x200000);
    }

    public static void prepare(String mode_requested) {
    	int mode = 0;

    	if (mode_requested.equalsIgnoreCase("quads")) {
    		mode = GL_QUADS;
    	} else if (mode_requested.equalsIgnoreCase("lines")) {
    		mode = GL_LINES;
    	} else if (mode_requested.equalsIgnoreCase("triangles")) {
    	    mode = GL_TRIANGLES;
        }

        prepare_gl();
        begin(mode);
    }

    public static void prepare_gl() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(1.5F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.color(1, 1, 1);
    }

    public static void begin(int mode) {
        INSTANCE.getBuffer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void release() {
        render();
        release_gl();
    }

    public static void render() {
        INSTANCE.draw();
    }

    public static void release_gl() {
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
    }

    public static void draw_cube(BlockPos blockPos, int r, int g, int b, int a, String sides) {
        draw_cube(INSTANCE.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, r, g, b, a, sides);
    }

    public static void draw_cube_line(BlockPos blockPos, int r, int g, int b, int a, String sides) {
        draw_cube_line(INSTANCE.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, r, g, b, a, sides);
    }

    public static BufferBuilder get_buffer_build() {
        return INSTANCE.getBuffer();
    }

    public static void draw_cube(final BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, String sides) {
        if (Arrays.asList(sides.split("-")).contains("down") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("up") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("north") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("south") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("south") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("south") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
    }
    
    public static void draw_gradiant_cube(final BufferBuilder buffer, float x, float y, float z, float w, float h, float d, Color startColor, Color endColor, String sides) {
        int r1 = startColor.getRed();
        int g1 = startColor.getGreen();
        int b1 = startColor.getBlue();
        int a1 = startColor.getAlpha();
        
        int r2 = endColor.getRed();
        int g2 = endColor.getGreen();
        int b2 = endColor.getBlue();
        int a2 = endColor.getAlpha();

        if (Arrays.asList(sides.split("-")).contains("north") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x, y, z).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x, y + h, z).color(r2, g2, b2, a2).endVertex();
            buffer.pos(x + w, y + h, z).color(r2, g2, b2, a2).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("south") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z + d).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x + w, y, z + d).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r2, g2, b2, a2).endVertex();
            buffer.pos(x, y + h, z + d).color(r2, g2, b2, a2).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("south") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x, y, z + d).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x, y + h, z + d).color(r2, g2, b2, a2).endVertex();
            buffer.pos(x, y + h, z).color(r2, g2, b2, a2).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("south") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z + d).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x + w, y, z).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x + w, y + h, z).color(r2, g2, b2, a2).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r2, g2, b2, a2).endVertex();
        }
    }

    public static void draw_gradiant_outline(final BufferBuilder buffer, double x, double y, double z, double height, Color startColor, Color endColor) {
        draw_gradiant_line(buffer, x, y, z, x, y + height, z, startColor, endColor);
        draw_gradiant_line(buffer, x + 1, y, z, x + 1, y + height, z, startColor, endColor);
        draw_gradiant_line(buffer, x, y, z + 1, x, y + height, z + 1, startColor, endColor);
        draw_gradiant_line(buffer, x + 1, y, z + 1, x + 1, y + height, z + 1, startColor, endColor);
    }

    public static void draw_gradiant_line(final BufferBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2, Color startColor, Color endColor) {
        buffer.pos(x1, y1, z1).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        buffer.pos(x2, y2, z2).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
    }

    public static void draw_cube_line(final BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, String sides) {
        if (Arrays.asList(sides.split("-")).contains("downwest") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("upwest") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("downeast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("upeast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }

       if (Arrays.asList(sides.split("-")).contains("downnorth") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("upnorth") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("downsouth") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("upsouth") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("nortwest") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("norteast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("southweast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }

        if (Arrays.asList(sides.split("-")).contains("southeast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
    }
}