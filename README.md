# TeleHome

A lightweight **Paper/Spigot plugin** for Minecraft that lets players save their favorite locations and teleport back to them instantly — or with a configurable countdown delay.

Built for **Paper 1.21+** on **Java 21**.

---

## ✨ Features

- 🏠 **Set unlimited named homes** — `/sethome castle`, `/sethome mine`, `/sethome farm`...
- ⚡ **Teleport back anytime** — `/home castle` returns you with exact position, facing direction, and pitch.
- 💾 **Persistent storage** — homes are saved per-player (by UUID) and survive restarts, name changes, and world reloads.
- ⏱ **Configurable teleport delay** — instant or countdown teleport (e.g. 3 seconds).
- 🚶 **Move-to-cancel** — optionally cancel teleport if the player moves during the countdown.
- 🌍 **Multi-world support** — homes remember which world they belong to.
- 🎨 **Fully customizable messages** via `config.yml`.

---

## 📦 Installation

1. Download the latest `TeleHome.jar` from the [Releases](../../releases) page.
2. Drop it into your server's `plugins/` folder.
3. Start (or restart) your server.
4. Configuration is generated at `plugins/TeleHome/config.yml`.

---

## 🎮 Commands

Command: Description: Permission

/sethome <name>
Set a home at your current location

telehome.sethome
/home <name>	

Teleport to one of your homes
telehome.home

Home name rules: letters, numbers, underscores, and dashes. Max 32 characters.

🔐 Permissions

Permission: Default Setting: Description:

telehome.sethome - true - Allows players to set homes

telehome.home - true - Allows players to teleport to homes

⚙️ Configuration

  Default config.yml:

  config.yml
  # Delay in seconds before the player is teleported after using /home.
  # Set to 0 for instant teleport.
  teleport-delay-seconds: 3

  # If true, the teleport will be cancelled if the player moves during the delay.
  cancel-on-move: true

  # Messages (supports legacy & color codes)
  messages:
    home-set: "&aHome '&e%name%&a' has been set!"
    home-not-found: "&cNo home found with the name '&e%name%&c'."
    teleporting: "&aTeleporting to '&e%name%&a' in &e%seconds% &aseconds. Don't move!"
    teleported: "&aTeleported to '&e%name%&a'!"
    teleport-cancelled: "&cTeleport cancelled because you moved!"
    world-missing: "&cThe world for that home no longer exists."
    usage-sethome: "&cUsage: /sethome <name>"
    usage-home: "&cUsage: /home <name>"
    players-only: "&cOnly players can use this command."

Placeholders:

    %name% — the home name
    %seconds% — the configured teleport delay

💾 Data Storage

Homes are stored in plugins/TeleHome/homes.yml, keyed by player UUID:

e5f1d2a7-...-...:
  castle:
    world: world
    x: 123.5
    y: 64.0
    z: -452.3
    yaw: 90.0
    pitch: 0.0

Because UUIDs are used instead of usernames, homes persist through Mojang name changes.

🛠 Building from Source

Requirements:

    Java 21+
    Maven 3.9+

Build:

bash
git clone https://github.com/ripsnortntear/TeleHome.git
cd TeleHome
mvn clean package

The compiled jar will be located at target/TeleHome.jar.

🧪 Tested On

    Paper 1.21.4
    Java 21

🗺 Roadmap

Planned features for future releases:

    [ ] /delhome <name> — delete a home
    [ ] /homes — list your homes
    [ ] Tab completion for home names
    [ ] Per-rank home limits (permission-based)
    [ ] Teleport cooldowns
    [ ] Cross-world teleport toggle
    [ ] Optional cost system (economy integration)
    [ ] Bed/respawn-home integration

Got a suggestion? Open an issue!

🐛 Issues & Contributions

    Bugs / feature requests: open an issue
    Pull requests: welcome! Please target the main branch and describe your changes clearly.

📄 License

This project is licensed under the MIT License — see the LICENSE file for details.

❤️ Credits

Built with Paper API.
