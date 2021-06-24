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

    private static CmdTest cmdTest = new CmdTest();
    private static MarkMe markMe = new MarkMe();
    private static DynmapColor dynmapColor = new DynmapColor();


    @Override
    protected void onEnable() {
        logColored("Initializing BetterDynmap for FactionsX");
        instance = this;
        FactionsX.baseCommand.addSubCommand(cmdTest);
        FactionsX.baseCommand.addSubCommand(markMe);
        FactionsX.baseCommand.addSubCommand(dynmapColor);
        Conf.load(this);
    }

    @Override
    protected void onDisable() {
        logColored("Disabling BetterDynmap for FactionsX");
        FactionsX.baseCommand.removeSubCommand(cmdTest);
        FactionsX.baseCommand.removeSubCommand(markMe);
        FactionsX.baseCommand.removeSubCommand(dynmapColor);
        // this is actually a really cool api
        // Load first to read changes from file, then save.
        Conf.load(this);
        Conf.save(this);
    }

    //How do i attach this to a GameObject
    // this isnt working

}
