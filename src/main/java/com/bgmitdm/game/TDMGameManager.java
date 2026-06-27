package com.bgmitdm.game;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.ChatFormatting;
import java.util.*;

public class TDMGameManager {
    private static TDMGameManager instance;

    public enum GameState {
        IDLE, RUNNING, ENDED
    }

    private GameState state = GameState.IDLE;
    private int redScore = 0;
    private int blueScore = 0;
    private final Map<UUID, Integer> playerKills = new HashMap<>();
    private int matchTimeSeconds;
    private int timeRemaining;
    private MinecraftServer server;
    private final TeamManager teamManager = new TeamManager();
    private final ScoreboardManager scoreboardManager = new ScoreboardManager();
    private final RespawnManager respawnManager = new RespawnManager();

    private static final int MAX_KILLS_TO_WIN = 40;
    private static final int MATCH_TIME_MINUTES = 10;

    private TDMGameManager() {}

    public static TDMGameManager getInstance() {
        if (instance == null) {
            instance = new TDMGameManager();
        }
        return instance;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public RespawnManager getRespawnManager() {
        return respawnManager;
    }

    public GameState getState() {
        return state;
    }

    public void startGame() {
        if (state == GameState.RUNNING) return;
        state = GameState.RUNNING;
        redScore = 0;
        blueScore = 0;
        playerKills.clear();
        matchTimeSeconds = MATCH_TIME_MINUTES * 60;
        timeRemaining = matchTimeSeconds;

        if (server != null) {
            Component title = Component.literal("MATCH STARTED").withStyle(ChatFormatting.GOLD);
            Component subtitle = Component.literal("Good Luck!").withStyle(ChatFormatting.GREEN);

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.connection.send(new ClientboundSetTitleTextPacket(title));
                player.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));
            }

            scoreboardManager.createScoreboard(server);
            teleportPlayersToSpawn();
        }
    }

    public void stopGame() {
        if (state != GameState.RUNNING) return;
        state = GameState.ENDED;
        endGame(TeamManager.Team.NONE);
    }

    public void addKill(Player killer) {
        if (state != GameState.RUNNING) return;

        UUID killerId = killer.getUUID();
        playerKills.put(killerId, playerKills.getOrDefault(killerId, 0) + 1);

        TeamManager.Team killerTeam = teamManager.getTeam(killer);
        if (killerTeam == TeamManager.Team.RED) {
            redScore++;
        } else if (killerTeam == TeamManager.Team.BLUE) {
            blueScore++;
        }

        scoreboardManager.updateScoreboard(server, redScore, blueScore);

        if (redScore >= MAX_KILLS_TO_WIN || blueScore >= MAX_KILLS_TO_WIN) {
            endGame(killerTeam);
        }
    }

    public int getPlayerKills(Player player) {
        return playerKills.getOrDefault(player.getUUID(), 0);
    }

    public int getTeamScore(TeamManager.Team team) {
        return team == TeamManager.Team.RED ? redScore : blueScore;
    }

    public void tick() {
        if (state != GameState.RUNNING) return;
        timeRemaining--;

        if (timeRemaining <= 0) {
            TeamManager.Team winner = redScore > blueScore ? TeamManager.Team.RED :
                    blueScore > redScore ? TeamManager.Team.BLUE : TeamManager.Team.NONE;
            endGame(winner);
        } else if (timeRemaining == 60) {
            broadcastBossbar(Component.literal("WARNING: 1 MINUTE REMAINING")
                    .withStyle(ChatFormatting.RED));
        }
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    private void endGame(TeamManager.Team winningTeam) {
        state = GameState.ENDED;

        if (server == null) return;

        Component title;
        if (winningTeam == TeamManager.Team.RED) {
            title = Component.literal("RED TEAM WINS!").withStyle(ChatFormatting.RED);
        } else if (winningTeam == TeamManager.Team.BLUE) {
            title = Component.literal("BLUE TEAM WINS!").withStyle(ChatFormatting.BLUE);
        } else {
            title = Component.literal("MATCH ENDED - DRAW!").withStyle(ChatFormatting.YELLOW);
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.send(new ClientboundSetTitleTextPacket(title));
        }

        server.getPlayerList().getPlayers().forEach(p -> {
            p.teleportTo(server.overworld(), 0, 64, 0, p.getYRot(), p.getXRot());
        });

        scoreboardManager.clearScoreboard(server);
        teamManager.clearTeams();
        playerKills.clear();
    }

    private void teleportPlayersToSpawn() {
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            TeamManager.Team team = teamManager.getTeam(player);
            if (team == TeamManager.Team.RED) {
                player.teleportTo(server.overworld(), 0, 64, -40, 0, 0);
            } else if (team == TeamManager.Team.BLUE) {
                player.teleportTo(server.overworld(), 0, 64, 40, 180, 0);
            }
        }
    }

    private void broadcastBossbar(Component message) {
        if (server == null) return;
        server.getPlayerList().getPlayers().forEach(p ->
                p.sendSystemMessage(message));
    }
}
