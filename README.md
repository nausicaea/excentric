# Excentric (Working Title)

This mod overhauls Minecraft's villagers, particularly their AI and the village as a unit. The aim is to use
utility-based NPC AI to govern villager actions. Similar to The Sims, the village unit also has needs that are evaluated
and met using the same AI system.

# Roadmap

- [x] Implement a utility AI architecture for villagers.
- [x] Implement the need hunger on villagers.
- [x] Implement a few services that satisfy hunger.
- [x] Make adjustments to the AI architecture based on findings from the previous two steps.
- [ ] Design and implement basic needs for villagers (nourishment, sleep, safety, social contact, etc.). Keep to the
  lowest level of Maslow's hierarchy of needs for now. Expand up along the hierarchy when the basic stuff runs.
- [ ] Design and implement basic services that satisfy those needs.
- [ ] Design and implement needs and services for the village.
- [ ] If necessary, implement optimisations for the reasoner and for pathfinding.

# Architecture

The architecture of the project is described in `doc/adr`.
