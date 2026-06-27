package com.bgmitdm.events;

import com.bgmitdm.game.TDMGameManager;
import com.bgmitdm.game.KitManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerRespawnEvent {
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        TDMGameManager gameManager = TDMGameManager.getInstance();
        if (gameManager.getState() != TDMGameManager.GameState.RUNNING) return;

        gameManager.getRespawnManager().respawnPlayer(player);
    }
}
