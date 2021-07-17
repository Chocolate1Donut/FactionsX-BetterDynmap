package net.donut.fxbetterdynmap;

import net.prosavage.factionsx.FactionsX;
import org.bukkit.scheduler.BukkitRunnable;

import static net.prosavage.factionsx.util.UtilKt.logColored;

public class FXBDRefreshDynmapTask extends BukkitRunnable {

    FactionsX plugin;
    FXBDEngine fxbdEngine = FXBDEngine.getInstance();

    public FXBDRefreshDynmapTask(FactionsX plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        fxbdEngine.refreshHomes();
    }
}