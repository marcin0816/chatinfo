**CHATINFO**
A lightweight plugin for Minecraft 1.21.4 Paper servers that allows administrators to create custom chat commands with predefined messages.
Features

Create unlimited custom chat commands with predefined messages
Easy configuration through the config.yml file
Support for color codes and formatting
Permission-based command access control
Admin command for reloading configuration and listing all available commands
Works with Minecraft 1.21.4

Installation

Download the latest version of the plugin
Place the JAR file in your server's plugins folder
Start or restart your server
Edit the config.yml file to customize your commands and messages

Configuration
The plugin will generate a default config.yml file when first started. The file contains the following sections:
yaml# Message displayed when reloading configuration
reload-message: "&aConfiguration has been reloaded!"
```
# Message displayed when permission is denied
no-permission-message: "&cYou don't have permission to use this command!"
```
```
# Commands and their associated messages
commands:
  info:
    messages:
      - "&8&l========== &6&lINFORMATION &8&l=========="
      - "&eWelcome to the server! Have fun!"
      - "&aOur Discord: &bdc.yourserver.com"
      - "&aWebsite: &bwww.yourserver.com"
      - "&8&l==============================="
    permission: "" # Empty value means no permissions required

  rules:
    messages:
      - "&8&l========== &c&lRULES &8&l=========="
      - "&71. No spamming in chat"
      - "&72. No advertising other servers"
      - "&73. Be nice to other players"
      - "&74. Respect server administration"
      - "&8&l==============================="
    permission: "" # Empty value means no permissions required
```
Adding Custom Commands
To add a new custom command, add a new section under commands: in the config.yml file:
```
yamlcommands:
  yourcommand:
    messages:
      - "&8&l========== &a&lYOUR COMMAND &8&l=========="
      - "&eThis is a custom command message"
      - "&8&l==============================="
    permission: "chatinformator.yourcommand" # Permission required to use the command
```
**Color Codes**
The plugin supports Minecraft color codes using the & symbol:
```
&0 - Black
&1 - Dark Blue
&2 - Dark Green
&3 - Dark Aqua
&4 - Dark Red
&5 - Dark Purple
&6 - Gold
&7 - Gray
&8 - Dark Gray
&9 - Blue
&a - Green
&b - Aqua
&c - Red
&d - Light Purple
&e - Yellow
&f - White
```
**Formatting Codes**
Additionally, the following formatting codes are supported:
```
&l - Bold
&n - Underline
&o - Italic
&k - Magic (random characters)
&m - Strikethrough
&r - Reset formatting
```
**Commands**
Player Commands
Players can use any commands defined in the configuration file. For example:
```
/info - Shows server information
/rules - Shows server rules
```
Admin Commands
```
/chatinfo reload - Reloads the configuration file
/chatinfo list - Lists all registered commands with their permissions
```
Permissions

**chatinformator.admin** - Required to use admin commands (reload, list)
Custom permissions defined in the configuration file for each command

**Support**
If you encounter any issues or have questions about the plugin, please create an issue on GitHub.


