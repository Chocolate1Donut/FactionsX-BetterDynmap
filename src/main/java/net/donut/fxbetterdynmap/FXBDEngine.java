package net.donut.fxbetterdynmap;

import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.FactionManager;
import net.prosavage.factionsx.persist.data.FLocation;
import net.prosavage.factionsx.addonframework.Addon;
import net.prosavage.factionsx.persist.data.wrappers.DataLocation;
import net.prosavage.factionsx.persist.data.wrappers.Warp;

import static net.prosavage.factionsx.util.UtilKt.logColored;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Chunk;
import org.bukkit.plugin.Plugin;

import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;

import java.awt.*;
import java.util.*;
// This library better work or i am going to go schizo
import java.awt.geom.*;
import java.util.List;

// Built for GeoLegacy.xyz by ChocolateDonut_
// June 2021

public class FXBDEngine {

    private final static FXBDEngine i = new FXBDEngine();

    public static FXBDEngine getInstance() {
        return i;
    }

    private FXBDEngine() {
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
        if (dynmapAPI.getMarkerAPI().getMarkerSet("factionsx") == null) {
            markerSet = dynmapAPI.getMarkerAPI().createMarkerSet
                    ("factionsx", Config.dynmapLayerName, markerAPI.getMarkerIcons(), false);
            if (markerSet == null) {
                return false;
            }
        }
        //refreshClaims();
        refreshHomes();
        refreshWarps();
        refreshClaims();
        logColored("FXBD Engine started successfully!");
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
        FactionManager factionManager = FactionManager.INSTANCE;
        Set<Faction> allFactions = FactionManager.INSTANCE.getFactions();
        // For all factions, handle a faction.
        for (Faction faction : allFactions) {
            handleFaction(faction);
        }
    }

    public void refreshWarps() {
        if (Config.showWarps) {
            logColored("Refreshing Warps");
            GridManager gridManager = GridManager.INSTANCE;
            FactionManager factionManager = FactionManager.INSTANCE;
            Set<Faction> allFactions = FactionManager.INSTANCE.getFactions();
            if (markerSet == null) {
                // Try and reinitialize. If it doesn't work, push an error.
                if (!init()) {
                    logColored("Failed to initialize FactionsX-BetterDynmap.");
                    return;
                }
                // If it does reinitialize, try refreshing warps again.
                else {
                    refreshWarps();
                }
            }
            // For all factions, handle a faction.
            for (Faction faction : allFactions) {
                List<Warp> allWarps = faction.getAllWarps();
                // Does this faction have warps to show?
                if (allWarps != null) {
                    // Is markerSet null for some reason?
                    // Iterate through every warp.
                    for (Warp warp : allWarps) {
                        // Does this marker not exist? If so, create it.
                        if (markerSet.findMarker(faction.getTag().replaceAll("&[a-zA-Z1-9]", "") + "_" + warp.getName()) == null) {
                            markerSet.createMarker(faction.getTag().replaceAll("&[a-zA-Z1-9]", "") + "_" + warp.getName(), warp.getName(),
                                    warp.getDataLocation().getWorldName(), warp.getDataLocation().getX(), warp.getDataLocation().getY(), warp.getDataLocation().getZ(),
                                    markerAPI.getMarkerIcon(Config.fWarpMarkerIcon), false);
                        }
                        // If this marker does exist, delete it and create another one.
                        // Is this efficient? Maybe not... But i'm trying my best.
                        else {
                            markerSet.findMarker((faction.getTag().replaceAll("&[a-zA-Z1-9]", "") + "_" + warp.getName())).deleteMarker();
                            markerSet.createMarker(faction.getTag().replaceAll("&[a-zA-Z1-9]", "") + "_" + warp.getName(), warp.getName(),
                                    warp.getDataLocation().getWorldName(), warp.getDataLocation().getX(), warp.getDataLocation().getY(), warp.getDataLocation().getZ(),
                                    markerAPI.getMarkerIcon(Config.fWarpMarkerIcon), false);
                        }
                    }
                } else {
                    continue;
                }
            }
        }
    }

    public void refreshHomes() {
        if (Config.showHomes) {
            GridManager gridManager = GridManager.INSTANCE;
            FactionManager factionManager = FactionManager.INSTANCE;
            Set<Faction> allFactions = FactionManager.INSTANCE.getFactions();
            // Is markerSet null for some reason?
            if (markerSet == null) {
                // Try and reinitialize. If it doesn't work, push an error.
                if (!init()) {
                    logColored("Failed to initialize FactionsX-BetterDynmap.");
                    return;
                }
                // If it does reinitialize, try refreshing homes again.
                else {
                    refreshHomes();
                }
            }
            // For all factions, handle a faction.
            for (Faction faction : allFactions) {
                DataLocation homeLocation = faction.getHome();
                // Does homeLocation exist for this faction?
                if (homeLocation != null) {
                    // Does a home marker exist? If not, create one.
                    if (markerSet.findMarker(faction.getTag().replaceAll("&[a-zA-Z1-9]", "") + "_home") == null) {
                        markerSet.createMarker(faction.getTag().replaceAll("&[a-zA-Z1-9]", "") + "_home", faction.getTag().replaceAll("&[a-zA-Z1-9]", ""),
                                homeLocation.getWorldName(), homeLocation.getX(), homeLocation.getY(), homeLocation.getZ(), markerAPI.getMarkerIcon(Config.fHomeMarkerIcon), false);
                    }
                    // If it does exist, then delete it and create it again.
                    else {
                        markerSet.findMarker(faction.getTag().replaceAll("&[a-zA-Z1-9]", "") + "_home").deleteMarker();
                        markerSet.createMarker(faction.getTag().replaceAll("&[a-zA-Z1-9]", "") + "_home", faction.getTag().replaceAll("&[a-zA-Z1-9]", ""),
                                homeLocation.getWorldName(), homeLocation.getX(), homeLocation.getY(), homeLocation.getZ(), markerAPI.getMarkerIcon(Config.fHomeMarkerIcon), false);
                    }
                } else {
                    continue;
                }
            }
        }
    }

    public void handleFaction(Faction faction) {
        logColored("Drawing faction: " + faction.getTag().replaceAll("&[a-zA-Z1-9]", ""));

        // Get GridManager and FactionManager so we can ask them stuff.
        GridManager gridManager = GridManager.INSTANCE;
        FactionManager factionManager = FactionManager.INSTANCE;

        // Get all claims owned by the faction
        Set<FLocation> allChunks = gridManager.getAllClaims(faction);

        // Then, we'll use an algorithm to put the corners in a proper order.
        ArrayList<Location> orderedPoints = new ArrayList<Location>();

        // Finally, we'll deliver the corners to dynmap with two different double arrays.
        // Later down the line, we convert this to a primitive array.
        ArrayList<Double> finalXs = new ArrayList<Double>();
        ArrayList<Double> finalZs = new ArrayList<Double>();

        // For this faction, handle every chunk it has.
        for (FLocation chunk : allChunks) {
            //logColored("Handling Chunk: " + chunk);

            // Get ints from chunk's location.
            int chunkX = (int) chunk.getX();
            int chunkZ = (int) chunk.getZ();

            // Create zero so we can initialize locations easier.
            Location zero = new Location(chunk.getChunk().getWorld(), 0, 0, 0);

            // Calculate the coordinates for each corner of the chunk.
            Location topLeftLocation = new Location(chunk.getChunk().getWorld(), (chunkX * 16), 0, (chunkZ * 16));
            Location topRightLocation = new Location(chunk.getChunk().getWorld(), (chunkX * 16 + 15), 0, (chunkZ * 16));
            Location bottomLeftLocation = new Location(chunk.getChunk().getWorld(), (chunkX * 16), 0, (chunkZ * 16 + 15));
            Location bottomRightLocation = new Location(chunk.getChunk().getWorld(), (chunkX * 16 + 15), 0, (chunkZ * 16 + 15));

            Area newArea;
        }

        //double[] finalXArray = finalXs.stream().mapToDouble(Double::doubleValue).toArray(); //via method reference
        double[] finalXArray = finalXs.stream().mapToDouble(d -> d).toArray(); //identity function, Java unboxes automatically to get the double value
        //double[] finalZArray = finalZs.stream().mapToDouble(Double::doubleValue).toArray(); //via method reference
        double[] finalZArray = finalZs.stream().mapToDouble(d -> d).toArray(); //identity function, Java unboxes automatically to get the double value
        // Create an area marker with the faction's ID for area marker ID and faction tag for label

        AreaMarker areaMarker = markerSet.findAreaMarker(String.valueOf(faction.getId()));
        if (areaMarker == null) {
            logColored("Creating area marker for " + faction + "with x coords of " + finalXs + " and z coords of " + finalZs);
            areaMarker = markerSet.createAreaMarker(String.valueOf(faction.getId()), faction.getTag().replaceAll("&[a-zA-Z1-9]", ""),
                    true, Bukkit.getWorld("world").getName(), new double[1000], new double[1000], false);
            if (areaMarker == null) {
                logColored("Error creating area marker for faction " + faction.getTag().replaceAll("&[a-zA-Z1-9]", ""));
                return;
            }
        }

        //double[] finalXArray = finalXs.stream().mapToDouble(Double::doubleValue).toArray(); //via method reference
        //double[] finalXArray = finalXs.stream().mapToDouble(d -> d).toArray(); //identity function, Java unboxes automatically to get the double value
        //double[] finalZArray = finalZs.stream().mapToDouble(Double::doubleValue).toArray(); //via method reference
        //double[] finalZArray = finalZs.stream().mapToDouble(d -> d).toArray(); //identity function, Java unboxes automatically to get the double value
        areaMarker.setCornerLocations(finalXArray, finalZArray);
        areaMarker.setBoostFlag(true);
        areaMarker.setDescription
                ("<span style=\"font-weight:bold;font-size:150%\">" + faction.getTag().replaceAll("&[a-zA-Z1-9]", "") + "</span>" +
                        "<span style=\"font-style:italic;font-size:110%\"> " + faction.getDescription().replaceAll("&[a-zA-Z1-9]", "") + "</span>" +
                        "<span style=\"font-weight:bold\">Leader:</span> " +
                        "<span style=\"font-weight:bold\">Officers:</span> 0 " +
                        "<span style=\"font-weight:bold\">Members:</span> 0 " +
                        "<span style=\"font-weight:bold\">Recruits:</span> 0 " +
                        "<span style=\"font-weight:bold\">TOTAL:</span> 1 " +

                        "<span style=\"font-weight:bold\">Age:</span> " + faction.getFormattedCreationDate() +
                        "<span style=\"font-weight:bold\"> Bank:</span> " + faction.getBank().getAmount() +
                        "<span style=\"font-weight:bold\"><br>Flags: </span> " +
                        "<span style=\"color:#800000\">open</span> " +
                        "| <span style=\"color:#008000\">monsters</span> " +
                        "| <span style=\"color:#008000\">animals</span> " +
                        "| <span style=\"color:#008000\">pvp</span>"
                );
    }


    public String getMemberInfo(Faction faction) {
        if (faction.isSystemFaction()) {
            return "<br><span style=\"font-weight:bold\">System Faction</span><br>";
        }
        return "string";
    }

    public void shutdown() {
        if (dynmapAPI.getMarkerAPI().getMarkerSet("factionsx") != null) {
            dynmapAPI.getMarkerAPI().getMarkerSet("factionsx").deleteMarkerSet();
        }
        markerSet = null;
        markerAPI = null;
        dynmapAPI = null;
        logColored("FXBD Engine disabled.");
    }
}
