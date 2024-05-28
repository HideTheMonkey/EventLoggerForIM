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
package com.hidethemonkey.elfim.helpers;

import org.bukkit.Location;

import java.text.MessageFormat;

public class StringUtils {

  /**
   * @param value
   * @return
   */
  public static String removeSpecialChars(String value) {
    /* Replace color code &color; */
    value = value.replace("\u00A7" + "2", "");
    value = value.replace("\u00A7" + "f", "");

    return value;
  }

  /**
   * @param location
   * @return
   */
  public static String getLocationString(Location location) {
    return MessageFormat.format(
        "{0}, {1}, {2}",
        String.format("%.0f", location.getX()),
        String.format("%.0f", location.getY()),
        String.format("%.0f", location.getZ()));
  }

  /**
   * Standardize the format of user locale
   * 
   * @param locale
   * @return
   */
  public static String formatLocale(String locale) {
    String[] parts = locale.split("_");
    if (parts.length == 2) {
      // return the language and country in the format "language_COUNTRY"
      return parts[0] + "_" + parts[1].toUpperCase();
    }
    return locale;
  }
}
