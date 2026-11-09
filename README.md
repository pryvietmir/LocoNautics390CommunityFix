# Loconautics 0.39 Community Fix

Unofficial fix mod for **Create: Loconautics 0.39** (NeoForge 1.21.1).
Install it next to the original `loconautics-0.39.jar`. It does not contain or modify any Loconautics files.

## What it fixes

- **Block placement on dedicated servers.** No block could be placed on a server: every right-click crashed the event because a server-side handler loaded a client-only class.
- **Diesel engine crash without Create Propulsion.** Placing, loading or viewing the engine in Ponder no longer crashes when Create Propulsion is not installed.
- **Block variant exploit.** The server rejects variant-change packets that swap a block into a different block family (item duplication / conversion).
- **Train relocation exploit.** The server checks that the player actually holds the Creative Train Relocator.
- **Moving other players' trains.** Optional server setting `requireOwnership`: only the train's owner can relocate it.
- **Debug lines over wheels.** The green / red / yellow wheel debug lines are no longer shown.
- **Client memory leak.** Cached client world data is cleared when leaving a server.
- **Wasted server ticks.** The stall watchdog runs only when tracing is enabled.
- **Log spam.** Rejected packets are logged at most once per 10 s per player; noisy INFO logs can be turned off.
- Player messages in English and Russian.

## Install

Put `loconautics_fixes-<version>.jar` into `mods/` on the **server and every client**.

The fixes are tied to Loconautics 0.39 internals. On any other Loconautics version the mod disables itself and says so in the log instead of crashing.
With a rebuilt source fork (`0.39+...`) only the fixes that the fork does not already contain are applied.

## Config

`<world>/serverconfig/loconautics_fixes-server.toml`:

```toml
[relocation]
requireOwnership = false   # only the owner may relocate a train
```

`config/loconautics_fixes-common.toml`:

```toml
[logging]
quietInfo = true           # hide Loconautics debug INFO logs
```

## Build

1. Put the compile-only jars listed in `libs/README.txt` into `libs/`.
2. Run `gradlew build` with **JDK 21**.
3. The jar is in `build/libs/`.

## Status

Each mixin target was checked against the Loconautics 0.39 bytecode, but the fixes have had limited in-game testing.
Please report problems with your `latest.log`.

## Credits

Create: Loconautics by Lycoris, MDLOP, Basilforrealss, TugKanTheMan and Hatchet (code under MIT).
This fix mod is not affiliated with the Loconautics team.
