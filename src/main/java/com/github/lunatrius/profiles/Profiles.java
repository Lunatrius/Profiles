package com.github.lunatrius.profiles;

import com.github.lunatrius.core.version.VersionChecker;
import com.github.lunatrius.profiles.command.ProfileCommand;
import com.github.lunatrius.profiles.reference.Reference;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.client.ClientCommandHandler;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class Profiles {
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Reference.logger = event.getModLog();
        Reference.config = event.getSuggestedConfigurationFile();

        if (Loader.isModLoaded("LunatriusCore")) {
            registerVersionChecker(event.getModMetadata());
        }
    }

    private void registerVersionChecker(ModMetadata modMetadata) {
        VersionChecker.registerMod(modMetadata, Reference.FORGE);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            ClientCommandHandler.instance.registerCommand(new ProfileCommand());
        } else {
            Reference.logger.warn("WARNING! You're loading a CLIENT only mod on a server!");
        }
    }
}
