package me.somepineaple.pineapleclient.main.hacks.movement;

import me.somepineaple.pineapleclient.main.event.events.EventMove;
import me.somepineaple.pineapleclient.main.event.events.EventPlayerJump;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;


public class Strafe extends Hack {

	public Strafe() {
		super(Category.MOVEMENT);

		this.name        = "Strafe";
		this.tag         = "Strafe";
		this.description = "its like running, but faster";
	}

	Setting speed_mode = create("Mode", "StrafeMode", "Strafe", combobox("Strafe", "On Ground"));
	Setting speed = create("Strafe Speed", "StrafeSpeed", 1.0d, 1.0d, 2.0d);
	Setting timerSpeed = create("Timer", "Strafe timer speed", 1.0, 1.0, 5.0);
	Setting auto_sprint = create("Auto Sprint", "StrafeSprint", true);
	Setting on_water = create("On Water", "StrafeOnWater", true);
	Setting auto_jump = create("Auto Jump", "StrafeAutoJump", true);
	Setting backward = create("Backwards", "StrafeBackwards", true);
	Setting bypass = create("Bypass", "StrafeBypass", false);

	@Override
	public void update() {
		mc.timer.tickLength = (float) (50.0 / timerSpeed.getValue(1.0));
		
		if (mc.player.isRiding()) return;

		if (mc.player.isInWater() || mc.player.isInLava()) {
			if (!on_water.getValue(true)) return;
		}

		if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {

			if (mc.player.moveForward < 0 && !backward.getValue(true)) return;

			if (auto_sprint.getValue(true)) {
				mc.player.setSprinting(true);
			}

			if (mc.player.onGround && speed_mode.in("Strafe")) {

				if (auto_jump.getValue(true)) {
					mc.player.motionY = 0.405f;
				}

				final float yaw = get_rotation_yaw() * 0.017453292F;
				mc.player.motionX -= MathHelper.sin(yaw) * 0.2f * speed.getValue(1.0d);
				mc.player.motionZ += MathHelper.cos(yaw) * 0.2f * speed.getValue(1.0d);

			} else if (mc.player.onGround && speed_mode.in("On Ground")) {

				final float yaw = get_rotation_yaw();
                mc.player.motionX -= MathHelper.sin(yaw) * 0.2f;
                mc.player.motionZ += MathHelper.cos(yaw) * 0.2f;
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY+0.4, mc.player.posZ, false));
				
			}
		}

		if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.onGround) {
			mc.player.motionY = 0.405f;
		}
	}

	@EventHandler
	private final Listener<EventPlayerJump> on_jump = new Listener<>(event -> {
		if (speed_mode.in("Strafe")) {
			event.cancel();
		}
	});

	@EventHandler
	private final Listener<EventMove> player_move = new Listener<>(event -> {

		if (speed_mode.in("On Ground")) return;

		if (mc.player.isInWater() || mc.player.isInLava()) {
			if (!speed_mode.getValue(true)) return;
		}

		if (mc.player.isSneaking() || mc.player.isOnLadder() || mc.player.isInWeb || mc.player.isInLava() || mc.player.isInWater() || mc.player.capabilities.isFlying) return;

		float player_speed = (float) (0.2873f * speed.getValue(1.0d));
		float move_forward = mc.player.movementInput.moveForward;
		float move_strafe = mc.player.movementInput.moveStrafe;
		float rotation_yaw = mc.player.rotationYaw;

		if (mc.player.isPotionActive(MobEffects.SPEED)) {
			final int amp = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
			player_speed *= (1.2f * (amp+1));
		}

		if (!bypass.getValue(true)) {
			player_speed *= 1.0064f;
		}

		if (move_forward == 0 && move_strafe == 0) {
			event.set_x(0.0d);
			event.set_z(0.0d);
		} else {
			if (move_forward != 0.0f) {
                if (move_strafe > 0.0f) {
                    rotation_yaw += ((move_forward > 0.0f) ? -45 : 45);
                } else if (move_strafe < 0.0f) {
                    rotation_yaw += ((move_forward > 0.0f) ? 45 : -45);
                }
                move_strafe = 0.0f;
                if (move_forward > 0.0f) {
                    move_forward = 1.0f;
                } else if (move_forward < 0.0f) {
                    move_forward = -1.0f;
                }
			}

			double cos = Math.cos(Math.toRadians((rotation_yaw + 90.0f)));
			double sin = Math.sin(Math.toRadians((rotation_yaw + 90.0f)));
			event.set_x((move_forward * player_speed) * cos + (move_strafe * player_speed) * sin);
            event.set_z((move_forward * player_speed) * sin - (move_strafe * player_speed) * cos);
		}

		event.cancel();
	});

	@Override
	protected void disable() {
		mc.player.motionX = 0;
		mc.player.motionY = 0;
	}

	private float get_rotation_yaw() {
		float rotation_yaw = mc.player.rotationYaw;
        if (mc.player.moveForward < 0.0f) {
            rotation_yaw += 180.0f;
        }
        float n = 1.0f;
        if (mc.player.moveForward < 0.0f) {
            n = -0.5f;
        }
        else if (mc.player.moveForward > 0.0f) {
            n = 0.5f;
        }
        if (mc.player.moveStrafing > 0.0f) {
            rotation_yaw -= 90.0f * n;
        }
        if (mc.player.moveStrafing < 0.0f) {
            rotation_yaw += 90.0f * n;
        }
        return rotation_yaw * 0.017453292f;
	}
}
