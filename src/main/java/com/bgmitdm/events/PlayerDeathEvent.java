package com.bgmitdm.events;

import com.bgmitdm.game.TDMGameManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.ChatFormatting;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class PlayerDeathEvent {
    public void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer victim)) return;

        TDMGameManager gameManager = TDMGameManager.getInstance();
        if (gameManager.getState() != TDMGameManager.GameState.RUNNING) return;

        DamageSource source = event.getSource();
        Player killer = null;

        if (source.getEntity() instanceof Player) {
            killer = (Player) source.getEntity();
        }

        if (killer != null) {
            gameManager.addKill(killer);

            String killerName = killer.getScoreboardName();
            String victimName = victim.getScoreboardName();

            Component actionMsg = Component.literal(killerName + " eliminated " + victimName)
                    .withStyle(ChatFormatting.RED);

            if (victim.server != null) {
                victim.server.getPlayerList().getPlayers().forEach(p ->
                        p.sendSystemMessage(actionMsg));
            }
        }

        Component title = Component.literal("ELIMINATED").withStyle(ChatFormatting.RED);
        Component subtitle = Component.literal("Respawning in 3 seconds...")
                .withStyle(ChatFormatting.YELLOW);

        victim.connection.send(new ClientboundSetTitleTextPacket(title));
        victim.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));

        gameManager.getRespawnManager().scheduleRespawn(victim);
    }
}
