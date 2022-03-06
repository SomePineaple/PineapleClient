package me.somepineaple.pineapleclient.main.util;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PlayerUtil {
    
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static BlockPos GetLocalPlayerPosFloored() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }
    
    public enum FacingDirection {
        North,
        South,
        East,
        West,
    }

    public static FacingDirection GetFacing() {
        switch (MathHelper.floor((double) (mc.player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 7)
        {
            case 0:
            case 1:
                return FacingDirection.South;
            case 2:
            case 3:
                return FacingDirection.West;
            case 4:
            case 5:
                return FacingDirection.North;
            case 6:
            case 7:
                return FacingDirection.East;
        }
        return FacingDirection.North;
    }

    public static void PacketFacePitchAndYaw(float p_Pitch, float p_Yaw)
    {
        boolean l_IsSprinting = mc.player.isSprinting();

        if (l_IsSprinting != mc.player.serverSprintState)
        {
            if (l_IsSprinting)
            {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            }
            else
            {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }

            mc.player.serverSprintState = l_IsSprinting;
        }

        boolean l_IsSneaking = mc.player.isSneaking();

        if (l_IsSneaking != mc.player.serverSneakState)
        {
            if (l_IsSneaking)
            {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
            else
            {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }

            mc.player.serverSneakState = l_IsSneaking;
        }

        AxisAlignedBB axisalignedbb = mc.player.getEntityBoundingBox();
        double l_PosXDifference = mc.player.posX - mc.player.lastReportedPosX;
        double l_PosYDifference = axisalignedbb.minY - mc.player.lastReportedPosY;
        double l_PosZDifference = mc.player.posZ - mc.player.lastReportedPosZ;
        double l_YawDifference = (double)(p_Yaw - mc.player.lastReportedYaw);
        double l_RotationDifference = (double)(p_Pitch - mc.player.lastReportedPitch);
        ++mc.player.positionUpdateTicks;
        boolean l_MovedXYZ = l_PosXDifference * l_PosXDifference + l_PosYDifference * l_PosYDifference + l_PosZDifference * l_PosZDifference > 9.0E-4D || mc.player.positionUpdateTicks >= 20;
        boolean l_MovedRotation = l_YawDifference != 0.0D || l_RotationDifference != 0.0D;

        if (mc.player.isRiding())
        {
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.motionX, -999.0D, mc.player.motionZ, p_Yaw, p_Pitch, mc.player.onGround));
            l_MovedXYZ = false;
        }
        else if (l_MovedXYZ && l_MovedRotation)
        {
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, axisalignedbb.minY, mc.player.posZ, p_Yaw, p_Pitch, mc.player.onGround));
        }
        else if (l_MovedXYZ)
        {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, axisalignedbb.minY, mc.player.posZ, mc.player.onGround));
        }
        else if (l_MovedRotation)
        {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(p_Yaw, p_Pitch, mc.player.onGround));
        }
        else if (mc.player.prevOnGround != mc.player.onGround)
        {
            mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));
        }

        if (l_MovedXYZ)
        {
            mc.player.lastReportedPosX = mc.player.posX;
            mc.player.lastReportedPosY = axisalignedbb.minY;
            mc.player.lastReportedPosZ = mc.player.posZ;
            mc.player.positionUpdateTicks = 0;
        }

        if (l_MovedRotation)
        {
            mc.player.lastReportedYaw = p_Yaw;
            mc.player.lastReportedPitch = p_Pitch;
        }

        mc.player.prevOnGround = mc.player.onGround;
        mc.player.autoJumpEnabled = mc.player.mc.gameSettings.autoJump;
    }

    public static boolean isEating() {
        if (mc.player.isHandActive()) {
            return mc.player.getActiveItemStack().getItemUseAction().equals(EnumAction.EAT) || mc.player.getActiveItemStack().getItemUseAction().equals(EnumAction.DRINK);
        }
        return false;
    }

    public static boolean isMending() {
        return mc.player.isHandActive() && mc.player.getActiveItemStack().getItem().equals(Items.EXPERIENCE_BOTTLE) && mc.gameSettings.keyBindUseItem.isKeyDown();
    }

    public static boolean isMining() {
        return isHolding(Items.DIAMOND_PICKAXE) && mc.playerController.getIsHittingBlock();
    }

    public static boolean isHolding(Item item) {
        return mc.player.getHeldItemMainhand().getItem().equals(item) || mc.player.getHeldItemOffhand().getItem().equals(item);
    }

    public static float getHealth() {
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    public static int getSlotForItem(Item item) {

        return -1;
    }
}
