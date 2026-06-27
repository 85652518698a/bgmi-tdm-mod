package com.bgmitdm.game;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class KitManager {
    public static void giveKit(Player player) {
        player.getInventory().clearContent();

        player.getInventory().setItem(0, new ItemStack(Items.BOW));
        player.getInventory().setItem(1, new ItemStack(Items.CROSSBOW));
        player.getInventory().setItem(2, new ItemStack(Items.IRON_SWORD));
        player.getInventory().setItem(3, new ItemStack(Items.SNOWBALL, 10));

        ItemStack arrows = new ItemStack(Items.ARROW, 64);
        ItemStack fireworks = new ItemStack(Items.FIREWORK_ROCKET, 16);

        player.getInventory().setItem(8, arrows);
        player.getInventory().add(fireworks);

        TDMGameManager.getInstance().getTeamManager().giveTeamArmor(player);
    }
}
