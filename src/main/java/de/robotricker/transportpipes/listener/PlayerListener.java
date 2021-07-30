package de.robotricker.transportpipes.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.robotricker.transportpipes.config.GeneralConf;
import de.robotricker.transportpipes.duct.Duct;
import de.robotricker.transportpipes.duct.DuctRegister;
import de.robotricker.transportpipes.duct.manager.DuctManager;
import de.robotricker.transportpipes.duct.manager.GlobalDuctManager;
import de.robotricker.transportpipes.duct.manager.PipeManager;
import de.robotricker.transportpipes.duct.types.BaseDuctType;
import de.robotricker.transportpipes.duct.types.DuctType;
import de.robotricker.transportpipes.items.ItemService;

public class PlayerListener implements Listener {

    @Inject
    private GlobalDuctManager globalDuctManager;

    @Inject
    private DuctRegister ductRegister;

    @Inject
    private ItemService itemService;

    @Inject
    private GeneralConf generalConf;

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        globalDuctManager.getPlayerDucts(e.getPlayer()).clear();
        ((PipeManager) (DuctManager<? extends Duct>) ductRegister.baseDuctTypeOf("pipe").getDuctManager()).getPlayerPipeItems(e.getPlayer()).clear();
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        //make sure that all duct that were visible to the player get removed, so they will spawn again when the player is nearby
        globalDuctManager.getPlayerDucts(e.getPlayer()).clear();
        ((PipeManager) (DuctManager<? extends Duct>) ductRegister.baseDuctTypeOf("pipe").getDuctManager()).getPlayerPipeItems(e.getPlayer()).clear();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (generalConf.isCraftingEnabled()) {
            List<NamespacedKey> keys = new ArrayList<>();
            for (BaseDuctType<? extends Duct> bdt : ductRegister.baseDuctTypes()) {
                for (DuctType type : bdt.ductTypes()) {
                    if (type.getDuctRecipe() != null) {
                        NamespacedKey key = ((Keyed) type.getDuctRecipe()).getKey();
                        keys.add(key);
                    }
                }
                if (bdt.is("pipe")) {
                    keys.add(((PipeManager) bdt.getDuctManager()).getWrenchRecipe().getKey());
                }
            }
            event.getPlayer().discoverRecipes(keys);
        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {
        if (e.getInventory().getViewers().isEmpty() || e.getInventory().getResult() == null) {
            return;
        }
        Player p = (Player) e.getInventory().getViewers().get(0);
        for (BaseDuctType<? extends Duct> bdt : ductRegister.baseDuctTypes()) {
            for (DuctType dt : bdt.ductTypes()) {
                if (Objects.requireNonNull(e.getRecipe()).getResult().isSimilar(bdt.getItemManager().getItem(dt))) {
                    if (!dt.hasPlayerCraftingPermission(p)) {
                        e.getInventory().setResult(null);
                        return;
                    }
                }
            }
        }
        if (itemService.isWrench(Objects.requireNonNull(e.getRecipe()).getResult())) {
            if (!p.hasPermission("transportpipes.craft.wrench")) {
                e.getInventory().setResult(null);
            }
        }
    }

}
