package ru.allformine.afmuf.hooks;

import com.mrcrayfish.furniture.network.message.MessageDoorMat;
import com.mrcrayfish.furniture.tileentity.TileEntityDoorMat;
import gloomyfolken.hooklib.asm.Hook;
import gloomyfolken.hooklib.asm.ReturnCondition;
import ic2.core.ExplosionIC2;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.server.machine.Machine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import pcl.opensecurity.common.tileentity.TileEntityEntityDetector;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.RadarUtils;
import ru.allformine.afmcp.vanish.VanishManager;
import ru.allformine.afmuf.alert.AlertContext;
import ru.allformine.afmuf.alert.AlertMod;
import ru.allformine.afmuf.net.discord.Webhook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public static HashMap<String, Object> info(Entity entity, BlockPos offset, BlockPos a, ComponentConnector node) {
        HashMap<String, Object> value = new HashMap<String, Object>();

        double rangeToEntity = entity.getDistance(a.getX(), a.getY(), a.getZ());
        String name;
        if (entity instanceof EntityPlayer)
            name = ((EntityPlayer) entity).getDisplayNameString();
        else
            name = entity.getName();

        BlockPos entityLocalPosition = entity.getPosition().subtract(offset);

        value.put("name", name);
        value.put("range", rangeToEntity);
        value.put("height", entity.height);
        value.put("x", entityLocalPosition.getX());
        value.put("y", entityLocalPosition.getY());
        value.put("z", entityLocalPosition.getZ());
        node.sendToReachable("computer.signal", "entityDetect", name, rangeToEntity, entityLocalPosition.getX(), entityLocalPosition.getY(), entityLocalPosition.getZ());

        return value;
    }

    // ================Vanish: OpenSecurity======================== //

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static Map<Integer, HashMap<String, Object>> scan(TileEntityEntityDetector anus, boolean players, BlockPos offset) {
        Map<Integer, HashMap<String, Object>> output = new HashMap<>();
        int index = 1;

        int range;

        try {
            Field field = anus.getClass().getDeclaredField("range");
            field.setAccessible(true);

            range = (int) field.get(anus);
        } catch (Exception e) {
            e.printStackTrace();

            return output;
        }

        for (Entity entity : anus.getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(anus.getPos(), anus.getPos()).grow(range))) {
            if (players && entity instanceof EntityPlayer) {
                if (VanishManager.tabList.tabList.contains(((EntityPlayer) entity).getDisplayNameString()))
                    output.put(index++, info(entity, offset, anus.getPos(), anus.node));
            } else if (!players && !(entity instanceof EntityPlayer)) {
                output.put(index++, info(entity, offset, anus.getPos(), anus.node));
            }
        }

        return output;
    }

    // ================Vanish: Computronics======================== //

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static Set<Map<String, Object>> getEntities(RadarUtils anus, World world, double xCoord, double yCoord, double zCoord, AxisAlignedBB bounds, Class<? extends EntityLivingBase> eClass) {
        Set<Map<String, Object>> entities = new HashSet<Map<String, Object>>();
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(eClass, bounds)) {
            if (eClass == EntityPlayer.class && !VanishManager.tabList.tabList.contains(((EntityPlayer) entity).getDisplayNameString())) {
                continue;
            }

            double dx = entity.posX - xCoord;
            double dy = entity.posY - yCoord;
            double dz = entity.posZ - zCoord;
            if (Math.sqrt(dx * dx + dy * dy + dz * dz) < Config.RADAR_RANGE) {
                Map<String, Object> entry = new HashMap<String, Object>();
                entry.put("name", entity.getName());
                if (!Config.RADAR_ONLY_DISTANCE) {
                    entry.put("x", (int) dx);
                    entry.put("y", (int) dy);
                    entry.put("z", (int) dz);
                }
                entry.put("distance", Math.sqrt(dx * dx + dy * dy + dz * dz));
                entities.add(entry);
            }
        }
        return entities;
    }

    // ================Vanish: OpenComputers======================= //

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static void addUser(Machine anus, String name) throws Exception {
        if (anus.li$cil$oc$server$machine$Machine$$_users().size() >= li.cil.oc.Settings.get().maxUsers()) {
            throw new Exception("too many users");
        } else if (anus.li$cil$oc$server$machine$Machine$$_users().contains(name)) {
            throw new Exception("user exists");
        } else if (name.length() > li.cil.oc.Settings.get().maxUsernameLength()) {
            throw new Exception("username too long");
        } else if (VanishManager.tabList.tabList.contains(name)) {
            synchronized (anus.li$cil$oc$server$machine$Machine$$_users()) {
                anus.li$cil$oc$server$machine$Machine$$_users().$plus$eq(name);

                try {
                    Method method = anus.getClass().getDeclaredMethod("usersChanged_$eq", boolean.class);
                    method.setAccessible(true);
                    method.invoke(anus, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            throw new Exception("player must be online (beu!)");
        }
    }

    // ============================================================ //

    @Hook(returnCondition = ReturnCondition.ON_TRUE)
    public static boolean doExplosion(ExplosionIC2 anus) {
        World world;

        try {
            Field field = anus.getClass().getDeclaredField("worldObj");
            field.setAccessible(true);

            world = (World) field.get(World.class);
        } catch (Exception e) {
            return false;
        }


        final ExplosionEvent event = new ExplosionEvent(world, anus);
        boolean test = MinecraftForge.EVENT_BUS.post(event);

        System.out.println(test);

        return test;
    }

    // ============================================================ //

    public static int getPlayerCount(MinecraftServer anus) {
        return VanishManager.getPlayerCountExcludingVanished();
    }
}
