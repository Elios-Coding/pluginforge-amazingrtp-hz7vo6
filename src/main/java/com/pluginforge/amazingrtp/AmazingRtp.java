package com.pluginforge.amazingrtp;

import com.pluginforge.amazingrtp.commands.RtpCommandRouter;
import com.pluginforge.amazingrtp.engine.RtpLogicCenter;
import com.pluginforge.amazingrtp.listeners.ActivationTracker;
import com.pluginforge.amazingrtp.listeners.MenuInteractionHandler;
import com.pluginforge.amazingrtp.util.ConfigWrapper;
import org.bukkit.plugin.java.JavaPlugin;

public class AmazingRtp extends JavaPlugin {

    private static AmazingRtp instance;
    private ConfigWrapper configWrapper;
    private RtpLogicCenter rtpLogic;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize Configuration
        saveDefaultConfig();
        this.configWrapper = new ConfigWrapper(getConfig());
        
        // Initialize Core Logic
        this.rtpLogic = new RtpLogicCenter(this);
        
        // Register Commands
        getCommand("rtp").setExecutor(new RtpCommandRouter(this));
        
        // Register Listeners
        getServer().getPluginManager().registerEvents(new MenuInteractionHandler(this), this);
        getServer().getPluginManager().registerEvents(new ActivationTracker(this), this);
        
        getLogger().info("AmazingRtp has been enabled successfully.");
    }

    public static AmazingRtp getInstance() {
        return instance;
    }

    public ConfigWrapper getCfg() {
        return configWrapper;
    }

    public RtpLogicCenter getRtpLogic() {
        return rtpLogic;
    }
}
