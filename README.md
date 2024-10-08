# Event Logger For IM (ELFIM)

![logo](./resources/logo_small.png)

ELFIM (_pronounced "elf eye em"_) is a light-weight event logging plugin that posts selected events to Slack and Discord. It's a convenient way to keep track of who logged on when, or what commands they used while playing.

#### Available at
- [PaperMC](https://hangar.papermc.io/HideTheMonkey/EventLoggerForIM)
- [SpigotMC](https://www.spigotmc.org/resources/eventloggerforim.118800/)
- [Modrinth](https://modrinth.com/plugin/eventloggerforim)

## Usage

After starting the server with `ELFIM-<version>.jar` in the plugins folder, edit `plugins/EventLoggerForIM/config.yml` with the details of your configured webhook and **enable**|**disable** the events you want to log.

Enable or disable the integrations

```yaml
enableSlack: true|false
enableDiscord: true|false
```

Enable or disable specific events:

```yaml
events:
  logBroadcasts: true|false
  logChat: true|false
  logPlayerAdvancement: true|false
  logPlayerCommands: true|false
  logPlayerDeath: true|false
  logPlayerJoinLeave: true|false
  logPlayerRespawn: true|false
  logPlayerTeleport: true|false
  logServerCommand: true|false
  logServerStartStop: true|false
  logStartupPlugins: true|false
  logUnsuccessfulLogin: true|false
```

Log specific server.properties by putting the keys in an array:

```yaml
# List of server.properties to log on server startup.
# NOTE: you must use square brackets [] for an empty array to disable this feature.
#       e.g. logServerProperties: []
logServerProperties:
  - online-mode
  - pvp
  - difficulty
  - level-type
```

## Slack Configuration and Examples

See the [Slack readme](./resources/Slack.md).

## Discord Configuration and Examples

See the [Discord readme](./resources/Discord.md).

## Building from Source

Run `mvn clean package shade:shade` from the root directory. This will create `target/ELFIM-<version>.jar` which you can then drop in your plugins folder.

_NOTE: release 2.1.1 updates the Java target to 17, so if you need to run on an older JVM you'll need to update the `maven.compiler.release` and `maven.compiler.target` versions in pom.xml and rebuild the jar._

## Metrics

I use [bStats](https://bstats.org/) to collect anonymous usage data which helps me decide where to focus development effort. The metrics are publicly available [here](https://bstats.org/plugin/bukkit/EventLoggerForIM/20980).

_The metrics can be disabled in config.yml if you really want, but please consider leaving them on._

## Potential Updates

- Internationalize / Localize text strings
- Add ability for players to send private messages to admins (From MC to Slack/Discord)
- Address Discord's rate limiting issue with webhooks.

## License

ELFIM is licensed under the permissive MIT license. Please see [`LICENSE`](https://github.com/HideTheMonkey/EventLogForIM/blob/main/LICENSE) for more info.
