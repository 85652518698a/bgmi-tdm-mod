package com.bgmitdm.game;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class ScoreboardManager {
    private static final String OBJECTIVE_NAME = "tdm_kills";
    private static final String RED_TEAM_NAME = "TDM_RED";
    private static final String BLUE_TEAM_NAME = "TDM_BLUE";

    public void createScoreboard(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();

        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective == null) {
            objective = scoreboard.addObjective(OBJECTIVE_NAME, ObjectiveCriteria.DUMMY,
                    Component.literal("BGMI TDM"), ObjectiveCriteria.RenderType.INTEGER);
        }
        scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, objective);

        if (scoreboard.getPlayerTeam(RED_TEAM_NAME) == null) {
            PlayerTeam redTeam = scoreboard.addPlayerTeam(RED_TEAM_NAME);
            redTeam.setColor(ChatFormatting.RED);
            redTeam.setDisplayName(Component.literal("RED TEAM").withStyle(ChatFormatting.RED));
        }

        if (scoreboard.getPlayerTeam(BLUE_TEAM_NAME) == null) {
            PlayerTeam blueTeam = scoreboard.addPlayerTeam(BLUE_TEAM_NAME);
            blueTeam.setColor(ChatFormatting.BLUE);
            blueTeam.setDisplayName(Component.literal("BLUE TEAM").withStyle(ChatFormatting.BLUE));
        }
    }

    public void updateScoreboard(MinecraftServer server, int redScore, int blueScore) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective == null) return;

        var redScoreObj = scoreboard.getOrCreatePlayerScore("RED TEAM", objective);
        redScoreObj.setScore(redScore);

        var blueScoreObj = scoreboard.getOrCreatePlayerScore("BLUE TEAM", objective);
        blueScoreObj.setScore(blueScore);
    }

    public void clearScoreboard(MinecraftServer server) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();

        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective != null) {
            scoreboard.removeObjective(objective);
        }

        PlayerTeam redTeam = scoreboard.getPlayerTeam(RED_TEAM_NAME);
        if (redTeam != null) {
            scoreboard.removePlayerTeam(redTeam);
        }

        PlayerTeam blueTeam = scoreboard.getPlayerTeam(BLUE_TEAM_NAME);
        if (blueTeam != null) {
            scoreboard.removePlayerTeam(blueTeam);
        }
    }
}
