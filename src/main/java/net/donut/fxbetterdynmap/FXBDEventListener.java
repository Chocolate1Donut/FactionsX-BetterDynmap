package net.donut.fxbetterdynmap;

import net.prosavage.factionsx.FactionsX;
import net.prosavage.factionsx.event.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;


// Listens for events that FactionsX sends to update the map
public class FXBDEventListener implements Listener {

    private static FXBDEventListener i = new FXBDEventListener();

    public static FXBDEventListener getInstance() {
        return i;
    }

    private FXBDEventListener() {
    }

    FXBDEngine fxbdEngine = FXBDEngine.getInstance();

    public void registerEvents(FactionsX factionsX) {
        factionsX.getServer().getPluginManager().registerEvents(this, factionsX);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void FactionUnClaimEvent(FactionUnClaimEvent event) {
        fxbdEngine.refreshClaims();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void FactionClaimEvent(FactionPreClaimEvent event) {
        fxbdEngine.refreshClaims();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void FactionDisbandEvent(FactionPreDisbandEvent event) {
        //fxbdEngine.removeHome(event.getFaction().toString().replaceAll("&[a-zA-Z1-9]", ""));
    }
}
