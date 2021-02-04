package me.somepineaple.pineapleclient.main.guiscreen.hud;


import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.render.Draw;
import me.somepineaple.pineapleclient.main.guiscreen.render.pinnables.Pinnable;
import me.somepineaple.pineapleclient.main.util.MathUtil;

public class Compass extends Pinnable {
    
    public Compass() {
		super("Compass", "Compass", 1, 0, 0);
    }
    
    public Draw font = new Draw(1);

    private static final double half_pi = Math.PI / 2;

    private enum Direction {
        N,
        W,
        S,
        E
    }

    @Override
	public void render() {
        
        int r = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorR").get_value(1);
        int g = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorG").get_value(1);
        int b = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorB").get_value(1);
        int a = PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDStringsColorA").get_value(1);

        for (Direction dir : Direction.values()) {

            double rad = get_pos_on_compass(dir);
            if (dir.name().equals("N")) {
                create_line(dir.name(), (int) (this.docking(1, dir.name()) + get_x(rad)), (int) get_y(rad), r, g, b, a);
            } else {
                create_line(dir.name(), (int) (this.docking(1, dir.name()) + get_x(rad)), (int) get_y(rad), 225, 225, 225, 225);
            }
            
        }

        this.set_width(50);
		this.set_height(50);

    }

    private double get_pos_on_compass(Direction dir) {

        double yaw = Math.toRadians(MathUtil.wrap(mc.getRenderViewEntity().rotationYaw));
        int index = dir.ordinal();
        return yaw + (index * half_pi);

    }

    private double get_x(double rad) {
        return Math.sin(rad) * (PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDCompassScale").get_value(1));
    }

    private double get_y(double rad) {

        final double epic_pitch = MathUtil.clamp2(mc.getRenderViewEntity().rotationPitch + 30f, -90f, 90f);
        final double pitch_radians = Math.toRadians(epic_pitch);
        return Math.cos(rad) * Math.sin(pitch_radians) * (PineapleClient.get_setting_manager().get_setting_with_tag("HUD", "HUDCompassScale").get_value(1));

    }

}