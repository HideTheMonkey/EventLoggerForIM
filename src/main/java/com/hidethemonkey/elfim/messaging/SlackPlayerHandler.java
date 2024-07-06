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
import com.hidethemonkey.elfim.ELFIM;
import com.hidethemonkey.elfim.helpers.StringUtils;
import com.slack.api.model.block.LayoutBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bstats.charts.SimplePie;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SlackPlayerHandler extends MessageHandler implements PlayerHandlerInterface {

  /**
   *
   * @return
   */
  @Override
  public String getServiceName() {
    return ELConfig.SLACK;
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerFailedLogin(PlayerLoginEvent event, ELConfig config, Logger logger) {
    String message = "*" + event.getPlayer().getName() + "* attempted to log in.\n" + event.getKickMessage();
    postMessage(message, config.getSlackChannelId(), config.getSlackAPIToken());
  }

  /**
   * @param player
   * @param config
   */
  @Override
  public void playerJoin(Player player, ELConfig config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Player Joined");
    blocks.add(
        BlockBuilder.getSectionWithFields(
            BlockBuilder.getImageElement(
                config.getMCUserBustUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown("*NAME:* " + player.getName()),
            BlockBuilder.getMarkdown("*ADDRESS:* " + player.getAddress().getHostName()),
            BlockBuilder.getMarkdown("*OP:* " + player.isOp()),
            BlockBuilder.getMarkdown(
                "*ONLINE PLAYERS:* " + player.getServer().getOnlinePlayers().size()),
            BlockBuilder.getMarkdown("*EXP:* " + player.getTotalExperience()),
            BlockBuilder.getMarkdown("*LEVEL:* " + player.getLevel()),
            BlockBuilder.getMarkdown("*WORLD:* " + player.getWorld().getName()),
            BlockBuilder.getMarkdown("*XYZ:* " + StringUtils.getLocationString(player.getLocation())),
            BlockBuilder.getMarkdown("*GAME MODE:* " + player.getGameMode().toString())));

    String message = player.getName() + " joined the server.";
    postBlocks(blocks, message, config.getSlackChannelId(), config.getSlackAPIToken());

    // Track player locale
    ELFIM plugin = (ELFIM) player.getServer().getPluginManager().getPlugin(config.getPluginName());
    plugin.getMetrics().addCustomChart(
        new SimplePie("player_locale", () -> StringUtils.formatLocale(player.getLocale())));
  }

  /**
   * @param player
   * @param config
   */
  @Override
  public void playerLeave(Player player, ELConfig config) {
    long count = player.getServer().getOnlinePlayers().size() - 1;
    String message = "*"
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
                config.getMCUserAvatarUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown(message)));

    postBlocks(blocks, message, config.getSlackChannelId(), config.getSlackAPIToken());
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerChat(AsyncPlayerChatEvent event, ELConfig config) {
    Player player = event.getPlayer();
    String message = "*" + player.getName() + "* said: " + event.getMessage();
    List<LayoutBlock> blocks = new ArrayList<>();
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getImageElement(
                config.getMCUserAvatarUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown(message)));

    postBlocks(blocks, message, config.getSlackChannelId(), config.getSlackAPIToken());
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerCommand(PlayerCommandPreprocessEvent event, ELConfig config) {
    String message = "*" + event.getPlayer().getName() + "* issued command: `" + event.getMessage() + "`";
    postMessage(message, config.getSlackChannelId(), config.getSlackAPIToken());
  }

  /**
   * @param event
   * @param config
   * @param advConfig
   */
  @Override
  public void playerAdvancement(
      PlayerAdvancementDoneEvent event, ELConfig config, AdvancementConfig advConfig) {
    if (!event.getAdvancement().getKey().getKey().startsWith("recipes")) {
      String message = "*"
          + event.getPlayer().getName()
          + "* has made the advancement `"
          + advConfig.getAdvancementTitle(event.getAdvancement().getKey().getKey().toString())
          + "`";
      postMessage(message, config.getSlackChannelId(), config.getSlackAPIToken());
    }
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerDeath(PlayerDeathEvent event, ELConfig config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Player Died");
    Player player = event.getEntity();
    String deathMessage = event.getDeathMessage();
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getImageElement(
                config.getMCUserAvatarUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown(deathMessage)));

    postBlocks(blocks, deathMessage, config.getSlackChannelId(), config.getSlackAPIToken());
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerRespawn(PlayerRespawnEvent event, ELConfig config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Player Respawned");
    Player player = event.getPlayer();
    RespawnReason reason = event.getRespawnReason();
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getImageElement(
                config.getMCUserAvatarUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown(player.getName() + " respawned due to " + reason.toString() + ".")));

    postBlocks(blocks, player.getName() + " respawned.", config.getSlackChannelId(), config.getSlackAPIToken());
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerTeleport(PlayerTeleportEvent event, ELConfig config) {
    // List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Player
    // Teleported");
    Player player = event.getPlayer();

    String message = "*" + player.getName() + "* teleported from *" + StringUtils.getLocationString(event.getFrom())
        + "* to *"
        + StringUtils.getLocationString(event.getTo()) + "*";
    postMessage(message, config.getSlackChannelId(), config.getSlackAPIToken());
  }
}
