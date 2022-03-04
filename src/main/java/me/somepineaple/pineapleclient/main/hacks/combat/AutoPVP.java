package me.somepineaple.pineapleclient.main.hacks.combat;

import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.MessageUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class AutoPVP extends Hack {
    public AutoPVP() {
        super(Category.COMBAT);
        name = "Auto PVP";
        tag = "AutoPVP";
        description = "Does the walking 4 u :)";
    }

    private final Setting pvpMode = create("PvP Mode", "autopvpmode", "Normal", combobox("Normal", "Gearplay"));
    private final Setting hand = create("Hand", "autopvphand", "Offhand", combobox("Offhand", "Mainhand"));
    private final Setting env = create("EnvMode", "autopvpenv", "Duel", combobox("Duel", "Normal"));
    private final Setting conf = create("Config", "autopvpconf", "Custom", combobox("Custom", "2b2tPvP", "2b2t", "CC"));
    private final Setting gappleHp = create("Gapple At", "autopvpgabblehp", 35D, 1D, 36D);
    private final Setting antiWeakness = create("AntiWeakness", "autopvpantiweakness", true);
    private final Setting strict = create("Strict", "autopvpstrict", false);
    private final Setting debug = create("Debug", "autopvpdebug", false);
    //private final Setting log = create("AutoLog", "autopvplog", false);
    private final Setting followDistance = create("Follow Distance", "autopvpfollowdist", 7D, 0D, getBlockInRenderer());
    private final Setting maxCloseDistance = create("Max Close Distance", "autopvpmaxclosedist", 7D, 0D, getBlockInRenderer());

    private EntityPlayer target;
    private BlockPos targetPos;
    private String ip;
    private String opp;
    private double targetDistance;
    private double range;
    private long delay1 = 0;
    private long delay2 = 0;

    @Override
    public void update() {
        if (mc.world.playerEntities.size() < 2) {
            MessageUtil.clientMessage("AutoPvP: No players in render distance, disabling");
            set_active(false);
        } else {
            configurer();
            ifDead();
            updateTarget();
            if (target == null)
                return;
            findPlayerCoords();
            // eatGap
            if (targetDistance > maxCloseDistance.getValue(0d) && getHeath() < 36f) {
                if (doesNextSlotHaveGapple() && mc.player.getHeldItemMainhand().getItem() != Items.GOLDEN_APPLE) {
                    for (int i = 0; i < 9; i++) {
                        if (mc.player.inventory.getStackInSlot(i).getItem() == Items.GOLDEN_APPLE) {
                            mc.player.inventory.currentItem = i;
                            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                        }
                    }
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                }
            }

            if (targetDistance > maxCloseDistance.getValue(0d) && getHeath() == 36f)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);

            float newDelay1 = 1000f;
            float newDelay2 = 1000f;
            if (PineapleClient.getModuleManager().getModuleWithTag("KillAura").isActive()
                    && !mc.player.isPotionActive(MobEffects.WEAKNESS)
                    && System.currentTimeMillis() - this.delay1 >= (long)newDelay1) {
                PineapleClient.getSettingManager().getSettingWithTag(PineapleClient.getModuleManager().getModuleWithTag("Offhand"), "OffhandOffhand").setValue("Gapple");

                if (doesNextSlotHaveSword() && mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_SWORD) {
                    for (int i = 0; i < 9; i++) {
                        if (mc.player.inventory.getStackInSlot(i).getItem() == Items.DIAMOND_SWORD) {
                            mc.player.inventory.currentItem = i;
                            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                        }
                    }
                }
            } else if (System.currentTimeMillis() - this.delay2 >= (long)newDelay2) {
                PineapleClient.getSettingManager().getSettingWithTag(PineapleClient.getModuleManager().getModuleWithTag("Offhand"), "OffhandOffhand").setValue("Crystal");
                if (doesNextSlotHaveSword() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
                    for (int i = 0; i < 9; i++) {
                        if (mc.player.inventory.getStackInSlot(i).getItem() == Items.DIAMOND_SWORD) {
                            mc.player.inventory.currentItem = i;
                            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render() {

    }

    @Override
    protected void enable() {
        if (mc.player == null || mc.world == null)
            return;
        if (mc.world.playerEntities.size() < 2) {
            MessageUtil.clientMessage("AutoPvP: No players in render distance, disabling");
            set_active(false);
        }

        mc.gameSettings.autoJump = false;
    }

    private int getBlockInRenderer() {
        return mc.gameSettings.renderDistanceChunks * 16;
    }

    private void configurer() {
        PineapleClient.getModuleManager().getModuleWithTag("AutoGapple").set_active(false);
        PineapleClient.getModuleManager().getModuleWithTag("MCP").set_active(true);
        PineapleClient.getModuleManager().getModuleWithTag("Velocity").set_active(true);
        PineapleClient.getModuleManager().getModuleWithTag("Step").set_active(false);

        if (conf.in("CC")) {
            mc.gameSettings.autoJump = false;
            mc.player.stepHeight = 2.0f;
        } else {
            mc.gameSettings.autoJump = true;
            mc.player.stepHeight = 0.5f;
        }

        if ((!strict.getValue(true) && conf.in("Custom")) || conf.in("2b2tPvP") || conf.in("CC")) {
            PineapleClient.getModuleManager().getModuleWithTag("reversestep").set_active(true);
            // enable no-slow as well, when/if i make it
        }

        if ((strict.getValue(true) && conf.in("Custom")) || conf.in("2b2t")) {
            PineapleClient.getModuleManager().getModuleWithTag("reversestep").set_active(false);
            // disable no-slow, if i make it ever
        }

        if (hand.in("Offhand")) {
            PineapleClient.getModuleManager().getModuleWithTag("AutoTotem").set_active(false);
            PineapleClient.getModuleManager().getModuleWithTag("Offhand").set_active(true);
        } else {
            PineapleClient.getModuleManager().getModuleWithTag("AutoTotem").set_active(true);
            PineapleClient.getModuleManager().getModuleWithTag("Offhand").set_active(false);
        }
    }

    private int findBiggestValue() {
        return Math.max(maxCloseDistance.getValue(0), followDistance.getValue(0));
    }

    private void updateTarget() {
        double closestDist = 999;

        target = null;

        for (EntityPlayer p : mc.world.playerEntities) {
            if (p.getDistance(mc.player) < closestDist) {
                target = p;
                closestDist = p.getDistance(mc.player);
                targetDistance = closestDist;
                targetPos = target.getPosition();
            }
        }
    }

    private void findPlayerCoords() {
        int thisX = mc.player.getPosition().getX();
        int thisZ = mc.player.getPosition().getZ();
        int playerX = targetPos.getX();
        int playerZ = targetPos.getZ();

        if (pvpMode.in("Normal")) {
            if (thisX > playerX && targetDistance >= followDistance.getValue(0d) && isWB() && targetDistance >= maxCloseDistance.getValue(0d)) {
                mc.player.rotationYaw = 90f;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
            }
            if (thisX < playerX && targetDistance >= followDistance.getValue(0d) && isWB() && targetDistance >= maxCloseDistance.getValue(0d)) {
                mc.player.rotationYaw = 270.0F;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
            }
            if (thisZ > playerZ && targetDistance >= followDistance.getValue(0d) && isWB() && targetDistance >= maxCloseDistance.getValue(0d)) {
                mc.player.rotationYaw = 180.0F;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
            }
            if (thisZ < playerZ && targetDistance >= followDistance.getValue(0d) && isWB() && targetDistance >= maxCloseDistance.getValue(0d)) {
                mc.player.rotationYaw = 0.0F;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
            }
            if (targetDistance < maxCloseDistance.getValue(0d)) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
            }
            if (targetDistance == followDistance.getValue(0d) && isWB()) {
                mc.gameSettings.autoJump = false;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
            }
        }
    }

    private void ifDead() {
        if (mc.currentScreen instanceof net.minecraft.client.gui.GuiGameOver)
            set_active(false);
    }

    private static double WBRadius() {
        return mc.world.getWorldBorder().getDiameter() / 2d;
    }

    private static boolean isWB() {
        return (distanceFromSpawn() < WBRadius() - 1d);
    }

    private static double distanceFromSpawn() {
        return mc.player.getDistance(0, mc.player.posY, 0);
    }

    private static float getHeath() {
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    private static boolean doesNextSlotHaveGapple() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.GOLDEN_APPLE)
                return true;
            }
        return false;
    }

    private static boolean doesNextSlotHaveSword() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.DIAMOND_SWORD)
                return true;
            }
        return false;
    }
}
