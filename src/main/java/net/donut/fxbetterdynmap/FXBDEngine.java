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

import java.util.*;

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
                }
                else {
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
                }
                else {
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

        // First, we'll figure out which corners we need to know about.
        ArrayList<Location> corners = null;

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

            // We use these to determine if the corner is one we should care about.
            boolean isHorizontalAClaim = false;
            boolean isVerticalAClaim = false;
            boolean isDiagonalAClaim = false;

            // Get Bukkit chunk from FLocation chunk.
            Chunk bukkitChunk = chunk.getChunk();

            // Iterate through every corner of a chunk.
            for (int i = 1; i <= 4; i++) {

                //logColored("Handling corner " + i + " of chunk.");

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

                //logColored("Checking surrounding chunks of corner: " +
                //        cornerLocation + "in chunk: " + bukkitChunk + " of corner type: " + i);

                if (i == 1 || i == 4) {
                    testLocation.setX((cornerLocation.clone().getX() - 1));
                    testLocation.setZ((cornerLocation.clone().getZ()));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isHorizontalAClaim = true;
                    } else {
                        //logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 1) {
                    testLocation.setX((cornerLocation.clone().getX() - 1));
                    testLocation.setZ((cornerLocation.clone().getZ() - 1));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isDiagonalAClaim = true;
                    } else {
                        //logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 1 || i == 2) {
                    testLocation.setX((cornerLocation.clone().getX()));
                    testLocation.setZ((cornerLocation.clone().getZ() - 1));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isVerticalAClaim = true;
                    } else {
                        //logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 2) {
                    testLocation.setX((cornerLocation.clone().getX() + 1));
                    testLocation.setZ((cornerLocation.clone().getZ() - 1));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isDiagonalAClaim = true;
                    } else {
                        //logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 2 || i == 3) {
                    testLocation.setX((cornerLocation.clone().getX() + 1));
                    testLocation.setZ((cornerLocation.clone().getZ()));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isHorizontalAClaim = true;
                    } else {
                        //logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 3) {
                    testLocation.setX((cornerLocation.clone().getX() + 1));
                    testLocation.setZ((cornerLocation.clone().getZ() + 1));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isDiagonalAClaim = true;
                    } else {
                        //logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 4 || i == 3) {
                    testLocation.setX((cornerLocation.clone().getX()));
                    testLocation.setZ((cornerLocation.clone().getZ() + 1));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isVerticalAClaim = true;
                    } else {
                        //logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (i == 4) {
                    testLocation.setX((cornerLocation.clone().getX() - 1));
                    testLocation.setZ((cornerLocation.clone().getZ() + 1));
                    testLocationChunk = testLocation.getChunk();
                    if (gridManager.getFactionAt(testLocationChunk) == faction) {
                        isDiagonalAClaim = true;
                    } else {
                        //logColored("Adjacent claim was not part of my faction.");
                    }
                }

                if (isDiagonalAClaim && isVerticalAClaim && isHorizontalAClaim) {
                    // Do nothing because its not a corner.
                } else if (!isDiagonalAClaim && isVerticalAClaim && isHorizontalAClaim) {
                    if (corners == null) {
                        corners = new ArrayList<>(Set.of(cornerLocation));
                    }
                    else {
                        corners.add(cornerLocation);
                    }
                } else if (!isDiagonalAClaim && isVerticalAClaim && !isHorizontalAClaim) {

                } else if (!isDiagonalAClaim && !isVerticalAClaim && isHorizontalAClaim) {

                } else if (!isDiagonalAClaim && !isVerticalAClaim && !isHorizontalAClaim) {
                   if (corners == null) {
                       //logColored("List of corners is null, so lets set it to something first.");
                       corners = new ArrayList<>(Set.of(cornerLocation));
                   }
                   else {
                       //logColored("List of corners already has stuff in it, so lets add to it.");
                       corners.add(cornerLocation);
                   }
                }

                //logColored("D: " + isDiagonalAClaim + " V: " + isVerticalAClaim + " H: " + isHorizontalAClaim);

            }
        }

        //logColored("corners: "+corners);
        // Does the faction even have any claims?
        if (corners != null) {
            // Now that we have all the actual corners of our faction, we want to
            // make sure they are in the right order, because if they are not it looks
            // like a complete clusterfuck.

            // Note: this is stolen from StackOverflow LOL

            orderedPoints.add(corners.remove(0)); //Arbitrary starting point

            while (corners.size() > 0) {
                //Find the index of the closest point (using another method)
                int nearestIndex=findNearestIndex(orderedPoints.get(orderedPoints.size()-1), corners);

                //Remove from the unorderedList and add to the ordered one
                orderedPoints.add(corners.remove(nearestIndex));
            }

            for (Location corner : orderedPoints) {
                finalXs.add(corner.getX());
                finalZs.add(corner.getZ());
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
        else {
            logColored(faction + " doesn't have any claims to display.");
        }
    }


void Test() {
    ArrayList<Location> originalPoints = new ArrayList<Location>();
    ArrayList<Location> orderedPoints = new ArrayList<Location>();

    orderedPoints.add(originalPoints.remove(0)); //Arbitrary starting point

    while (originalPoints.size() > 0) {
        //Find the index of the closest point (using another method)
        int nearestIndex=findNearestIndex(orderedPoints.get(orderedPoints.size()-1), originalPoints);

        //Remove from the unorderedList and add to the ordered one
        orderedPoints.add(originalPoints.remove(nearestIndex));
    }
}

    int findNearestIndex (Location thisPoint, ArrayList<Location> listToSearch) {
        double nearestDistSquared=Double.POSITIVE_INFINITY;
        int nearestIndex = 9999;
        for (int i=0; i< listToSearch.size(); i++) {
            Location point2 = listToSearch.get(i);
            double distsq = (thisPoint.getX() - point2.getX())*(thisPoint.getX() - point2.getX())
                    + (thisPoint.getZ() - point2.getZ())*(thisPoint.getZ() - point2.getZ());
            if(distsq < nearestDistSquared) {
                nearestDistSquared = distsq;
                nearestIndex=i;
            }
        }
        return nearestIndex;
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
        if (dynmapAPI.getMarkerAPI().getMarkerSet("factionsx") != null) {
            dynmapAPI.getMarkerAPI().getMarkerSet("factionsx").deleteMarkerSet();
        }
        markerSet = null;
        markerAPI = null;
        dynmapAPI = null;
        logColored("FXBD Engine disabled.");
    }
}
