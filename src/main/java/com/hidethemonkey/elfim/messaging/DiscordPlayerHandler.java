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

import com.google.gson.Gson;
import com.hidethemonkey.elfim.AdvancementConfig;
import com.hidethemonkey.elfim.ELConfig;
import com.hidethemonkey.elfim.ELPlugin;
import com.hidethemonkey.elfim.helpers.StringUtils;
import com.hidethemonkey.elfim.messaging.json.DiscordMessage;
import com.hidethemonkey.elfim.messaging.json.DiscordMessageFactory;
import com.hidethemonkey.elfim.messaging.json.Embed;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import org.bstats.charts.SimplePie;

import java.util.logging.Logger;

public class DiscordPlayerHandler extends MessageHandler implements PlayerHandlerInterface {

  private final Gson gson = new Gson();
  private DiscordMessageFactory messageFactory;

  public DiscordPlayerHandler(DiscordMessageFactory messageFactory) {
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
    embed.setTitle("Tried to Log in");
    embed.addAuthor(event.getPlayer().getName(), config.getMCUserAvatarUrl(event.getPlayer().getUniqueId().toString()));
    embed.setDescription(event.getKickMessage());
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
    embed.setTitle("Joined Server");
    embed.addAuthor(player.getName(), config.getMCUserAvatarUrl(player.getUniqueId().toString()));
    embed.addField("NAME", player.getName());
    embed.addField("OP", Boolean.toString(player.isOp()));
    embed.addField("LEVEL", Integer.toString(player.getLevel()));
    embed.addField("ADDRESS", player.getAddress().getHostName());
    embed.addField("EXP", Integer.toString(player.getTotalExperience()));
    embed.addField("XYZ", StringUtils.getLocationString(player.getLocation()));
    embed.addField("ONLINE PLAYERS", Integer.toString(player.getServer().getOnlinePlayers().size()));
    embed.addField("WORLD", player.getWorld().getName());
    embed.addField("GAME MODE", player.getGameMode().toString());
    embed.setImage(config.getMCUserBustUrl(player.getUniqueId().toString()));
    DiscordMessage message = messageFactory.getMessage(embed);

    Logger logger = player.getServer().getPluginManager().getPlugin(config.getPluginName()).getLogger();
    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);

    // Track player locale
    ELPlugin plugin = (ELPlugin) player.getServer().getPluginManager().getPlugin(config.getPluginName());
    plugin.getMetrics().addCustomChart(
        new SimplePie("player_locale", () -> String.valueOf(player.getLocale())));
  }

  /**
   * @param player
   * @param config
   */
  @Override
  public void playerLeave(Player player, ELConfig config) {
    long count = player.getServer().getOnlinePlayers().size() - 1;
    String content = "Online count: **" + count + "/" + player.getServer().getMaxPlayers() + "**";

    Embed embed = new Embed(config.getDiscordColor("playerLeave"));
    embed.setTitle("Left Server");
    embed.addAuthor(player.getName(), config.getMCUserAvatarUrl(player.getUniqueId().toString()));
    embed.setDescription(content);
    DiscordMessage message = messageFactory.getMessage(embed);

    Logger logger = player.getServer().getPluginManager().getPlugin(config.getPluginName()).getLogger();
    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);
  }

  /**
   * @param event
   * @param config
   */
  @Override
  public void playerChat(AsyncPlayerChatEvent event, ELConfig config) {
    Player player = event.getPlayer();
    Embed embed = new Embed(config.getDiscordColor("playerChat"));
    embed.setTitle("Said in Chat");
    embed.addAuthor(player.getName(), config.getMCUserAvatarUrl(player.getUniqueId().toString()));
    embed.setDescription(event.getMessage());
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
    embed.setTitle("Issued Command");
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
      embed.setTitle("Made Advancement");
      embed.addAuthor(player.getName(), config.getMCUserAvatarUrl(player.getUniqueId().toString()));
      embed.setDescription("`"
          + advConfig.getAdvancementTitle(event.getAdvancement().getKey().getKey().toString())
          + "`");
      DiscordMessage message = messageFactory.getMessage(embed);

      Logger logger = player.getServer().getPluginManager().getPlugin(config.getPluginName()).getLogger();
      postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);
    }
  }

  @Override
  public void playerDeath(PlayerDeathEvent event, ELConfig config) {
    Player player = event.getEntity();
    Embed embed = new Embed(config.getDiscordColor("playerDeath"));
    embed.setTitle("Died");
    embed.addAuthor(player.getName(), config.getMCUserAvatarUrl(player.getUniqueId().toString()));
    embed.setDescription(event.getDeathMessage());
    DiscordMessage message = messageFactory.getMessage(embed);

    Logger logger = player.getServer().getPluginManager().getPlugin(config.getPluginName()).getLogger();
    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);
  }
}
