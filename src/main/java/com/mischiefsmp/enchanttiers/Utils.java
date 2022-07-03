package com.mischiefsmp.enchanttiers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;

public class Utils {
    public static TierPlacementResult isValidTierPlacement(Block enchantmentTable) {
        Location loc = enchantmentTable.getLocation().subtract(0, 1, 0);
        String blockId = enchantmentTable.getWorld().getBlockAt(loc).getType().toString();
        String pp = blockId.replaceAll("_", " ").toLowerCase();

        HashMap<String, Integer> tiers = MischiefEnchantStats.getPluginConfig().getTiers();
        if(tiers == null)
            return new TierPlacementResult(false, false, blockId, pp, 0);

        if(!tiers.containsKey(blockId)) return new TierPlacementResult(false, false,blockId, pp, 0);

        for(int z = -1; z < 2; z++) {
            for(int x = -1; x < 2; x++) {
                Block b = enchantmentTable.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY(), loc.getBlockZ() + z);
                if(b.getType() != Material.getMaterial(blockId)) {
                    return new TierPlacementResult(false, true, blockId, pp, 0);
                }
            }
        }
        return new TierPlacementResult(true, true, blockId, pp, MischiefEnchantStats.getPluginConfig().getTiers().get(blockId));
    }
}
