package com.pluginforge.amazingrtp.listeners;

import com.pluginforge.amazingrtp.AmazingRtp;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class ActivationTracker implements Listener {

    private static final Set<UUID> pendingActivation = new HashSet<>();
    private final AmazingRtp plugin;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final Pattern IP_PATTERN = Pattern.compile(
            "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$");

    public ActivationTracker(AmazingRtp plugin) {
        this.plugin = plugin;
    }

    public static void initiateActivation(Player player) {
        pendingActivation.add(player.getUniqueId());
        player.sendMessage(ChatColor.AQUA + "Please enter your official server IP.");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!pendingActivation.contains(player.getUniqueId())) return;

        event.setCancelled(true);
        String input = event.getMessage().trim();

        if (isValidHostname(input)) {
            pendingActivation.remove(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "Validating IP...");
            sendToFirebase(input, player);
        } else {
            player.sendMessage(ChatColor.RED + "Invalid IP format. Please try again (e.g., play.example.com).");
        }
    }

    private boolean isValidHostname(String host) {
        if (host.equalsIgnoreCase("localhost") || host.equals("127.0.0.1")) return false;
        if (!IP_PATTERN.matcher(host).matches()) return false;
        try {
            InetAddress.getByName(host);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void sendToFirebase(String ip, Player player) {
        String url = "https://rtp-backend-6c475-default-rtdb.firebaseio.com/activations/" + ip.replace(".", "_") + ".json";
        String json = String.format("{\"plugin\":\"AmazingRtp\", \"timestamp\":%d}", System.currentTimeMillis() / 1000);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    player.sendMessage(ChatColor.GREEN + "Activation data synchronized successfully.");
                }).exceptionally(ex -> {
                    player.sendMessage(ChatColor.RED + "Failed to sync activation.");
                    return null;
                });
    }
}
