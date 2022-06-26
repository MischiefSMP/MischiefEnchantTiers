package com.mischiefsmp.enchanttiers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class Utils {
    public static boolean isValidTierPlacement(Block block) {
        Location loc = block.getLocation().subtract(0, 1, 0);
        String blockId = block.getWorld().getBlockAt(loc).getType().toString();
        for(int z = -1; z < 2; z++) {
            for(int x = -1; x < 2; x++) {
                Block b = block.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY(), loc.getBlockZ() + z);
                if(b.getType() != Material.getMaterial(blockId)) {
                    return false;
                }
            }
        }
        return true;
    }
}
