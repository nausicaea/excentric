---
state: proposed
owner: nausicaea
acceptance_date:
modification_date: 2026-10-23
---

# Decision

- Client code lives in a separate `client` source set of the `fabric` module (Loom's `splitEnvironmentSourceSets()`),
  below `fabric/src/client`. It may use Fabric's client APIs and is used for debugging only (see ADR 001).
- Because of the client entrypoint, `fabric.mod.json` declares the environment `*`. The mod stays optional on clients:
  the server never requires it, and sends debug data only to clients that have registered the corresponding payload.
- The debug overlay shows, as a hovering text billboard above each villager:
  - the satisfaction of each of the villager's needs,
  - the currently active service: its type, a description (`Service.describe()`), the need it addresses, its base
    utility, its subjective score, and its selection weight.
- The villager's state is synchronised with a dedicated server-to-client custom payload (`excentric:villager_debug`).
  Vanilla entity data (`SynchedEntityData`) is not used, to avoid breaking clients without the mod.
- The payload is sent every 8 ticks, in one batch per player, covering the villagers within 32 blocks of the player.
- The payload is sent only to operators (permission level 2) who opted in with `/excentric debug needs [true|false]`.
  The opt-in is not persisted; it is cleared when the player disconnects.
- Logic that does not depend on a mod loader (snapshot types, their codecs, building snapshots from the needs and the
  reasoner) lies in `common`. Everything that uses Fabric's APIs (payload registration, command, rendering) lies under
  `net.nausicaea.excentric.fabric`.
