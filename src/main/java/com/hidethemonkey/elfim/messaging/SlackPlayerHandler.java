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
import com.hidethemonkey.elfim.helpers.Localizer;
import com.hidethemonkey.elfim.helpers.StringUtils;
import com.slack.api.model.block.LayoutBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bstats.charts.SimplePie;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SlackPlayerHandler extends MessageHandler implements PlayerHandlerInterface {

  public SlackPlayerHandler(Localizer localizer) {
    super(localizer);
  }

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
    String message = localizer.t(
        "slack.player.attempted_login", event.getPlayer().getName(), ((TextComponent) event.kickMessage()).content());
    postMessage(message, config.getSlackChannelId(), config.getSlackAPIToken());
  }

  /**
   * @param player
   * @param config
   */
  @Override
  public void playerJoin(Player player, ELConfig config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader(localizer.t("slack.player.header.joined"));
    blocks.add(
        BlockBuilder.getSectionWithFields(
            BlockBuilder.getImageElement(
                config.getMCUserBustUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown(String.format("*%s:* %s", localizer.t("name"), player.getName())),
            BlockBuilder
                .getMarkdown(String.format("*%s:* %s", localizer.t("address"), player.getAddress().getHostName())),
            BlockBuilder.getMarkdown(String.format("*%s:* %s", localizer.t("op"), player.isOp())),
            BlockBuilder.getMarkdown(
                String.format("*%s:* %d", localizer.t("online_players"), player.getServer().getOnlinePlayers().size())),
            BlockBuilder.getMarkdown(String.format("*%s:* %d", localizer.t("exp"), player.getTotalExperience())),
            BlockBuilder.getMarkdown(String.format("*%s:* %d", localizer.t("level"), player.getLevel())),
            BlockBuilder.getMarkdown(String.format("*%s:* %s", localizer.t("world"), player.getWorld().getName())),
            BlockBuilder.getMarkdown(
                String.format("*%s:* %s", localizer.t("xyz"), StringUtils.getLocationString(player.getLocation()))),
            BlockBuilder
                .getMarkdown(String.format("*%s:* %s", localizer.t("game_mode"), player.getGameMode().toString()))));

    postBlocks(blocks, localizer.t("slack.player.joined_template", player.getName()), config.getSlackChannelId(),
        config.getSlackAPIToken());

    // Track player locale
    ELFIM plugin = (ELFIM) player.getServer().getPluginManager().getPlugin(config.getPluginName());
    plugin.getMetrics().addCustomChart(
        new SimplePie("player_locale", () -> player.locale().toString()));
  }

  /**
   * @param player
   * @param config
   */
  @Override
  public void playerLeave(Player player, ELConfig config) {
    long count = player.getServer().getOnlinePlayers().size() - 1;
    String message = localizer.t("slack.player.leave_template", player.getName(), count,
        player.getServer().getMaxPlayers());
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader(localizer.t("slack.player.header.left"));
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
  public void playerChat(AsyncChatEvent event, ELConfig config) {
    Player player = event.getPlayer();
    String message = localizer.t("slack.player.chat_template", player.getName(),
        ((TextComponent) event.message()).content());
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
    String message = localizer.t("slack.player.command_template", event.getPlayer().getName(), event.getMessage());
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
      String advancementKey = event.getAdvancement().getKey().getKey().toString();
      String message = localizer.t("slack.player.advancement_template", event.getPlayer().getName(),
          advConfig.getAdvancementTitle(advancementKey), advConfig.getAdvancementDescription(advancementKey));
      postMessage(message, config.getSlackChannelId(), config.getSlackAPIToken());
    }
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerDeath(PlayerDeathEvent event, ELConfig config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader(localizer.t("slack.player.header.died"));
    Player player = event.getEntity();
    String deathMessage = (String) LegacyComponentSerializer.legacySection().serializeOrNull(event.deathMessage());
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
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader(localizer.t("slack.player.header.respawned"));
    Player player = event.getPlayer();
    RespawnReason reason = event.getRespawnReason();
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getImageElement(
                config.getMCUserAvatarUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown(
                localizer.t("slack.player.respawned_template", player.getName(), reason.toString()))));

    postBlocks(blocks, localizer.t("slack.player.body.respawn_template", player.getName()), config.getSlackChannelId(),
        config.getSlackAPIToken());
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerTeleport(PlayerTeleportEvent event, ELConfig config) {
    // List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Player
    // Teleported");
    String fromLoc = StringUtils.getLocationString(event.getFrom());
    String toLoc = StringUtils.getLocationString(event.getTo());
    if (fromLoc.equals(toLoc) || event.isCancelled()) {
      return;
    }
    Player player = event.getPlayer();

    String message = localizer.t("slack.player.teleported_template", player.getName(), fromLoc, toLoc,
        event.getCause().toString());
    postMessage(message, config.getSlackChannelId(), config.getSlackAPIToken());
  }
}
