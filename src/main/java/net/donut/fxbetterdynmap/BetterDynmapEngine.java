package net.donut.fxbetterdynmap;

import net.donut.dynmaplibrary.TempMarkerSet;
import net.prosavage.factionsx.FactionsX;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.addonframework.Addon;
import net.prosavage.factionsx.manager.FactionManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;
import org.dynmap.DynmapCore;
import org.dynmap.*;

import java.util.Set;

/** Based off SaberFactions Dynmap code (https://github.com/SaberLLC/Saber-Factions/blob/1.6.x/src/main/java/com/massivecraft/factions/integration/dynmap/EngineDynmap.java),
 * Modified to work as a FactionsX addon
 * By Chocolate1Donut */

public class BetterDynmapEngine {

    public final static int BLOCKS_PER_CHUNK = 16;

    public final static String FACTIONS = "factions";
    public final static String FACTIONS_ = FACTIONS + "_";

    public final static String FACTIONS_MARKERSET = FACTIONS_ + "markerset";

    public final static String FACTIONS_HOME = FACTIONS_ + "home";
    public final static String FACTIONS_HOME_ = FACTIONS_HOME + "_";

    public final static String FACTIONS_PLAYERSET = FACTIONS_ + "playerset";
    public final static String FACTIONS_PLAYERSET_ = FACTIONS_PLAYERSET + "_";

    public DynmapAPI dynmapApi;
    public MarkerAPI markerApi;
    public MarkerSet markerSet;
    public MarkerIcon markerIcon;
    public DynmapCore dyncore;
    public DynmapCommonAPI dynmapCommonAPI;

    public FactionsX factionsX;
    public Faction faction;
    public FactionManager factionManager;
    public Addon fxapi;

    public void init() {
        Plugin dynmap = Bukkit.getServer().getPluginManager().getPlugin("dynmap");
        if (dynmap == null || !dynmap.isEnabled()) {
            fxapi.logColored("Failed to find dynmap or it is disabled.");
            return;
        }
        else {
            updateCore();

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
    public TempMarkerSet createLayer() {
        TempMarkerSet ret = new TempMarkerSet();
        ret.label = Conf.dynmapLayerName;
        ret.minimumZoom = Conf.dynmapLayerMinimumZoom;
        ret.priority = Conf.dynmapLayerPriority;
        ret.hideByDefault = !Conf.dynmapLayerVisible;
        return ret;
    }
    public Set allFactions() {
        Set<Faction> allFactions = factionManager.INSTANCE.getFactions();
        return allFactions;
    }
    public boolean updateHomes() {
        fxapi.logColored("Updating Faction Homes.");

        fxapi.logColored("Retrieving factions: "+allFactions());
        markerApi.createMarkerSet("Homes", "Faction Homes", null,true );
        return true;
    }

}