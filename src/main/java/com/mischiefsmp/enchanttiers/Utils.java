package com.mischiefsmp.enchanttiers;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Utils {
    public static boolean containsStringIgnoreCase(List<String> list, String string) {
        for(String str : list) {
            if(str.equalsIgnoreCase(string))
                return true;
        }
        return false;
    }

    public static ItemStack getIfWeaponCause(ItemStack stack, EntityDamageEvent cause) {
        if(stack == null)
            return null;

        return Utils.isWeaponCause(cause) ? stack : null;
    }

    public static boolean isPlayer(Object obj) {
        return obj instanceof Player;
    }

    public static ItemStack getWeapon(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public static boolean isWeaponCause(EntityDamageEvent cause) {
        return switch (cause.getCause()) {
            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK, PROJECTILE, MAGIC -> true;
            default -> false;
        };
    }

    public static boolean checkIfAllowedWeapon(ItemStack weapon) {
        if(weapon == null)
            return true;

        return !Utils.containsStringIgnoreCase(MischiefEnchantStats.getPluginConfig().getDisabledWeapons(), weaponToString(weapon));
    }

    public static boolean checkIfAllowedCause(EntityDamageEvent cause) {
        if(cause == null)
            return true;

        return !Utils.containsStringIgnoreCase(MischiefEnchantStats.getPluginConfig().getDisabledCauses(), damageCauseToString(cause));
    }

    public static boolean checkIfAllowedCreative(Player player) {
        if(player == null)
            return true;

        if(player.getGameMode() == GameMode.CREATIVE) {
            return MischiefEnchantStats.getPluginConfig().isAllowCreative();
        }
        return true;
    }

    public static String livingEntityToString(LivingEntity ent) {
        return ent.getType().toString().toLowerCase();
    }

    public static String damageCauseToString(EntityDamageEvent cause) {
        return cause.getCause().toString().toLowerCase();
    }

    public static String capitalizeAll(String string) {
        char[] charArray = string.toCharArray();
        boolean foundSpace = true;
        for(int i = 0; i < charArray.length; i++) {
            if (Character.isLetter(charArray[i])) {
                if (foundSpace) {
                    charArray[i] = Character.toUpperCase(charArray[i]);
                    foundSpace = false;
                }
            } else {
                foundSpace = true;
            }
        }
        return String.valueOf(charArray);
    }

    public static boolean startsWithIgnoreCase(String str1, String str2) {
        return str1.toLowerCase().startsWith(str2.toLowerCase());
    }

    public static String prettyPrint(String string) {
        if(string == null)
            return "None";
        string = string.replaceAll("_", " ");
        string = capitalizeAll(string);
        return string;
    }

    public static String weaponToString(ItemStack item) {
        return item.getType() == Material.AIR ? "hand" : item.getType().toString().toLowerCase();
    }
}
