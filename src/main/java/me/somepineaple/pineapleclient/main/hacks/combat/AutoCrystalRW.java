package me.somepineaple.pineapleclient.main.hacks.combat;

import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;

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
    private final Setting explodeSwitch = create("Switch Delay", "acrwswd", 0.0, 0.0, 500.0);
    private final Setting explodeTicksExisted = create("Ticks Existed", "acrwte", 0.0, 0.0, 5.0);
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
    private final Setting overrdeHealth = create("Override HP", "acrwoh", 10.0, 0.0, 36.0);
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
