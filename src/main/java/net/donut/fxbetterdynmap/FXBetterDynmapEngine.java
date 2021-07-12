package net.donut.fxbetterdynmap;

import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.FactionManager;
import net.prosavage.factionsx.persist.data.FLocation;
import net.prosavage.factionsx.addonframework.Addon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;

import java.util.*;

import static net.prosavage.factionsx.util.UtilKt.logColored;

public class FXBetterDynmapEngine {

    private final static FXBetterDynmapEngine i = new FXBetterDynmapEngine();

    public static FXBetterDynmapEngine getInstance() {
        return i;
    }

    private FXBetterDynmapEngine() {
    }

    public DynmapAPI dynmapAPI;
    public MarkerAPI markerAPI;
    public MarkerSet markerSet;

    public Addon fxaapi;

    public boolean init() {
        Plugin dynmap = Bukkit.getPluginManager().getPlugin("dynmap");
        this.dynmapAPI = (DynmapAPI) dynmap;
        if (dynmap == null || !dynmap.isEnabled()) {
            logColored("Failed to find dynmap or it is disabled.");
            return false;
        }
        this.markerAPI = this.dynmapAPI.getMarkerAPI();
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
        for (Faction faction : allFactions) {
            handleFaction(faction);
        }
    }

    public void refreshHomes() {

    }

    public void handleFaction(Faction faction) {
        logColored("Handling faction: " + faction);
        GridManager gridManager = GridManager.INSTANCE;
        Set<FLocation> allChunks = gridManager.getAllClaims(faction);
        // Create an area marker with the faction's ID for area marker ID and faction tag for label
        AreaMarker areaMarker = markerSet.createAreaMarker(String.valueOf(faction.getId()), faction.getTag(),
                true, Bukkit.getWorld("world").getName(), new double[1000], new double[1000], false);
        double[] d1 = {3586.0, 3571.0, 3572.0, 3591.0, 3596.0};
        double[] d2 = {181.0, 181.0, 200.0, 200.0, 193.0};
        areaMarker.setCornerLocations(d1, d2);
        areaMarker.setBoostFlag(true);
        areaMarker.setDescription
                ("<span style=\"font-weight:bold;font-size:150%\">" + faction.getTag() + "</span>" +
                        "<span style=\"font-style:italic;font-size:110%\">" + faction.getDescription() + "</span>" +
                        "<span style=\"font-weight:bold\">Leader:</span> " + faction.getLeader() +
                        "<span style=\"font-weight:bold\">Officers:</span> 0" +
                        "<span style=\"font-weight:bold\">Members:</span> 0" +
                        "<span style=\"font-weight:bold\">Recruits:</span> 0" +
                        "<span style=\"font-weight:bold\">TOTAL:</span> 1" +

                        "<span style=\"font-weight:bold\">Age:</span> " + faction.getFormattedCreationDate() +
                        "<span style=\"font-weight:bold\">Bank:</span> " + faction.getBank().getAmount() +
                        "<span style=\"font-weight:bold\">Flags:</span>" +
                        "<span style=\"color:#800000\">open</span> " +
                        "| <span style=\"color:#008000\">monsters</span> " +
                        "| <span style=\"color:#008000\">animals</span><br>" +
                        "| <span style=\"color:#008000\">pvp</span>"
                );

        for (FLocation chunk : allChunks) {
            logColored("Doing something with faction chunk at " + chunk);
        }
    }

    public void shutdown() {

    }
}
