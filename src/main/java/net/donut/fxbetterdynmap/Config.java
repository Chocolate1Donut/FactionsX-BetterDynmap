package net.donut.fxbetterdynmap;

import net.prosavage.factionsx.FactionsX;
import net.prosavage.factionsx.addonframework.Addon;

import java.io.File;

public class Config {

    public static transient Config instance = new Config();

    // Region Style
    public static final transient String styleLineColor = "#00FF00";
    public static final transient double styleLineOpacity = 0.8D;
    public static final transient int styleLineWeight = 3;
    public static final transient String styleFillColor = "#00FF00";
    public static final transient double styleFillOpacity = 0.35D;
    public static final transient String homeIcon = "greenflag";
    public static final transient String warpIcon = "greenflag";
    public static final transient boolean boostResolution = false;

    public static String dynmapColorPermissionNode = "factionsx.betterdynmap.color";
    public static String dynmapColorHelpInfo = "Changes your faction's color on the dynmap.";
    public static String dynmapColorPermissonDenied = "Permisson Denied.";

    // Should the dynmap intagration be used?
    public static boolean dynmapUse = false;
    // Name of the Factions layer
    public static String dynmapLayerName = "FactionsXTest";
    // Should the layer be visible per default
    public static boolean dynmapLayerVisible = true;
    // Ordering priority in layer menu (low goes before high - default is 0)
    public static int dynmapLayerPriority = 2;
    // (optional) set minimum zoom level before layer is visible (0 = default, always visible)
    public static int dynmapLayerMinimumZoom = 0;
    // Format for popup - substitute values for macros
    public static String dynmapDescription =
            "<div class=\"infowindow\">\n"
                    + "<span style=\"font-weight: bold; font-size: 150%;\">%name%</span><br>\n"
                    + "<span style=\"font-style: italic; font-size: 110%;\">%description%</span><br>"
                    + "<br>\n"
                    + "<span style=\"font-weight: bold;\">Leader:</span> %players.leader%<br>\n"
                    + "<span style=\"font-weight: bold;\">Admins:</span> %players.admins.count%<br>\n"
                    + "<span style=\"font-weight: bold;\">Moderators:</span> %players.moderators.count%<br>\n"
                    + "<span style=\"font-weight: bold;\">Members:</span> %players.normals.count%<br>\n"
                    + "<span style=\"font-weight: bold;\">TOTAL:</span> %players.count%<br>\n"
                    + "</br>\n"
                    + "<span style=\"font-weight: bold;\">Bank:</span> %money%<br>\n"
                    + "<br>\n"
                    + "</div>";
    // Enable the %money% macro. Only do this if you know your economy manager is thread-safe.
    public static boolean dynmapDescriptionMoney = false;

    public static void save(Addon addon) {
        addon.getConfigSerializer().save(instance,
                new File(FXBetterDynmap.getInstance().getAddonDataFolder(), "config.json")
        );
    }

    public static void load(Addon addon) {
        FactionsX.baseCommand.getHelpInfo();
        addon.getConfigSerializer().load(instance,
                Config.class,
                new File(FXBetterDynmap.getInstance().getAddonDataFolder(), "config.json")
        );
    }
}
