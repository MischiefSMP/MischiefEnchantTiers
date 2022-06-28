package com.mischiefsmp.enchanttiers.events;

import com.mischiefsmp.enchanttiers.MischiefEnchantStats;
import com.mischiefsmp.enchanttiers.TierPlacementResult;
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
            TierPlacementResult result = Utils.isValidTierPlacement(event.getBlock());
            if(result.success())
                MischiefEnchantStats.getLangManager().sendString(event.getPlayer(), "place-success", result.tier());
            else if(result.isOnTierBlock())
                MischiefEnchantStats.getLangManager().sendString(event.getPlayer(), "place-failure", result.blockPrettyPrint());
        }
    }

    @EventHandler
    public void onPreEnchant(PrepareItemEnchantEvent event) {
        TierPlacementResult result = Utils.isValidTierPlacement(event.getEnchantBlock());
        if(!result.success()) return;

        for(EnchantmentOffer offer : event.getOffers())
            offer.setEnchantmentLevel(offer.getEnchantmentLevel() * result.tier());
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        TierPlacementResult result = Utils.isValidTierPlacement(event.getEnchantBlock());
        if(!result.success()) return;

        for(Enchantment enchantment : event.getEnchantsToAdd().keySet()) {
            int newLevel = event.getEnchantsToAdd().get(enchantment) * result.tier();
            event.getEnchantsToAdd().put(enchantment, newLevel);
        }
    }
}
