package me.somepineaple.pineapleclient.main.hacks.combat;

import com.mojang.realmsclient.util.Pair;
import io.netty.util.internal.ConcurrentSet;
import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.event.events.EventMotionUpdate;
import me.somepineaple.pineapleclient.main.event.events.EventPacket;
import me.somepineaple.pineapleclient.main.event.events.EventRender;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.Timer;
import me.somepineaple.pineapleclient.main.util.*;
import me.somepineaple.pineapleclient.mixins.ICPacketUseEntity;
import me.somepineaple.pineapleclient.mixins.IEntityPlayerSP;
import me.somepineaple.turok.draw.RenderHelp;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

// Yoinked from cosmos: https://github.com/momentumdevelopment/cosmos/blob/main/src/main/java/cope/cosmos/client/features/modules/combat/AutoCrystalModule.java
public class AutoCrystalRW extends Hack {
    public AutoCrystalRW() {
        super(Category.COMBAT);
        this.name = "Auto Crystal RW";
        this.tag = "AutoCrystalRW";
        this.description = "Auto Crystal, but yoinked from cosmos";
    }

    // Explode settings
    private final Setting explode = create("Explode", "acrwexp", true);
    private final Setting explodeRange = create("Explode Range", "acrwexpr", 6.0, 0.0, 8.0);
    private final Setting explodeWall = create("Explode Wall", "acrwexpwr", 3.5, 0.0, 8.0);
    private final Setting explodeDelay = create("Explode Delay", "acrwexpd", 60, 0, 500);
    private final Setting explodeRandom = create("Random Delay", "acrwexprd", 1, 1, 500);
    private final Setting explodeTicksExisted = create("Ticks Existed", "acrwte", 0, 0, 5);
    private final Setting explodeDamage = create("Explode Damage", "acrwexpda", 5.0, 0.0, 36.0);
    private final Setting explodeLocal = create("Explode Local", "acrwexplda", 5.0, 0.0, 36.0);
    private final Setting explodeLimit = create("Limit", "acrwexpl", 10, 0, 10);
    private final Setting explodePacket = create("Explode Packets", "acrwexpwpk", true);
    private final Setting explodeInhibit = create("Inhibit", "acrwexpi", false);
    private final Setting explodeHand = create("Hand", "acrweh", "Mainhand", combobox("Sync", "Offhand", "Mainhand", "Packet", "None"));
    private final Setting explodeWeakness = create("Anti Weakness", "acrwsw", "Off", combobox("Normal", "Packet", "Off"));

    // Place settings
    private final Setting place = create("Place", "acrwpl", true);
    private final Setting placeRange = create("Place Range", "acrwplr", 5.0, 0.0, 8.0);
    private final Setting placeWall = create("Place Wall", "acrwplrw", 3.5, 0.0, 8.0);
    private final Setting placeDelay = create("Place Delay", "acrwpld", 20, 0, 500);
    private final Setting placeDamage = create("Place Damage", "acrwplda", 5.0, 0.0, 36.0);
    private final Setting placeLocal = create("Place Local", "acrwpllda", 5.0, 0.0, 36.0);
    private final Setting placePacket = create("Place Packet", "acrwplp", true);
    private final Setting placeInteract = create("Place Interact", "acrwpli", "Normal", combobox("Strict", "None", "Normal"));
    private final Setting placeRaytrace = create("Place Raytrace", "acrwplray", "Base", combobox("Normal", "Double", "Triple", "None", "Base"));
    private final Setting placeHand = create("Place Hand", "acrwplh", "Mainhand", combobox("Sync", "Offhand", "Mainhand", "Packet", "None"));
    private final Setting placeSwitch = create("Switch", "acrwplsw", "Off", combobox("Normal", "Packet", "Off"));

    // Pause settings
    private final Setting pause = create("Pause", "acrwp", true);
    private final Setting pauseHealth = create("Pause Health", "acrwphp", 10.0, 0.0, 36.0);
    private final Setting pauseSafety = create("Pause Safety", "acrwps", true);
    private final Setting pauseEating = create("Pause Eating", "acrwpe", false);
    private final Setting pauseMining = create("Pause Mining", "acrwpm", false);
    private final Setting pauseMending = create("Pause Mending", "acrwpme", false);

    // Override settings
    private final Setting override = create("Override", "acrwo", true);
    private final Setting overrideHealth = create("Override HP", "acrwoh", 10.0, 0.0, 36.0);
    private final Setting overrideThreshold = create("Override Threshold", "acrwoth", 0, 0, 4);
    private final Setting overrideArmor = create("Override Armor", "acrwoa", 20.0, 0.0, 100.0);

    // Rotation settings
    private final Setting rotate = create("Rotation", "acrwr", "None", combobox("Packet", "Client", "None"));
    private final Setting rotateLimit = create("Yaw Limit", "acrwroyl", "None", combobox("Normal", "Strict", "None"));
    private final Setting rotateWhen = create("Rotate When", "acrwrow", "Both", combobox("Break", "Place", "Both"));
    private final Setting rotateRandom = create("Rotate Random", "acrwror", 0, 0, 5);

    // Calculations settings
    private final Setting timing = create("Timing", "acrwc", "Linear", combobox("Uniform", "Sequential", "Tick", "Linear"));
    private final Setting onePointThirteenMode = create("1.13+ mode", "acrwc1.13", false);
    private final Setting logic = create("Logic", "acrwcl", "Damage", combobox("Minimax", "Uniform", "Damage"));
    private final Setting sync = create("Synchronize", "acrwcs", "Sound", combobox("Instant", "None", "Sound"));

    // Target settings
    private final Setting targetRange = create("Target Range", "acrwtr", 10, 0, 16);
    private final Setting targetPlayers = create("Target Players", "acrwtpl", true);
    private final Setting targetPassives = create("Target Passives", "acrwtps", false);
    private final Setting targetNeutrals = create("Target Neutrals", "acrwtn", false);
    private final Setting targetHostiles = create("Target Hostiles", "acrwth", false);

    // Render settings
    private final Setting renderMode = create("Render", "acrwrm", "Pretty", combobox("Pretty", "Solid", "Outline", "Glow", "Glow 2", "None"));
    private final Setting topBlock = create("Render Top Block", "acrwrtb", false);
    private final Setting r = create("R", "acrwrr", 255, 0, 255);
    private final Setting g = create("G", "acrwrg", 255, 0, 255);
    private final Setting b = create("B", "acrwrb", 255, 0, 255);
    private final Setting a = create("A", "acrwra", 100, 0, 255);
    private final Setting aOutline = create("Outline A", "acrwoutlinea", 255, 0, 255);
    private final Setting height = create("Glow Height", "acrwheight", 1.0, 0.0, 1.0);
    private final Setting renderDamage = create("Render Damage", "acrwrda",true);

    // Crystal info
    private Crystal explodeCrystal = new Crystal(null, null, 0, 0);
    private final Timer explodeTimer = new Timer();
    private final Map<Integer, Integer> attemptedExplosions = new ConcurrentHashMap<>();
    private final Set<EntityEnderCrystal> inhibitExplosions = new ConcurrentSet<>();

    // Placement info
    private static CrystalPosition placePosition = new CrystalPosition(BlockPos.ORIGIN, null, 0, 0);
    private final Timer placeTimer = new Timer();
    private final Map<BlockPos, Integer> attemptedPlacements = new ConcurrentHashMap<>();

    private int strictTicks;

    // Rotation info
    private boolean yawLimit;
    private Vec3d interactVector = Vec3d.ZERO;

    // Response time
    private long startTime = 0;
    private static double  responseTime = 0;

    // For speeedy execution
    //private Thread thinkerThread = null;

    @Override
    protected void enable() {
        //thinkerThread = new Thread(this::runMultiThread);
        //thinkerThread.start();
    }

    @Override
    protected void disable(){
        /*if (thinkerThread != null) {
            try {
                thinkerThread.join();
            } catch (InterruptedException e) {
                System.err.println("Failed to join autocrystalrw thinker thread");
                e.printStackTrace();
            }
        }*/

        reset();
    }

    @Override
    public void update() {
        // Check tick clearance
        if (pause.getValue(true)) {
            if (checkPause()) {
                reset();
                strictTicks = 2;
                return;
            }
        }

        explodeCrystal = searchCrystal();
        placePosition = searchPosition();

        if (strictTicks > 0)
            strictTicks--;
        else {
            explodeCrystal();
            placeCrystal();
        }
    }

    private void runMultiThread() {
        while (moduleState) {
            if (pause.getValue(true)) {
                if (checkPause()) {
                    reset();
                    strictTicks = 2;
                    continue;
                }
            }

            explodeCrystal = searchCrystal();
            placePosition = searchPosition();

            System.out.println("Ran multi thread");
        }
    }

    private Crystal searchCrystal() {
        if (explode.getValue(true)) {
            TreeMap<Float, Crystal> crystalMap = new TreeMap<>();

            for (Entity calculatedCrystal : mc.world.loadedEntityList) {
                if (!(calculatedCrystal instanceof EntityEnderCrystal) || calculatedCrystal.isDead)
                    continue;

                EntityEnderCrystal crystal = (EntityEnderCrystal) calculatedCrystal;

                float distance = mc.player.getDistance(crystal);
                if (distance > explodeRange.getValue(1.0) || (!mc.player.canEntityBeSeen(crystal) && distance > explodeWall.getValue(1.0)))
                    continue;

                if (explodeInhibit.getValue(true) && inhibitExplosions.contains(crystal))
                    continue;

                if (crystal.ticksExisted < explodeTicksExisted.getValue(1))
                    continue;

                float localDamage = CrystalUtil.calculateDamage(crystal, mc.player);
                if (localDamage > explodeLocal.getValue(1.0) || (localDamage + 1 > PlayerUtil.getHealth() && pauseSafety.getValue(true)))
                    continue;

                // Check if we've attacked this crystal too many times
                if (explodeLocal.getValue(1) < 10 && attemptedExplosions.containsKey(crystal.getEntityId())) {
                    if (attemptedExplosions.get(crystal.getEntityId()) > explodeLimit.getValue(1))
                        continue;
                }

                for (Entity calculatedTarget : mc.world.loadedEntityList) {
                    if (checkTarget(calculatedTarget))
                        continue;

                    float targetDamage = CrystalUtil.calculateDamage(crystal, calculatedTarget);
                    float damageHeuristic = 0;
                    if (logic.in("Damage"))
                        damageHeuristic = targetDamage;
                    else if (logic.in("Minimax"))
                        damageHeuristic = targetDamage - localDamage;
                    else if (logic.in("Uniform"))
                        damageHeuristic = targetDamage - localDamage - distance;

                    crystalMap.put(damageHeuristic, new Crystal(crystal, calculatedTarget, targetDamage, localDamage));
                }
            }

            if (!crystalMap.isEmpty()) {
                // In this map, the best crystal will be the last entry
                Crystal idealCrystal = crystalMap.lastEntry().getValue();

                // Required damage for the crystal to be continued
                double requiredDamage = explodeDamage.getValue(1.0);

                // Find out if we need to override the required damage
                if (override.getValue(true)) {
                    if (idealCrystal.getTargetDamage() * overrideThreshold.getValue(1) >= EnemyUtil.getHealth(idealCrystal.getExplodeTarget()))
                        requiredDamage = idealCrystal.getTargetDamage();

                    // Critical health
                    if (EnemyUtil.getHealth(idealCrystal.getExplodeTarget()) < overrideHealth.getValue(1.0))
                        requiredDamage = 1.5;

                    // Critical armor
                    if (EnemyUtil.getLowestArmor(idealCrystal.getExplodeTarget()) <= overrideArmor.getValue(1.0))
                        requiredDamage = 1.5;
                }

                if (idealCrystal.getTargetDamage() >= requiredDamage)
                    return idealCrystal;
            }
        }

        return null;
    }

    private CrystalPosition searchPosition() {
        if (place.getValue(true)) {
            TreeMap<Float, CrystalPosition> positionMap = new TreeMap<>();

            for (BlockPos calculatedPosition : CrystalUtil.possiblePlacePositions((float) placeRange.getValue(1.0), onePointThirteenMode.getValue(true), true)) {
                // Make sure it doesn't do too much damage
                float localDamage = CrystalUtil.calculateDamage(calculatedPosition.getX() + 0.5, calculatedPosition.getY() + 1, calculatedPosition.getZ() + 0.5, mc.player);
                if (localDamage > placeLocal.getValue(1.0) || (localDamage + 1 > PlayerUtil.getHealth() && pauseSafety.getValue(true)))
                    continue;

                float rayTraceOffset = 0;
                if (placeRaytrace.in("Base"))
                    rayTraceOffset = 0.5f;
                else if (placeRaytrace.in("Normal"))
                    rayTraceOffset = 1.5f;
                else if (placeRaytrace.in("Double"))
                    rayTraceOffset = 2.5f;
                else if (placeRaytrace.in("Triple"))
                    rayTraceOffset = 3.5f;

                boolean wallPlacement = !BlockUtil.rayTracePlaceCheck(calculatedPosition, placeRaytrace.in("None"), rayTraceOffset);

                Vec3d distancePosition = new Vec3d(calculatedPosition.getX() + 0.5, calculatedPosition.getY() + 1, calculatedPosition.getZ() + 0.5);

                if (calculatedPosition.getY() > mc.player.posY + mc.player.getEyeHeight())
                    distancePosition = new Vec3d(calculatedPosition.getX() + 0.5, calculatedPosition.getY(), calculatedPosition.getZ() + 0.5);

                double distance = mc.player.getDistance(distancePosition.x, distancePosition.y, distancePosition.z);
                if (distance > placeRange.getValue(1.0))
                    continue;

                if (wallPlacement && distance > placeWall.getValue(1.0))
                    continue;

                for (Entity calculatedTarget : mc.world.loadedEntityList) {
                    if (checkTarget(calculatedTarget))
                        continue;

                    float targetDamage = CrystalUtil.calculateDamage(calculatedPosition.getX() + 0.5, calculatedPosition.getY() + 1, calculatedPosition.getZ() + 0.5, calculatedTarget);
                    float damageHeuristic = 0;
                    if (logic.in("Damage"))
                        damageHeuristic = targetDamage;
                    else if (logic.in("Minimax"))
                        damageHeuristic = targetDamage - localDamage;
                    else if (logic.in("Uniform"))
                        damageHeuristic = (float) (targetDamage - localDamage - distance);

                    positionMap.put(damageHeuristic, new CrystalPosition(calculatedPosition, calculatedTarget, targetDamage, localDamage));
                }
            }

            if (!positionMap.isEmpty()) {
                // In this map, the best crystal will be the last entry
                CrystalPosition idealPos = positionMap.lastEntry().getValue();

                // Required damage for the crystal to be continued
                double requiredDamage = placeDamage.getValue(1.0);

                // Find out if we need to override the required damage
                if (override.getValue(true)) {
                    if (idealPos.getTargetDamage() * overrideThreshold.getValue(1) >= EnemyUtil.getHealth(idealPos.getPlaceTarget()))
                        requiredDamage = idealPos.getTargetDamage();

                    // Critical health
                    if (EnemyUtil.getHealth(idealPos.getPlaceTarget()) < overrideHealth.getValue(1.0))
                        requiredDamage = 1.5;

                    // Critical armor
                    if (EnemyUtil.getLowestArmor(idealPos.getPlaceTarget()) <= overrideArmor.getValue(1.0))
                        requiredDamage = 1.5;
                }

                if (idealPos.getTargetDamage() >= requiredDamage)
                    return idealPos;
            }
        }

        return null;
    }

    private void explodeCrystal() {
        if (explodeCrystal != null) {
            boolean silentSwapped = false;
            int silentBefore = mc.player.inventory.currentItem;

            if (!rotate.in("None") && (rotateWhen.in("Break") || rotateWhen.in("Both"))) {
                // our last interaction will be the attack on the crystal
                interactVector = explodeCrystal.getCrystal().getPositionVector();

                if (rotate.in("Client")) {
                    float[] angles = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), interactVector);
                    mc.player.rotationYaw = angles[0];
                    mc.player.rotationYawHead = angles[0];
                    mc.player.rotationPitch = angles[1];
                }
            }

            if (!explodeWeakness.in("Off")) {
                // strength and weakness effects on the player
                PotionEffect weaknessEffect = mc.player.getActivePotionEffect(MobEffects.WEAKNESS);
                PotionEffect strengthEffect = mc.player.getActivePotionEffect(MobEffects.STRENGTH);

                if (weaknessEffect != null && (strengthEffect == null || strengthEffect.getAmplifier() < weaknessEffect.getAmplifier())) {
                    int swordSlot = getSwordSlot();
                    int pickSlot = getPickSlot();

                    if (!(mc.player.inventory.getCurrentItem().getItem() instanceof ItemSword || mc.player.inventory.getCurrentItem().getItem() instanceof ItemPickaxe)) {
                        if (swordSlot != -1) {
                            if (explodeWeakness.in("Normal"))
                                mc.player.inventory.currentItem = swordSlot;

                            mc.player.connection.sendPacket(new CPacketHeldItemChange(swordSlot));

                            if (explodeWeakness.in("Packet"))
                                silentSwapped = true;
                        } else if (pickSlot != -1) {
                            if (explodeWeakness.in("Normal"))
                                mc.player.inventory.currentItem = pickSlot;

                            mc.player.connection.sendPacket(new CPacketHeldItemChange(pickSlot));
                            if (explodeWeakness.in("Packet"))
                                silentSwapped = true;
                        }
                    }
                }
            }

            if (explodeTimer.passed(explodeDelay.getValue(1) + ThreadLocalRandom.current().nextInt(explodeRandom.getValue(1)))) {
                explodeCrystal(explodeCrystal.getCrystal(), explodePacket.getValue(true));
                swingCrystal(explodeHand);

                explodeTimer.reset();

                attemptedExplosions.put(explodeCrystal.getCrystal().getEntityId(), attemptedExplosions.containsKey(explodeCrystal.getCrystal().getEntityId()) ? attemptedExplosions.get(explodeCrystal.getCrystal().getEntityId()) + 1 : 1);

                if (sync.in("Instant"))
                    mc.world.removeEntityDangerously(explodeCrystal.getCrystal());

                if (silentSwapped) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(silentBefore));
                }
            }
        }
    }

    private void placeCrystal() {
        if (placePosition != null) {
            boolean silentSwapped = false;
            int silentBefore = mc.player.inventory.currentItem;

            if (!rotate.in("None") && (rotateWhen.in("Place") || rotateWhen.in("Both"))) {
                interactVector = new Vec3d(placePosition.getPosition()).add(0.5, 0.5, 0.5);

                if (rotate.in("Client")) {
                    float[] angles = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), interactVector);

                    mc.player.rotationYaw = angles[0];
                    mc.player.rotationYawHead = angles[0];
                    mc.player.rotationPitch = angles[1];
                }
            }

            if (!PlayerUtil.isHolding(Items.END_CRYSTAL)) {
                int crystalSlot = getCrystalSlot();
                if (crystalSlot != -1) {
                    if (placeSwitch.in("Normal")) {
                        mc.player.inventory.currentItem = crystalSlot;
                        return;
                    }

                    mc.player.connection.sendPacket(new CPacketHeldItemChange(crystalSlot));
                    silentSwapped = true;
                }
            }

            if (placeTimer.passed(placeDelay.getValue(1)) && (PlayerUtil.isHolding(Items.END_CRYSTAL) || placeSwitch.in("Packet"))) {
                // directions of placement
                double facingX = 0;
                double facingY = 0;
                double facingZ = 0;

                // assume the face is visible
                EnumFacing facingDirection = EnumFacing.UP;

                float[] rotationAngles = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), interactVector);
                Vec3d placeVector = MathUtil.getVectorForRotation(rotationAngles);
                RayTraceResult vectorResult = mc.world.rayTraceBlocks(mc.player.getPositionEyes(1), mc.player.getPositionEyes(1).add(placeVector.x * placeRange.getValue(1.0), placeVector.y * placeRange.getValue(1.0), placeVector.z * placeRange.getValue(1.0)), false, false, false);

                if (placeInteract.in("None")) {
                    facingDirection = EnumFacing.DOWN;
                    facingX = 0.5;
                    facingY = 0.5;
                    facingZ = 0.5;
                } else if (placeInteract.in("Normal")) {
                    // find the direction to place against
                    RayTraceResult laxResult = mc.world.rayTraceBlocks(mc.player.getPositionEyes(1), interactVector);

                    if (laxResult != null && laxResult.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                        facingDirection = laxResult.sideHit;

                        // if we're at world height, we can still place a crystal if we interact with the bottom of the block, this doesn't work on strict servers
                        if (placePosition.getPosition().getY() >= (mc.world.getActualHeight() - 1)) {
                            facingDirection = EnumFacing.DOWN;
                        }
                    }

                    // find rotations based on the placement
                    if (vectorResult != null && vectorResult.hitVec != null) {
                        facingX = vectorResult.hitVec.x - placePosition.getPosition().getX();
                        facingY = vectorResult.hitVec.y - placePosition.getPosition().getY();
                        facingZ = vectorResult.hitVec.z - placePosition.getPosition().getZ();
                    }
                } else if (placeInteract.in("Strict")) {
                    // if the place position is likely out of sight
                    if (placePosition.getPosition().getY() > mc.player.posY + mc.player.getEyeHeight()) {
                        // the place vectors lowest bounds
                        Vec3d strictVector = new Vec3d(placePosition.getPosition());

                        // our nearest visible face
                        Pair<Double, EnumFacing> closestDirection = Pair.of(Double.MAX_VALUE, EnumFacing.UP);

                        // iterate through all points on the block
                        for (float x = 0; x <= 1; x += 0.05) {
                            for (float y = 0; y <= 1; y += 0.05) {
                                for (float z = 0; z <= 1; z += 0.05) {
                                    // find the vector to raytrace to
                                    Vec3d traceVector = strictVector.add(x, y, z);

                                    // check visibility, raytrace to the current point
                                    RayTraceResult strictResult = mc.world.rayTraceBlocks(mc.player.getPositionEyes(1), traceVector, false, true, false);

                                    // if our raytrace is a block, check distances
                                    if (strictResult != null && strictResult.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                                        // distance to face
                                        double directionDistance = mc.player.getDistance(traceVector.x, traceVector.y, traceVector.z);

                                        // if the face is the closest to the player and trace distance is reasonably close, then we have found a new ideal visible side to place against
                                        if (directionDistance < closestDirection.first()) {
                                            closestDirection = Pair.of(directionDistance, strictResult.sideHit);
                                        }
                                    }
                                }
                            }
                        }

                        facingDirection = closestDirection.second();
                    }

                    // find rotations based on the placement
                    if (vectorResult != null && vectorResult.hitVec != null) {
                        facingX = vectorResult.hitVec.x - placePosition.getPosition().getX();
                        facingY = vectorResult.hitVec.y - placePosition.getPosition().getY();
                        facingZ = vectorResult.hitVec.z - placePosition.getPosition().getZ();
                    }
                }

                placeCrystal(placePosition.getPosition(), facingDirection, new Vec3d(facingX, facingY, facingZ), placePacket.getValue(true));
                swingCrystal(placeHand);

                if (silentSwapped)
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(silentBefore));

                placeTimer.reset();
                attemptedPlacements.put(placePosition.getPosition(), attemptedPlacements.containsKey(placePosition.getPosition()) ? attemptedPlacements.get(placePosition.getPosition()) + 1 : 1);
            }
        }
    }

    private void explodeCrystal(EntityEnderCrystal crystal, boolean packet) {
        if (crystal != null) {
            if (packet)
                mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
            else
                mc.playerController.attackEntity(mc.player, crystal);
        }
    }

    public void explodeCrystal(int entityID) {
        CPacketUseEntity attackPacket = new CPacketUseEntity();
        ((ICPacketUseEntity) attackPacket).setID(entityID);
        ((ICPacketUseEntity) attackPacket).setAction(CPacketUseEntity.Action.ATTACK);
        mc.player.connection.sendPacket(attackPacket);
    }

    public void placeCrystal(BlockPos position, EnumFacing facing, Vec3d vector, boolean packet) {
        if (position != null) {
            if (packet) {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(position, facing, mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) || placeSwitch.in("Packet") ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND, (float) vector.x, (float) vector.y, (float) vector.z));
            }

            else {
                mc.playerController.processRightClickBlock(mc.player, mc.world, position, facing, vector, mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) || placeSwitch.in("Packet") ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
            }
        }
    }

    private void swingCrystal(Setting hand) {
        if (hand.in("Sync")) {
            if (mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) || placeSwitch.in("Packet"))
                mc.player.swingArm(EnumHand.MAIN_HAND);
            else if (mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL))
                mc.player.swingArm(EnumHand.OFF_HAND);
        }
        else if (hand.in("Mainhand"))
            mc.player.swingArm(EnumHand.MAIN_HAND);
        else if (hand.in("Offhand"))
            mc.player.swingArm(EnumHand.OFF_HAND);
        else if (hand.in("Packet"))
            mc.player.connection.sendPacket(new CPacketAnimation(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) || placeSwitch.in("Packet") ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
    }

    @Override
    public void render(EventRender event) {
        if (placePosition == null)
            return;

        boolean outline = false;
        boolean solid = false;
        boolean glow = false;
        boolean glowLines = false;

        if (renderMode.in("None")) return;

        if (renderMode.in("Pretty")) {
            outline = true;
            solid = true;
        }

        if (renderMode.in("Solid")) {
            outline = false;
            solid = true;
        }

        if (renderMode.in("Outline")) {
            outline = true;
            solid = false;
        }

        if (renderMode.in("Glow")) {
            outline = false;
            solid = false;
            glow = true;
        }

        if (renderMode.in("Glow 2")) {
            outline = false;
            solid = false;
            glow = true;
            glowLines = true;
        }

        BlockPos renderBlock = topBlock.getValue(true) ? placePosition.getPosition().up() : placePosition.getPosition();

        renderBlock(renderBlock, solid, outline, glow, glowLines);
        if (renderDamage.getValue(true))
            RenderUtil.drawText(renderBlock, String.format("%.1f", placePosition.getTargetDamage()));
    }

    private void renderBlock(BlockPos pos, boolean solid, boolean outline, boolean glow, boolean glowLines) {
        float h = (float) height.getValue(1.0);
        //MessageUtil.clientMessageSimple("rendering block at pos " + pos.getX() + " " + pos.getY() + " " + pos.getZ());

        if (solid) {
            RenderHelp.prepare("quads");
            RenderHelp.draw_cube(RenderHelp.get_buffer_build(),
                    pos.getX(), pos.getY(), pos.getZ(),
                    1, h, 1,
                    r.getValue(1), g.getValue(1), b.getValue(1), a.getValue(1),
                    "all"
            );
            RenderHelp.release();
        }

        if (outline) {
            RenderHelp.prepare("lines");
            RenderHelp.draw_cube_line(RenderHelp.get_buffer_build(),
                    pos.getX(), pos.getY(), pos.getZ(),
                    1, h, 1,
                    r.getValue(1), g.getValue(1), b.getValue(1), aOutline.getValue(1),
                    "all"
            );
            RenderHelp.release();
        }

        if (glow) {
            RenderHelp.prepare("lines");
            RenderHelp.draw_cube_line(RenderHelp.get_buffer_build(),
                    pos.getX(), pos.getY(), pos.getZ(),
                    1, 0, 1,
                    r.getValue(1), g.getValue(1), b.getValue(1), aOutline.getValue(1),
                    "all"
            );
            RenderHelp.release();
            RenderHelp.prepare("quads");
            RenderHelp.draw_gradiant_cube(RenderHelp.get_buffer_build(),
                    pos.getX(), pos.getY(), pos.getZ(),
                    1, h, 1,  new Color(r.getValue(1), g.getValue(1), b.getValue(1), a.getValue(1)),
                    new Color(0, 0, 0, 0),
                    "all"
            );
            RenderHelp.release();
        }

        if (glowLines) {
            RenderHelp.prepare("lines");
            RenderHelp.draw_gradiant_outline(RenderHelp.get_buffer_build(),
                    pos.getX(), pos.getY(), pos.getZ(),
                    h, new Color(r.getValue(1), g.getValue(1), b.getValue(1), aOutline.getValue(1)),
                    new Color(0, 0, 0, 0),
                    "all"
            );
            RenderHelp.release();
        }
    }

    private boolean checkTarget(Entity target) {
        if (target.equals(mc.player) || target.isDead)
            return true;

        if ((target instanceof EntityPlayer && (!targetPlayers.getValue(true) || FriendUtil.isFriend(target.getName()))) || (EntityUtil.isPassiveMob(target) && !targetPassives.getValue(true)) || (EntityUtil.isNeutralMob(target) && !targetNeutrals.getValue(true)) || (EntityUtil.isHostileMob(target) && !targetHostiles.getValue(true)))
            return true;

        float targetDistance = mc.player.getDistance(target);

        return targetDistance > targetRange.getValue(1.0);
    }

    private boolean checkPause() {
        if ((PlayerUtil.isEating() && pauseEating.getValue(true)) || (PlayerUtil.isMining() && pauseMining.getValue(true)) || (PlayerUtil.isMending() && pauseMending.getValue(true)))
            return true;

        if (PlayerUtil.getHealth() < pauseHealth.getValue(1.0) && !mc.player.capabilities.isCreativeMode)
            return true;

        Surround s = (Surround) PineapleClient.getHackManager().getModuleWithTag("Surround");
        if (s.areBlocksLeftToSurrond())
            return true;

        if (PineapleClient.getHackManager().getModuleWithTag("HoleFill").isActive())
            return true;

        if (PineapleClient.getHackManager().getModuleWithTag("BlockLag").isActive())
            return true;

        return PineapleClient.getHackManager().getModuleWithTag("Trap").isActive();
    }

    private void reset() {
        explodeCrystal = new Crystal(null, null, 0, 0);
        placePosition = new CrystalPosition(BlockPos.ORIGIN, null, 0, 0);
        interactVector = Vec3d.ZERO;
        yawLimit = false;
        strictTicks = 0;
        startTime = 0;
        responseTime = 0;
        placeTimer.reset();
        explodeTimer.reset();
        attemptedExplosions.clear();
        attemptedPlacements.clear();
        inhibitExplosions.clear();
    }

    private int getSwordSlot() {
        int swordSlot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemSword) {
                swordSlot = i;
                break;
            }
        }

        return swordSlot;
    }

    private int getPickSlot() {
        int pickSlot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemPickaxe) {
                pickSlot = i;
                break;
            }
        }

        return pickSlot;
    }

    private int getCrystalSlot() {
        int crystalSlot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem().equals(Items.END_CRYSTAL)) {
                crystalSlot = i;
            }
        }

        return crystalSlot;
    }

    @EventHandler
    private final Listener<EventMotionUpdate> on_movement = new Listener<>(event -> {
        if (event.stage == 0) {
            PosManager.updatePosition();
            RotationUtil.updateRotations();

            if (rotate.in("Packet")) {
                float[] packetAngles = MathUtil.calcAngle(mc.player.getPositionEyes(1), interactVector);

                if (rotateRandom.getValue(1) > 0) {
                    Random randomAngle = new Random();
                    packetAngles[0] = packetAngles[0] + (randomAngle.nextFloat() * (randomAngle.nextBoolean() ? rotateRandom.getValue(1) : -rotateRandom.getValue(1)));
                }

                if (!rotateLimit.in("None")) {
                    float yawDiff = MathHelper.wrapDegrees(packetAngles[0] - ((IEntityPlayerSP) mc.player).getLastReportedYaw());

                    if (Math.abs(yawDiff) > 55 && !yawLimit) {
                        packetAngles[0] = ((IEntityPlayerSP) mc.player).getLastReportedYaw();
                        strictTicks++;
                        yawLimit = true;
                    }

                    if (strictTicks <= 0) {
                        if (rotateLimit.in("Strict"))
                            packetAngles[0] = ((IEntityPlayerSP) mc.player).getLastReportedYaw() + (yawDiff > 0 ? Math.min(Math.abs(yawDiff), 55) : -Math.min(Math.abs(yawDiff), 55));
                        yawLimit = false;
                    }

                    RotationUtil.setPlayerRotations(packetAngles[0], packetAngles[1]);
                }
            }
        }
        if (event.stage == 1) {
            PosManager.restorePosition();
            RotationUtil.restoreRotations();
        }
    });

    @EventHandler
    private final Listener<EventPacket.ReceivePacket> receivePacketListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketSpawnObject && ((SPacketSpawnObject) event.getPacket()).getType() == 51) {
            // position of the placed crystal
            BlockPos linearPosition = new BlockPos(((SPacketSpawnObject) event.getPacket()).getX(), ((SPacketSpawnObject) event.getPacket()).getY(), ((SPacketSpawnObject) event.getPacket()).getZ());

            if (attemptedPlacements.containsKey(linearPosition.down())) {
                startTime = System.currentTimeMillis();

                if (timing.in("Sequential")) {
                    if (!explodeTimer.passed(explodeDamage.getValue(1)))
                        explodeTimer.setTimePassed(explodeDelay.getValue(1));
                }

                attemptedPlacements.clear();
            }

            if ((timing.in("Linear") || timing.in("Uniform")) && explode.getValue(true)) {
                float rayTraceOffset = 0;
                if (placeRaytrace.in("Base"))
                    rayTraceOffset = 0.5f;
                else if (placeRaytrace.in("Normal"))
                    rayTraceOffset = 1.5f;
                else if (placeRaytrace.in("Double"))
                    rayTraceOffset = 2.5f;
                else if (placeRaytrace.in("Triple"))
                    rayTraceOffset = 3.5f;

                boolean wallLinear = !BlockUtil.rayTracePlaceCheck(linearPosition, placeRaytrace.in("None"), rayTraceOffset);

                double distance = mc.player.getDistance(linearPosition.getX() + 0.5, linearPosition.getY() + 1, linearPosition.getZ() + 0.5);
                if (distance > explodeWall.getValue(1.0) && wallLinear)
                    return;

                float localDamage = CrystalUtil.calculateDamage(linearPosition.getX() + 0.5, linearPosition.getY() + 1, linearPosition.getZ(), mc.player);
                if (localDamage > explodeLocal.getValue(1.0) || (localDamage + 1 > PlayerUtil.getHealth() && pauseSafety.getValue(true)))
                    return;

                TreeMap<Float, Integer> linearMap = new TreeMap<>();
                for (EntityPlayer calculatedTarget : mc.world.playerEntities) {
                    if (calculatedTarget.equals(mc.player) || calculatedTarget.isDead || calculatedTarget.getHealth() <= 0)
                        continue;

                    float targetDistance = mc.player.getDistance(calculatedTarget);
                    if (targetDistance > targetRange.getValue(1.0))
                        continue;

                    float targetDamage = CrystalUtil.calculateDamage(linearPosition.getX() + 0.5, linearPosition.getY() + 1, linearPosition.getZ(), calculatedTarget);

                    float damageHeuristic = 0;
                    if (logic.in("Damage"))
                        damageHeuristic = targetDamage;
                    else if (logic.in("Minimax"))
                        damageHeuristic = targetDamage - localDamage;
                    else if (logic.in("Uniform"))
                        damageHeuristic = (float) (targetDamage - localDamage - distance);

                    linearMap.put(damageHeuristic, ((SPacketSpawnObject) event.getPacket()).getEntityID());
                }

                if (!linearMap.isEmpty()) {
                    Map.Entry<Float, Integer> idealLinear = linearMap.lastEntry();

                    if (idealLinear.getKey() > explodeDamage.getValue(1.0)) {
                        boolean silentSwapped = false;
                        int silentBefore = mc.player.inventory.currentItem;

                        if (!rotate.in("None") && (rotateWhen.in("Break") || rotateWhen.in("Both"))) {
                            interactVector = new Vec3d(linearPosition).add(0.5, 0.5, 0.5);

                            if (rotate.in("Client")) {
                                float[] rotationAngles = MathUtil.calcAngle(mc.player.getPositionEyes(1), interactVector);
                                mc.player.rotationYaw = rotationAngles[0];
                                mc.player.rotationYawHead = rotationAngles[0];
                                mc.player.rotationPitch = rotationAngles[1];
                            }
                        }

                        if (!explodeWeakness.in("Off")) {
                            // strength and weakness effects on the player
                            PotionEffect weaknessEffect = mc.player.getActivePotionEffect(MobEffects.WEAKNESS);
                            PotionEffect strengthEffect = mc.player.getActivePotionEffect(MobEffects.STRENGTH);

                            if (weaknessEffect != null && (strengthEffect == null || strengthEffect.getAmplifier() < weaknessEffect.getAmplifier())) {
                                int swordSlot = getSwordSlot();
                                int pickSlot = getPickSlot();

                                if (!(mc.player.inventory.getCurrentItem().getItem() instanceof ItemSword || mc.player.inventory.getCurrentItem().getItem() instanceof ItemPickaxe)) {
                                    if (swordSlot != -1) {
                                        if (explodeWeakness.in("Normal"))
                                            mc.player.inventory.currentItem = swordSlot;

                                        mc.player.connection.sendPacket(new CPacketHeldItemChange(swordSlot));

                                        if (placeSwitch.in("Packet"))
                                            silentSwapped = true;
                                    } else if (pickSlot != -1) {
                                        if (explodeWeakness.in("Normal"))
                                            mc.player.inventory.currentItem = pickSlot;

                                        mc.player.connection.sendPacket(new CPacketHeldItemChange(pickSlot));

                                        if (placeSwitch.in("Packet"))
                                            silentSwapped = true;
                                    }
                                }
                            }
                        }

                        explodeCrystal(idealLinear.getValue());
                        swingCrystal(explodeHand);

                        attemptedExplosions.put(((SPacketSpawnObject) event.getPacket()).getEntityID(), attemptedExplosions.containsKey(((SPacketSpawnObject) event.getPacket()).getEntityID()) ? attemptedExplosions.get(((SPacketSpawnObject) event.getPacket()).getEntityID()) + 1 : 1);

                        if (sync.in("Instant"))
                            mc.world.removeEntityFromWorld(idealLinear.getValue());

                        if (silentSwapped)
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(silentBefore));
                    }
                }
            }
        } else if (event.getPacket() instanceof SPacketDestroyEntities) {
            for (int entityId : ((SPacketDestroyEntities) event.getPacket()).getEntityIDs()) {
                if (attemptedExplosions.containsKey(entityId)) {
                    responseTime = System.currentTimeMillis() - startTime;

                    if (timing.in("Sequential") || timing.in("Uniform")) {
                        if (!placeTimer.passed(placeDelay.getValue(1)))
                            placeTimer.setTimePassed(placeDelay.getValue(1));
                    }

                    attemptedExplosions.clear();
                    break;
                }
            }
        } else if (event.getPacket() instanceof SPacketSoundEffect && ((SPacketSoundEffect) event.getPacket()).getSound().equals(SoundEvents.ENTITY_GENERIC_EXPLODE)) {
            inhibitExplosions.clear();
            // schedule to main mc thread
            mc.addScheduledTask(() -> {
                for (Iterator<Entity> entityList = mc.world.loadedEntityList.iterator(); entityList.hasNext();) {
                    // next entity in the world
                    Entity entity = entityList.next();

                    // make sure it's a crystal
                    if (!(entity instanceof EntityEnderCrystal) || entity.isDead) {
                        continue;
                    }

                    // make sure the crystal is in range from the sound to be destroyed
                    double soundDistance = entity.getDistance(((SPacketSoundEffect) event.getPacket()).getX(), ((SPacketSoundEffect) event.getPacket()).getY(), ((SPacketSoundEffect) event.getPacket()).getZ());
                    if (soundDistance > 6) {
                        continue;
                    }

                    // going to be exploded anyway, so don't attempt explosion
                    if (explodeInhibit.getValue(true)) {
                        inhibitExplosions.add((EntityEnderCrystal) entity);
                    }

                    // the world sets the crystal dead one tick after this packet, but we can speed up the placements by setting it dead here
                    if (sync.in("Sound")) {
                        mc.world.removeEntityDangerously(entity);
                    }
                }
            });
        }
    });

    // Utility classes
    public static class CrystalPosition {
        // place info
        private final BlockPos blockPos;
        private final Entity placeTarget;

        // damage info
        private final double targetDamage;
        private final double localDamage;

        public CrystalPosition(BlockPos blockPos, Entity placeTarget, double targetDamage, double localDamage) {
            this.blockPos = blockPos;
            this.placeTarget = placeTarget;
            this.targetDamage = targetDamage;
            this.localDamage = localDamage;
        }

        /**
         * Gets the position of a placement
         * @return The {@link BlockPos} position of the placement
         */
        public BlockPos getPosition() {
            return blockPos;
        }

        /**
         * Gets the target of a placement
         * @return The {@link Entity} target of the placement
         */
        public Entity getPlaceTarget() {
            return placeTarget;
        }

        /**
         * Gets the damage to a target
         * @return The damage to the target
         */
        public double getTargetDamage() {
            return targetDamage;
        }

        /**
         * Gets the damage to the player
         * @return The damage to the player
         */
        public double getLocalDamage() {
            return localDamage;
        }
    }

    public static class Crystal {

        // crystal info
        private final EntityEnderCrystal crystal;
        private final Entity explodeTarget;

        // damage info
        private final double targetDamage;
        private final double localDamage;

        public Crystal(EntityEnderCrystal crystal, Entity explodeTarget, double targetDamage, double localDamage) {
            this.crystal = crystal;
            this.explodeTarget = explodeTarget;
            this.targetDamage = targetDamage;
            this.localDamage = localDamage;
        }

        /**
         * Gets the crystal entity
         * @return The {@link EntityEnderCrystal} crystal entity
         */
        public EntityEnderCrystal getCrystal() {
            return crystal;
        }

        /**
         * Gets the target of a explosion
         * @return The {@link Entity} target of the explosion
         */
        public Entity getExplodeTarget() {
            return explodeTarget;
        }

        /**
         * Gets the damage to a target
         * @return The damage to the target
         */
        public double getTargetDamage() {
            return targetDamage;
        }

        /**
         * Gets the damage to the player
         * @return The damage to the player
         */
        public double getLocalDamage() {
            return localDamage;
        }
    }
}
