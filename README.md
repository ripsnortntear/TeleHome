# TeleHome

[![Minecraft](https://img.shields.io/badge/Minecraft-26.3-brightgreen.svg)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://adoptium.net)
[![License](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)
[![Release](https://img.shields.io/badge/Release-v1.3.0-blue.svg)](https://github.com/ripsnortntear/TeleHome/releases)

A modern, high-performance **Paper/Spigot** home and teleportation plugin for Minecraft 26.3.  
Save custom homes, view clickable home lists, return to your previous location, and teleport to your last death point — all with customizable warmup delays, move/damage cancellation, and permissions.

---

## ✨ Features

- 🏠 **Named Homes** — `/sethome <name>`, `/home [name]`, and `/delhome <name>` with case-insensitive lookup while preserving your exact naming case.
- 📜 **Clickable Home List** — `/listhome` displays your homes with coordinates and lets you click any home in chat to teleport instantly!
- 💀 **Death Teleportation** — `/death` teleports you back to where you died to quickly retrieve your items.
- 🔄 **Back Teleportation** — `/back` returns you to your previous location prior to teleporting.
- ⏱️ **Warmup Countdown & Cancellation** — Configurable warmup delay with automatic cancellation if a player moves or takes damage.
- 🛡️ **Home Limits & Tiered Permissions** — Configurable default home cap (e.g. 10), permission-based caps (`telehome.max.<n>`), and bypass permissions (`telehome.bypass.limit`).
- ⚡ **Delay Bypass** — Players or VIP ranks with `telehome.bypass.delay` teleport immediately without waiting.
- 💾 **Reliable YAML Storage** — Homes and death/back locations are stored separately (`homes.yml` & `deathback.yml`) by player UUID to survive name changes and server restarts.
- 🔄 **Hot-Reloadable** — `/telehome reload` reloads all configuration and data files in real time without restarting the server.
- 🎨 **Adventure & Legacy Color Support** — Clean color formatting with support for `&` color codes and hex colors.

---

## 📦 Installation

1. Download `TeleHome-1.3.0.jar` from the [Releases](https://github.com/ripsnortntear/TeleHome/releases) tab.
2. Place the `.jar` into your server's `plugins/` directory.
3. Ensure your server is running **Paper 26.3** with **Java 25**.
4. Start or restart your server. Configuration files will be generated in `plugins/TeleHome/`.

---

## 🎮 Commands & Aliases

| Command | Aliases | Description | Permission |
| :--- | :--- | :--- | :--- |
| `/sethome <name>` | — | Sets a home at your current coordinates | `telehome.sethome` |
| `/home [name]` | — | Teleports to the specified home (or default) | `telehome.home` |
| `/delhome <name>` | `/rmhome`, `/removehome` | Deletes the specified home | `telehome.delhome` |
| `/listhome` | `/homes`, `/listhomes` | Lists all your homes with clickable chat teleport | `telehome.listhome` |
| `/death` | `/lastdeath` | Teleports to your last death location | `telehome.death` |
| `/back` | `/return` | Teleports to your location prior to last teleport | `telehome.back` |
| `/telehome reload` | `/th reload` | Reloads `config.yml`, `homes.yml`, and `deathback.yml` | `telehome.admin` |
| `/telehome version`| `/th version`| Displays version and compatibility info | `telehome.admin` |
| `/telehome help`   | `/th help`   | Displays command reference | `telehome.admin` |

---

## 🔐 Permissions

| Permission | Description | Default |
| :--- | :--- | :--- |
| `telehome.sethome` | Allows creating/updating homes | `true` (Everyone) |
| `telehome.home` | Allows teleporting to homes | `true` (Everyone) |
| `telehome.delhome` | Allows deleting homes | `true` (Everyone) |
| `telehome.listhome` | Allows listing your saved homes | `true` (Everyone) |
| `telehome.death` | Allows teleporting to your last death point | `true` (Everyone) |
| `telehome.back` | Allows returning to your prior teleport location | `true` (Everyone) |
| `telehome.bypass.delay` | Bypasses warmup teleport delay | `op` |
| `telehome.bypass.limit` | Allows setting unlimited homes | `op` |
| `telehome.max.<amount>` | Grants custom max homes limit (e.g. `telehome.max.20`) | `none` |
| `telehome.admin` | Access to `/telehome reload` and administrative commands | `op` |

---

## ⚙️ Configuration (`config.yml`)

```yaml
# ==============================================================================
#                     TeleHome Configuration (Minecraft 26.3)
# ==============================================================================

# Delay in seconds before a teleport completes. Set to 0 for instant teleport.
teleport-delay-seconds: 3

# Apply warmup delay to /death and /back commands as well.
delay-death-and-back: true

# If true, teleport will be cancelled if the player moves more than 0.5 blocks during warmup.
cancel-on-move: true

# If true, teleport will be cancelled if the player takes damage during warmup.
cancel-on-damage: true

# Maximum number of homes a player can set by default.
# Players with 'telehome.bypass.limit' or 'telehome.admin' have unlimited homes.
# Custom limits can also be given via permission: telehome.max.<amount> (e.g. telehome.max.20)
max-homes: 10

# Commands enabled
features:
  death-command: true
  back-command: true

# Messages (supports '&' color codes and Adventure format)
messages:
  # ... customizable messages
```

---

## 🛠️ Building from Source

Requires **Java 25** and **Maven 3.9+**:

```bash
git clone https://github.com/ripsnortntear/TeleHome.git
cd TeleHome
mvn clean package
```

The compiled shaded jar will be located in `target/TeleHome-1.3.0.jar`.

---

## 📄 License

This project is licensed under the terms of the GNU General Public License v3.0 (GPLv3). See the [LICENSE](LICENSE) file for details.
