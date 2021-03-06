package com.mischiefsmp.enchanttiers.config;

import com.mischiefsmp.core.config.ConfigFile;
import com.mischiefsmp.core.config.ConfigManager;
import com.mischiefsmp.core.config.ConfigValue;

import com.mischiefsmp.core.utils.Utils;
import com.mischiefsmp.enchanttiers.OpenTableStorage;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@Getter
public class PluginConfig extends ConfigFile {
    @ConfigValue(path = "language")
    private String defaultLanguage;

    @ConfigValue(path = "languages")
    private ArrayList<String> languages;

    @ConfigValue(path = "show-title")
    private boolean showTitle;

    @ConfigValue(path = "title-seconds")
    private int titleSeconds;

    @ConfigValue(path = "spawn-firework")
    private boolean spawnFirework;

    @ConfigValue(path = "protect-mode")
    private String protectMode;

    @ConfigValue(path = "fail-chances")
    private HashMap<String, Integer> failChances;

    @ConfigValue(path = "explode-power")
    private int explodePower;

    @ConfigValue(path = "tiers")
    private HashMap<String, Integer> tiers;

    public PluginConfig(Plugin plugin) {
        super(plugin, "config.yml", "config.yml");
        ConfigManager.init(this);
    }

    public boolean isTierBlock(Block block) {
        return isTierBlock(block.getType().toString());
    }

    public boolean isTierBlock(String name) {
        if(tiers == null) return false;

        for(String tierBlock : tiers.keySet()) {
            if(tierBlock.equals(name))
                return true;
        }
        return false;
    }

    public void runProtect(Block enchantTable, Cancellable event) {
        switch (protectMode) {
            case "prevent" -> event.setCancelled(true);
            case "close" -> OpenTableStorage.closeOpenInventories(enchantTable);
        }
    }

    //Returns false if fail
    public boolean runChance(Block tierBlock) {
        String id = tierBlock.getType().toString();
        if(failChances == null || !failChances.containsKey(id)) return true;
        return new Random().nextInt(100) > failChances.get(id);
    }
}
