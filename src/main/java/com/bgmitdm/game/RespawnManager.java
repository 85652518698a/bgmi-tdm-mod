package com.bgmitdm.game;

import com.bgmitdm.game.TeamManager.Team;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RespawnManager {
    private final Map<UUID, Long> deathTimestamps = new HashMap<>();
    private static final int RESPAWN_DELAY_SECONDS = 3;

    public void scheduleRespawn(ServerPlayer player) {
        deathTimestamps.put(player.getUUID(), System.currentTimeMillis());
    }

    public boolean isRespawnReady(ServerPlayer player) {
        Long deathTime = deathTimestamps.get(player.getUUID());
        if (deathTime == null) return true;
        return (System.currentTimeMillis() - deathTime) >= (RESPAWN_DELAY_SECONDS * 1000L);
    }

    public void clearDeath(ServerPlayer player) {
        deathTimestamps.remove(player.getUUID());
    }

    public BlockPos getRespawnPosition(ServerPlayer player) {
        TDMGameManager gameManager = TDMGameManager.getInstance();
        Team team = gameManager.getTeamManager().getTeam(player);

        if (team == Team.RED) {
            return new BlockPos(0, 64, -40);
        } else if (team == Team.BLUE) {
            return new BlockPos(0, 64, 40);
        }

        return player.serverLevel().getSharedSpawnPos();
    }

    public void respawnPlayer(ServerPlayer player) {
        BlockPos respawnPos = getRespawnPosition(player);
        player.teleportTo(player.serverLevel(), respawnPos.getX() + 0.5, respawnPos.getY(),
                respawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
        player.setHealth(20.0f);
        KitManager.giveKit(player);
        clearDeath(player);
    }
}
