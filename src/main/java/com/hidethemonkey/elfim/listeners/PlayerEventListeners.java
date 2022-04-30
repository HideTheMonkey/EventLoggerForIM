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
package com.hidethemonkey.elfim.listeners;

import com.hidethemonkey.elfim.AdvancementConfig;
import com.hidethemonkey.elfim.ELConfig;
import com.hidethemonkey.elfim.messaging.PlayerHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class PlayerEventListeners implements Listener {
  private final ELConfig config;
  private final AdvancementConfig advConfig;
  private boolean logChat = false;
  private boolean logPlayerAdvancement = false;
  private boolean logPlayerCommands = false;
  private boolean logPlayerDeath = false;
  private boolean logPlayerJoinLeave = false;
  private boolean logUnsuccessfulLogin = false;

  public PlayerEventListeners(ELConfig config, AdvancementConfig advConfig) {
    this.config = config;
    this.advConfig = advConfig;
    logChat = config.getLogChat();
    logPlayerAdvancement = config.getLogPlayerAdvancement();
    logPlayerCommands = config.getLogPlayerCommands();
    logPlayerDeath = config.getLogPlayerDeath();
    logPlayerJoinLeave = config.getLogPlayerJoinLeave();
    logUnsuccessfulLogin = config.getLogUnsuccessfulLogin();
  }

  /**
   * @param event
   */
  @EventHandler
  public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
    if (logChat) {
      PlayerHandler.playerChat(event, config);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
    if (logPlayerCommands) {
      PlayerHandler.playerCommand(event, config);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (logPlayerDeath) {
      PlayerHandler.playerDeath(event, config);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (logPlayerJoinLeave) {
      PlayerHandler.playerJoin(event.getPlayer(), config);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (logPlayerJoinLeave) {
      PlayerHandler.playerLeave(event.getPlayer(), config);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
    if (logPlayerAdvancement) {
      PlayerHandler.playerAdvancement(event, config, advConfig);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPlayerLogin(PlayerLoginEvent event) {
    if (logUnsuccessfulLogin) {
      if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
        PlayerHandler.unsuccessfulLogin(event, config);
      }
    }
  }
}
