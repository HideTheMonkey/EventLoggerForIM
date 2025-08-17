/*
 * MIT License
 *
 * Copyright (c) 2025 HideTheMonkey
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
package com.hidethemonkey.elfim.helpers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Localizer {
    private static final String DEFAULT_LOCALE = "en_US";
    private static final String SUFFIX = ".properties";
    private Properties properties;
    private String configPath = "";
    private String locale = "";
    private Locale localeClass;

    public Localizer(String pluginPath, String locale) {
        this.configPath = pluginPath + "/i18n/";
        this.locale = locale;
        this.localeClass = Locale.forLanguageTag(locale);
        // Load properties from the configured locale
        properties = loadTranslations();
    }

    /**
     * @return the configured locale
     */
    public String getLocale() {
        return this.locale;
    }

    /**
     * t is for translate
     * 
     * @param key
     * @return
     */
    public String t(String key) {
        return properties.getProperty(key, key);
    }

    public String t(String key, Object... args) {
        String template = t(key);
        return String.format(this.localeClass, template, args);
    }

    /**
     * Load the translation strings from a properties file in the config dir
     * 
     * @return Properties with translation strings
     */
    private Properties loadTranslations() {
        Properties properties = new Properties();
        Path path = Paths.get(this.configPath + this.locale + SUFFIX);
        if (!Files.exists(path)) {
            path = Paths.get(this.configPath + this.locale.split("_")[0] + SUFFIX);
            if (!Files.exists(path)) {
                path = Paths.get(this.configPath + DEFAULT_LOCALE + SUFFIX);
            }
        }
        try (InputStream input = new FileInputStream(path.toString())) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
