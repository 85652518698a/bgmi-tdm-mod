package com.bgmitdm.game;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import java.util.*;

public class TeamManager {
    public enum Team {
        RED, BLUE, NONE
    }

    private static final int MAX_PLAYERS_PER_TEAM = 8;
    private final Map<UUID, Team> playerTeams = new HashMap<>();

    public Team assignTeam(Player player) {
        int redCount = getTeamCount(Team.RED);
        int blueCount = getTeamCount(Team.BLUE);

        Team team;
        if (redCount <= blueCount) {
            team = Team.RED;
        } else {
            team = Team.BLUE;
        }

        if (getTeamCount(team) >= MAX_PLAYERS_PER_TEAM) {
            return Team.NONE;
        }

        playerTeams.put(player.getUUID(), team);
        return team;
    }

    public void setTeam(Player player, Team team) {
        if (team == Team.RED || team == Team.BLUE) {
            if (getTeamCount(team) < MAX_PLAYERS_PER_TEAM) {
                playerTeams.put(player.getUUID(), team);
            }
        }
    }

    public Team getTeam(Player player) {
        return playerTeams.getOrDefault(player.getUUID(), Team.NONE);
    }

    public int getTeamCount(Team team) {
        return (int) playerTeams.values().stream().filter(t -> t == team).count();
    }

    public boolean isSameTeam(Player p1, Player p2) {
        Team t1 = getTeam(p1);
        Team t2 = getTeam(p2);
        return t1 != Team.NONE && t1 == t2;
    }

    public void removePlayer(Player player) {
        playerTeams.remove(player.getUUID());
    }

    public void clearTeams() {
        playerTeams.clear();
    }

    public List<Player> getTeamPlayers(Team team) {
        return new ArrayList<>(); // We don't store Player references directly
    }

    public int getRedScore() {
        return TDMGameManager.getInstance().getTeamScore(Team.RED);
    }

    public int getBlueScore() {
        return TDMGameManager.getInstance().getTeamScore(Team.BLUE);
    }

    public void giveTeamArmor(Player player) {
        Team team = getTeam(player);
        if (team == Team.NONE) return;

        int color;
        if (team == Team.RED) {
            color = 0xFF0000;
        } else {
            color = 0x0000FF;
        }

        ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);

        setArmorColor(helmet, color);
        setArmorColor(chestplate, color);
        setArmorColor(leggings, color);
        setArmorColor(boots, color);

        player.getInventory().armor.set(0, boots);
        player.getInventory().armor.set(1, leggings);
        player.getInventory().armor.set(2, chestplate);
        player.getInventory().armor.set(3, helmet);
    }

    private void setArmorColor(ItemStack stack, int color) {
        CompoundTag tag = stack.getOrCreateTagElement("display");
        tag.putInt("color", color);
    }
}
