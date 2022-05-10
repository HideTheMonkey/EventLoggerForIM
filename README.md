# Event Logger For IM (ELFIM)

ELFIM (_pronounced "elf eye em"_)is an event logging plugin for Bukkit based Minecraft servers (Bukkit/Spigot/Paper/etc). 
It forwards server and player events to Slack via an incoming webhook. See [Slack's documentation](https://api.slack.com/messaging/webhooks) for how to set up a webhook.

## Slack Examples
### Server Events
#### Server Started event
![server started](./resources/ServerStarted.png)
#### Server Stopping event
![server stopping](./resources/ServerStopping.png)
### Player Events
#### Player Joined event
![player joined](./resources/PlayerJoin.png)
#### Player Left event
![player leaves](./resources/PlayerLeft.png)
#### Player Died event
![player died](./resources/PlayerDied.png)
#### Player Chat event
![player chat](./resources/PlayerChat.png)
#### Player Advancement event
![player advancement](./resources/PlayerAdvancement.png)
#### Player Command event
![player command](./resources/PlayerCommand.png)

## Discord Examples
_comming soon_

## Usage
After starting the server with `ELFIM-<version>.jar` in the plugins folder, you will need to edit `plugins/EventLoggerForIM/config.yml` to configure the options.

Enable or disable the Slack integration
```yaml
enableSlack: true|false
```

Update the following settings with your Slack api token and the ID of the channel you want the messages sent to.
For example, replace `xoxb-replace-me` and `change-me-to-a-channel-id` with their respective values.
```yaml
slack:
  apiToken: xoxb-replace-me
  channelId: change-me-to-a-channel-id
```
Once these values are populated restart the server to enable full functionality.  Each event can be toggled on or off via a console command or by editing `config.yml`.

```yaml
slack:
  events:
    logBroadcasts: true|false
    logChat: true|false
    logPlayerAdvancement: true|false
    logPlayerCommands: true|false
    logPlayerDeath: true|false
    logPlayerJoinLeave: true|false
    logServerCommand: true|false
    logServerStartStop: true|false
    logStartupPlugins: true|false
    logUnsuccessfulLogin: true|false
```

## Commands
- `/elfs [enable|disable <key>] [set token|channel|avatarUrl|bustUrl <value>]` (_note: settings take effect after server restart_)

### Permissions
In order to use the `elfs` command you must have the `ELFIM.elfimadmin` permission explicitly set on your user. The easiest way to do that is with another plugin like [LuckPerms](https://luckperms.net/).

### Example commands
- `elfs enable slack` or `elfs disable slack`
- `elfs set channel ABC123`
- `elfs disable logBroadcasts`

## Building
Run `mvn clean package shade:shade` from the root directory.  This will create `target/ELFIM-<version>.jar` which you can then drop in your plugins folder.

## Planned Updates
- Add support for Discord

## Potential Updates
- Internationalize / Localize text strings
- Add ability for players to send messages to admins (From MC to Slack/Discord)

## License
ELFIM is licensed under the permissive MIT license. Please see [`LICENSE`](https://github.com/HideTheMonkey/EventLogForIM/blob/main/LICENSE) for more info.
