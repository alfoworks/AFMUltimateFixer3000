package ru.allformine.afmuf;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

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

        MinecraftForge.EVENT_BUS.register(this);
    }
}
