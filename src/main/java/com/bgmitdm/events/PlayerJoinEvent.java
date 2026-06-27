package com.bgmitdm.events;

import com.bgmitdm.game.TDMGameManager;
import com.bgmitdm.game.TeamManager;
import com.bgmitdm.game.KitManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerJoinEvent {
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        TDMGameManager gameManager = TDMGameManager.getInstance();
        TeamManager teamManager = gameManager.getTeamManager();

        TeamManager.Team team = teamManager.assignTeam(player);
        if (team != TeamManager.Team.NONE) {
            String teamName = team == TeamManager.Team.RED ? "RED" : "BLUE";
            ChatFormatting color = team == TeamManager.Team.RED ? ChatFormatting.RED : ChatFormatting.BLUE;

            player.sendSystemMessage(Component.literal("You have been assigned to the " + teamName + " team!")
                    .withStyle(color));

            player.sendSystemMessage(Component.literal("Use /tdm join <red/blue> to change teams.")
                    .withStyle(ChatFormatting.GRAY));

            if (gameManager.getState() == TDMGameManager.GameState.RUNNING) {
                KitManager.giveKit(player);
            }
        }
    }
}
