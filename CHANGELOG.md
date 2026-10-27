## 0.3.0 (2026-10-27)

### BREAKING CHANGE

- JDK 21 is not supported anymore

### Feat

- **NeedsCollection**: decayfirst delta uses the whole game time needs are not persisted (#46)
- **debug**: add a client overlay for villager needs and services (#48)

### Fix

- Position.poll arrival check disagrees with path accuracy stuck navigator never retried (#53)
- **ServiceFuture**: check for service expiration (#49)
- **deps**: update dependency net.fabricmc:fabric-loader to v0.20.0 (#45)
- **NeedsReasoner**: when the total urgency is approximately zero, don't return any services from score() (#47)

## 0.2.0 (2026-10-18)

### BREAKING CHANGE

- This change fixes a bad search inefficiency for the item search volume.

### Feat

- **FabricNeedsContext**: add BedBlock collection via PoiManager (#41)
- **needs**: add a restedness need (#26)

### Fix

- **Reasoner**: implement service hysteresis (#38)
- **deps**: update dependency net.fabricmc:fabric-language-kotlin to v1.14.2+kotlin.2.4.21 (#25)

## 0.1.0 (2026-10-16)

### Feat

- **Villager**: draft an AI system for Villagers (#15)
- remove the dependency on cardinal components (#14)
- **village_manager**: add recurring reconciliation and debug visualisation of village bounding boxes
- **village_manager**: perform reconciliation on chunks neighbouring the new village center
- **village_manager**: add a fixme comment on reconcileChunk to illustrate its inefficiency
- **village_manager**: implement village searching and claiming
- **village_manager**: construct the chunk's AABB with correct minimum Z coordinate
- **village_manager**: find village centers more narrowly by matching for "town_centers"
- **mixin**: implement the onBlockPlace and onChunkLoad hooks
- **bell**: implement the BellBlock::onPlace hook
- **village**: add an implementation plan for each of the mixins and new functionality
- **village**: add scaffolding for the village creation code
- add scaffolding for VillageManager and per-village data
- add the fabric template

### Fix

- apply Minecraft package layout (#10)
- **deps**: update dependency spotbugs to v4.10.4 (#6)
- **villager_mixin**: add a null check for missing village ID during serialization
- **village_manager**: clarify that searching for villagers and bells in a large volume is deferred for later
- **bell_block_mixin**: add early exists for non-bell blocks, for piston movement, and for block state changes
- **block_behaviour_mixin**: inject into HEAD as the ADR states (not TAIL)
- **villager_mixin**: also persist the village ID
- add implementation notices (exceptions) as default interface implementations for VillagerDataHolder
- remove run configurations from the common module

### Refactor

- move all code to the fabric module and begin using CCA (#1)
- **village_manager**: factor out chunk reconciliation
- remove the public visibility keyword from mixins
- rename the logger variable
- reformat the code
- reformat the file
- remove the CCA dependency, and add spongepowered's mixins as compile-time dependency to common
- **fabric**: rename the fabric mod entrypoint
- configure tabs for indentation, and version catalogs for dependencies
- remove unnecessary comments
- split the code into loader-independent and fabric-only code
