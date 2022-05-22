# Event Logger For IM (ELFIM)

ELFIM (_pronounced "elf eye em"_) is an event logging plugin for Bukkit based Minecraft servers (_Bukkit/Spigot/Paper/etc_). 
It posts selected events to Slack and/or Discord via an incoming webhook.

## Usage
After starting the server with `ELFIM-<version>.jar` in the plugins folder, edit `plugins/EventLoggerForIM/config.yml` with the appropriate options.

Enable or disable the integrations
```yaml
enableSlack: true|false
enableDiscord: true|false
```

## Slack Configuration and Examples
See the [Slack readme](./resources/Slack.md).

## Discord Configuration and Examples
See the [Discord readme](./resources/Discord.md).

## Building
Run `mvn clean package shade:shade` from the root directory.  This will create `target/ELFIM-<version>.jar` which you can then drop in your plugins folder.

## Planned Updates
- ?

## Potential Updates
- Internationalize / Localize text strings
- Add ability for players to send messages to admins (From MC to Slack/Discord)
- Address Discord's rate limiting issue with webhooks.

## License
ELFIM is licensed under the permissive MIT license. Please see [`LICENSE`](https://github.com/HideTheMonkey/EventLogForIM/blob/main/LICENSE) for more info.
