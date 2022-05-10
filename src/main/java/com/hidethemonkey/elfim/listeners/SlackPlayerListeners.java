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

public class SlackPlayerListeners {
  private final ELConfig config;
  private final AdvancementConfig advConfig;

  public SlackPlayerListeners(ELConfig config, AdvancementConfig advConfig) {
    this.config = config;
    this.advConfig = advConfig;
  }

  public class AsyncPlayerChatListener implements Listener {
    /**
     * @param event
     */
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
      PlayerHandler.playerChat(event, config);
    }
  }

  public class PlayerCommandListener implements Listener {
    /**
     * @param event
     */
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
      PlayerHandler.playerCommand(event, config);
    }
  }

  public class PlayerDeathListener implements Listener {
    /**
     * @param event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
      PlayerHandler.playerDeath(event, config);
    }
  }

  public class PlayerJoinListener implements Listener {
    /**
     * @param event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
      PlayerHandler.playerJoin(event.getPlayer(), config);
    }
  }

  public class PlayerQuitListener implements Listener {
    /**
     * @param event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
      PlayerHandler.playerLeave(event.getPlayer(), config);
    }
  }

  public class PlayerAdvancementListener implements Listener {
    /**
     * @param event
     */
    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
      PlayerHandler.playerAdvancement(event, config, advConfig);
    }
  }

  public class PlayerLoginListener implements Listener {
    /**
     * @param event
     */
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
      if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
        PlayerHandler.unsuccessfulLogin(event, config);
      }
    }
  }
}
