package net.donut.fxbetterdynmap;

import net.prosavage.factionsx.FactionsX;
import net.prosavage.factionsx.addonframework.Addon;
import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.manager.PlayerManager;

import java.util.List;

public class FXBetterDynmap extends Addon {

    private static FXBetterDynmap instance;
    public List<FPlayer> onlineFPlayers = PlayerManager.INSTANCE.getOnlineFPlayers();
    public static FXBetterDynmap getInstance() {
        return instance;
        // what???
    }
    private static DynmapColor dynmapColor = new DynmapColor();
    private static DynmapTestCommand dynmapTestCommand = new DynmapTestCommand();

    @Override
    protected void onEnable() {
        logColored("Initializing BetterDynmap for FactionsX");
        instance = this;
        FactionsX.baseCommand.addSubCommand(dynmapColor);
        FactionsX.baseCommand.addSubCommand(dynmapTestCommand);
        Conf.load(this);
    }

    @Override
    protected void onDisable() {
        logColored("Disabling BetterDynmap for FactionsX");
        FactionsX.baseCommand.removeSubCommand(dynmapColor);
        FactionsX.baseCommand.removeSubCommand(dynmapTestCommand);
        // this is actually a really cool api
        // Load first to read changes from file, then save.
        Conf.load(this);
        Conf.save(this);
    }

    //How do i attach this to a GameObject
    // this isnt working

}
