package me.somepineaple.pineapleclient.main.hacks.combat;

import me.somepineaple.pineapleclient.main.event.events.EventPacket;
import me.somepineaple.pineapleclient.main.event.events.EventRender;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.turok.draw.RenderHelp;
import me.somepineaple.pineapleclient.PineapleClient;
import me.somepineaple.pineapleclient.main.event.events.EventEntityRemoved;
import me.somepineaple.pineapleclient.main.event.events.EventMotionUpdate;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.hacks.chat.AutoEz;
import me.somepineaple.pineapleclient.main.util.*;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AutoCrystal extends Hack {

    private static EntityPlayer ca_target = null;

    public AutoCrystal() {
        super(Category.COMBAT);

        this.name        = "Auto Crystal";
        this.tag         = "AutoCrystal";
        this.description = "kills people (if ur good)";
    }

    Setting debug = create("Debug", "CaDebug", false);
    Setting place_crystal = create("Place", "CaPlace", true);
    Setting break_crystal = create("Break", "CaBreak", true);
    Setting break_trys = create("Break Attempts", "CaBreakAttempts", 2, 1, 6);
    Setting anti_weakness = create("Anti-Weakness", "CaAntiWeakness", true);

    Setting hit_range = create("Hit Range", "CaHitRange", 5.2f, 1f, 6f);
    Setting place_range = create("Place Range", "CaPlaceRange", 5.2f, 1f, 6f);
    Setting hit_range_wall = create("Range Wall", "CaRangeWall", 4f, 1f, 6f);

    Setting place_delay = create("Place Delay", "CaPlaceDelay", 0, 0, 10);
    Setting break_delay = create("Break Delay", "CaBreakDelay", 2, 0, 10);

    Setting min_player_place = create("Min Enemy Place", "CaMinEnemyPlace", 8, 0, 20);
    Setting min_player_break = create("Min Enemy Break", "CaMinEnemyBreak", 6, 0, 20);
    Setting max_self_damage = create("Max Self Damage", "CaMaxSelfDamage", 6, 0, 20);

    Setting rotate_mode = create("Rotate", "CaRotateMode", "Good", combobox("Off", "Old", "Const", "Good"));
    Setting raytrace = create("Raytrace", "CaRaytrace", false);

    Setting switch_mode = create("Switch: ", "CaSwitchMode", "Silent", combobox("Swap", "Silent", "Off"));
    Setting anti_suicide = create("Anti Suicide", "CaAntiSuicide", true);

    Setting fast_mode = create("Fast Mode", "CaSpeed", true);
    Setting client_side = create("Client Side", "CaClientSide", false);
    Setting jumpy_mode = create("Jumpy Mode", "CaJumpyMode", false);
    Setting attempt_chain = create("Attemp Chain", "CaAttemptChain", false);

    Setting anti_stuck = create("Anti Stuck", "CaAntiStuck", false);
    Setting endcrystal = create("1.13 Mode", "CaThirteen", false);

    Setting faceplace_mode = create("Tabbott Mode", "CaTabbottMode", true);
    Setting faceplace_mode_damage = create("T Health", "CaTabbottModeHealth", 8, 0, 36);

    Setting fuck_armor_mode = create("Armor Destroy", "CaArmorDestory", true);
    Setting fuck_armor_mode_precent = create("Armor %", "CaArmorPercent", 25, 0, 100);

    Setting stop_while_mining = create("Stop While Mining", "CaStopWhileMining", false);
    Setting faceplace_check = create("No Sword FP", "CaJumpyFaceMode", false);

    Setting swing = create("Swing", "CaSwing", "Mainhand", combobox("Mainhand", "Offhand", "Both", "None"));

    Setting render_mode = create("Render", "CaRenderMode", "Pretty", combobox("Pretty", "Solid", "Outline", "Glow", "Glow 2", "None"));
    Setting old_render = create("Old Render", "CaOldRender", false);
    Setting future_render = create("Future Render", "CaFutureRender", false);
    Setting top_block = create("Top Block", "CaTopBlock", false);
    Setting r = create("R", "CaR", 255, 0, 255);
    Setting g = create("G", "CaG", 255, 0, 255);
    Setting b = create("B", "CaB", 255, 0, 255);
    Setting a = create("A", "CaA", 100, 0, 255);
    Setting a_out = create("Outline A", "CaOutlineA", 255, 0, 255);
    Setting rainbow_mode = create("Rainbow", "CaRainbow", false);
    Setting sat = create("Satiation", "CaSatiation", 0.8, 0, 1);
    Setting brightness = create("Brightness", "CaBrightness", 0.8, 0, 1);
    Setting height = create("Height", "CaHeight", 1.0, 0.0, 1.0);

    Setting render_damage = create("Render Damage", "RenderDamage", true);

    // Setting attempt_chain = create("Chain Mode", "CaChainMode", false);
    Setting chain_length = create("Chain Length", "CaChainLength", 3, 1, 6);

    private final ConcurrentHashMap<EntityEnderCrystal, Integer> attacked_crystals = new ConcurrentHashMap<>();

    private final Timer remove_visual_timer = new Timer();
    private final Timer chain_timer = new Timer();

    private EntityPlayer autoez_target = null;

    private String detail_name = null;
    private int detail_hp = 0;

    private BlockPos render_block_init;
    private BlockPos render_block_old;

    private double render_damage_value;

    private float yaw;
    private float pitch;

    private boolean already_attacking = false;
    private boolean place_timeout_flag = false;
    private boolean is_rotating;
    private boolean did_anything;
    private boolean outline;
    private boolean solid;
    private boolean glow;
    private boolean glowLines;

    private int chain_step = 0;
    private int current_chain_index = 0;
    private int place_timeout;
    private int break_timeout;
    private int break_delay_counter;
    private int place_delay_counter;
    private int prev_slot;

    @EventHandler
    private final Listener<EventEntityRemoved> on_entity_removed = new Listener<>(event -> {
        if (event.get_entity() instanceof EntityEnderCrystal) {
            attacked_crystals.remove(event.get_entity());
        }
    });

    @EventHandler
    private final Listener<EventPacket.SendPacket> send_listener = new Listener<>(event -> {
        if (event.get_packet() instanceof CPacketPlayer && is_rotating && rotate_mode.in("Old")) {
            if (debug.get_value(true)) {
                MessageUtil.send_client_message("Rotating");
            }
            final CPacketPlayer p = (CPacketPlayer) event.get_packet();
            p.yaw = yaw;
            p.pitch = pitch;
            is_rotating = false;
        }
        if (event.get_packet() instanceof CPacketPlayerTryUseItemOnBlock && is_rotating && rotate_mode.in("Old")) {
            if (debug.get_value(true)) {
                MessageUtil.send_client_message("Rotating");
            }
            final CPacketPlayerTryUseItemOnBlock p = (CPacketPlayerTryUseItemOnBlock) event.get_packet();
            p.facingX = render_block_init.getX();
            p.facingY = render_block_init.getY();
            p.facingZ = render_block_init.getZ();
            is_rotating = false;
        }
    });

    @EventHandler
    private final Listener<EventMotionUpdate> on_movement = new Listener<>(event -> {
        if (event.stage == 0 && (rotate_mode.in("Good") || rotate_mode.in("Const"))) {
            if (debug.get_value(true)) {
                MessageUtil.send_client_message("updating rotation");
            }
            PosManager.updatePosition();
            RotationUtil.updateRotations();
            do_ca();
        }
        if (event.stage == 1 && (rotate_mode.in("Good") || rotate_mode.in("Const"))) {
            if (debug.get_value(true)) {
                MessageUtil.send_client_message("resetting rotation");
            }
            PosManager.restorePosition();
            RotationUtil.restoreRotations();
        }
    });

    @EventHandler
    private final Listener<EventPacket.ReceivePacket> receive_listener = new Listener<>(event -> {
        if (event.get_packet() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.get_packet();

            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity e : mc.world.loadedEntityList) {
                    if (e instanceof EntityEnderCrystal) {
                        if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                            e.setDead();
                        }
                    }
                }
            }
        }

    });

    public void do_ca() {
        did_anything = false;

        if (mc.player == null || mc.world == null) return;

        if (rainbow_mode.get_value(true)) {
            cycle_rainbow();
        }

        if (remove_visual_timer.passed(1000)) {
            remove_visual_timer.reset();
            attacked_crystals.clear();
        }

        if (check_pause()) {
            return;
        }

        if (place_crystal.get_value(true) && place_delay_counter > place_timeout) {
            place_crystal();
        }

        if (break_crystal.get_value(true) && break_delay_counter > break_timeout) {
            break_crystal();
        }

        if (!did_anything) {
            if (old_render.get_value(true)) {
                render_block_init = null;
            }
            autoez_target = null;
            is_rotating = false;
        }

        if (autoez_target != null) {
            AutoEz.add_target(autoez_target.getName());
            detail_name = autoez_target.getName();
            detail_hp = Math.round(autoez_target.getHealth() + autoez_target.getAbsorptionAmount());
        }

        if (chain_timer.passed(1000)) {
            chain_timer.reset();
            chain_step = 0;
        }

        render_block_old = render_block_init;

        break_delay_counter++;
        place_delay_counter++;
    }

    @Override
    public void update() {
        if (rotate_mode.in("Off") || rotate_mode.in("Old")) {
            do_ca();
        }

        if (mc.player.isDead || mc.player.getHealth() <= 0) ca_target = null;
    }

    public void cycle_rainbow() {

        float[] tick_color = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32)
        };

        int color_rgb_o = Color.HSBtoRGB(tick_color[0], sat.get_value(1), brightness.get_value(1));

        r.set_value((color_rgb_o >> 16) & 0xFF);
        g.set_value((color_rgb_o >> 8) & 0xFF);
        b.set_value(color_rgb_o & 0xFF);

    }

    public EntityEnderCrystal get_best_crystal() {

        double best_damage = 0;

        double minimum_damage;
        double maximum_damage_self = this.max_self_damage.get_value(1);

        double best_distance = 0;

        EntityEnderCrystal best_crystal = null;

        for (Entity c : mc.world.loadedEntityList) {

            if (!(c instanceof EntityEnderCrystal)) continue;

            EntityEnderCrystal crystal = (EntityEnderCrystal) c;
            if (mc.player.getDistance(crystal) > (!mc.player.canEntityBeSeen(crystal) ? hit_range_wall.get_value(1) : hit_range.get_value(1))) {
                continue;
            }
            if (!mc.player.canEntityBeSeen(crystal) && raytrace.get_value(true)) {
                continue;
            }
            if (crystal.isDead) continue;

            if (attacked_crystals.containsKey(crystal) && attacked_crystals.get(crystal) > 5 && anti_stuck.get_value(true)) continue;

            for (Entity player : mc.world.playerEntities) {

                if (player == mc.player || !(player instanceof EntityPlayer)) continue;

                if (FriendUtil.isFriend(player.getName())) continue;

                if (player.getDistance(mc.player) >= 11) continue; // stops lag

                final EntityPlayer target = (EntityPlayer) player;

                if (target.isDead || target.getHealth() <= 0) continue;

                boolean no_place = faceplace_check.get_value(true) && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD;
                if ((target.getHealth() < faceplace_mode_damage.get_value(1) && faceplace_mode.get_value(true)&& !no_place) || (get_armor_fucker(target) && !no_place)) {
                    minimum_damage = 2;
                } else {
                    minimum_damage = this.min_player_break.get_value(1);
                }

                final double target_damage = CrystalUtil.calculateDamage(crystal, target);

                if (target_damage < minimum_damage) continue;

                final double self_damage = CrystalUtil.calculateDamage(crystal, mc.player);

                if (self_damage > maximum_damage_self || (anti_suicide.get_value(true) && (mc.player.getHealth() + mc.player.getAbsorptionAmount()) - self_damage <= 0.5)) continue;

                if (target_damage > best_damage && !jumpy_mode.get_value(true)) {
                    autoez_target = target;
                    best_damage = target_damage;
                    best_crystal = crystal;
                }

            }

            if (jumpy_mode.get_value(true) && mc.player.getDistanceSq(crystal) > best_distance) {
                best_distance = mc.player.getDistanceSq(crystal);
                best_crystal = crystal;
            }

        }

        return best_crystal;
    }

    public BlockPos get_best_block() {

        if (get_best_crystal() != null && !fast_mode.get_value(true)) {
            place_timeout_flag = true;
            return null;
        }

        if (place_timeout_flag) {
            place_timeout_flag = false;
            return null;
        }

        List<Pair<Double, BlockPos>> damage_blocks = new ArrayList<>();
        double best_damage = 0;
        double minimum_damage;
        double maximum_damage_self = this.max_self_damage.get_value(1);

        BlockPos best_block = null;

        List<BlockPos> blocks = CrystalUtil.possiblePlacePositions((float) place_range.get_value(1), endcrystal.get_value(true), true);

        for (Entity player : mc.world.playerEntities) {

            if (FriendUtil.isFriend(player.getName()) || player.getName().equals(mc.player.getName())) continue;

            for (BlockPos block : blocks) {

                if (player == mc.player || !(player instanceof EntityPlayer)) continue;

                if (player.getDistance(mc.player) >= 11) continue;

                if (!BlockUtil.rayTracePlaceCheck(block, this.raytrace.get_value(true))) {
                    continue;
                }

                if (!BlockUtil.canSeeBlock(block) && mc.player.getDistance(block.getX(), block.getY(), block.getZ()) > hit_range_wall.get_value(1)) {
                    continue;
                }

                final EntityPlayer target = (EntityPlayer) player;

                ca_target = target;

                if (target.isDead || target.getHealth() <= 0) continue;

                boolean no_place = faceplace_check.get_value(true) && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD;
                if ((target.getHealth() < faceplace_mode_damage.get_value(1) && faceplace_mode.get_value(true)&& !no_place) || (get_armor_fucker(target) && !no_place)) {
                    minimum_damage = 2;
                } else {
                    minimum_damage = this.min_player_place.get_value(1);
                }

                final double target_damage = CrystalUtil.calculateDamage((double) block.getX() + 0.5, (double) block.getY() + 1, (double) block.getZ() + 0.5, target);

                if (target_damage < minimum_damage) continue;

                final double self_damage = CrystalUtil.calculateDamage((double) block.getX() + 0.5, (double) block.getY() + 1, (double) block.getZ() + 0.5, mc.player);

                if (self_damage > maximum_damage_self || (anti_suicide.get_value(true) && (mc.player.getHealth() + mc.player.getAbsorptionAmount()) - self_damage <= 0.5)) continue;

                if (target_damage > best_damage) {
                    best_damage = target_damage;
                    best_block = block;
                    autoez_target = target;
                }

            }

            if (best_block == null) ca_target = null;

        }

        blocks.clear();

        if (chain_step == 1) {
            current_chain_index = chain_length.get_value(1);
        } else if (chain_step > 1) {
            current_chain_index--;
        } 

        render_damage_value = best_damage;
        render_block_init = best_block;

        damage_blocks = sort_best_blocks(damage_blocks);

        if (!attempt_chain.get_value(true)) {
            return best_block;
        } else {
            if (damage_blocks.size() == 0) {
                return null;
            }
            if (damage_blocks.size() < current_chain_index) {
                return damage_blocks.get(0).getValue();
            }
            return damage_blocks.get(current_chain_index).getValue();
        }

    }

    public List<Pair<Double, BlockPos>> sort_best_blocks(List<Pair<Double, BlockPos>> list) {
        List<Pair<Double, BlockPos>> new_list = new ArrayList<>();
        double damage_cap = 1000;
        for (int i = 0; i < list.size(); i++) {
            double biggest_dam = 0;
            Pair<Double, BlockPos> best_pair = null;
            for (Pair<Double, BlockPos> pair : list) {
                if (pair.getKey() > biggest_dam && pair.getKey() < damage_cap) {
                    best_pair = pair;
                }
            }
            if (best_pair == null) continue;
            damage_cap = best_pair.getKey();
            new_list.add(best_pair);
        }
        return new_list;
    }

    public void place_crystal() {

        BlockPos target_block = get_best_block();

        if (target_block == null) {
            return;
        }

        place_delay_counter = 0;

        already_attacking = false;

        boolean offhand_check = false;
        prev_slot = mc.player.inventory.currentItem;
        int crystal_slot = find_crystals_hotbar();
        if (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
            if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
                if (crystal_slot == -1) return;
                if (switch_mode.in("Swap")) {
                    mc.player.inventory.currentItem = crystal_slot;
                    return;
                } else if (switch_mode.in("Silent")) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(crystal_slot));
                }
            }
        } else {
            offhand_check = true;
        }

        if (debug.get_value(true)) {
            MessageUtil.send_client_message("placing");
        }

        chain_step++;
        did_anything = true;
        rotate_to_pos(target_block);
        chain_timer.reset();
        BlockUtil.placeCrystalOnBlock(target_block, offhand_check ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        if (switch_mode.in("Silent")) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(prev_slot));
        }
    }

    public boolean get_armor_fucker(EntityPlayer p) {

        for (ItemStack stack : p.getArmorInventoryList()) {

            if (stack == null || stack.getItem() == Items.AIR) return true;

            final float armor_percent = ((float) (stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage()) * 100.0f;

            if (fuck_armor_mode.get_value(true) && fuck_armor_mode_precent.get_value(1) >= armor_percent) return true;

        }

        return false;

    }

    public void break_crystal() {

        EntityEnderCrystal crystal = get_best_crystal();
        if (crystal == null) {
            return;
        }

        if (anti_weakness.get_value(true) && mc.player.isPotionActive(MobEffects.WEAKNESS)) {

            boolean should_weakness = true;

            if (mc.player.isPotionActive(MobEffects.STRENGTH)) {

                if (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() == 2) {
                    should_weakness = false;
                }

            }

            if (should_weakness) {

                if (!already_attacking) {
                    already_attacking = true;
                }

                int new_slot = -1;

                for (int i = 0; i < 9; i++) {

                    ItemStack stack = mc.player.inventory.getStackInSlot(i);

                    if (stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool) {
                        new_slot = i;
                        mc.playerController.updateController();
                        break;
                    }
                }

                if (new_slot != -1) {
                    mc.player.inventory.currentItem = new_slot;
                }
            }
        }

        if (debug.get_value(true)) {
            MessageUtil.send_client_message("attacking");
        }

        did_anything = true;

        rotate_to(crystal);
        for (int i = 0; i < break_trys.get_value(1); i++) {
            EntityUtil.attackEntity(crystal, false, swing);
        }
        add_attacked_crystal(crystal);

        if (client_side.get_value(true) && crystal.isEntityAlive()) {
            crystal.setDead();
        }

        break_delay_counter = 0;
    }

    public boolean check_pause() {

        if (find_crystals_hotbar() == -1 && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
            return true;
        }

        if (stop_while_mining.get_value(true) && mc.gameSettings.keyBindAttack.isKeyDown() && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
            if (old_render.get_value(true)) {
                render_block_init = null;
            }
            return true;
        }

        if (PineapleClient.get_hack_manager().get_module_with_tag("Surround").is_active()) {
            if (old_render.get_value(true)) {
                render_block_init = null;
            }
            return true;
        }

        if (PineapleClient.get_hack_manager().get_module_with_tag("HoleFill").is_active()) {
            if (old_render.get_value(true)) {
                render_block_init = null;
            }
            return true;
        }

        if (PineapleClient.get_hack_manager().get_module_with_tag("Trap").is_active()) {
            if (old_render.get_value(true)) {
                render_block_init = null;
            }
            return true;
        }

        return false;
    }

    private int find_crystals_hotbar() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
                return i;
            }
        }
        return -1;
    }

    private void add_attacked_crystal(EntityEnderCrystal crystal) {

        if (attacked_crystals.containsKey(crystal)) {
            int value = attacked_crystals.get(crystal);
            attacked_crystals.put(crystal, value + 1);
        } else {
            attacked_crystals.put(crystal, 1);
        }
    }

    public void rotate_to_pos(final BlockPos pos) {
        final float[] angle;
        if (rotate_mode.in("Const")) {
            angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
        } else {
            angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f));
        }
        if (rotate_mode.in("Off")) {
            is_rotating = false;
        }
        if (rotate_mode.in("Good") || rotate_mode.in("Const")) {
            RotationUtil.setPlayerRotations(angle[0], angle[1]);
        }
        if (rotate_mode.in("Old")) {
            yaw = angle[0];
            pitch = angle[1];
            is_rotating = true;
        }
    }

    public void rotate_to(final Entity entity) {
        final float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
        if (rotate_mode.in("Off")) {
            is_rotating = false;
        }
        if (rotate_mode.in("Good")) {
            RotationUtil.setPlayerRotations(angle[0], angle[1]);
        }
        if (rotate_mode.in("Old") || rotate_mode.in("Cont")) {
            yaw = angle[0];
            pitch = angle[1];
            is_rotating = true;
        }
    }

    @Override
    public void render(EventRender event) {

        if (render_block_init == null) return;

        if (render_mode.in("None")) return;

        if (render_mode.in("Pretty")) {
            outline = true;
            solid = true;
            glow = false;
            glowLines = false;
        }

        if (render_mode.in("Solid")) {
            outline = false;
            solid = true;
            glow = false;
            glowLines = false;
        }

        if (render_mode.in("Outline")) {
            outline = true;
            solid = false;
            glow = false;
            glowLines = false;
        } 

        if (render_mode.in("Glow")) {
            outline = false;
            solid = false;
            glow = true;
            glowLines = false;
        }

        if (render_mode.in("Glow 2")) {
            outline = false;
            solid = false;
            glow = true;
            glowLines = true;
        }

        render_block(render_block_init);

        if (future_render.get_value(true) && render_block_old != null) {
            render_block(render_block_old);
        }

        if (render_damage.get_value(true)) {
            RenderUtil.drawText(render_block_init, ((Math.floor(this.render_damage_value) == this.render_damage_value) ? Integer.valueOf((int)this.render_damage_value) : String.format("%.1f", this.render_damage_value)) + "");
        }

    }

    public void render_block(BlockPos pos) {
        BlockPos render_block = (top_block.get_value(true) ? pos.up() : pos);

        float h = (float) height.get_value(1.0);

        if (solid) {
            RenderHelp.prepare("quads");
            RenderHelp.draw_cube(RenderHelp.get_buffer_build(),
                    render_block.getX(), render_block.getY(), render_block.getZ(),
                    1, h, 1,
                    r.get_value(1), g.get_value(1), b.get_value(1), a.get_value(1),
                    "all"
            );
            RenderHelp.release();
        }

        if (outline) {
            RenderHelp.prepare("lines");
            RenderHelp.draw_cube_line(RenderHelp.get_buffer_build(),
                    render_block.getX(), render_block.getY(), render_block.getZ(),
                    1, h, 1,
                    r.get_value(1), g.get_value(1), b.get_value(1), a_out.get_value(1),
                    "all"
            );
            RenderHelp.release();
        }

        if (glow) {
            RenderHelp.prepare("lines");
            RenderHelp.draw_cube_line(RenderHelp.get_buffer_build(),
                    render_block.getX(), render_block.getY(), render_block.getZ(),
                    1, 0, 1,
                    r.get_value(1), g.get_value(1), b.get_value(1), a_out.get_value(1),
                    "all"
            );
            RenderHelp.release();
            RenderHelp.prepare("quads");
            RenderHelp.draw_gradiant_cube(RenderHelp.get_buffer_build(), 
                    render_block.getX(), render_block.getY(), render_block.getZ(), 
                    1, h, 1,  new Color(r.get_value(1), g.get_value(1), b.get_value(1), a.get_value(1)),
                    new Color(0, 0, 0, 0), 
                    "all"
            );
            RenderHelp.release();
        }

        if (glowLines) {
            RenderHelp.prepare("lines");
            RenderHelp.draw_gradiant_outline(RenderHelp.get_buffer_build(), 
                    render_block.getX(), render_block.getY(), render_block.getZ(), 
                    h, new Color(r.get_value(1), g.get_value(1), b.get_value(1), a_out.get_value(1)), 
                    new Color(0, 0, 0, 0),
                    "all"
            );
            RenderHelp.release();
        }
    }

    @Override
    public void enable() {
        place_timeout = this.place_delay.get_value(1);
        break_timeout = this.break_delay.get_value(1);
        place_timeout_flag = false;
        is_rotating = false;
        autoez_target = null;
        chain_step = 0;
        current_chain_index = 0;
        chain_timer.reset();
        remove_visual_timer.reset();
        detail_name = null;
        detail_hp = 20;
    }

    @Override
    public void disable() {
        render_block_init = null;
        autoez_target = null;
    }

    @Override
    public String array_detail() {
        return (detail_name != null) ? detail_name + " | " + detail_hp : "None";
    }


    public static EntityPlayer get_target() {
        return ca_target;
    }
}
