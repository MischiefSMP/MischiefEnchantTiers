package com.mischiefsmp.enchanttiers.events;

import com.mischiefsmp.enchanttiers.MischiefEnchantStats;
import com.mischiefsmp.enchanttiers.OpenTableStorage;
import com.mischiefsmp.enchanttiers.Utils;
import com.mischiefsmp.enchanttiers.config.PluginConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.List;

public class ProtectEvents implements Listener {
    private final PluginConfig cfg;

    public ProtectEvents() {
        cfg = MischiefEnchantStats.getPluginConfig();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        OpenTableStorage.removeOpenInventory((Player)event.getPlayer());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Location loc = event.getInventory().getLocation();
        if(loc != null && loc.getBlock().getType() == Material.ENCHANTING_TABLE) {
            Block block = loc.getBlock();
            if(Utils.isValidTierPlacement(block).success()) {
                OpenTableStorage.addOpenInventory(block, (Player)event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        checkTierBlocks(event.getBlock(), event);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        checkTierBlocks(event.getBlocks(), event);
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        checkTierBlocks(event.getBlocks(), event);
    }

    private void checkTierBlocks(Block block, Cancellable event) {
        List<Block> list = List.of(new Block[]{block});
        checkTierBlocks(list, event);
    }

    private void checkTierBlocks(List<Block> blocks, Cancellable event) {
        for(Block b : blocks) {
            if (cfg.isTierBlock(b)) {
                if (OpenTableStorage.isActiveTierBlock(b)) {
                    cfg.runProtect(OpenTableStorage.getEnchantTableFromTierBlock(b), event);
                }
            }
        }
    }
}
