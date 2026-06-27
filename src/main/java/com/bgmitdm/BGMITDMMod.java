package com.bgmitdm;

import com.bgmitdm.init.ModEvents;
import com.bgmitdm.commands.TDMCommand;
import com.bgmitdm.commands.JoinTeamCommand;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BGMITDMMod.MOD_ID)
public class BGMITDMMod {
    public static final String MOD_ID = "bgmitdm";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BGMITDMMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ModEvents());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("BGMI TDM Mod initialized!");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        TDMCommand.register(event.getDispatcher());
        JoinTeamCommand.register(event.getDispatcher());
    }
}
