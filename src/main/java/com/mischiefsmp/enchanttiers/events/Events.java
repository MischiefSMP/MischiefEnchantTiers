package com.mischiefsmp.enchanttiers.events;

import com.mischiefsmp.enchanttiers.MischiefEnchantStats;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Locale;

public class Events implements Listener {
    private MischiefEnchantStats plugin;

    public Events(MischiefEnchantStats plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getBlock().getType() == Material.ENCHANTING_TABLE) {
            Location loc = event.getBlock().getLocation().subtract(0, 1, 0);
            String blockId = event.getBlock().getWorld().getBlockAt(loc).getType().toString();

            if(!MischiefEnchantStats.getPluginConfig().getTiers().containsKey(blockId)) return;

            boolean works = true;
            for(int z = -1; z < 2; z++) {
                for(int x = -1; x < 2; x++) {
                    Block b = event.getBlock().getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY(), loc.getBlockZ() + z);
                    if(b.getType() != Material.getMaterial(blockId)) {
                        works = false;
                        break;
                    }
                }
            }

            if(works) {
                int tier = MischiefEnchantStats.getPluginConfig().getTiers().get(blockId);
                MischiefEnchantStats.getLangManager().sendString(event.getPlayer(), "place-success", tier);
            } else {
                MischiefEnchantStats.getLangManager().sendString(event.getPlayer(), "place-failure", blockId);
            }

        }
    }
}
