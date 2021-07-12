package net.donut.fxbetterdynmap;

import net.prosavage.factionsx.FactionsX;
import net.prosavage.factionsx.addonframework.Addon;
import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.manager.PlayerManager;
import net.prosavage.factionsx.persist.data.Factions;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class FXBetterDynmap extends Addon {

    private static FXBetterDynmap instance;
    public static FXBetterDynmap getInstance() {
        return instance;
        // what???
        
        /* Note by Jack: this code block is creating an instance of the FXBetterDynamp class inside itself on line 12. It probably shouldn't do this because it is recursively 
        *  creating instances an infinite amount of times as is. Line 13 is a list of datatype FPlayer called onlineFPlayers and is fetching the set of online players with getOnlineFPlayers().
        *  public static FXBetterDynmap getInstance() {return instance} is necessary because the object defined in line 12 is private meaning it cannot be accesed directly from 
        *  external classes and must have a public method to fetch or "get" it, hence the name getInstance. 
        */
    }
    
    //Dynmap color object initialized as a new Dynmap color object
    private static DynmapColor dynmapColor = new DynmapColor();

    FXBetterDynmapEngine fxBetterDynmapEngine = FXBetterDynmapEngine.getInstance();
    FXBDEventListener fxbdEventListener = FXBDEventListener.getInstance();

    @Override
    protected void onEnable() {
        logColored("Initializing BetterDynmap for FactionsX");
        instance = this;
        fxBetterDynmapEngine.init();
        fxbdEventListener.registerEvents(FactionsX.instance);
        FactionsX.baseCommand.addSubCommand(dynmapColor);
        Config.load(this);
    }

    @Override
    protected void onDisable() {
        logColored("Disabling BetterDynmap for FactionsX");
        FactionsX.baseCommand.removeSubCommand(dynmapColor);
        fxBetterDynmapEngine.shutdown();

        // Load first to read changes from file, then save.
        Config.load(this);
        Config.save(this);
    }


    //How do i attach this to a GameObject
    // this isnt working

}
