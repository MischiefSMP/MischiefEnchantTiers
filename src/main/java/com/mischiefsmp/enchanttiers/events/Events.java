package com.mischiefsmp.enchanttiers.events;

import com.mischiefsmp.core.LangManager;
import com.mischiefsmp.enchanttiers.MischiefEnchantStats;
import com.mischiefsmp.enchanttiers.TierPlacementResult;
import com.mischiefsmp.enchanttiers.Utils;
import com.mischiefsmp.enchanttiers.config.PluginConfig;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;

public class Events implements Listener {
    private final LangManager lm;
    private final PluginConfig cfg;

    public Events() {
        lm = MischiefEnchantStats.getLangManager();
        cfg = MischiefEnchantStats.getPluginConfig();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getBlock().getType() == Material.ENCHANTING_TABLE) {
            TierPlacementResult result = Utils.isValidTierPlacement(event.getBlock());

            if(!result.success()) {
                if(result.isOnTierBlock())
                    lm.sendString(event.getPlayer(), "place-failure", result.blockPrettyPrint());
                return;
            }

            lm.sendString(event.getPlayer(), "place-success", result.tier());

            if(cfg.isSpawnFirework())
                Utils.spawnFirework(event.getBlock().getLocation().add(0, 1, 0));

            if(cfg.isShowTitle()) {
                String l1 = lm.getString(event.getPlayer(), "tier-title-line1");
                String l2 = lm.getString(event.getPlayer(), "tier-title-line2", result.tier() + "");
                event.getPlayer().sendTitle(l1, l2, 0, cfg.getTitleSeconds() * 20, 0);
            }
        }
    }

    @EventHandler
    public void onPreEnchant(PrepareItemEnchantEvent event) {
        TierPlacementResult result = Utils.isValidTierPlacement(event.getEnchantBlock());
        if(!result.success()) return;

        for(EnchantmentOffer offer : event.getOffers()) {
            if(offer != null)
                offer.setEnchantmentLevel(offer.getEnchantmentLevel() * result.tier());
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        TierPlacementResult result = Utils.isValidTierPlacement(event.getEnchantBlock());
        if(!result.success()) return;

        if(cfg.runChance(event.getEnchantBlock().getRelative(0, -1, 0))) {
            //Success
            if(cfg.isSpawnFirework())
                Utils.spawnFirework(event.getEnchantBlock().getLocation().add(0, 1, 0));

            for(Enchantment enchantment : event.getEnchantsToAdd().keySet()) {
                int newLevel = event.getEnchantsToAdd().get(enchantment) * result.tier();
                event.getEnchantsToAdd().put(enchantment, newLevel);
            }
        } else {
            event.setCancelled(true);
            event.getEnchantBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onPreAnvil(PrepareAnvilEvent event) {
        ItemStack first = event.getInventory().getItem(0);
        ItemStack second = event.getInventory().getItem(1);

        //Both items are books
        if(first != null && first.getType() != Material.ENCHANTED_BOOK && second != null && second.getType() == Material.ENCHANTED_BOOK) {
            ItemStack resultItem = event.getResult();

            EnchantmentStorageMeta bookEnchantments = (EnchantmentStorageMeta) second.getItemMeta();
            if(bookEnchantments == null || resultItem == null)
                return;

            for(Enchantment e : bookEnchantments.getStoredEnchants().keySet()) {
                int eLevel = bookEnchantments.getStoredEnchants().get(e);
                if(e.canEnchantItem(first)) {
                    if(first.containsEnchantment(e)) {
                        int firstLevel = first.getEnchantmentLevel(e);
                        if(eLevel >= firstLevel) {
                            //Upgrade. Lets go!
                            resultItem.addUnsafeEnchantment(e, eLevel + 1);
                        } else {
                            //Make sure mc doesnt just cap it
                            resultItem.addUnsafeEnchantment(e, firstLevel);
                        }
                    } else {
                        //Doesnt have it yet, just add it.
                        resultItem.addUnsafeEnchantment(e, eLevel);
                    }
                }
            }
        } else if(event.getResult() != null && first != null && second != null && first.getType() == Material.ENCHANTED_BOOK && second.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta firstMeta = (EnchantmentStorageMeta) first.getItemMeta();
            EnchantmentStorageMeta secondMeta = (EnchantmentStorageMeta) second.getItemMeta();
            EnchantmentStorageMeta resultMeta = (EnchantmentStorageMeta) event.getResult().getItemMeta();
            if(firstMeta == null || secondMeta == null || resultMeta == null)
                return;

            for(Enchantment key : secondMeta.getStoredEnchants().keySet()) {
                int storedLevel = secondMeta.getStoredEnchants().get(key);
                if(!firstMeta.hasStoredEnchant(key)) {
                    //Enchantment doesn't exist in book yet, just add it as is
                    resultMeta.addStoredEnchant(key, storedLevel, true);
                } else {
                    //Enchantment exists, allow upgrading it if the stored level is higher or equal to the one currently applied
                    int firstLevel = firstMeta.getStoredEnchantLevel(key);
                    if(storedLevel >= firstLevel) {
                        resultMeta.removeStoredEnchant(key);
                        resultMeta.addStoredEnchant(key, firstLevel + 1, true);
                    }
                }
            }

            event.getResult().setItemMeta(resultMeta);
        }
    }
}
