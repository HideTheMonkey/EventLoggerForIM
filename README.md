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

_NOTE: version 2.1.0 added [bStats](https://bstats.org/) to help me keep track of plugin usage. This can be disabled in config.yml if you really want, but please consider leaving it on._

## Slack Configuration and Examples

See the [Slack readme](./resources/Slack.md).

## Discord Configuration and Examples

See the [Discord readme](./resources/Discord.md).

## Building

Run `mvn clean package shade:shade` from the root directory. This will create `target/ELFIM-<version>.jar` which you can then drop in your plugins folder.

_NOTE: release 2.1.1 updates the Java target to 17. That means if you need to run on an older JVM you'll need to change the `maven.compiler.release` version and `maven.compiler.target` version in pom.xml and rebuild the jar._

## Planned Updates

- ?

## Potential Updates

- Internationalize / Localize text strings
- Add ability for players to send messages to admins (From MC to Slack/Discord)
- Address Discord's rate limiting issue with webhooks.

## License

ELFIM is licensed under the permissive MIT license. Please see [`LICENSE`](https://github.com/HideTheMonkey/EventLogForIM/blob/main/LICENSE) for more info.
