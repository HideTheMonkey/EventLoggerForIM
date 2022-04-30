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

import com.hidethemonkey.elfim.AdvancementConfig;
import com.hidethemonkey.elfim.ELConfig;
import com.slack.api.model.block.LayoutBlock;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class PlayerHandler extends MessageHandler {

  /**
   * @param event
   * @param config
   */
  public static void unsuccessfulLogin(PlayerLoginEvent event, ELConfig config) {
    String message = "*" + event.getPlayer().getName() + "* attempted to log in.\n" + event.getKickMessage();
    postMessage(message, config.getChannelId(), config.getToken());
  }

  /**
   * @param location
   * @return
   */
  private static String getLocationString(Location location) {
    return MessageFormat.format(
        "{0}, {1}, {2}",
        String.format("%.0f", location.getX()),
        String.format("%.0f", location.getY()),
        String.format("%.0f", location.getZ()));
  }

  /**
   * @param player
   * @param config
   */
  public static void playerJoin(Player player, ELConfig config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Player Joined");
    blocks.add(
        BlockBuilder.getSectionWithFields(
            BlockBuilder.getImageElement(
                config.getBustUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown("*NAME:* " + player.getName()),
            BlockBuilder.getMarkdown("*ADDRESS:* " + player.getAddress().getHostName()),
            BlockBuilder.getMarkdown("*OP:* " + player.isOp()),
            BlockBuilder.getMarkdown(
                "*ONLINE PLAYERS:* " + player.getServer().getOnlinePlayers().size()),
            BlockBuilder.getMarkdown("*EXP:* " + player.getTotalExperience()),
            BlockBuilder.getMarkdown("*LEVEL:* " + player.getLevel()),
            BlockBuilder.getMarkdown("*WORLD:* " + player.getWorld().getName()),
            BlockBuilder.getMarkdown("*XYZ:* " + getLocationString(player.getLocation()))));

    String message = player.getName() + " joined the server.";
    postBlocks(blocks, message, config.getChannelId(), config.getToken());
  }

  /**
   * @param player
   * @param config
   */
  public static void playerLeave(Player player, ELConfig config) {
    long count = player.getServer().getOnlinePlayers().size() - 1;
    String message =
        "*"
            + player.getName()
            + "* left the server! Online count: *"
            + count
            + "/"
            + player.getServer().getMaxPlayers()
            + "*";
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Player Left");
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getImageElement(
                config.getAvatarUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown(message)));

    postBlocks(blocks, message, config.getChannelId(), config.getToken());
  }

  /**
   * @param event
   * @param config
   */
  public static void playerChat(AsyncPlayerChatEvent event, ELConfig config) {
    Player player = event.getPlayer();
    String message = "*" + player.getName() + "* said: " + event.getMessage();
    List<LayoutBlock> blocks = new ArrayList<LayoutBlock>();
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getImageElement(
                config.getAvatarUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown(message)));

    postBlocks(blocks, message, config.getChannelId(), config.getToken());
  }

  /**
   * @param event
   * @param config
   */
  public static void playerCommand(PlayerCommandPreprocessEvent event, ELConfig config) {
    String message =
        "*" + event.getPlayer().getName() + "* issued command: `" + event.getMessage() + "`";
    postMessage(message, config.getChannelId(), config.getToken());
  }


  /**
   * @param event
   * @param config
   * @param advConfig
   */
  public static void playerAdvancement(
      PlayerAdvancementDoneEvent event, ELConfig config, AdvancementConfig advConfig) {
    if (!event.getAdvancement().getKey().getKey().startsWith("recipes")) {
      String message =
          "*"
              + event.getPlayer().getName()
              + "* has made the advancement `"
              + advConfig.getAdvancementTitle(event.getAdvancement().getKey().getKey().toString())
              + "`";
      postMessage(message, config.getChannelId(), config.getToken());
    }
  }


  public static void playerDeath(PlayerDeathEvent event, ELConfig config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Player Died");
    Player player = event.getEntity();
    String deathMessage = event.getDeathMessage();
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getImageElement(
                config.getAvatarUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown(deathMessage)));

    postBlocks(blocks, deathMessage, config.getChannelId(), config.getToken());
  }
}
