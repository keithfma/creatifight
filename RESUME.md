# Creatifight — Project Resume Document

This document is a self-contained checkpoint so the Creatifight Minecraft mod project can be picked up from a different user account on the same laptop (or another machine), without losing any context.

When resuming, start a new Claude Code session in the project directory and say something like:

> Read `RESUME.md` in this directory and pick up where we left off. We were about to start Milestone 1, step "check existing Java installation."

---

## 1. Project summary

**Creatifight** is a Minecraft Java Edition mod that adds a new play style: full Creative-mode freedom (all resources, invulnerable, flight) combined with Survival-mode mob aggression (hostile mobs still chase and attack the player, though damage is silently blocked by creative invulnerability). The motivation is to give new/young players the *thrill* of building and fighting without the drudgery of resource gathering or the fear of dying.

The name is a portmanteau of *creative* + *fight*.

---

## 2. Current progress

**Phase:** Planning complete. Implementation begun.
**Active milestone:** M1 (Toolchain and "hello mod").
**Last completed step:** Verified Homebrew is installed at `/opt/homebrew/bin/brew`.
**Next step:** Check for existing Java installation (`/usr/libexec/java_home -V`), then install JDK 21 if missing.

**Todo state at pause:**

| # | Step | Status |
|---|---|---|
| 1 | Verify Homebrew is installed | ✅ completed |
| 2 | Check for existing Java installation | ▶ in progress (paused here) |
| 3 | Install JDK 21 (Temurin) via Homebrew | pending |
| 4 | Install IntelliJ IDEA Community Edition via Homebrew | pending |
| 5 | Download NeoForge MDK for Minecraft 1.21.x | pending |
| 6 | Unpack MDK into working directory | pending |
| 7 | Rename example mod id to `creatifight` and set metadata | pending |
| 8 | Open project in IntelliJ and trigger Gradle sync | pending |
| 9 | Run `runClient` task and verify Creatifight appears in the Mods screen | pending |

The working directory `/Users/keith/personal/creatifight/` is currently empty (apart from this RESUME.md). No code has been written yet.

---

## 3. User profile and working preferences (carry these into the new session)

These were saved as memories under the original user account; restate them at the top of the new session so the next Claude has the same context.

- **Programming background:** Experienced programmer, particularly fluent in **Python via VSCode**. New to **Java** but able to pick up the language quickly. Explain Java-specific concepts (Gradle, annotations, JVM toolchain, IDE differences) when they come up, but don't over-explain general programming.
- **IDE preference:** Default is VSCode, but open to switching when another IDE has a significantly smoother experience. For *this* project, we chose **IntelliJ IDEA Community Edition** because the NeoForge MDK ships `.idea` project config and run-task launchers — least friction. User prefers least-friction / best-DX over sticking with familiar tools.
- **Milestone cadence:** Stop at each milestone boundary and wait for the user to verify the work in-game before starting the next milestone. Do not chain M1 → M2 → M3 without a checkpoint. Auto-accept covers individual tool calls *within* a milestone, not milestone-to-milestone transitions.
- **Shell commands:** Prefer simple, single-purpose Bash commands. Avoid chaining many unrelated operations with `&&`. Run them as separate tool calls instead (in parallel when independent).

---

## 4. Approved implementation plan

The full plan is reproduced below. (Originals on the previous user account live at `~/.claude/plans/i-want-to-make-reactive-wall.md` — filename comes from an earlier mis-prompt; content is correct.)

---

### Approach

**Build on top of Creative mode rather than inventing a new mode from scratch.**

Vanilla Minecraft already provides every piece of behavior the user wants:
- Creative mode supplies invulnerability, flight, and the full creative-inventory UI.
- Survival-mode aggression is the default mob AI; it's *suppressed* against creative players by a small set of targeting filters (the `NearestAttackableTargetGoal` predicate and `Mob#canAttack`, both of which exclude players with `abilities.invulnerable`).

The mod's only real job is to flip that suppression for players who have opted into Creatifight mode. Damage is still cancelled by creative invulnerability, so mobs swing, lunge, and detonate but the player never actually dies.

**Loader: NeoForge** (1.21.x, Java 21). Chosen over Fabric because the mod's core work is intercepting vanilla AI decisions, which NeoForge exposes as event-bus hooks (`LivingChangeTargetEvent`, etc.) instead of requiring Mixins.

**Mode delivery: `/creatifight [target]` command — modeled on vanilla `/gamemode`.** A true 5th `GameType` enum entry is possible via NeoForge's enum extension but ripples through UI/serialization — overkill for a first mod. Instead the command sets the target player's game mode to `CREATIVE` *and* sets a persistent flag on them, atomically. To leave Creatifight, the player runs `/gamemode <anything-else>` as normal — the mod listens for game-mode changes and clears the flag automatically, so the flag can never go stale.

#### Why this shape (vs. `on|off`)?

| Aspect | Vanilla `/gamemode` | Our `/creatifight` |
|---|---|---|
| Syntax | `/gamemode <mode> [target]` | `/creatifight [target]` (mode is implicit) |
| Permission | Level 2 (op) | Level 2 (op) — same |
| Exit | Run another `/gamemode <mode>` | Run any `/gamemode <mode>` — flag clears automatically |
| Stateful "off"? | No — every entry is by `/gamemode <new>` | No — same model |

### Stack

| Piece | Choice |
|---|---|
| Edition | Minecraft Java Edition |
| Version | 1.21.x (latest stable at start of M1) |
| Loader | NeoForge |
| Language | Java 21 (required by MC 1.21) |
| Build | Gradle (comes with the NeoForge MDK template) |
| IDE | IntelliJ IDEA Community Edition |
| Mappings | Mojmap (NeoForge default — Mojang's official names) |

### Milestones

**Execution cadence:** Pause between milestones for the user to review and verify in-game before moving on.

#### M1 — Toolchain and "hello mod"
Goal: an empty mod that loads in a dev Minecraft and prints to the log.

1. Install **JDK 21** (Temurin via Homebrew: `brew install --cask temurin@21`).
2. Install **IntelliJ IDEA Community Edition** (free).
3. Download the **NeoForge MDK** for the target MC version from neoforged.net.
4. Unzip into the working directory; open in IntelliJ and let Gradle sync.
5. Rename the example mod id to `creatifight`, set `neoforge.mods.toml` metadata.
6. Run the pre-configured `runClient` task to launch a dev Minecraft.
7. Confirm the mod appears in the in-game Mods list.

**Verify:** Mods screen shows "Creatifight" with our metadata; no errors in the Gradle console.

#### M2 — `/creatifight` command + persistent player flag
Goal: a server command modeled on `/gamemode` that puts the target player into Creatifight, with the flag auto-clearing on any later game-mode change.

1. Register a Brigadier command `/creatifight [target]` via `RegisterCommandsEvent`. Gate with `.requires(src -> src.hasPermission(2))`.
2. Command effect (atomic): set the target's flag, then call `player.setGameMode(GameType.CREATIVE)`. Friendly feedback to source and (if different) target.
3. Store the per-player flag using NeoForge **Attachments** (`AttachmentType.builder(...).serialize(...).build()`).
4. Subscribe to `PlayerEvent.PlayerChangeGameModeEvent` — when a flagged player switches to any non-CREATIVE mode, clear the flag.
5. No-op cases: re-running `/creatifight` while already in Creatifight should be friendly.

**What `<target>` can be** (using vanilla's `EntityArgument.player()`):

| Form | Example | Means |
|---|---|---|
| (omitted) | `/creatifight` | the command sender; errors if console |
| Player name | `/creatifight Steve` | the player named Steve |
| Self selector | `/creatifight @s` | the entity executing the command |
| Nearest player | `/creatifight @p` | nearest player to the source |
| Random player | `/creatifight @r` | one random online player |
| UUID | `/creatifight 069a79f4-...` | the player with that UUID |
| Filtered selector | `/creatifight @p[distance=..10]` | nearest player within 10 blocks |

**Verify:**
- `/creatifight` → in Creatifight; creative inventory + flight + invulnerability active.
- Log out, log back in → still in Creatifight (flag persisted via Attachment).
- `/gamemode survival` → flag clears; later `/gamemode creative` returns to *regular* creative, NOT Creatifight.
- `/creatifight OtherPlayer` (multiplayer / second dev client) → other player switches.
- As a non-op, `/creatifight` is hidden / rejected.

#### M3 — Make mobs target Creatifight players
Goal: a zombie spawned near a Creatifight player walks toward them and swings.

1. Subscribe (server side, FORGE event bus):
   - Primary: `LivingChangeTargetEvent` — if proposed new target is null but a nearby player has the flag, set target to that player.
   - Backup: tick-based scan of `Mob` entities near flagged players, calling `mob.setTarget(player)` if hostile and no current target.
2. Determine "hostile mob" via `Enemy.class.isInstance(entity)`.
3. Don't make passive mobs aggressive — only act on mobs that normally would target players.

**Verify:** Summon zombie, skeleton, creeper near a Creatifight player. Mobs converge, swing, fire arrows, detonate. Player takes 0 damage.

#### M4 — Behavior polish

- Confirm creeper explosion is visual-only (no knockback / no damage), tune to taste.
- Confirm Endermen react to being looked at.
- Confirm phantoms / drowned / pillagers behave correctly.
- On `/gamemode survival` (exit), clear targets of currently-aggroed mobs that point at this player.

#### M5 — Stretch (defer until M1–M4 ship)

- True 5th `GameType.CREATIFIGHT` entry via NeoForge enum extension; shows up in `/gamemode <mode>` natively.
- Custom HUD icon for Creatifight mode.
- Server config for which mobs respect the mode.
- Multiplayer permission node tuning.

### Critical files (to be created)

- `build.gradle` — set `minecraft_version`, `neoforge_version`, `mod_id = creatifight`.
- `src/main/resources/META-INF/neoforge.mods.toml` — mod metadata.
- `src/main/java/<group>/creatifight/CreatifightMod.java` — `@Mod("creatifight")` entrypoint.
- `src/main/java/<group>/creatifight/CreatifightCommand.java` — Brigadier registration (M2).
- `src/main/java/<group>/creatifight/CreatifightFlag.java` — Attachment type + helpers (M2).
- `src/main/java/<group>/creatifight/GameModeChangeHandler.java` — auto-clear on game-mode change (M2).
- `src/main/java/<group>/creatifight/TargetingHandler.java` — targeting hook + tick scanner (M3).

### End-to-end test (after M3)

1. Launch dev Minecraft via IntelliJ's `runClient`.
2. Create a new world, set time to night.
3. `/creatifight`.
4. `/summon zombie ~ ~ ~3` ten times.
5. Expected: zombies converge, swing repeatedly, 0 damage. Creative inventory + flight work.
6. `/gamemode survival` → zombies stop pathing toward the player within ~1s; flag clears.

### Open questions

- Exact Minecraft target version (1.21.1 vs 1.21.4 vs newest) — pin to whatever NeoForge has the most stable MDK for at M1 time.
- Attachments vs scoreboard tags for the flag — defer to M2; both work. Plan calls for Attachments.

---

## 5. Environment notes (what was found on the original user account)

- macOS Darwin 24.5.0, zsh shell.
- Homebrew installed at `/opt/homebrew/bin/brew` (Apple Silicon path).
- Java installation status: **not yet checked at the time of pause** — first thing to do on resume is run `/usr/libexec/java_home -V` to see what JDKs (if any) are already present.
- Working directory: `/Users/keith/personal/creatifight` (on original user account). On the new user account, set the working directory accordingly under their home dir.

---

## 6. Resume instructions

When you (or the new user) restart on a different account:

1. Copy this `RESUME.md` to the new user's project directory (e.g., `~/personal/creatifight/RESUME.md`).
2. Open Claude Code in that directory.
3. Tell Claude:
   > Read RESUME.md. We're resuming an in-progress Minecraft mod project called Creatifight. Pick up at the next pending todo step. Honor the working preferences in §3.
4. Claude should re-create memory entries from §3 (so future sessions inherit them) and then continue from the "Check for existing Java installation" step.
