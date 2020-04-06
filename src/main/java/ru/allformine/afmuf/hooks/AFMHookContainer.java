package ru.allformine.afmuf.hooks;

import com.mrcrayfish.furniture.network.message.MessageDoorMat;
import com.mrcrayfish.furniture.tileentity.TileEntityDoorMat;
import gloomyfolken.hooklib.asm.Hook;
import gloomyfolken.hooklib.asm.ReturnCondition;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.allformine.afmuf.alert.AlertContext;
import ru.allformine.afmuf.alert.AlertMod;
import ru.allformine.afmuf.net.discord.Webhook;
import pl.asie.computronics.oc.driver.RobotUpgradeChatBox;
import pl.asie.computronics.tile.TileChatBox;
import pl.asie.computronics.oc.driver.DriverCardFX;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;

public class AFMHookContainer {
    @Hook(returnCondition = ReturnCondition.ON_TRUE, returnNull = true)
    public static boolean onMessage(MessageDoorMat anus, MessageDoorMat message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        World world = player.world;
        BlockPos pos = new BlockPos(message.x, message.y, message.z);
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof TileEntityDoorMat) {
            TileEntityDoorMat doorMat = (TileEntityDoorMat) tileEntity;

            if (doorMat.getMessage() != null) {
                Webhook.sendSecureAlert(new AlertContext(ctx.getServerHandler().player.getName(),
                        "MessageDoorMat",
                        null,
                        tileEntity,
                        AlertMod.FURNITURE));

                return true;
            }

            // player.gui
            //TODO сделать проверку на текущий GuiScreen, проверка выше не работает для поставвленных ковриков
        }

        return false;
    }

    @Hook(returnCondition = ReturnCondition.ON_TRUE, returnNull = true)
    public static boolean spawn(DriverCardFX anus, Context context, Arguments args){
        double velX = args.checkDouble(4);
        double velY = args.checkDouble(5);
        double velZ = args.checkDouble(6);
        return velX < 65536 && velX > -65536 && velY < 65536 && velY > -65536 && velZ < 65536 && velZ > -65536;
    }

    public static boolean say(Arguments args){
        return args.checkString(0).length() < 128;
    }

    @Hook(returnCondition = ReturnCondition.ON_TRUE, returnNull = true)
    public static boolean say(TileChatBox anus, Arguments args){
        return say(args);
    }

    @Hook(returnCondition = ReturnCondition.ON_TRUE, returnNull = true)
    public static boolean say(RobotUpgradeChatBox anus, Arguments args){
        return say(args);
    }

}
