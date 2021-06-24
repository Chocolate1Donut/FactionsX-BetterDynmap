package net.donut.fxbetterdynmap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;

import net.prosavage.factionsx.addonframework.Addon;

/** Based off SaberFactions Dynmap code (https://github.com/SaberLLC/Saber-Factions/blob/1.6.x/src/main/java/com/massivecraft/factions/integration/dynmap/EngineDynmap.java),
 * Modified to work as a FactionsX addon
 * By Donut */

public class BetterDynmapEngine {

    public final static int BLOCKS_PER_CHUNK = 16;

    public final static String DYNMAP_INTEGRATION = "\u00A7dFactionsX-BetterDynmap:\u00A7e";

    public final static String FACTIONS = "factions";
    public final static String FACTIONS_ = FACTIONS + "_";

    public final static String FACTIONS_MARKERSET = FACTIONS_ + "markerset";

    public final static String FACTIONS_HOME = FACTIONS_ + "home";
    public final static String FACTIONS_HOME_ = FACTIONS_HOME + "_";

    public final static String FACTIONS_PLAYERSET = FACTIONS_ + "playerset";
    public final static String FACTIONS_PLAYERSET_ = FACTIONS_PLAYERSET + "_";

    public DynmapAPI dynmapApi;
    public MarkerAPI markerApi;
    public MarkerSet markerset;

    public Addon fxapi;

    public void init() {
        Plugin dynmap = Bukkit.getServer().getPluginManager().getPlugin("dynmap");

        if (dynmap == null || !dynmap.isEnabled()) {
            fxapi.logColored("Failed to find dynmap or it is disabled.");
            return;
        }
    }

    public boolean updateCore() {
        fxapi.logColored("Updating Core");
        // Get DynmapAPI
        this.dynmapApi = (DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap");
        if (this.dynmapApi == null) {
            fxapi.logColored("Could not retrieve the DynmapAPI.");
            return false;
        }

        // Get MarkerAPI
        this.markerApi = this.dynmapApi.getMarkerAPI();
        if (this.markerApi == null) {
            fxapi.logColored("Could not retrieve the MarkerAPI.");
            return false;
        }

        return true;
    }

}