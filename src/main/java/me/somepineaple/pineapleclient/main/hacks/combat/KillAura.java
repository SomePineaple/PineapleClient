package me.somepineaple.pineapleclient.main.hacks.combat;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.FriendUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.stream.Collectors;

public class KillAura extends Hack {

	public KillAura() {
		super(Category.COMBAT);

		this.name        = "Kill Aura";
		this.tag         = "KillAura";
		this.description = "To able hit enemies in a range.";
	}

	Setting mode      = create("Mode", "KillAuraMode", "A32k", combobox("A32k", "Normal"));
	Setting player    = create("Player",   "KillAuraPlayer",  true);
	Setting hostile   = create("Hostile",  "KillAuraHostile", false);
	Setting sword     = create("Sword",    "KillAuraSword",   true);
	Setting syncTps   = create("Sync TPS", "KillAuraSyncTps", true);
	Setting range     = create("Range",    "KillAuraRange",   5.0, 0.5, 6.0);
	Setting delay     = create("Delay", "KillAuraDelay", 2, 0, 10);

	boolean startVerify = true;

	EnumHand actualHand = EnumHand.MAIN_HAND;

	double tick = 0;

	@Override
	protected void enable() {
		tick = 0;
	}

	@Override
	public void update() {
		if (mc.player != null && mc.world != null) {

			tick++;

			if (mc.player.isDead | mc.player.getHealth() <= 0) {
				return;
			}

			if (mode.in("Normal")) {
				if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) && sword.getValue(true)) {
					startVerify = false;
				} else if ((mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) && sword.getValue(true)) {
					startVerify = true;
				} else if (!sword.getValue(true)) {
					startVerify = true;
				}

				Entity entity = find_entity();

				if (entity != null && startVerify) {
					// Tick.
					float tickToHit  = 20.0f - PineapleClient.getEventHandler().getTickRate();

					// If possible hit or no.
					boolean isPossibleAttack = mc.player.getCooledAttackStrength(syncTps.getValue(true) ? -tickToHit : 0.0f) >= 1;

					// To hit if able.
					if (isPossibleAttack) {
						attackEntity(entity);
					}
				}
			} else {

				if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) return;

				if (tick < delay.getValue(1)) return;

				tick = 0;

				Entity entity = find_entity();

				if (entity != null) {
					attackEntity(entity);
				}
			}

		}
	}

	public void attackEntity(Entity entity) {

		if (mode.in("A32k")) {

			int newSlot = -1;

			for (int i = 0; i < 9; i++) {
				ItemStack stack = mc.player.inventory.getStackInSlot(i);
				if (stack == ItemStack.EMPTY) {
					continue;
				}
				if (checkSharpness(stack)) {
					newSlot = i;
					break;
				}
			}

			if (newSlot != -1) {
				mc.player.inventory.currentItem = newSlot;
			}

		}

		// Get actual item off hand.
		ItemStack offHandItem = mc.player.getHeldItemOffhand();

		// If off hand not null and is some SHIELD like use.
		if (offHandItem.getItem() == Items.SHIELD) {
			// Ignore ant continue.
			mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
		}

		// Start hit on entity.
		mc.player.connection.sendPacket(new CPacketUseEntity(entity));
		mc.player.swingArm(actualHand);
		mc.player.resetCooldown();
	}

	// For find a entity.
	public Entity find_entity() {
		// Create a request.
		Entity entityRequested = null;

		for (Entity player : mc.world.playerEntities.stream().filter(entityPlayer -> !FriendUtil.isFriend(entityPlayer.getName())).collect(Collectors.toList())) {
			// If entity is not null continue to next event.
			if (player != null) {
				// If is compatible.
				if (isCompatible(player)) {
					// If is possible to get.
					if (mc.player.getDistance(player) <= range.getValue(1.0)) {
						// Atribute the entity into entityRequested.
						entityRequested = player;
					}
				}
			}
		}

		// Return the entity requested.
		return entityRequested;
	}

	// Compatible or no.
	public boolean isCompatible(Entity entity) {
		// Instend entity with some type entity to continue or no.
		if (player.getValue(true) && entity instanceof EntityPlayer) {
			if (entity != mc.player && !(entity.getName().equals(mc.player.getName())) /* && FriendManager.is_friend(entity) == false */) {
				return true;
			}
		}

		// If is hostile.
		if (hostile.getValue(true) && entity instanceof IMob) {
			return true;
		}

		// If entity requested die.
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

			if (entityLivingBase.getHealth() <= 0) {
				return false;
			}
		}

		// Return false.
		return false;
	}

	private boolean checkSharpness(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			return false;
		}

		NBTTagList enchants = (NBTTagList) stack.getTagCompound().getTag("ench");

		for (int i = 0; i < enchants.tagCount(); i++) {
			NBTTagCompound enchant = enchants.getCompoundTagAt(i);
			if (enchant.getInteger("id") == 16) {
				int lvl = enchant.getInteger("lvl");
				if (lvl > 5) {
					return true;
				}
				break;
			}
		}

		return false;
	}
}
