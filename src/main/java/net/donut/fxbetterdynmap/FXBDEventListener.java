package net.donut.fxbetterdynmap;

import net.prosavage.factionsx.FactionsX;
import net.prosavage.factionsx.event.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

// Listens for events that FactionsX sends to update the map
public class FXBDEventListener implements Listener {

    FXBetterDynmapEngine fxbdEngine = FXBetterDynmapEngine.getInstance();

    public void registerEvents(FactionsX factionsX) {
        factionsX.getServer().getPluginManager().registerEvents(this, factionsX);
    }

    @EventHandler
    public void FactionUnClaimEvent(FactionUnClaimEvent event) {
        fxbdEngine.refreshClaims();
    }

    @EventHandler
    public void FactionClaimEvent(FactionPreClaimEvent event) {
        fxbdEngine.refreshClaims();
    }
}
