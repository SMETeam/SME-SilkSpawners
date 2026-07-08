package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.plugin.events.SpawnerPlaceEvent;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;

public class BlockPlaceListener extends SilkSpawnersListener<BlockPlaceEvent> {

    private final List<Block> editedSpawners;

    public BlockPlaceListener(List<Block> editedSpawners){
        this.editedSpawners = editedSpawners;
    }

    @Override @EventHandler(priority = EventPriority.HIGHEST)
    protected void onCall(BlockPlaceEvent e) {
        if(e.isCancelled()) return;

        Player p = e.getPlayer();

        Spawner spawner = new Spawner(plugin, e.getItemInHand());
        if(!spawner.isValid()) return;

        if(!p.hasPermission("silkspawners.place." + spawner.serializedEntityType())
                && !p.hasPermission("silkspawners.place.*")
                && !new ConfigValue<Boolean>(PluginConfig.SPAWNER_PERMISSION_DISABLE_PLACE).get()) {
            e.setCancelled(true);
            if(new ConfigValue<Boolean>(PluginConfig.SPAWNER_MESSAGE_DENY_PLACE).get()) p.sendMessage(plugin.getLocale().getMessage("SPAWNER_PLACE_DENIED"));
            return;
        }

        SpawnerPlaceEvent event = new SpawnerPlaceEvent(p, spawner, e.getBlock().getLocation(), plugin);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()) {
            e.setCancelled(true);
            return;
        }
        this.editedSpawners.add(e.getBlock());
        event.getSpawner().setSpawnerBlockType(e.getBlock(), this.editedSpawners);
    }
}
