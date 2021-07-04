package net.donut.fxbetterdynmap;

import com.google.common.collect.ImmutableMap;
import net.donut.dynmaplibrary.DynmapStyle;
import net.prosavage.factionsx.FactionsX;
import net.prosavage.factionsx.addonframework.Addon;

import java.io.File;
import java.util.*;

public class Conf {

    public static transient Conf instance = new Conf();

    public static String coolConfigOption = "yeah boiii";
    public static String funnyWordToSayOnCommand = "funny word!";

    public static String dynmapColorHelpInfo = "Changes your faction's color on the dynmap.";
    public static String dynmapColorPermissonDenied = "Permisson Denied.";

    // Region Style
    public static final transient String DYNMAP_STYLE_LINE_COLOR = "#00FF00";
    public static final transient double DYNMAP_STYLE_LINE_OPACITY = 0.8D;
    public static final transient int DYNMAP_STYLE_LINE_WEIGHT = 3;
    public static final transient String DYNMAP_STYLE_FILL_COLOR = "#00FF00";
    public static final transient double DYNMAP_STYLE_FILL_OPACITY = 0.35D;
    public static final transient String DYNMAP_STYLE_HOME_MARKER = "greenflag";
    public static final transient boolean DYNMAP_STYLE_BOOST = false;
    public static List<String> baseCommandAliases = new ArrayList<>();
    public static String serverTimeZone = "EST";
    public static boolean allowNoSlashCommand = true;

    // Should the dynmap intagration be used?
    public static boolean dynmapUse = false;
    // Name of the Factions layer
    public static String dynmapLayerName = "Factions";
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
    // Allow players in faction to see one another on Dynmap (only relevant if Dynmap has 'player-info-protected' enabled)
    public static boolean dynmapVisibilityByFaction = true;
    // Optional setting to limit which regions to show.
    // If empty all regions are shown.
    // Specify Faction either by name or UUID.
    // To show all regions on a given world, add 'world:<worldname>' to the list.
    public static Set<String> dynmapVisibleFactions = new HashSet<>();
    // Optional setting to hide specific Factions.
    // Specify Faction either by name or UUID.
    // To hide all regions on a given world, add 'world:<worldname>' to the list.
    public static Set<String> dynmapHiddenFactions = new HashSet<>();
    public static DynmapStyle dynmapDefaultStyle = new DynmapStyle()
            .setStrokeColor(DYNMAP_STYLE_LINE_COLOR)
            .setLineOpacity(DYNMAP_STYLE_LINE_OPACITY)
            .setLineWeight(DYNMAP_STYLE_LINE_WEIGHT)
            .setFillColor(DYNMAP_STYLE_FILL_COLOR)
            .setFillOpacity(DYNMAP_STYLE_FILL_OPACITY)
            .setHomeMarker(DYNMAP_STYLE_HOME_MARKER)
            .setBoost(DYNMAP_STYLE_BOOST);

    // Optional per Faction style overrides. Any defined replace those in dynmapDefaultStyle.
    // Specify Faction either by name or UUID.
    public static Map<String, DynmapStyle> dynmapFactionStyles = ImmutableMap.of(
            "SafeZone", new DynmapStyle().setStrokeColor("#FF00FF").setFillColor("#FF00FF").setBoost(false),
            "WarZone", new DynmapStyle().setStrokeColor("#FF0000").setFillColor("#FF0000").setBoost(false)
    );


    public static void save(Addon addon) {
        addon.getConfigSerializer().save(instance,
                new File(FXBetterDynmap.getInstance().getAddonDataFolder(), "config.json")
        );
    }

    public static void load(Addon addon) {
        FactionsX.baseCommand.getHelpInfo();
        addon.getConfigSerializer().load(instance,
                Conf.class,
                new File(FXBetterDynmap.getInstance().getAddonDataFolder(), "config.json")
        );
    }
}
