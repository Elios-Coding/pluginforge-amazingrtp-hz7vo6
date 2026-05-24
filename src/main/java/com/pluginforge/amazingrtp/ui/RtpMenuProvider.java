package com.pluginforge.amazingrtp.ui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RtpMenuProvider {

    public static void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "RTP Menu");

        ItemStack rtpItem = new ItemStack(Material.COMPASS);
        ItemMeta meta = rtpItem.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Random Teleport");
        meta.setLore(List.of(ChatColor.GRAY + "Teleport to a random location in this world."));
        rtpItem.setItemMeta(meta);

        inv.setItem(13, rtpItem);
        player.openInventory(inv);
    }
}
