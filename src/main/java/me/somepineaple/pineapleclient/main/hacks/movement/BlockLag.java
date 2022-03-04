package me.somepineaple.pineapleclient.main.hacks.movement;

import me.somepineaple.pineapleclient.main.event.EventCancellable;
import me.somepineaple.pineapleclient.main.event.events.EventMotionUpdate;
import me.somepineaple.pineapleclient.main.event.events.EventPacket;
import me.somepineaple.pineapleclient.main.guiscreen.settings.Setting;
import me.somepineaple.pineapleclient.main.hacks.Category;
import me.somepineaple.pineapleclient.main.hacks.Hack;
import me.somepineaple.pineapleclient.main.util.BlockInteractHelper;
import me.somepineaple.pineapleclient.main.util.BlockUtil;
import me.somepineaple.pineapleclient.main.util.MSTimer;
import me.somepineaple.pineapleclient.main.util.PlayerUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;

public class BlockLag extends Hack {
    public BlockLag() {
        super(Category.MOVEMENT);
        name = "Burrow";
        tag = "BlockLag";
        description = "be annoying";
    }

    Setting Delay = create("Delay", "BlockLagDelay", 0f, 0f, 1.0f);
    Setting rotate = create("Rotate", "BlockLagRotate", true);
    Setting arm = create("Swing:", "BlockLagArm", "Mainhand", combobox("Offhand", "Mainhand"));

    private final MSTimer _timer = new MSTimer();
    private final MSTimer _towerPauseTimer = new MSTimer();
    private final MSTimer _towerTimer = new MSTimer();

    @EventHandler
    private final Listener<EventMotionUpdate> onMotionUpdate = new Listener<>(event -> {
        if (event.isCancelled())
            return;

        if (event.get_era() != EventCancellable.Era.EVENT_PRE)
            return;

        if (!_timer.passed((long) (Delay.getValue(1.0) * 1000)))
            return;

        // verify we have a block in our hand
        ItemStack stack = mc.player.getHeldItemMainhand();

        int prevSlot = -1;

        if (!verifyStack(stack))
        {
            for (int i = 0; i < 9; ++i)
            {
                stack = mc.player.inventory.getStackInSlot(i);

                if (verifyStack(stack))
                {
                    prevSlot = mc.player.inventory.currentItem;
                    mc.player.inventory.currentItem = i;
                    mc.playerController.updateController();
                    break;
                }
            }
        }

        if (!verifyStack(stack))
            return;

        _timer.reset();

        BlockPos toPlaceAt = null;

        BlockPos feetBlock = PlayerUtil.GetLocalPlayerPosFloored().down();

        boolean placeAtFeet = isValidPlaceBlockState(feetBlock);

        // verify we are on tower mode, feet block is valid to be placed at, and
        if (placeAtFeet && _towerTimer.passed(250) && !mc.player.isElytraFlying())
        {
            // todo: this can be moved to only do it on an SPacketPlayerPosLook?
            if (_towerPauseTimer.passed(1500))
            {
                _towerPauseTimer.reset();
                mc.player.motionY = -0.28f;
            }
            else
            {
                final float towerMotion = 0.41999998688f;

                mc.player.setVelocity(0, towerMotion, 0);
            }
        }

        if (placeAtFeet)
            toPlaceAt = feetBlock;
        else // find a supporting position for feet block
        {
            BlockInteractHelper.ValidResult result = BlockInteractHelper.valid(feetBlock);

            // find a supporting block
            if (result != BlockInteractHelper.ValidResult.Ok && result != BlockInteractHelper.ValidResult.AlreadyBlockThere)
            {
                BlockPos[] array = { feetBlock.north(), feetBlock.south(), feetBlock.east(), feetBlock.west() };

                BlockPos toSelect = null;
                double lastDistance = 420.0;

                for (BlockPos pos : array)
                {
                    if (!isValidPlaceBlockState(pos))
                        continue;

                    double dist = pos.getDistance((int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ);
                    if (lastDistance > dist)
                    {
                        lastDistance = dist;
                        toSelect = pos;
                    }
                }

                // if we found a position, that's our selection
                if (toSelect != null)
                    toPlaceAt = toSelect;
            }
        }

        if (toPlaceAt != null)
        {
            BlockUtil.placeBlock(toPlaceAt, mc.player.inventory.currentItem, rotate.getValue(true), rotate.getValue(true), false, arm);
        }
        else
            _towerPauseTimer.reset();

        // set back our previous slot
        if (prevSlot != -1)
        {
            mc.player.inventory.currentItem = prevSlot;
            mc.playerController.updateController();
        }
    });

    @EventHandler
    private final Listener<EventPacket.ReceivePacket> PacketEvent = new Listener<>(event ->
    {
        if (event.get_packet() instanceof SPacketPlayerPosLook)
        {
            this.set_active(false);
        }
    });

    private boolean isValidPlaceBlockState(BlockPos pos)
    {
        BlockInteractHelper.ValidResult result = BlockInteractHelper.valid(pos);

        if (result == BlockInteractHelper.ValidResult.AlreadyBlockThere)
            return mc.world.getBlockState(pos).getMaterial().isReplaceable();

        return result == BlockInteractHelper.ValidResult.Ok;
    }

    private boolean verifyStack(ItemStack stack)
    {
        return !stack.isEmpty() && stack.getItem() instanceof ItemBlock;
    }
}
