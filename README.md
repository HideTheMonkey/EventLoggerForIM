# Event Log For IM (ELFIM)

ELFIM is an event logging plugin for bukkit/spigot Minecraft servers. 
It forwards server and player events to Slack via an incoming webhook.

### Server Examples
#### Server Started event
![server started](./resources/ServerStarted.png)
#### Server Stopping event
![server stopping](./resources/ServerStopping.png)
### Player Examples
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

## Usage
After starting the server with the ELFIM.jar in the plugins directory, there will be a new folder `plugins/EventLogForIM` with a `config.yml`.
Update the following settings with your Slack api token and the ID of the channel to post messages to.

For example, replace `xoxb-replace-me` and `change-me-to-a-channel-id` with their respective values.
```access transformers
slack:
  apiToken: xoxb-replace-me
  channelId: change-me-to-a-channel-id
```
Once these values are populated restart the server to see the enabled events sent to Slack.  Each event can be toggled on or off via a console command.  See below:

```access transformers
slack:
  events:
    logBroadcasts: true
    logChat: true
    logPlayerAdvancement: true
    logPlayerCommands: true
    logPlayerDeath: true
    logPlayerJoinLeave: true
    logServerCommand: true
    logServerStartStop: true
    logUnsuccessfulLogin: true
```

## Commands
- `/elfs [enable|disable <key>] [set token|channel|avatarUrl|bustUrl <value>]` (_note: settings take effect after server restart_)

## Planned Updates
- Add support for Discord
- Add ability for players to send messages to admins (From MC to Slack/Discord)
- Improve listener interface (only add a listener if it's configured)

## License
ELFIM is licensed under the permissive MIT license. Please see [`LICENSE`](https://github.com/HideTheMonkey/EventLogForIM/blob/main/LICENSE) for more info.