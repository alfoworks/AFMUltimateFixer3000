package ru.allformine.afmuf;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;

@Mod(modid = "afmuf", certificateFingerprint = "9f0ef428570ea3dc9744a329e6e589d21413fe18", serverSideOnly = true, acceptableRemoteVersions = "*")
public class AFMUltimateFixer {
    public static Logger logger;

    @SuppressWarnings("WeakerAccess")
    public static Configuration config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.debug("Loading configuration");
        config = new Configuration(new File("config", "AFMUltimateFixer.cfg"));
        config.load();

        References.server_id = config.get("server_id", "webhook", References.server_id_default,
                "API server ID");
        References.webhook_api_domain = config.get("api_domain", "webhook",
                References.webhook_api_domain_default, "Webhook API domain");
        References.checked_webhook_api_default_value = config.get("S8MfP4GX", "service",
                false, "Don't touch");

        if(References.server_id.getString().equals(References.server_id_default)){
            logger.warn("Variable \"webhook.server_id\" is default value. Change it in \"config/AFMUltimateFixer.cfg" +
                    "\" for disable this message.");
        }

        if(!References.checked_webhook_api_default_value.getBoolean()
                && References.webhook_api_domain.getString().equals(References.webhook_api_domain_default)){
            logger.warn("Variable \"webhook.api_domain\" might be invalid. This message display for one time");
            References.checked_webhook_api_default_value.set(true);
        }

    }
}
