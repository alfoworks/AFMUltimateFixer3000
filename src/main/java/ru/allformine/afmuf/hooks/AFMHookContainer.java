package ru.allformine.afmuf.hooks;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrcrayfish.furniture.network.message.MessageDoorMat;
import com.mrcrayfish.furniture.tileentity.TileEntityDoorMat;
import gloomyfolken.hooklib.asm.Hook;
import gloomyfolken.hooklib.asm.ReturnCondition;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.item.tool.ItemToolMiningLaser;
import ic2.core.util.StackUtil;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.server.machine.Machine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.SPacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import pcl.opensecurity.common.tileentity.TileEntityEntityDetector;
import pl.asie.computronics.oc.driver.DriverCardFX;
import pl.asie.computronics.oc.driver.RobotUpgradeChatBox;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.tile.TileChatBox;
import pl.asie.computronics.util.RadarUtils;
import ru.allformine.afmuf.Utils;
import ru.allformine.afmuf.alert.AlertContext;
import ru.allformine.afmuf.alert.AlertMod;
import ru.allformine.afmuf.ic2.AFMExplosionIC2;
import ru.allformine.afmuf.net.discord.Webhook;
import ru.allformine.afmvanish.vanish.VanishManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

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

    public static int getPlayerCount(MinecraftServer anus) {
        return VanishManager.getPlayerCountExcludingVanished();
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static void doExplosion(ExplosionIC2 anus) {
        // Вытаскиваем все необходимые приватные поля из класса. По другому никак.
        World world;
        Entity entity;

        double x;
        double y;
        double z;

        float power;
        float drop;
        ExplosionIC2.Type type;
        EntityLivingBase igniter;
        int radiationRange;

        try {
            world = Utils.getPrivateValue(ExplosionIC2.class, anus, "worldObj");
            entity = Utils.getPrivateValue(ExplosionIC2.class, anus, "exploder");

            x = Utils.getPrivateValue(ExplosionIC2.class, anus, "explosionX");
            y = Utils.getPrivateValue(ExplosionIC2.class, anus, "explosionY");
            z = Utils.getPrivateValue(ExplosionIC2.class, anus, "explosionZ");

            power = Utils.getPrivateValue(ExplosionIC2.class, anus, "power");
            drop = Utils.getPrivateValue(ExplosionIC2.class, anus, "explosionDropRate");
            type = Utils.getPrivateValue(ExplosionIC2.class, anus, "type");
            igniter = Utils.getPrivateValue(ExplosionIC2.class, anus, "igniter");
            radiationRange = Utils.getPrivateValue(ExplosionIC2.class, anus, "radiationRange");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Инициализируем кастомный класс
        AFMExplosionIC2 afmExplosionIC2 = new AFMExplosionIC2(world, entity, x, y, z, power, drop, AFMExplosionIC2.getAFMExplosionType(type), igniter, radiationRange);
        afmExplosionIC2.doExplosion();
    }

    @Hook(returnCondition = ReturnCondition.ON_TRUE, returnAnotherMethod = "zalupa")
    public static boolean onItemRightClick(ItemToolMiningLaser anus, final World world, final EntityPlayer player, final EnumHand hand) {
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            return false;
        }

        final ItemStack stack = StackUtil.get(player, hand);

        final NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        int laserSetting = nbtData.getInteger("laserSetting");

        if (laserSetting == 5 || laserSetting == 6) {
            player.sendMessage(new TextComponentString("Этот режим был отключен. С любовью, ALFO:MINE."));

            return true;
        }

        return false;
    }

    public static ActionResult<ItemStack> zalupa(ItemToolMiningLaser anus, final World world, final EntityPlayer player, final EnumHand hand) {
        return new ActionResult<>(EnumActionResult.PASS, StackUtil.get(player, hand));
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
    public static boolean say(TileChatBox anus, Arguments args) {
        return say(args);
    }

    @Hook(returnCondition = ReturnCondition.ON_TRUE, returnNull = true)
    public static boolean say(RobotUpgradeChatBox anus, Arguments args) {
        return say(args);
    }

    // Forge: server is still starting message //

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static boolean handleServerHandshake(FMLCommonHandler anus, C00Handshake packet, NetworkManager manager) {
        if (!anus.shouldAllowPlayerLogins()) {
            TextComponentString text = new TextComponentString(ChatFormatting.LIGHT_PURPLE + "Сервер всё ещё запускается! Пожалуйста, попробуйте позже.");
            FMLLog.log.info("Disconnecting Player: {}", text.getUnformattedText());
            manager.sendPacket(new SPacketDisconnect(text));
            manager.closeChannel(text);
            return false;
        }

        if (packet.getRequestedState() == EnumConnectionState.LOGIN && (!NetworkRegistry.INSTANCE.isVanillaAccepted(Side.CLIENT) && !packet.hasFMLMarker())) {
            manager.setConnectionState(EnumConnectionState.LOGIN);
            TextComponentString text = new TextComponentString("This server has mods that require FML/Forge to be installed on the client. Contact your server admin for more details.");
            Collection<String> modNames = NetworkRegistry.INSTANCE.getRequiredMods(Side.CLIENT);
            FMLLog.log.info("Disconnecting Player: This server has mods that require FML/Forge to be installed on the client: {}", modNames);
            manager.sendPacket(new SPacketDisconnect(text));
            manager.closeChannel(text);
            return false;
        }

        manager.channel().attr(NetworkRegistry.FML_MARKER).set(packet.hasFMLMarker());
        return true;
    }
}
