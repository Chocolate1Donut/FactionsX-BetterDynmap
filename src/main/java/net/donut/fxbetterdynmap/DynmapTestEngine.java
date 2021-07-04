package net.donut.fxbetterdynmap;

import net.prosavage.factionsx.core.FPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;
import org.dynmap.utils.TileFlags;

import java.awt.geom.Area;
import java.util.List;

import net.prosavage.factionsx.addonframework.Addon;

public class DynmapTestEngine {

    private static final DynmapTestEngine i = new DynmapTestEngine();
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
            fxaapi.logColored("Failed to find dynmap or it is disabled.");
            return false;
        }
            markerSet = dynmapAPI.getMarkerAPI().createMarkerSet
                    ("factionsx", "FactionsX", markerAPI.getMarkerIcons(), false);
            createAreaMarker();
            return true;
    }

    public void createAreaMarker() {
        // String markerID = Bukkit.getWorld(faction.world) + faction.name - at some point make this work
        AreaMarker areaMarker = markerSet.createAreaMarker("testID", "FactionTest",
                true, Bukkit.getWorld("world").getName(), new double[1000], new double[1000], false);
        double[] d1 = {-50, -9};
        double[] d2 = {-720, -679};
        areaMarker.setCornerLocations(d1, d2);
        areaMarker.setLabel("Faction");
        areaMarker.setDescription("<span style=\"font-weight:bold;font-size:150%\">BritannicEmpire</span>" +
                "<span style=\"font-style:italic;font-size:110%\">Britannic Empire // Capitals: London, Annwn, Warszawa // Grand Alliance Founder // Government: Monarchy // Immigration: Open // Tourists Welcome // BRITANNIA RULES THE WAVES</span>");
    }

    public void shutdown() {
    }
}
