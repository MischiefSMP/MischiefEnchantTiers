package com.mischiefsmp.enchanttiers.config;

import com.mischiefsmp.core.config.ConfigFile;
import com.mischiefsmp.core.config.ConfigManager;
import com.mischiefsmp.core.config.ConfigValue;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;

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

    @ConfigValue(path = "tiers")
    private HashMap<String, Integer> tiers;

    public PluginConfig(Plugin plugin) {
        super(plugin, "config.yml", "config.yml");
        ConfigManager.init(this);
    }
}
