package ru.allformine.afmuf.hooks;

import com.mrcrayfish.furniture.network.message.MessageDoorMat;
import com.mrcrayfish.furniture.tileentity.TileEntityDoorMat;
import gloomyfolken.hooklib.asm.Hook;
import gloomyfolken.hooklib.asm.ReturnCondition;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.allformine.afmcp.vanish.VanishManager;
import ru.allformine.afmuf.alert.AlertContext;
import ru.allformine.afmuf.alert.AlertMod;
import ru.allformine.afmuf.net.discord.Webhook;

public class AFMHookContainer {
    @Hook(returnCondition = ReturnCondition.ON_TRUE, returnNull = true)
    public static boolean onMessage(MessageDoorMat anus, MessageDoorMat message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        World world = player.world;
        BlockPos pos = new BlockPos(message.x, message.y, message.z);
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof TileEntityDoorMat) {
            TileEntityDoorMat doorMat = (TileEntityDoorMat) tileEntity;

            PlayerInteractEvent event = new PlayerInteractEvent.RightClickBlock(ctx.getServerHandler().player, EnumHand.MAIN_HAND, tileEntity.getPos(), EnumFacing.UP, Vec3d.ZERO);
            MinecraftForge.EVENT_BUS.post(event);

            System.out.println(event.isCanceled());
            if (doorMat.getMessage() != null || event.isCanceled()) {
                Webhook.sendSecureAlert(new AlertContext(ctx.getServerHandler().player.getName(),
                        "MessageDoorMat",
                        null,
                        tileEntity,
                        AlertMod.FURNITURE,
                        event.isCanceled() ? "Event cancelled" : "Attempt to change written doormat."));

                return true;
            }
        }

        return false;
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS, returnAnotherMethod = "getPlayerCount")
    public static boolean getCurrentPlayerCount(MinecraftServer anus) {
        return true;
    }

    public static int getPlayerCount(MinecraftServer anus) {
        return VanishManager.getPlayerCountExcludingVanished();
    }
}
