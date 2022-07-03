package com.mischiefsmp.enchanttiers.events;

import com.mischiefsmp.enchanttiers.MischiefEnchantStats;
import com.mischiefsmp.enchanttiers.TierPlacementResult;
import com.mischiefsmp.enchanttiers.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Events implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Material t = event.getBlock().getType();
        if(t == Material.ENCHANTING_TABLE || t == Material.ANVIL) {
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

    @EventHandler
    public void onPreAnvil(PrepareAnvilEvent event) {
        ItemStack bookItem = event.getInventory().getStorageContents()[1];
        if(bookItem == null || bookItem.getType() != Material.ENCHANTED_BOOK)
            return;

        Location anvilLoc = event.getInventory().getLocation();
        if(anvilLoc == null)
            return;

        TierPlacementResult tierResult = Utils.isValidTierPlacement(anvilLoc.getBlock());
        if(!tierResult.success())
            return;

        ItemStack resultItem = event.getResult();
        if(resultItem == null)
            return;

        EnchantmentStorageMeta bookEnchants = (EnchantmentStorageMeta)bookItem.getItemMeta();
        ItemMeta resultEnchants = resultItem.getItemMeta();
        if(bookEnchants == null || resultEnchants == null)
            return;

        //TODO: Do we remove the enchantment or ignore it if it exists in any capacity?
        for(Enchantment key : bookEnchants.getStoredEnchants().keySet()) {
            if(!resultEnchants.hasEnchant(key))
                resultEnchants.removeEnchant(key);

            resultEnchants.addEnchant(key, key.getStartLevel() * tierResult.tier(), false);
        }

        resultItem.setItemMeta(resultEnchants);
    }
}
