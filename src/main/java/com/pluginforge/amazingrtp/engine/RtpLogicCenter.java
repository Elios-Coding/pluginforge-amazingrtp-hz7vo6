package com.pluginforge.amazingrtp.engine;

import com.pluginforge.amazingrtp.AmazingRtp;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RtpLogicCenter {

    private final AmazingRtp plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Random random = new Random();

    public RtpLogicCenter(AmazingRtp plugin) {
        this.plugin = plugin;
    }

    public void performRandomTeleport(Player player, World world) {
        if (isOnCooldown(player)) {
            player.sendMessage(ChatColor.RED + "Please wait before RTPing again.");
            return;
        }

        player.sendMessage(ChatColor.GRAY + "Searching for a safe location...");
        
        findSafeLocation(world).thenAccept(location -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (location == null) {
                    player.sendMessage(ChatColor.RED + "Could not find a safe location. Try again.");
                    return;
                }
                
                player.teleport(location.add(0.5, 1, 0.5));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                player.spawnParticle(Particle.PORTAL, player.getLocation(), 50);
                player.sendMessage(ChatColor.GREEN + "Teleported to: " + location.getBlockX() + ", " + location.getBlockZ());
                
                setCooldown(player);
            });
        });
    }

    private CompletableFuture<Location> findSafeLocation(World world) {
        return CompletableFuture.supplyAsync(() -> {
            int radius = plugin.getCfg().getRadius();
            for (int i = 0; i < 15; i++) {
                int x = random.nextInt(radius * 2) - radius;
                int z = random.nextInt(radius * 2) - radius;

                // Off-thread chunk check (Paper optimization)
                if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                    world.getChunkAtAsync(x >> 4, z >> 4);
                }

                int y = world.getHighestBlockYAt(x, z);
                Block block = world.getBlockAt(x, y, z);
                Block below = world.getBlockAt(x, y - 1, z);

                if (isSafe(block, below)) {
                    return new Location(world, x, y, z);
                }
            }
            return null;
        });
    }

    private boolean isSafe(Block block, Block below) {
        Material type = block.getType();
        Material floor = below.getType();
        return type.isAir() && !floor.isAir() && floor != Material.LAVA && floor != Material.WATER && floor != Material.FIRE;
    }

    private boolean isOnCooldown(Player player) {
        if (player.hasPermission("amazingrtp.bypass")) return false;
        return cooldowns.containsKey(player.getUniqueId()) && 
               System.currentTimeMillis() < cooldowns.get(player.getUniqueId());
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getCfg().getCooldown() * 1000L));
    }
}
