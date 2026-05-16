# ChatInfo

A lightweight yet powerful plugin for **Minecraft 1.21.4 Paper** servers that allows administrators to create custom chat commands with configurable messages.

## Features

- Create unlimited custom chat commands with predefined messages
- **Inventory GUI editor** — edit messages directly in-game via chest GUI + chat input
- **MiniMessage support** — `<gold>`, `<gradient:...>`, `<click:open_url:'...'>` alongside legacy `&` codes
- **PlaceholderAPI support** — use `%player_name%`, `%server_online%` etc. in messages
- **Cooldown system** — per-command cooldown in seconds
- **Aliases** — multiple names for the same command
- **Description** — shown in `/chatinfo list`
- Permission-based command access control
- Hot reload — no server restart needed
- Brigadier sync — new commands appear in tab-complete instantly

## Installation

1. Download the latest JAR from [Releases](https://github.com/marcin0816/chatinfo/releases)
2. Place the JAR in your server's `plugins/` folder
3. *(Optional)* Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for placeholder support
4. Start or restart your server
5. Edit `plugins/ChatInfo/config.yml` to customize commands

## Commands

### Admin (`chatinformator.admin` / OP)

| Command | Description |
|---|---|
| `/chatinfo` | Show help |
| `/chatinfo reload` | Reload config without restart |
| `/chatinfo list` | List all commands with details |
| `/chatinfo create <name>` | Create new command (opens GUI) |
| `/chatinfo edit <name>` | Edit command messages (opens GUI) |
| `/chatinfo delete <name>` | Delete a command |
| `/chatinfo setperm <name> [perm]` | Set or clear permission |

### Players
Any command defined in `config.yml` — e.g. `/info`, `/rules`, `/vip`

## Configuration

```yaml
reload-message: "<green>Configuration reloaded!"
no-permission-message: "<red>You don't have permission!"
cooldown-message: "<red>Please wait <yellow>{time}s</yellow> before using this again!"

commands:
  info:
    description: "Server information"
    aliases: []
    cooldown: 0
    permission: ""
    messages:
      - "<dark_gray>━━━━━━ <gold><bold>INFO</bold></gold> ━━━━━━"
      - "<yellow>Welcome to the server!"
      - "<green>Discord: <aqua>dc.example.com"
      - "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━"

  rules:
    description: "Server rules"
    aliases: [rule]
    cooldown: 5
    permission: ""
    messages:
      - "<dark_gray>━━━━━━ <red><bold>RULES</bold></red> ━━━━━━"
      - "<gray>1. No spamming"
      - "<gray>2. Be respectful"
      - "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━"
```

## Message Formatting

| Format | Example |
|---|---|
| Legacy colors | `&6Gold  &cRed  &aGreen` |
| Legacy formatting | `&lBold  &oItalic  &nUnderline` |
| MiniMessage | `<gold>Gold</gold>  <red>Red</red>` |
| Hex color | `<color:#FF5500>Custom</color>` |
| Gradient | `<gradient:#FF0000:#0000FF>Rainbow</gradient>` |
| Clickable link | `<click:open_url:'https://example.com'>Click!</click>` |
| PlaceholderAPI | `%player_name%  %server_online%` |

## Permissions

| Permission | Description | Default |
|---|---|---|
| `chatinformator.admin` | Access to all `/chatinfo` subcommands | OP |

## Requirements

- Java 21+
- PlaceholderAPI *(optional)*

## Compatibility

| Minecraft Version | Status |
|---|---|
| 1.21.4 | ✅ Supported |
| 1.21.5 (Spring to Life) | ✅ Supported |
| 1.21.6 (Chase the Skies) | ✅ Supported |
| 1.21.7 | ✅ Supported |
| 1.21.8 | ✅ Supported |
| 1.21.9 (The Copper Age) | ✅ Supported |
| 1.21.10 | ✅ Supported |
| 1.21.11 (Mounts of Mayhem) | ✅ Supported |
| 26.1 (Tiny Takeover) | ✅ Supported |
| 26.1.2 *(latest)* | ✅ Supported |

> Requires **Paper** (not Spigot/Vanilla). Built on Paper 1.21.4 API — works on all newer versions.

## License

Released under the [MIT License](LICENSE).
