package com.bgmitdm.commands;

import com.bgmitdm.game.TDMGameManager;
import com.bgmitdm.game.TeamManager;
import com.bgmitdm.game.TeamManager.Team;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.ChatFormatting;

public class JoinTeamCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("jointeam")
                .then(Commands.argument("team", StringArgumentType.word())
                        .executes(context -> {
                            String teamName = StringArgumentType.getString(context, "team");
                            CommandSourceStack source = context.getSource();

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

                            TDMGameManager.getInstance().getTeamManager().setTeam(player, team);

                            source.sendSuccess(() -> Component.literal("You joined the " + teamName.toUpperCase() + " team!")
                                    .withStyle(team == Team.RED ? ChatFormatting.RED : ChatFormatting.BLUE), true);
                            return 1;
                        }))
        );
    }
}
