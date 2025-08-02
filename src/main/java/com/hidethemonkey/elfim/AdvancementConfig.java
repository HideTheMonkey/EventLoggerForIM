/*
 * MIT License
 *
 * Copyright (c) 2022 HideTheMonkey
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.hidethemonkey.elfim;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

public class AdvancementConfig {
    private YamlConfiguration config;

    public AdvancementConfig(File dataFolder) {
        File file = new File(dataFolder, "advancements.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     *
     * @param key
     * @return
     */
    public String getAdvancementTitle(String key) {
        String title = config.getString(key.replaceAll("/", ".") + ".title");
        if (title == null || title.isEmpty()) {
            return key;
        }
        return title;
    }

    /**
     *
     * @param key
     * @return
     */
    public String getAdvancementDescription(String key) {
        String description = config.getString(key.replaceAll("/", ".") + ".description");
        if (description == null || description.isEmpty()) {
            return "unknown";
        }
        return description;
    }

    /**
     *
     * @param key
     * @return
     */
    public String getAdvancement(String key) {
        String value = config.getString(key.replaceAll("/", "."));
        if (value == null || value.isEmpty()) {
            return key;
        }
        return value;
    }
}
