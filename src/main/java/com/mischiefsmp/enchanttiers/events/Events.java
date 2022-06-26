package com.mischiefsmp.enchanttiers.events;

import com.mischiefsmp.enchanttiers.MischiefEnchantStats;
import com.mischiefsmp.enchanttiers.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

public class Events implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getBlock().getType() == Material.ENCHANTING_TABLE) {
            final String blockId = event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().subtract(0, 1, 0)).getType().toString();
            if(!MischiefEnchantStats.getPluginConfig().getTiers().containsKey(blockId)) return;

            if(Utils.isValidTierPlacement(event.getBlock())) {
                int tier = MischiefEnchantStats.getPluginConfig().getTiers().get(blockId);
                MischiefEnchantStats.getLangManager().sendString(event.getPlayer(), "place-success", tier);
            } else {
                MischiefEnchantStats.getLangManager().sendString(event.getPlayer(), "place-failure", blockId);
            }
        }
    }

    @EventHandler
    public void onPreEnchant(PrepareItemEnchantEvent event) {
        if(!Utils.isValidTierPlacement(event.getEnchantBlock())) return;

        Block b = event.getEnchantBlock().getWorld().getBlockAt(event.getEnchantBlock().getLocation().subtract(0, 1 , 0));
        for(EnchantmentOffer offer : event.getOffers()) {
            offer.setEnchantmentLevel(offer.getEnchantmentLevel() * MischiefEnchantStats.getPluginConfig().getTiers().get(b.getType().toString()));
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if(!Utils.isValidTierPlacement(event.getEnchantBlock())) return;

        Block b = event.getEnchantBlock().getWorld().getBlockAt(event.getEnchantBlock().getLocation().subtract(0, 1 , 0));
        int tierMulti = MischiefEnchantStats.getPluginConfig().getTiers().get(b.getType().toString());

        for(Enchantment enchantment : event.getEnchantsToAdd().keySet()) {
            int newLevel = event.getEnchantsToAdd().get(enchantment) * tierMulti;
            event.getEnchantsToAdd().put(enchantment, newLevel);
        }
    }
}
