package me.somepineaple.pineapleclient.main.hacks.movement;

import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;

public class ReverseStep extends Hack {
    public ReverseStep() {
        super(Category.MOVEMENT);
        this.name = "Reverse Step";
        this.tag = "reversestep";
        this.description = "Step, but in reverse";
    }

    @Override
    public void update() {
        if (!mc.player.onGround || mc.player.isOnLadder() || mc.player.isInWater() || mc.player.isInLava() || mc.player.movementInput.jump || mc.player.noClip) return;
        if (mc.player.moveForward == 0 && mc.player.moveStrafing == 0) return;

        mc.player.motionY = -1;
    }
}
