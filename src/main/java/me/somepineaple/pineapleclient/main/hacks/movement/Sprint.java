package me.somepineaple.pineapleclient.main.hacks.movement;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;

public class Sprint extends Hack {
    
    public Sprint() {
        super(Category.MOVEMENT);

		this.name        = "Sprint";
		this.tag         = "Sprint";
		this.description = "ZOOOOOOOOM";
    }

    Setting rage = create("Rage", "SprintRage", true);

    @Override
	public void update() {

    	if (mc.player == null) return;

    	if (rage.get_value(true) && (mc.player.moveForward != 0 || mc.player.moveStrafing != 0)) {
			mc.player.setSprinting(true);
		} else mc.player.setSprinting(mc.player.moveForward > 0 || mc.player.moveStrafing > 0);
	}
}
