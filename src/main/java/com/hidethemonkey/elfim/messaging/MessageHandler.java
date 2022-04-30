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
package com.hidethemonkey.elfim.messaging;

import com.slack.api.Slack;
import com.slack.api.SlackConfig;
import com.slack.api.model.block.LayoutBlock;

import java.util.List;

public abstract class MessageHandler {

  private static Slack slack = Slack.getInstance(new SlackConfig());

  /**
   *
   * @param blocks
   * @param message
   * @param channel
   * @param token
   */
  protected static void postBlocks(
      List<LayoutBlock> blocks, String message, String channel, String token) {
    slack.methodsAsync(token).chatPostMessage(r -> r.channel(channel).blocks(blocks).text(message));
  }

  /**
   * @param message
   * @param channel
   * @param token
   */
  protected static void postMessage(String message, String channel, String token) {
    slack.methodsAsync(token).chatPostMessage(r -> r.channel(channel).text(message));
  }
}
