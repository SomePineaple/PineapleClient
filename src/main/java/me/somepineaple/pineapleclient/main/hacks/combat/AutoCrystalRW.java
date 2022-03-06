package me.somepineaple.pineapleclient.main.hacks.combat;

import io.netty.util.internal.ConcurrentSet;
import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

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
    private final Setting explodeWall = create("Explode Wall Range", "acrwexpwr", 3.5, 0.0, 8.0);
    private final Setting explodeDelay = create("Explode Delay", "acrwexpd", 60, 0, 500);
    private final Setting explodeRandom = create("Explode Random Delay", "acrwexprd", 0, 0, 500);
    private final Setting explodeSwitch = create("Switch Delay", "acrwswd", 0, 0, 500);
    private final Setting explodeTicksExisted = create("Ticks Existed", "acrwte", 0, 0, 5);
    private final Setting explodeDamage = create("Explode Damage", "acrwexpda", 5.0, 0.0, 36.0);
    private final Setting explodeLocal = create("Explode Local Damage", "acrwexplda", 5.0, 0.0, 36.0);
    private final Setting explodeLimit = create("Limit", "acrwexpl", 10, 0, 10);
    private final Setting explodePacket = create("Explode w/ Packets", "acrwexpwpk", true);
    private final Setting explodeInhibit = create("Inhibit", "acrwexpi", false);
    private final Setting explodeHand = create("Hand", "acrweh", "Mainhand", combobox("Offhand", "Mainhand", "Both", "None"));
    private final Setting explodeWeakness = create("Anti Weakness", "acrwsw", "Off", combobox("Swap", "Silent", "Off"));

    // Place settings
    private final Setting place = create("Place", "acrwpl", true);
    private final Setting placeRange = create("Place Range", "acrwplr", 5.0, 0.0, 8.0);
    private final Setting placeWall = create("Place Range Wall", "acrwplrw", 3.5, 0.0, 8.0);
    private final Setting placeDelay = create("Place Delay", "acrwpld", 20, 0, 500);
    private final Setting placeDamage = create("Place Damage", "acrwplda", 5.0, 0.0, 36.0);
    private final Setting placeLocal = create("Place Local Damage", "acrwpllda", 5.0, 0.0, 36.0);
    private final Setting placePacket = create("Place Packet", "acrwplp", true);
    private final Setting placeInteract = create("Place Interact", "acrwpli", "Normal", combobox("Strict", "None", "Normal"));
    private final Setting placeRaytrace = create("Place Raytrace", "acrwplr", "Base", combobox("Normal", "Double", "Triple", "None"));
    private final Setting placeHand = create("Place Hand", "acrwplh", "Mainhand", combobox("Offhand", "Mainhand", "Both", "None"));
    private final Setting placeSwitch = create("Switch", "acrwplsw", "Off", combobox("Swap", "Silent", "Off"));

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
    private final Setting rotateLimit = create("Yaw Limit", "acrwryl", "None", combobox("Normal", "Strict", "None"));
    private final Setting rotateWhen = create("Rotate When", "acrwrw", "Both", combobox("Break", "Place", "Both"));
    private final Setting rotateRandom = create("Rotate Random", "acrwrr", 0, 0, 5);

    // Calculations settings
    private final Setting timing = create("Timing", "acrwc", "Linear", combobox("Uniform", "Sequential", "Tick", "Linear"));
    private final Setting placements = create("1.13+ mode", "acrwc1.13", false);
    private final Setting logic = create("Logic", "acrwcl", "Damage", combobox("Minimax", "Uniform", "Damage"));
    private final Setting sync = create("Synchronize", "acrwcs", "Sound", combobox("Instant", "None", "Sound"));
    private final Setting prediction = create("Prediction", "acrwcp", false);
    private final Setting ignoreTerrain = create("Ignore Terrain", "acrwcit", false);

    // Target settings
    private final Setting target = create("Target", "acrwt", "Closest", combobox("Min Health", "Min Armor", "Closest"));
    private final Setting targetRange = create("Target Range", "acrwtr", 10, 0, 16);
    private final Setting targetPlayers = create("Target Players", "acrwtpl", true);
    private final Setting targetPassives = create("Target Passives", "acrwtps", false);
    private final Setting targetNeutrals = create("Target Neutrals", "acrwtn", false);
    private final Setting targetHostiles = create("Target Hostiles", "acrwth", false);

    // Render settings
    private final Setting renderMode = create("Render", "acrwrm", "Pretty", combobox("Pretty", "Solid", "Outline", "Glow", "Glow 2", "None"));
    private final Setting topBlock = create("Render Top Block", "acrwrtb", false);
    private final Setting r = create("R", "acrwr", 255, 0, 255);
    private final Setting g = create("G", "acrwg", 255, 0, 255);
    private final Setting b = create("B", "acrwb", 255, 0, 255);
    private final Setting a = create("A", "acrwa", 100, 0, 255);
    private final Setting aOutline = create("Outline A", "acrwoutlinea", 255, 0, 255);
    private final Setting height = create("Glow Height", "acrwheight", 1.0, 0.0, 1.0);
    private final Setting renderDamage = create("Render Damage", "acrwrda",true);

    // Crystal info
    private Crystal explodeCrystal = new Crystal(null, null, 0, 0);
    private final Timer explodeTimer = new Timer();
    private final Timer switchTimer = new Timer();
    private final Map<Integer, Integer> attemptedExplosions = new ConcurrentHashMap<>();
    private final Set<EntityEnderCrystal> inhibitExplosions = new ConcurrentSet<>();

    // Placement info
    private static CrystalPosition placePositon = new CrystalPosition(BlockPos.ORIGIN, null, 0, 0);
    private final Timer placeTimer = new Timer();
    private final Map<BlockPos, Integer> attemptedPlacements = new ConcurrentHashMap<>();

    // Tick clamp
    private int switchTicks = 10;
    private int strictTicks;

    // Rotation info
    private boolean yawLimit;
    private Vec3d interactVector = Vec3d.ZERO;

    // Switch info
    private int previousSlot = -1;

    // Response time
    private long startTime = 0;
    private static double  responseTime = 0;

    // For speeedy execution
    private Thread thinkerThread = null;

    @Override
    protected void enable() {
        thinkerThread = new Thread(this::runMultiThread);
        thinkerThread.start();
    }

    @Override
    protected void disable(){
        if (thinkerThread != null) {
            try {
                thinkerThread.join();
            } catch (InterruptedException e) {
                System.err.println("Failed to join autocrystalrw thinker thread");
                e.printStackTrace();
            }
        }

        reset();
    }

    @Override
    public void update() {
        // Check tick clearance
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
            placePositon = searchPosition();
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
                    if (calculatedTarget.equals(mc.player) || calculatedTarget.isDead)
                        continue;

                    if ((calculatedTarget instanceof EntityPlayer && (!targetPlayers.getValue(true) || FriendUtil.isFriend(((EntityPlayer)calculatedTarget).getName()))) || (EntityUtil.isPassiveMob(calculatedTarget) && !targetPassives.getValue(true)) || (EntityUtil.isNeutralMob(calculatedTarget) && !targetNeutrals.getValue(true)) || (EntityUtil.isHostileMob(calculatedTarget) && !targetHostiles.getValue(true)))
                        continue;

                    float targetDistance = mc.player.getDistance(calculatedTarget);
                    if (targetDistance > targetRange.getValue(1.0))
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
        return null;
    }

    private void explodeCrystal() {

    }

    private void placeCrystal() {

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
        placePositon = new CrystalPosition(BlockPos.ORIGIN, null, 0, 0);
        interactVector = Vec3d.ZERO;
        yawLimit = false;
        previousSlot = -1;
        strictTicks = 0;
        switchTicks = 10;
        startTime = 0;
        responseTime = 0;
        placeTimer.reset();
        explodeTimer.reset();
        attemptedExplosions.clear();
        attemptedPlacements.clear();
        inhibitExplosions.clear();
    }

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
