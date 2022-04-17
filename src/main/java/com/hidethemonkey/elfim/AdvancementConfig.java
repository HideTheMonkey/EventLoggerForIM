package com.hidethemonkey.elfim;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

public class AdvancementConfig {
    private YamlConfiguration config;

    public AdvancementConfig(File dataFolder) {
        File file = new File(dataFolder, "advancements.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public String getAdvancementTitle(String key) {
        return config.getString(key.replaceAll("/", ".") + ".title");
    }

    public String getAdvancementDescription(String key) {
        return config.getString(key.replaceAll("/", ".") + ".description");
    }

    public String getAdvancement(String key) {
        return config.getString(key.replaceAll("/", "."));
    }
}
