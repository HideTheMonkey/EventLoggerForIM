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

import java.util.ArrayList;
import java.util.List;

public class DiscordMessage {
  private String username;
  private String avatar_url;
  private String content;
  private List<Embed> embeds;

  public DiscordMessage(String username) {
    this.username = username;
  }

  public DiscordMessage(String username, String avatarUrl) {
    this.username = username;
    this.avatar_url = avatarUrl;
  }

  public void setAvatarUrl(String url) {
    this.avatar_url = url;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void addEmbed(Embed embed) {
    if (this.embeds == null) {
      this.embeds = new ArrayList<>();
    }
    this.embeds.add(embed);
  }
}
