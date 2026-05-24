package com.pluginforge.amazingrtp.listeners;

import com.pluginforge.amazingrtp.AmazingRtp;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuInteractionHandler implements Listener {

    private final AmazingRtp plugin;

    public MenuInteractionHandler(AmazingRtp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.DARK_GRAY + "RTP Menu")) return;
        
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;

        var item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        if (item.getType() == Material.COMPASS) {
            player.closeInventory();
            plugin.getRtpLogic().performRandomTeleport(player, player.getWorld());
        }
    }
}
