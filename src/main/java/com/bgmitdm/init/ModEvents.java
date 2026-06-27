package com.bgmitdm.init;

import com.bgmitdm.events.PlayerDeathEvent;
import com.bgmitdm.events.PlayerJoinEvent;
import com.bgmitdm.events.PlayerRespawnEvent;
import com.bgmitdm.game.TDMGameManager;
import com.bgmitdm.game.TeamManager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModEvents {
    private final PlayerDeathEvent deathHandler = new PlayerDeathEvent();
    private final PlayerJoinEvent joinHandler = new PlayerJoinEvent();
    private final PlayerRespawnEvent respawnHandler = new PlayerRespawnEvent();

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        deathHandler.onDeath(event);
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        joinHandler.onPlayerJoin(event);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        respawnHandler.onRespawn(event);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player victim && event.getSource().getEntity() instanceof Player attacker) {
            TDMGameManager gameManager = TDMGameManager.getInstance();
            if (gameManager.getState() == TDMGameManager.GameState.RUNNING) {
                if (gameManager.getTeamManager().isSameTeam(attacker, victim)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            TDMGameManager.getInstance().tick();
        }
    }
}
