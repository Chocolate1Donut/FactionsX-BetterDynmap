package net.donut.fxbetterdynmap;

import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.PlayerManager;
import net.prosavage.factionsx.manager.FactionManager;
import net.prosavage.factionsx.persist.data.FLocation;
import net.prosavage.factionsx.addonframework.Addon;
import net.prosavage.factionsx.persist.data.Grid;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;
import org.dynmap.utils.TileFlags;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

import static net.prosavage.factionsx.util.UtilKt.logColored;

import net.prosavage.factionsx.addonframework.Addon;

public class DynmapTestEngine {

    private static DynmapTestEngine i = new DynmapTestEngine();
    public static DynmapTestEngine getInstance() {
        return i;
    }
    private DynmapTestEngine() {
    }

    public DynmapAPI dynmapAPI;
    public MarkerAPI markerAPI;
    public MarkerSet markerSet;

    public Addon fxaapi;

    public boolean init() {
        Plugin dynmap = Bukkit.getPluginManager().getPlugin("dynmap");
        this.dynmapAPI = (DynmapAPI) dynmap;
        this.markerAPI = this.dynmapAPI.getMarkerAPI();
        if (dynmap == null || !dynmap.isEnabled()) {
            logColored("Failed to find dynmap or it is disabled.");
            return false;
        }
            markerSet = dynmapAPI.getMarkerAPI().createMarkerSet
                    ("factionsx", Config.dynmapLayerName, markerAPI.getMarkerIcons(), false);
            refreshClaims();
            return true;
    }

    public void createAreaMarker() {
        // String markerID = Bukkit.getWorld(faction.world) + faction.name - at some point make this work
        AreaMarker areaMarker = markerSet.createAreaMarker("testID", "FactionTest",
                true, Bukkit.getWorld("world").getName(), new double[1000], new double[1000], false);
        double[] d1 = {3586.0, 3571.0, 3572.0, 3591.0, 3596.0};
        double[] d2 = {181.0, 181.0, 200.0, 200.0, 193.0};
        areaMarker.setCornerLocations(d1, d2);
        areaMarker.setLabel("Faction");
        areaMarker.setDescription("<span style=\"font-weight:bold;font-size:150%\">TestEmpire</span>" +
                "<span style=\"font-style:italic;font-size:110%\">Britannic Empire // Capitals: London, Annwn, Warszawa // Grand Alliance Founder // Government: Monarchy // Immigration: Open // Tourists Welcome // BRITANNIA RULES THE WAVES</span>");
    }

    public void refreshClaims() {
        GridManager gridManager = GridManager.INSTANCE;
        Set<Faction> allFactions = FactionManager.INSTANCE.getFactions();
        // For all factions, handle a faction.
        for(Faction faction : allFactions) {
            handleFaction(faction);
        }
    }

    public void refreshHomes() {

    }

    public void handleFaction(Faction faction) {
        logColored("Drawing faction: " +faction);
        GridManager gridManager = GridManager.INSTANCE;
        Set<FLocation> allChunks = gridManager.getAllClaims(faction);
        for (FLocation chunk : allChunks) {
            logColored("Doing something with faction chunk at "+chunk);
        }
    }

    public void shutdown() {

    }
}
