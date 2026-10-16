---
state: accepted
owner: nausicaea
acceptance_date: 2026-10-15
modification_date: 2026-10-15
---

# Decision

- Architectural decisions shall be maintained as architecture decision records in `doc/adr`.
- Project dependencies shall be maintained
  as [version catalogues](https://docs.gradle.org/current/userguide/version_catalogs.html), for example
  `gradle/libs.versions.toml`.
- Developers shall activate and maintain pre-commit and pre-push hooks using `pre-commit`, `prek`, or equivalent
  technology.
- Automated pull requests for dependency and security updates shall be set up, for example using Mend.io's Renovate.
- Automated CI builds and tests (ex. `.github/workflows/build.yml`) shall be set up and maintained.
