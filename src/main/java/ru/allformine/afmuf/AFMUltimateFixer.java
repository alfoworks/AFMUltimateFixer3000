package ru.allformine.afmuf;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import org.apache.logging.log4j.Logger;
import pcl.opensecurity.common.tileentity.TileEntityEntityDetector;

import java.io.File;
import java.lang.reflect.Method;

@Mod(modid = "afmuf", certificateFingerprint = "9f0ef428570ea3dc9744a329e6e589d21413fe18", serverSideOnly = true, acceptableRemoteVersions = "*")
public class AFMUltimateFixer {
    public static Logger logger;

    @SuppressWarnings("WeakerAccess")
    public static Configuration config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        config = new Configuration(new File("config", "AFMUltimateFixer.cfg"));
        config.load();

        References.server_id = config.getString("Discord", "server_id", References.server_id, "Server ID");
    }

    @EventHandler
    public void serverStart(FMLServerAboutToStartEvent event) {
        for (Method method : TileEntityEntityDetector.class.getMethods()) {
            System.out.println(method.toString());
        }
    }
}
