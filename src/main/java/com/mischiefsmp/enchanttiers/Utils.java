package com.mischiefsmp.enchanttiers;

import com.mischiefsmp.enchanttiers.config.PluginConfig;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final PluginConfig cfg = MischiefEnchantStats.getPluginConfig();

    public static TierPlacementResult isValidTierPlacement(Block eTable) {
        Location loc = eTable.getLocation().subtract(0, 1, 0);
        String blockId = eTable.getWorld().getBlockAt(loc).getType().toString();
        String pp = blockId.replaceAll("_", " ").toLowerCase();

        if(cfg.getTiers() == null || !cfg.isTierBlock(blockId)) return new TierPlacementResult(false, false, blockId, pp, 0);

        for(Block b : getTierAreaBlocks(eTable)) {
            if(b.getType() != Material.getMaterial(blockId))
                return new TierPlacementResult(false, true, blockId, pp, 0);
        }
        return new TierPlacementResult(true, true, blockId, pp, MischiefEnchantStats.getPluginConfig().getTiers().get(blockId));
    }

    public static void breakBlock(Block b) {
        b.getWorld().spawnParticle(Particle.BLOCK_DUST, b.getLocation().add(0.5,0.5,0.5), 1, 1, 0.1, 0.1, 0.1, b.getBlockData());
        b.setType(Material.AIR);
    }

    public static void spawnFirework(Location location) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.GREEN).with(FireworkEffect.Type.CREEPER).build());
        fw.setFireworkMeta(fwm);
    }

    public static List<Block> getTierAreaBlocks(Block eTable) {
        ArrayList<Block> list = new ArrayList<>();
        Location loc = eTable.getLocation().subtract(0, 1, 0);
        for(int z = -1; z < 2; z++) {
            for(int x = -1; x < 2; x++) {
                World w = loc.getWorld();
                if(w != null) {
                    Block b = loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY(), loc.getBlockZ() + z);
                    list.add(b);
                }
            }
        }
        return list;
    }

    public static String printLocation(Location loc) {
        return String.format("(%s, %s, %s, %s)", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
    }
}
