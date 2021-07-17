package net.donut.fxbetterdynmap;

import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.FactionManager;
import net.prosavage.factionsx.persist.data.FLocation;
import net.prosavage.factionsx.addonframework.Addon;
import net.prosavage.factionsx.persist.data.wrappers.DataLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Chunk;
import org.bukkit.plugin.Plugin;

import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;

import java.util.*;

import static net.prosavage.factionsx.util.UtilKt.logColored;

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
        markerSet = dynmapAPI.getMarkerAPI().createMarkerSet
                ("factionsx", Config.dynmapLayerName, markerAPI.getMarkerIcons(), false);
        //refreshClaims();
        refreshHomes();
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
            DataLocation homeLocation = faction.getHome();

        }
    }

    public void refreshHomes() {
        GridManager gridManager = GridManager.INSTANCE;
        FactionManager factionManager = FactionManager.INSTANCE;
        Set<Faction> allFactions = FactionManager.INSTANCE.getFactions();
        // For all factions, handle a faction.
        for (Faction faction : allFactions) {
            DataLocation homeLocation = faction.getHome();
            // Does homeLocation exist for this faction?
            if (homeLocation != null) {
                // Is markerSet null for some reason?
                if (markerSet == null) {
                    // Try and reinitialize. If it doesn't work, push an error.
                    if (!init()) {
                        logColored("Failed to initialize FactionsX-BetterDynmap.");
                        break;
                    }
                    // If it does reinitialize, try refreshing homes again.
                    else {
                        refreshHomes();
                    }
                }
                if (markerSet.findMarker(faction.getTag().replaceAll("&[a-zA-Z1-9]", "")) == null) {
                    markerSet.createMarker(faction.getTag().replaceAll("&[a-zA-Z1-9]", ""), faction.getTag().replaceAll("&[a-zA-Z1-9]", ""),
                            homeLocation.getWorldName(), homeLocation.getX(), homeLocation.getY(), homeLocation.getZ(), markerAPI.getMarkerIcon("greenflag"), false);
                }
                else {
                    markerSet.findMarker((faction.getTag().replaceAll("&[a-zA-Z1-9]", ""))).setLocation(homeLocation.getWorldName(), homeLocation.getX(),
                            homeLocation.getY(), homeLocation.getZ());
                }
            }
            else {
                continue;
            }
        }
    }

    public void removeHome(String markerid){
        if (markerid == null) {
            logColored("Tried to delete an f home marker but the faction name came back null. Oh well.");
        }
        else {
            markerSet.findMarker(markerid).deleteMarker();
        }
    }

    public void handleFaction(Faction faction) {
        logColored("Drawing faction: " + faction.getTag().replaceAll("&[a-zA-Z1-9]", ""));

        // Get GridManager and FactionManager so we can ask them stuff.
        GridManager gridManager = GridManager.INSTANCE;
        FactionManager factionManager = FactionManager.INSTANCE;

        // Get all claims owned by the faction
        Set<FLocation> allChunks = gridManager.getAllClaims(faction);

        // This is where we will store all our corners. We'll
        // make this null for now.
        ArrayList<Double> finalXs = null;
        ArrayList<Double> finalZs = null;

        Set<Location> corners = null;

        for (FLocation chunk : allChunks) {
            logColored("Handling Chunk: " + chunk);
            int chunkX = (int) chunk.getX();
            int chunkZ = (int) chunk.getZ();

            Location zero = new Location(chunk.getChunk().getWorld(), 0, 0, 0);

            Location topLeftLocation = new Location(chunk.getChunk().getWorld(), (chunkX * 16), 0, (chunkZ * 16));
            Location topRightLocation = new Location(chunk.getChunk().getWorld(), (chunkX * 16 + 15), 0, (chunkZ * 16));
            Location bottomLeftLocation = new Location(chunk.getChunk().getWorld(), (chunkX * 16), 0, (chunkZ * 16 + 15));
            Location bottomRightLocation = new Location(chunk.getChunk().getWorld(), (chunkX * 16 + 15), 0, (chunkZ * 16 + 15));

            boolean isHorizontalAClaim = false;
            boolean isVerticalAClaim = false;
            boolean isDiagonalAClaim = false;

            // Get Bukkit chunk from FLocation chunk.
            Chunk myChunk = chunk.getChunk();

            // Iterate through every corner of a chunk.
            for (int i = 1; i <= 4; i++) {

                logColored("Handling corner " + i + " of chunk.");

                // Create cornerLocation and testLocation and set it to 0, 0, 0 object.
                Location cornerLocation = zero;
                Location testLocation = zero;
                Chunk testLocationChunk = zero.getChunk();

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
                        cornerLocation = bottomRightLocation.clone();
                        break;
                    case 4:
                        cornerLocation = bottomLeftLocation.clone();
                        break;
                }

                logColored("Checking surrounding chunks of corner: " +
                        cornerLocation + "in chunk: " + myChunk + " of corner type: " + i);

                if (i == 1 || i == 4) {
                    testLocation.setX((cornerLocation.clone().getX() - 1));
                    testLocation.setZ((cornerLocation.clone().getZ()));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isHorizontalAClaim = true;
                    } else {
                        logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 1) {
                    testLocation.setX((cornerLocation.clone().getX() - 1));
                    testLocation.setZ((cornerLocation.clone().getZ() - 1));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isDiagonalAClaim = true;
                    } else {
                        logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 1 || i == 2) {
                    testLocation.setX((cornerLocation.clone().getX()));
                    testLocation.setZ((cornerLocation.clone().getZ() - 1));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isVerticalAClaim = true;
                    } else {
                        logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 2) {
                    testLocation.setX((cornerLocation.clone().getX() + 1));
                    testLocation.setZ((cornerLocation.clone().getZ() - 1));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isDiagonalAClaim = true;
                    } else {
                        logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 2 || i == 3) {
                    testLocation.setX((cornerLocation.clone().getX() + 1));
                    testLocation.setZ((cornerLocation.clone().getZ()));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isHorizontalAClaim = true;
                    } else {
                        logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 3) {
                    testLocation.setX((cornerLocation.clone().getX() + 1));
                    testLocation.setZ((cornerLocation.clone().getZ() + 1));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isDiagonalAClaim = true;
                    } else {
                        logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 4 || i == 3) {
                    testLocation.setX((cornerLocation.clone().getX()));
                    testLocation.setZ((cornerLocation.clone().getZ() + 1));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isVerticalAClaim = true;
                    } else {
                        logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 4) {
                    testLocation.setX((cornerLocation.clone().getX() - 1));
                    testLocation.setZ((cornerLocation.clone().getZ() + 1));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isDiagonalAClaim = true;
                    } else {
                        logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (isDiagonalAClaim && isVerticalAClaim && isHorizontalAClaim) {
                    // Do nothing because its not a corner.
                } else if (!isDiagonalAClaim && isVerticalAClaim && isHorizontalAClaim) {
                    if (corners == null) {
                        corners = new HashSet<Location>(Set.of(cornerLocation));
                    }
                    else {
                        corners.add(cornerLocation);
                    }
                } else if (!isDiagonalAClaim && isVerticalAClaim && !isHorizontalAClaim) {

                } else if (!isDiagonalAClaim && !isVerticalAClaim && isHorizontalAClaim) {

                } else if (!isDiagonalAClaim && !isVerticalAClaim && !isHorizontalAClaim) {
                   if (corners == null) {
                       logColored("List of corners is null, so lets set it to something first.");
                       corners = new HashSet<Location>(Set.of(cornerLocation));
                   }
                   else {
                       logColored("List of corners already has stuff in it, so lets add to it.");
                       corners.add(cornerLocation);
                   }
                }

                logColored("D: " + isDiagonalAClaim + " V: " + isVerticalAClaim + " H: " + isHorizontalAClaim);

            }
        }

        // Now that we have all the actual corners of our faction, we want to
        // make sure they are in the right order, because if they are not it looks
        // like a complete clusterfuck.

        Set<Location> sortedCorners = null;
        logColored("Here is our list of corners, unsorted: " +corners);
        if (corners != null) {
            for (Location corner : corners) {
                Location testLocation = corner.clone();
                testLocation.setX(corner.clone().getX());
                testLocation.setZ(corner.clone().getZ() - 16);
                logColored("Checking 16 blocks north at "+testLocation+" from "+corner);
                if (corners.contains(testLocation)) {
                    if ((finalXs == null) || (finalZs == null)) {
                        logColored("The final x or z array is null, so lets first set their values to testLocation");
                        finalXs = new ArrayList<Double>(List.of(testLocation.getX()));
                        finalZs = new ArrayList<Double>(List.of(testLocation.getZ()));
                    } else {
                        logColored("The final x or z array already has stuff in it, so add onto it.");
                        logColored("Before: " + finalXs + " " + finalZs);
                        finalXs.add(testLocation.getX());
                        finalZs.add(testLocation.getZ());
                        logColored("After: " + finalXs + " " + finalZs);
                    }
                    break;
                }
                testLocation.setX(corner.clone().getX() + 16);
                testLocation.setZ(corner.clone().getZ());
                logColored("Checking 16 blocks east at "+testLocation+" from "+corner);
                if (corners.contains(testLocation)) {
                    if ((finalXs == null) || (finalZs == null)) {
                        logColored("The final x or z array is null, so lets first set their values to testLocation");
                        finalXs = new ArrayList<Double>(List.of(testLocation.getX()));
                        finalZs = new ArrayList<Double>(List.of(testLocation.getZ()));
                    } else {
                        logColored("The final x or z array already has stuff in it, so add onto it.");
                        logColored("Before: " + finalXs + " " + finalZs);
                        finalXs.add(testLocation.getX());
                        finalZs.add(testLocation.getZ());
                        logColored("After: " + finalXs + " " + finalZs);
                    }
                    break;
                }
                testLocation.setX(corner.clone().getX());
                testLocation.setZ(corner.clone().getZ() + 16);
                logColored("Checking 16 blocks south at "+testLocation+" from "+corner);
                if (corners.contains(testLocation)) {
                    if ((finalXs == null) || (finalZs == null)) {
                        logColored("The final x or z array is null, so lets first set their values to testLocation");
                        finalXs = new ArrayList<Double>(List.of(testLocation.getX()));
                        finalZs = new ArrayList<Double>(List.of(testLocation.getZ()));
                    } else {
                        logColored("The final x or z array already has stuff in it, so add onto it.");
                        logColored("Before: " + finalXs + " " + finalZs);
                        finalXs.add(testLocation.getX());
                        finalZs.add(testLocation.getZ());
                        logColored("After: " + finalXs + " " + finalZs);
                    }
                    break;
                }
                testLocation.setX(corner.clone().getX() - 16);
                testLocation.setZ(corner.clone().getZ());
                logColored("Checking 16 blocks west at "+testLocation+" from "+corner);
                if (corners.contains(testLocation)) {
                    if ((finalXs == null) || (finalZs == null)) {
                        logColored("The final x or z array is null, so lets first set their values to testLocation");
                        finalXs = new ArrayList<Double>(List.of(testLocation.getX()));
                        finalZs = new ArrayList<Double>(List.of(testLocation.getZ()));
                    } else {
                        logColored("The final x or z array already has stuff in it, so add onto it.");
                        logColored("Before: " + finalXs + " " + finalZs);
                        finalXs.add(testLocation.getX());
                        finalZs.add(testLocation.getZ());
                        logColored("After: " + finalXs + " " + finalZs);
                    }
                    break;
                }
            }
        }


        if ((finalXs == null) && (finalZs == null)) {
            logColored(faction + " doesn't have any claims to display.");
        } else {
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
            double[] finalXArray = finalXs.stream().mapToDouble(d -> d).toArray(); //identity function, Java unboxes automatically to get the double value
            //double[] finalZArray = finalZs.stream().mapToDouble(Double::doubleValue).toArray(); //via method reference
            double[] finalZArray = finalZs.stream().mapToDouble(d -> d).toArray(); //identity function, Java unboxes automatically to get the double value
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
    }

    // Function to add x in arr
    public static double[] addToArray(int n, double[] arr, double x) {
        int i;

        // create a new array of size n+1
        double[] newarr = new double[n + 1];

        // insert the elements from
        // the old array into the new array
        // insert all elements till n
        // then insert x at n+1
        for (i = 0; i < n; i++)
            newarr[i] = arr[i];

        newarr[n] = x;

        return newarr;
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
