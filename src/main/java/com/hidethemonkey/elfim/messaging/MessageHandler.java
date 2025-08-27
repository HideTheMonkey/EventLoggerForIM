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

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hidethemonkey.elfim.helpers.Localizer;
import com.slack.api.Slack;
import com.slack.api.SlackConfig;
import com.slack.api.model.block.LayoutBlock;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class MessageHandler {

  private final Slack slack = Slack.getInstance(new SlackConfig());
  private final MediaType JSON = MediaType.get("application/json; charset=utf-8" );
  private final OkHttpClient client = new OkHttpClient();
  protected final Localizer localizer;

  /**
   * Constructor for MessageHandler passing the Localizer
   * 
   * @param localizer
   */
  public MessageHandler(Localizer localizer) {
    this.localizer = localizer;
  }

  /**
   * @param url
   * @param json
   * @return
   * @throws IOException
   */
  protected String postWebhook(String url, String json, Logger logger) {
    RequestBody body = RequestBody.create(json, JSON);
    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();
    String responseBody = "";
    try (Response response = client.newCall(request).execute()) {
      responseBody = response.body().string();
    } catch (IOException ioe) {
      logger.log(Level.SEVERE, "Error in MessageHandler.postWebhook()", ioe);
    }
    return responseBody;
  }

  /**
   * @param blocks
   * @param message
   * @param channel
   * @param token
   */
  protected void postBlocks(
      List<LayoutBlock> blocks, String message, String channel, String token) {
    slack.methodsAsync(token).chatPostMessage(r -> r.channel(channel).blocks(blocks).text(message));
  }

  /**
   * @param message
   * @param channel
   * @param token
   */
  protected void postMessage(String message, String channel, String token) {
    slack.methodsAsync(token).chatPostMessage(r -> r.channel(channel).text(message));
  }
}
