package net.donut.fxbetterdynmap;

import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.FactionManager;
import net.prosavage.factionsx.persist.data.FLocation;
import net.prosavage.factionsx.addonframework.Addon;
import net.prosavage.factionsx.util.Coordinate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Chunk;
import org.bukkit.plugin.Plugin;

import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;

import java.lang.reflect.Array;
import java.util.*;

import static net.prosavage.factionsx.util.UtilKt.getFPlayer;
import static net.prosavage.factionsx.util.UtilKt.logColored;

// Built for GeoLegacy.xyz by ChocolateDonut_
// June 2021

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

    public void refreshHomes() {

    }

    public void handleFaction(Faction faction) {
        logColored("Handling faction: " + faction);
        GridManager gridManager = GridManager.INSTANCE;
        FactionManager factionManager = FactionManager.INSTANCE;
        Set<FLocation> allChunks = gridManager.getAllClaims(faction);
        double[] finalXs = {10, 0, 10, 0};
        double[] finalYs = {0, 10, 0, 10};

        for (FLocation chunk : allChunks) {
            logColored("Handling Chunk: " + chunk);
            int chunkX = (int) chunk.getX();
            int chunkZ = (int) chunk.getZ();

            Location zero = new Location(chunk.getChunk().getWorld(), 0, 0, 0);

            Location topLeftLocation = new Location(chunk.getChunk().getWorld(), (chunkX * 16), 0, (chunkZ * 16));
            Location topRightLocation = new Location(chunk.getChunk().getWorld(), (chunkX * 16 - 16), 0, (chunkZ * 16));
            Location bottomLeftLocation = new Location(chunk.getChunk().getWorld(), (chunkX * 16), 0, (chunkZ * 16 - 16));
            Location bottomRightLocation = new Location(chunk.getChunk().getWorld(), (chunkX * 16 - 16), 0, (chunkZ * 16 - 16));

            boolean isHorizontalAClaim = false;
            boolean isVerticalAClaim = false;
            boolean isDiagonalAClaim = false;

            // Get Bukkit chunk from FLocation chunk.
            Chunk myChunk = chunk.getChunk();

            // Iterate through every corner of a chunk.
            for (int i = 1; i <= 4; i++) {

                logColored("Handling corner "+i+" of chunk.");

                // Create cornerLocation and testLocation and set it to 0, 0, 0 object.
                Location cornerLocation = zero;
                Location testLocation = zero;

                // Create variables for determining if the corner is a corner.
                isDiagonalAClaim = false;
                isVerticalAClaim = false;
                isHorizontalAClaim = false;

                // Which corner are we working with?
                // Depending on which one it is, set it to a certain corner's location.
                switch (i) {
                    case 1:
                        cornerLocation = topLeftLocation.clone();
                        break;
                    case 2:
                        cornerLocation = topRightLocation.clone();
                        break;
                    case 3:
                        cornerLocation = bottomLeftLocation.clone();
                        break;
                    case 4:
                        cornerLocation = bottomRightLocation.clone();
                        break;
                }

                logColored("Checking surrounding chunks of corner: " +
                        cornerLocation + "in chunk: " + myChunk + " of corner type: " + i);

                if (i == 1 || i == 3) {
                    testLocation.setX((cornerLocation.clone().getX() - 1));
                    testLocation.setZ((cornerLocation.clone().getZ()));
                    if (testLocation.getChunk() == myChunk) {
                        logColored("Ignoring. This is the same chunk");
                    }
                    else {
                        logColored("Setting horizontal to true");
                        isHorizontalAClaim = true;
                    }
                }

                if (i == 1) {
                    testLocation.setX((cornerLocation.clone().getX() - 1));
                    testLocation.setZ((cornerLocation.clone().getZ() - 1));
                    if (testLocation.getChunk() == myChunk) {
                        logColored("Ignoring. This is the same chunk");
                    }
                    else {
                        logColored("Setting diagonal to true");
                        isDiagonalAClaim = true;
                    }
                }

                if (i == 1 || i == 2) {
                    testLocation.setX((cornerLocation.clone().getX()));
                    testLocation.setZ((cornerLocation.clone().getZ() - 1));
                    if (testLocation.getChunk() == myChunk) {
                        logColored("Ignoring. This is the same chunk");
                    }
                    else {
                        logColored("Setting vertical to true");
                        isVerticalAClaim = true;
                    }
                }

                if (i == 2) {
                    testLocation.setX((cornerLocation.clone().getX() + 1));
                    testLocation.setZ((cornerLocation.clone().getZ() - 1));
                    if (testLocation.getChunk() == myChunk) {
                        logColored("Ignoring. This is the same chunk");
                    }
                    else {
                        logColored("Setting diagonal to true");
                        isDiagonalAClaim = true;
                    }
                }

                if (i == 2 || i == 4) {
                    testLocation.setX((cornerLocation.clone().getX() + 1));
                    testLocation.setZ((cornerLocation.clone().getZ()));
                    if (testLocation.getChunk() == myChunk) {
                        logColored("Ignoring. This is the same chunk");
                    }
                    else {
                        logColored("Setting horizontal to true");
                        isHorizontalAClaim = true;
                    }
                }

                if (i == 4) {
                    testLocation.setX((cornerLocation.clone().getX() + 1));
                    testLocation.setZ((cornerLocation.clone().getZ() + 1));
                    if (testLocation.getChunk() == myChunk) {
                        logColored("Ignoring. This is the same chunk");
                    }
                    else {
                        logColored("Setting diagonal to true");
                        isDiagonalAClaim = true;
                    }
                }

                if (i == 3 || i == 4) {
                    testLocation.setX((cornerLocation.clone().getX()));
                    testLocation.setZ((cornerLocation.clone().getZ() + 1));
                    if (testLocation.getChunk() == myChunk) {
                        logColored("Ignoring. This is the same chunk");
                    }
                    else {
                        logColored("Setting vertical to true");
                        isVerticalAClaim = true;
                    }
                }

                if (i == 3) {
                    testLocation.setX((cornerLocation.clone().getX() - 1));
                    testLocation.setZ((cornerLocation.clone().getZ() + 1));
                    if (testLocation.getChunk() == myChunk) {
                        logColored("Ignoring. This is the same chunk");
                    }
                    else {
                        logColored("Setting diagonal to true");
                        isDiagonalAClaim = true;
                    }
                }

                if (isDiagonalAClaim && isVerticalAClaim && isHorizontalAClaim) {
                    // false
                } else if (!isDiagonalAClaim && isVerticalAClaim && isHorizontalAClaim) {
                    // true
                } else if (!isDiagonalAClaim && isVerticalAClaim && !isHorizontalAClaim) {
                    // false
                } else if (!isDiagonalAClaim && !isVerticalAClaim && isHorizontalAClaim) {
                    // false
                } else if (!isDiagonalAClaim && !isVerticalAClaim && !isHorizontalAClaim) {
                    // true
                }

                logColored(isDiagonalAClaim+" "+isVerticalAClaim+" "+isHorizontalAClaim);

            }
        }

        // Create an area marker with the faction's ID for area marker ID and faction tag for label
        AreaMarker areaMarker = markerSet.findAreaMarker(String.valueOf(faction.getId()));
        if (areaMarker == null) {
             areaMarker = markerSet.createAreaMarker(String.valueOf(faction.getId()), faction.getTag().replaceAll("&[a-zA-Z1-9]", ""),
                    true, Bukkit.getWorld("world").getName(), new double[1000], new double[1000], false);
             if (areaMarker == null) {
                 logColored("Error creating area marker for faction "+ faction.getTag().replaceAll("&[a-zA-Z1-9]", ""));
                 return;
             }
        }
        areaMarker.setCornerLocations(finalXs, finalYs);
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

    }
}
