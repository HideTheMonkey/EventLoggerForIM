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

import java.util.logging.Logger;

import org.bstats.charts.SimplePie;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.google.gson.Gson;
import com.hidethemonkey.elfim.AdvancementConfig;
import com.hidethemonkey.elfim.ELConfig;
import com.hidethemonkey.elfim.ELFIM;
import com.hidethemonkey.elfim.helpers.Localizer;
import com.hidethemonkey.elfim.helpers.StringUtils;
import com.hidethemonkey.elfim.messaging.json.DiscordMessage;
import com.hidethemonkey.elfim.messaging.json.DiscordMessageFactory;
import com.hidethemonkey.elfim.messaging.json.Embed;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class DiscordPlayerHandler extends MessageHandler implements PlayerHandlerInterface {

  private final Gson gson = new Gson();
  private final DiscordMessageFactory messageFactory;

  public DiscordPlayerHandler(DiscordMessageFactory messageFactory, Localizer localizer) {
    super(localizer);
    this.messageFactory = messageFactory;
  }

  /**
   * @return
   */
  @Override
  public String getServiceName() {
    return ELConfig.DISCORD;
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerFailedLogin(PlayerLoginEvent event, ELConfig config, Logger logger) {
    Embed embed = new Embed(config.getDiscordColor("playerFailedLogin"));
    embed.setTitle(localizer.t("discord.player.attempted_login_title"));
    embed.addAuthor(event.getPlayer().getName(), config.getMCUserAvatarUrl(event.getPlayer().getUniqueId().toString()));
    embed.setDescription(((TextComponent) event.kickMessage()).content());
    DiscordMessage message = messageFactory.getMessage(embed);

    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);
  }

  /**
   * @param player
   * @param config
   */
  @Override
  public void playerJoin(Player player, ELConfig config) {
    Embed embed = new Embed(config.getDiscordColor("playerJoin"));
    embed.setTitle(localizer.t("discord.player.joined_title"));
    embed.addAuthor(player.getName(), config.getMCUserAvatarUrl(player.getUniqueId().toString()));
    embed.addField(localizer.t("name"), player.getName());
    embed.addField(localizer.t("op"), Boolean.toString(player.isOp()));
    embed.addField(localizer.t("level"), Integer.toString(player.getLevel()));
    embed.addField(localizer.t("address"), player.getAddress().getHostName());
    embed.addField(localizer.t("exp"), Integer.toString(player.getTotalExperience()));
    embed.addField(localizer.t("xyz"), StringUtils.getLocationString(player.getLocation()));
    embed.addField(localizer.t("online_players"), Integer.toString(player.getServer().getOnlinePlayers().size()));
    embed.addField(localizer.t("world"), player.getWorld().getName());
    embed.addField(localizer.t("game_mode"), player.getGameMode().toString());
    embed.setImage(config.getMCUserBustUrl(player.getUniqueId().toString()));
    DiscordMessage message = messageFactory.getMessage(embed);

    Logger logger = player.getServer().getPluginManager().getPlugin(config.getPluginName()).getLogger();
    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);

    // Track player locale
    ELFIM plugin = (ELFIM) player.getServer().getPluginManager().getPlugin(config.getPluginName());
    plugin.getMetrics().addCustomChart(
        new SimplePie("player_locale", () -> String.valueOf(player.locale())));
  }

  /**
   * @param player
   * @param config
   */
  @Override
  public void playerLeave(Player player, ELConfig config) {
    long count = player.getServer().getOnlinePlayers().size() - 1;

    Embed embed = new Embed(config.getDiscordColor("playerLeave"));
    embed.setTitle(localizer.t("discord.player.left_title"));
    embed.addAuthor(player.getName(), config.getMCUserAvatarUrl(player.getUniqueId().toString()));
    embed.setDescription(localizer.t("discord.player.leave_template", count, player.getServer().getMaxPlayers()));
    DiscordMessage message = messageFactory.getMessage(embed);

    Logger logger = player.getServer().getPluginManager().getPlugin(config.getPluginName()).getLogger();
    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerChat(AsyncChatEvent event, ELConfig config) {
    Player player = event.getPlayer();
    Embed embed = new Embed(config.getDiscordColor("playerChat"));
    embed.setTitle(localizer.t("discord.player.chat_title"));
    embed.addAuthor(player.getName(), config.getMCUserAvatarUrl(player.getUniqueId().toString()));
    embed.setDescription(((TextComponent) event.message()).content());
    DiscordMessage message = messageFactory.getMessage(embed);

    Logger logger = player.getServer().getPluginManager().getPlugin(config.getPluginName()).getLogger();
    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);

  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerCommand(PlayerCommandPreprocessEvent event, ELConfig config) {
    Player player = event.getPlayer();
    Embed embed = new Embed(config.getDiscordColor("playerCommand"));
    embed.setTitle(localizer.t("discord.player.command_title"));
    embed.addAuthor(player.getName(), config.getMCUserAvatarUrl(player.getUniqueId().toString()));
    embed.setDescription("`" + event.getMessage() + "`");
    DiscordMessage message = messageFactory.getMessage(embed);

    Logger logger = player.getServer().getPluginManager().getPlugin(config.getPluginName()).getLogger();
    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);
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
      Player player = event.getPlayer();
      Embed embed = new Embed(config.getDiscordColor("playerAdvancement"));
      embed.setTitle(localizer.t("discord.player.advancement_title"));
      embed.addAuthor(player.getName(), config.getMCUserAvatarUrl(player.getUniqueId().toString()));
      String advancementKey = event.getAdvancement().getKey().getKey();
      embed.setDescription("`"
          + advConfig.getAdvancementTitle(advancementKey) + "`\n(_"
          + advConfig.getAdvancementDescription(advancementKey)
          + "_)");
      DiscordMessage message = messageFactory.getMessage(embed);

      Logger logger = player.getServer().getPluginManager().getPlugin(config.getPluginName()).getLogger();
      postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);
    }
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerDeath(PlayerDeathEvent event, ELConfig config) {
    Player player = event.getEntity();
    Embed embed = new Embed(config.getDiscordColor("playerDeath"));
    embed.setTitle(localizer.t("discord.player.died_title"));
    embed.addAuthor(player.getName(), config.getMCUserAvatarUrl(player.getUniqueId().toString()));
    String deathMessage = (String) LegacyComponentSerializer.legacySection().serializeOrNull(event.deathMessage());
    embed.setDescription(deathMessage);
    DiscordMessage message = messageFactory.getMessage(embed);

    Logger logger = player.getServer().getPluginManager().getPlugin(config.getPluginName()).getLogger();
    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerRespawn(PlayerRespawnEvent event, ELConfig config) {
    Player player = event.getPlayer();
    RespawnReason reason = event.getRespawnReason();
    Embed embed = new Embed(config.getDiscordColor("playerRespawn"));
    embed.setTitle(localizer.t("discord.player.respawned_title"));
    embed.addAuthor(player.getName(), config.getMCUserAvatarUrl(player.getUniqueId().toString()));
    embed.setDescription(localizer.t("discord.player.respawned_template",
        player.getName(), reason.toString()));
    DiscordMessage message = messageFactory.getMessage(embed);

    Logger logger = player.getServer().getPluginManager().getPlugin(config.getPluginName()).getLogger();
    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerTeleport(PlayerTeleportEvent event, ELConfig config) {
    String fromLoc = StringUtils.getLocationString(event.getFrom());
    String toLoc = StringUtils.getLocationString(event.getTo());
    if (fromLoc.equals(toLoc) || event.isCancelled()) {
      return;
    }
    Player player = event.getPlayer();
    Embed embed = new Embed(config.getDiscordColor("playerTeleport"));
    embed.setTitle(localizer.t("discord.player.teleported_title"));
    embed.addAuthor(player.getName(), config.getMCUserAvatarUrl(player.getUniqueId().toString()));
    embed.setDescription(localizer.t("discord.player.teleported_template",
        player.getName(), fromLoc, toLoc, event.getCause().toString()));
    DiscordMessage message = messageFactory.getMessage(embed);

    Logger logger = player.getServer().getPluginManager().getPlugin(config.getPluginName()).getLogger();
    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);
  }
}
