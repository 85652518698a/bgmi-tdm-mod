package com.bgmitdm.commands;

import com.bgmitdm.game.TDMGameManager;
import com.bgmitdm.game.TeamManager;
import com.bgmitdm.game.KitManager;
import com.bgmitdm.game.TeamManager.Team;
import com.bgmitdm.map.WarehouseMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.ChatFormatting;

public class TDMCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tdm")
                .then(Commands.literal("start")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> executeStart(context.getSource())))
                .then(Commands.literal("stop")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> executeStop(context.getSource())))
                .then(Commands.literal("join")
                        .then(Commands.argument("team", StringArgumentType.word())
                                .executes(context -> executeJoin(context.getSource(),
                                        StringArgumentType.getString(context, "team")))))
                .then(Commands.literal("score")
                        .executes(context -> executeScore(context.getSource())))
                .then(Commands.literal("map")
                        .then(Commands.literal("generate")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> executeMapGenerate(context.getSource()))))
                .then(Commands.literal("kit")
                        .executes(context -> executeKit(context.getSource())))
                .then(Commands.literal("tp")
                        .then(Commands.literal("red")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> executeTpRed(context.getSource())))
                        .then(Commands.literal("blue")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> executeTpBlue(context.getSource()))))
        );
    }

    private static int executeStart(CommandSourceStack source) {
        TDMGameManager gameManager = TDMGameManager.getInstance();
        if (gameManager.getState() == TDMGameManager.GameState.RUNNING) {
            source.sendFailure(Component.literal("A match is already running!"));
            return 0;
        }
        gameManager.setServer(source.getServer());
        gameManager.startGame();
        source.sendSuccess(() -> Component.literal("TDM match started!").withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static int executeStop(CommandSourceStack source) {
        TDMGameManager gameManager = TDMGameManager.getInstance();
        if (gameManager.getState() != TDMGameManager.GameState.RUNNING) {
            source.sendFailure(Component.literal("No match is running!"));
            return 0;
        }
        gameManager.stopGame();
        source.sendSuccess(() -> Component.literal("TDM match stopped!").withStyle(ChatFormatting.RED), true);
        return 1;
    }

    private static int executeJoin(CommandSourceStack source, String teamName) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Only players can join teams!"));
            return 0;
        }

        Team team;
        if (teamName.equalsIgnoreCase("red")) {
            team = Team.RED;
        } else if (teamName.equalsIgnoreCase("blue")) {
            team = Team.BLUE;
        } else {
            source.sendFailure(Component.literal("Invalid team! Use 'red' or 'blue'."));
            return 0;
        }

        TDMGameManager gameManager = TDMGameManager.getInstance();
        gameManager.getTeamManager().setTeam(player, team);

        source.sendSuccess(() -> Component.literal("You joined the " + teamName.toUpperCase() + " team!")
                .withStyle(team == Team.RED ? ChatFormatting.RED : ChatFormatting.BLUE), true);
        return 1;
    }

    private static int executeScore(CommandSourceStack source) {
        TDMGameManager gameManager = TDMGameManager.getInstance();
        int redScore = gameManager.getTeamScore(Team.RED);
        int blueScore = gameManager.getTeamScore(Team.BLUE);

        source.sendSuccess(() -> Component.literal("=== TDM SCORE ===").withStyle(ChatFormatting.GOLD), false);
        source.sendSuccess(() -> Component.literal("RED TEAM: " + redScore).withStyle(ChatFormatting.RED), false);
        source.sendSuccess(() -> Component.literal("BLUE TEAM: " + blueScore).withStyle(ChatFormatting.BLUE), false);

        if (source.getEntity() instanceof ServerPlayer player) {
            int kills = gameManager.getPlayerKills(player);
            source.sendSuccess(() -> Component.literal("Your kills: " + kills).withStyle(ChatFormatting.GREEN), false);
        }

        int timeRemaining = gameManager.getTimeRemaining();
        if (timeRemaining > 0) {
            int minutes = timeRemaining / 60;
            int seconds = timeRemaining % 60;
            source.sendSuccess(() -> Component.literal(String.format("Time remaining: %02d:%02d", minutes, seconds))
                    .withStyle(ChatFormatting.YELLOW), false);
        }

        return 1;
    }

    private static int executeMapGenerate(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Only players can generate maps!"));
            return 0;
        }

        WarehouseMap warehouseMap = new WarehouseMap();
        warehouseMap.generate(player.serverLevel(), player.blockPosition());

        source.sendSuccess(() -> Component.literal("Warehouse map generated!").withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static int executeKit(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Only players can get kits!"));
            return 0;
        }

        KitManager.giveKit(player);
        source.sendSuccess(() -> Component.literal("Weapon kit given!").withStyle(ChatFormatting.GREEN), false);
        return 1;
    }

    private static int executeTpRed(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Only players can teleport!"));
            return 0;
        }

        player.teleportTo(player.serverLevel(), 0, 65, -40, 0, 0);
        source.sendSuccess(() -> Component.literal("Teleported to Red spawn!").withStyle(ChatFormatting.RED), true);
        return 1;
    }

    private static int executeTpBlue(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Only players can teleport!"));
            return 0;
        }

        player.teleportTo(player.serverLevel(), 0, 65, 40, 180, 0);
        source.sendSuccess(() -> Component.literal("Teleported to Blue spawn!").withStyle(ChatFormatting.BLUE), true);
        return 1;
    }
}
