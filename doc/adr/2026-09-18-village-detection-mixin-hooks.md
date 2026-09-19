---
status: accepted
date: 2026-09-18
---

# Mixin injection points for village detection and bell handling

## Context

The following writing assumes Mojang's obfuscation mappings.

A `Village` is defined by an anchor position and a bounding AABB (fixed at
creation, never recalculated):

- **Anchor**: calculated first. Determined from the jigsaw pool `town_centers`
  origin (world-gen), or a
  `BellBlock`'s `GlobalPos` (runtime, outside any existing village).
- **Radius**: pre-configured value (empirically determined).
- **Center**: centroid of `PoiTypes.HOME` (beds) within `radius` of anchor.
  Fallback if none found: `center = anchor`.
- **Height**: band centered on mean Y of those beds. Fallback: `anchor.Y`.

Two moments need separate handling, because neither can observe the other:

1. A chunk holding a village becomes resident (world-gen *or* an existing
   save loading for the first time under this mod).
2. A bell is placed live, in a chunk that's already loaded (the common
   case).

## Decision

### Hook 1 — Discovery: `LevelChunk#runPostLoad()V`

`@Inject` into this method. Fires exactly once per `LevelChunk` instance,
on the main thread, for both freshly-generated and loaded-from-disk
chunks alike (`ChunkStatusTasks.full(...)`). This works for world-gen
and in existing worlds.

Inside:
1. `StructureManager#startsForStructure(ChunkPos, Predicate)`
   filtered to `town_centers`-rooted structures (match by pool path
   suffix across biome variants, not a hardcoded `ResourceLocation`) →
   is this a village chunk?
2. If yes and no `Village` registered yet: anchor = piece origin;
   `PoiManager#getInRange`/`getInSquare` for `PoiTypes.HOME` near anchor →
   center/height per Context.
3. Scan chunk for `BellBlockEntity` (or POI `MEETING`) → attach to the
   resolved `Village`. (This is what covers the retrofit case too.)

**Why `PoiManager`, not `StructureManager`, for the extents query:**
`PoiManager` reads its own independent `poi/` storage — no extra chunk
load. `StructureManager#startsForStructure` forces a load to
`ChunkStatus.STRUCTURE_REFERENCES` as a side effect, so it's used
only for classification (step 1), never extents.

### Hook 2 — Runtime placement: `Block#onPlace(...)`

`@Inject` at `HEAD`, filtered to `state.is(Blocks.BELL)`, excluding
`movedByPiston` and `state.is(oldState.getBlock())` (state-property
change, not a new placement).

Confirmed in source (`LevelChunk#setBlockState`) that `onPlace` fires only
for blocks set into an already-resident `LevelChunk` — `ProtoChunk`
(world-gen) never calls it. So this hook is a strict complement to Hook 1,
never overlapping with it.

On fire: look up the position against the mod's own `Village` registry —
**not** `PoiManager`, since this bell's own POI record isn't guaranteed
populated yet (`ServerLevel#onBlockStateChange` defers `PoiManager.add`
via `getServer().execute(...)`). Outside all villages → create new
`Village` anchored here. Inside one → attach reference on the placed
bell's `BellBlockEntity`.

### Rejected alternatives

| Option | Why rejected |
|---|---|
| Mixin on `ChunkAccess#setBlockState` (world-gen bell detection) | Two separate targets (`LevelChunk` + `ProtoChunk`); needs worldgen-thread-safe deferral; superseded once Hook 1 covers it post-hoc |
| Mixin on `JigsawPlacement.addPieces` (generation-time) | `StructureStart` (incl. `startPool`, pieces) persists and is queryable after the fact — generation-time hook is redundant |
| Door-centroid via `StructureTemplate` | Doors aren't POI-indexed (removed pre-1.14 system); only works for world-gen villages, not player-built/extended ones; beds work for both |

## Consequences

- Village creation/lookup is entered from two independent mixins — must
  be idempotent, safe from either caller.
- Unproven (believed likely, not fully traced): freshly-generated chunk's
  deferred POI adds have landed by the time `runPostLoad` fires. Worth an
  empirical check (log bed counts at hook time) rather than trusting the
  scheduling argument alone.
- `Block#onPlace` fires for *every* block placement — `Blocks.BELL` check
  must be the first line in the injection.
- `movedByPiston`/same-block-state filters must track vanilla's own
  convention, or villages get double-created or bells get missed.
- Both targets are vanilla classes → mixins live in `common`, referenced from any loader-specific configs in their
  respective modules.
- Zero-bed villages fall back to `center = anchor`, height around
  `anchor.Y` — revisit if this proves too coarse in practice.
