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
- The mod is designed to run _exclusively_ on Minecraft servers.
- Client code may be used for debugging only.
- Initially, the mod is designed for fabric.
