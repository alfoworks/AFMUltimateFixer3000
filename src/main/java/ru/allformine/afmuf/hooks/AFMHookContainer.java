package ru.allformine.afmuf.hooks;

import com.mrcrayfish.furniture.network.message.MessageDoorMat;
import com.mrcrayfish.furniture.tileentity.TileEntityDoorMat;
import gloomyfolken.hooklib.asm.Hook;
import gloomyfolken.hooklib.asm.ReturnCondition;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.allformine.afmuf.alert.AlertContext;
import ru.allformine.afmuf.net.discord.Webhook;

public class AFMHookContainer {
    @Hook(returnCondition = ReturnCondition.ON_TRUE, returnNull = true)
    public static boolean onMessage(MessageDoorMat anus, MessageDoorMat message, MessageContext ctx) {
        World world = ctx.getServerHandler().player.world;
        BlockPos pos = new BlockPos(message.x, message.y, message.z);
        TileEntity tileEntity = world.getTileEntity(pos);
        Webhook.sendSecureAlert(new AlertContext("123", "123", null, null, null));
        if (tileEntity instanceof TileEntityDoorMat) {
            TileEntityDoorMat doorMat = (TileEntityDoorMat) tileEntity;

            return doorMat.getMessage() != null;

            //TODO сделать проверку на текущий GuiScreen, проверка выше не работает для поставвленных ковриков
        }

        return false;
    }
}
