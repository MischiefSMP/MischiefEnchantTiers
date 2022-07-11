package com.mischiefsmp.enchanttiers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class OpenTableStorage {
    private static final HashMap<UUID, Location> openTables = new HashMap<>();

    public static void addOpenInventory(Block enchantTable, Player player) {
        openTables.put(player.getUniqueId(), enchantTable.getLocation());
    }

    public static void removeOpenInventory(Player player) {
        openTables.remove(player.getUniqueId());
    }

    public static Block getEnchantTableFromTierBlock(Block tierBlock) {
        for(UUID uuid : openTables.keySet())
            if(isLocationUnderEnchantTable(tierBlock.getLocation(), openTables.get(uuid)))
                return openTables.get(uuid).getBlock();
        return null;
    }

    public static boolean isActiveTierBlock(Block tierBlock) {
        if(!MischiefEnchantStats.getPluginConfig().isTierBlock(tierBlock))
            return false;

        for(UUID uuid : openTables.keySet())
             if(isLocationUnderEnchantTable(tierBlock.getLocation(), openTables.get(uuid)))
                 return true;

        return false;
    }

    public static void closeOpenInventories(Block enchantTable) {
        for(UUID uuid : openTables.keySet()) {
            if(openTables.get(uuid).equals(enchantTable.getLocation())) {
                Player p = Bukkit.getServer().getPlayer(uuid);
                if(p != null) {
                    MischiefEnchantStats.getLangManager().sendString(p, "protect-close");
                    p.closeInventory();
                }
            }
        }
    }

    public static boolean hasOpenInventory(Block enchantTable) {
        for(UUID uuid : openTables.keySet()) {
            if(enchantTable.getLocation().equals(openTables.get(uuid)))
                return true;
        }
        return false;
    }

    private static boolean isLocationUnderEnchantTable(Location lookingFor, Location eTable) {
        for(Block b : Utils.getTierAreaBlocks(eTable.getBlock())) {
            if(lookingFor.equals(b.getLocation()))
                return true;
        }
        return false;
    }
}
