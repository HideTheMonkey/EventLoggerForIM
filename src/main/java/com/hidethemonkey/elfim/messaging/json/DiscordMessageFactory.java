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
package com.hidethemonkey.elfim.messaging.json;

import com.hidethemonkey.elfim.ELConfig;
import com.hidethemonkey.elfim.helpers.MD5Util;

import java.util.logging.Logger;

@SuppressWarnings("unused")
public class DiscordMessageFactory {
  private ELConfig config;
  private Logger logger;

  private String botUserName;
  private String botAvatarUrl;
  private String gravatarEmail;
  private boolean useGravatar = false;

  public DiscordMessageFactory(ELConfig config, Logger logger) {
    this.config = config;
    this.logger = logger;
    this.botUserName = config.getDiscordBotName();
    this.botAvatarUrl = config.getDiscordAvatarUrl();
    this.gravatarEmail = config.getGravatarEmail();
    boolean discordEnabled = config.getDiscordEnabled();

    if (this.gravatarEmail.equals(ELConfig.REPLACE_ME)) {
      this.gravatarEmail = null;
    } else {
      useGravatar = true;
    }
    if (!useGravatar && this.botAvatarUrl.equals(ELConfig.REPLACE_ME)) {
      if (discordEnabled) {
        logger.info("botAvatarUrl is not set in config.yml.");
      }
      this.botAvatarUrl = null;
    }
    if (useGravatar) {
      this.botAvatarUrl = config.getGravatarUrl() + MD5Util.md5Hex(this.gravatarEmail);
    }
  }

  /**
   * @return
   */
  public DiscordMessage getMessage() {
    return new DiscordMessage(this.botUserName, this.botAvatarUrl);
  }

  /**
   * @param content
   * @return
   */
  public DiscordMessage getMessage(String content) {
    DiscordMessage message = new DiscordMessage(this.botUserName, this.botAvatarUrl);
    message.setContent(content);
    return message;
  }

  /**
   * @param embed
   * @return
   */
  public DiscordMessage getMessage(Embed embed) {
    DiscordMessage message = new DiscordMessage(this.botUserName, this.botAvatarUrl);
    message.addEmbed(embed);
    return message;
  }
}
