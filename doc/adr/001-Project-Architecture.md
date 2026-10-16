---
state: accepted
owner: nausicaea
acceptance_date: 2026-10-15
modification_date: 2026-10-15
---

# Decision

- The `main` branch is the productive branch.
- Git commit messages follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
- Git branch names follow [Conventional Branches](https://conventionalbranch.org)
- The `gradle` build system is used.
- The source code is split into common and mod-loader-specific source sets.
- The code shall be written in Kotlin and use functional paradigms where possible.
- The mod is designed to run _exclusively_ on Minecraft servers.
- Client code may be used for debugging only.
- Initially, the mod is designed for fabric.
- The root package name is `net.nausicaea.excentric` (adapt to the final title once known).
- Packages that modify vanilla Minecraft behavior shall lie under `net.nausicaea.excentric.minecraft` and follow
  Minecraft's own package naming scheme.
- Packages that rely on Fabric's APIs shall lie under `net.nausicaea.excentric.fabric` and clearly separate the used
  APIs.
