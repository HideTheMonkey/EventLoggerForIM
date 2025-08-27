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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

public class Localizer {
    private final String DEFAULT_LOCALE = "en_US";
    private final String SUFFIX = ".properties";

    private final Properties properties;
    private final String configPath;
    private final String locale;
    private final Locale localeClass;

    public Localizer(String pluginPath, String locale) {
        this.configPath = pluginPath + "/i18n/";
        this.localeClass = Locale.forLanguageTag(locale);
        this.locale = this.localeClass.toLanguageTag();
        // Load properties from the configured locale
        properties = loadTranslations(this.configPath, this.locale);
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
    private Properties loadTranslations(String configPath, String locale) {
        Properties props = new Properties();
        Path path = Paths.get(configPath + locale + SUFFIX);
        if (!Files.exists(path)) {
            path = Paths.get(configPath + locale.split("_")[0] + SUFFIX);
            if (!Files.exists(path)) {
                path = Paths.get(configPath + DEFAULT_LOCALE + SUFFIX);
            }
        }
        try (InputStream input = new FileInputStream(path.toString())) {
            Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
            props.load(reader);  // Use load(Reader) to support UTF-8
        } catch (IOException e) {
            // Try to load default one more time
            try (InputStream input = new FileInputStream(configPath + DEFAULT_LOCALE + SUFFIX)) {
                Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
                props.load(reader);  // Use load(Reader) to support UTF-8
            } catch (IOException ex) {
            }
        }
        return props;
    }
}
