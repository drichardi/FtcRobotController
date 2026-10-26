# Blocks to Java: An 8-Week Robotics Programming Course

A course that takes students from FTC Blocks to confident Java, one concept at a time,
on a real robot. It is built for **8 weeks of 90-minute classes every other day (about 20
sessions)**, sharing time with building and fabrication.

---

## Course design

**1. Every student codes.** On most teams, one "programmer kid" ends up doing all the coding.
To prevent that, every student has their own laptop and their own folder in the group repo,
so every line they commit is theirs. Students sit with a **buddy** who is working on the same
lesson. Before moving on, they compare predictions and explain their code to each other. The
checkpoints are passed **individually**.

**2. Read before you write (PRIMM).** Blocks students aren't used to a blank Java file, so
every lesson starts from a *working* example and moves step by step toward writing their own:

| Step | Students… |
|---|---|
| **P**redict | read the code and predict what it will do |
| **R**un | test it on the robot and compare with their prediction |
| **I**nvestigate | answer guided questions and run small experiments |
| **M**odify | make small changes to the working code |
| **M**ake | write something new using the same idea |

**3. One CS concept per lesson, one robot skill per lesson.** Each lesson teaches one
computer science concept and one FTC skill at the same time.

**4. Programming doesn't depend on the robot being finished.** See [the bench board](#the-bench-board-the-most-important-setup-step).
Half the lessons need nothing but a Control Hub and a gamepad.

**5. The robot is the reward.** Every lesson ends with something that physically moves or
shows on the Driver Station.

---

## What's in this folder

```
curriculum/
├── README.md                  ← you are here (teacher guide)
├── capstone.md                ← final project + rubric
├── lessons/00 … 16-*.md       ← student handouts (00 = GitHub Desktop + project tour)
└── reference/
    ├── blocks-to-java.md      ← student cheat sheet (print it!)
    └── frc-bridge.md          ← how this maps to FRC / WPILib

TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/
├── L01_HelloRobot.java … L16_AutoWithClass.java   ← one working OpMode per lesson
└── RobotHardware.java                              ← the robot class (Lessons 15–16)
```

All 17 Java files compile against FTC SDK 12.0 (this fork's version). They show up on the
Driver Station under the group **Lessons**. Answer keys and checkpoint quizzes are kept
**outside** this repository so students can't see them.

---

## The 90-minute session template

| Minutes | Block | What happens |
|---|---|---|
| 0–10 | **Do Now** | Code-reading warm-up: put 5–8 lines on the board and have students predict the output. Use the lesson's *Predict* section or the previous lesson's exit ticket. |
| 10–50 | **Code block** | PRIMM lesson, each student on their own laptop, checking in with a buddy. Group members take turns deploying to the bench board or robot. |
| 50–85 | **Build block** | Fabrication and assembly. Programmers rotate in to test code on the robot. |
| 85–90 | **Exit ticket + notebook** | One question from the handout, plus a quick engineering-notebook entry. |

On build-heavy days, shorten the code block to 25 minutes and do Predict/Run/Investigate only.
Modify and Make can be finished next time. The schedule below marks those days.

---

## Scope and sequence

About 2.5 sessions per week. Adjust the week column to your A/B calendar.

| # | Wk | Lesson | CS concept | FTC skill | Code min |
|---|---|---|---|---|---|
| 1 | 1 | **Bridge day:** FTCSIM Intro + Movement puzzles in **Blocks**, then switch each solution to the **Java** view (30 min) · [00 Getting the Code](lessons/00-getting-the-code.md) **Part A**: fork, clone, open in Android Studio (45 min) | Text vs. blocks; version control | Simulator, GitHub Desktop, Android Studio | 75 |
| 2 | 1 | [00](lessons/00-getting-the-code.md) **Part B**: project tour, first deploy, first commit (25 min) · [01 Hello Robot](lessons/01-hello-robot.md) + Break-It Lab | Packages, project structure, syntax, errors | Deploying code, telemetry | 55 |
| 3 | 2 | [02 Drive Sequence](lessons/02-drive-sequence.md) | Sequential execution | Motors, direction, `sleep` | 40 |
| 4 | 2 | [03 Variables](lessons/03-variables.md) | Variables, types, operators | — | 40 |
| 5 | 2 | [04 Gamepad Input](lessons/04-gamepad-input.md) + [05 Tank Drive](lessons/05-tank-drive.md) | Input → Process → Output, the game loop | Gamepad, TeleOp | 45 |
| 6 | 3 | [06 Arcade Drive](lessons/06-arcade-drive.md) | Expressions, formatted output | Arcade drive, clipping | 35 |
| 7 | 3 | [07 Buttons and If](lessons/07-buttons-and-if.md) | `if` / `else if` / `else`, scope | Speed modes | 40 |
| 8 | 4 | [08 Sensors and Logic](lessons/08-sensors-and-logic.md) | Boolean logic, truth tables | Touch sensor, limit switches | 40 |
| 9 | 4 | **Checkpoint 1:** Bronze license *(build-heavy day)* | Units 1–3 review | Drive test | 25 |
| 10 | 4 | [09 Loops](lessons/09-loops.md) | `for` / `while`, off-by-one | Autonomous patterns | 40 |
| 11 | 5 | [10 Timer Auto](lessons/10-timer-auto.md) | Loop conditions, nested loops | `ElapsedTime`, non-blocking code | 40 |
| 12 | 5 | **FTCSIM Java challenge day** / catch-up *(build-heavy day)* | Practice | Sim puzzles in Java | 25 |
| 13 | 6 | [11 Methods](lessons/11-methods.md) | Methods, parameters, fields | Reusable drive commands | 40 |
| 14 | 6 | [12 Encoder Drive](lessons/12-encoder-drive.md) | Return values, constants, casting | Encoders, `RUN_TO_POSITION`, calibration | 45 |
| 15 | 6 | [13 Toggle](lessons/13-toggle.md) + **Checkpoint 2:** Silver license | State, edge detection | Servos, toggles | 45 |
| 16 | 7 | [14 State Machine Auto](lessons/14-state-machine-auto.md) | Enums, `switch`, state machines | Competition-style autonomous | 45 |
| 17 | 7 | [15 Robot Class TeleOp](lessons/15-robot-class-teleop.md) | Classes, objects, constructors, encapsulation | Hardware class pattern | 45 |
| 18 | 7 | [16 Robot Class Auto](lessons/16-robot-class-auto.md) + peer code review | Reuse, objects as parameters | Shared code across OpModes | 40 |
| 19 | 8 | [Capstone](capstone.md) work session | All of it | Team TeleOp + Auto | 60 |
| 20 | 8 | Capstone showcase + [FRC bridge](reference/frc-bridge.md) | Communicating code | Drive team demo | 60 |

### Units at a glance
1. **From Blocks to Java** (S1–3): syntax, sequence
2. **Variables and Input** (S4–6): types, expressions, the game loop
3. **Decisions** (S7–9): conditionals, boolean logic
4. **Loops and Time** (S10–12): iteration, non-blocking code
5. **Methods** (S13–15): abstraction, return values, state
6. **State Machines** (S16): enums, `switch`
7. **Objects** (S17–18): classes, encapsulation, reuse
8. **Capstone** (S19–20)

---

## Setup (do this before Session 1)

### How students get and share code
Students use **Android Studio** and **GitHub Desktop**. [Lesson 00](lessons/00-getting-the-code.md)
walks groups through it step by step. Here's how it fits together:

```
your fork (drichardi/FtcRobotController)      ← you add and update lessons here
   └── fork → captain/robotics-group-#        ← one per group of 3–4
                 └── clone → each student's laptop
                               └── TeamCode/.../teamcode/students/<name>/   ← each student's own folder
```

- **Each student copies lessons into their own folder** (`students/<name>`) and changes the
  OpMode `name` to something unique. Nobody ever edits the same file as someone else, so
  beginners never hit merge conflicts. The capstone is the one exception: groups split the
  files by owner.
- **Pushing new or fixed lessons to groups:** commit to your fork. Each group's Captain opens
  their repo on GitHub.com and clicks **Sync fork → Update branch**, and then everyone pulls.
  Because students never edit `lessons/`, this always goes through cleanly.
- **Checking participation:** each group repo's **Insights → Contributors** page and commit
  history show who committed what. Commits are signed with the name each student sets in
  Lesson 00, Step 3.
- **Fallback:** if a laptop can't run Android Studio, OnBot Java runs in a browser connected
  to the Control Hub. Students make a folder that matches the `package` line and paste the
  file in. There's no git in that workflow, though.

### Before Lesson 00 (allow a week)
- [ ] **GitHub accounts** for every student. GitHub requires users to be **13 or older**.
  Have students sign up ahead of time, because verification emails can get stuck in school
  filters. Collect usernames in a spreadsheet so Captains can invite teammates quickly.
  Teachers can get free benefits through GitHub Education.
- [ ] **Install Android Studio** (the fork's README requires *Narwhal 3 Feature Drop* or later)
  and **GitHub Desktop** on every lab computer.
- [ ] **Pre-warm Gradle.** The first time each computer opens the project, Android Studio
  downloads a large amount of tools and libraries into the *user's* home folder. If 25 laptops
  do this at once on school Wi-Fi, Session 1 is gone. Open the project once on every machine
  beforehand. If students log in with their own profiles, do it once per profile or plan
  extra time.
- [ ] **Network:** ask IT to allow `github.com`, `dl.google.com`, `maven.google.com`,
  `repo.maven.apache.org`, `services.gradle.org`, and `plugins.gradle.org`.
- [ ] **Clone folder:** pick a short local path that isn't synced to the cloud (for example
  `C:\Robotics`). OneDrive-synced folders cause random build failures.
- [ ] **USB-C data cables** for deploying to each Control Hub. Some cables only charge.
- [ ] Write your fork's URL in Step 1 of the Lesson 00 handout (or on the board).

*Alternative:* GitHub Classroom group assignments can create group repos for you and skip
Steps 1–2. However, a Classroom repo isn't a fork, so the one-click **Sync fork** for lesson
updates won't be there.

### Robot configuration
Every lesson uses these exact names. Create **one** configuration on each Control Hub:

| Name | Device | Port | Used in |
|---|---|---|---|
| `left_drive` | DC motor (with encoder cable) | Motor 0 | L02, L05–L07, L09–L12, L14–L16 |
| `right_drive` | DC motor (with encoder cable) | Motor 1 | same |
| `arm` | DC motor | Motor 2 | L08, L15–L16 |
| `claw` | Servo | Servo 0 | L13, L15–L16 |
| `touch_sensor` | REV Touch Sensor | Digital 1 (use the odd channel of the pair on REV hubs) | L08, L10, L14–L16 |

If a device is missing from the configuration, the OpMode stops with an error when you press
INIT. That makes a good teaching moment for students to read. Lessons **01, 03 and 04 need no
devices at all.**

### The bench board: the most important setup step
Programming usually gets left behind because the robot isn't done yet. To fix that, give each
team a **bench board**: a piece of plywood or a Tetrix/goBILDA plate with a Control Hub, battery,
switch, 2 motors with wheels or flags taped on, 1 servo, and 1 touch sensor, all wired to the
configuration above. Students can do every lesson on it while the real robot is being built.
Bring the code over to the real robot on integration days. Programmers will also learn wiring
along the way.

### FTCSIM
[FTCSIM](https://ftcsim.org) (from FIRST Robotics Canada) runs in a browser and supports
Blocks and OnBot Java. It has Intro, Movement, Sensors, and Puzzle tracks. Create a teacher
account before Session 1 so students can save work.
- **Session 1:** students solve Intro/Movement puzzles in **Blocks**, then switch to the **Java**
  view to see the same program as text. This works as a side-by-side translation guide.
- **Session 12 and early finishers:** solve Movement/Sensors puzzles directly in Java.
- The simulated robots use their own hardware names. Check them in each puzzle's Java view
  before pasting lesson code in.

---

## Assessment

**Exit tickets** (every session, ~2 min): one question at the bottom of each handout.
Use them to plan the next Do Now.

**Licenses** (individual, short practical + code reading):
| License | When | Practical | Code reading |
|---|---|---|---|
| 🥉 **Bronze** | Session 9 | From a blank OpMode, write arcade drive with a slow-mode button | Predict output of `if`/`else` and boolean expressions |
| 🥈 **Silver** | Session 15 | Write an autonomous that drives a square **using methods** and an `ElapsedTime` loop | Trace a loop; spot an off-by-one bug |
| 🥇 **Gold** | Capstone | Explain your part of the team's `RobotHardware` class and state machine | Live Q&A during the showcase |

A good rule: **only students with a Bronze license can drive the competition robot.** It
makes the programming matter to the whole team.

**Capstone:** team project with a 20-point rubric in [capstone.md](capstone.md).

---

## Differentiation

**For students who are flying:**
- Every *Make* section is open-ended. Push for clean code, not just working code.
- Extensions: IMU-based turning (`RobotAutoDriveByGyro_Linear` sample), mecanum drive
  (`BasicOmniOpMode_Linear` sample), AprilTags (`ConceptAprilTagEasy` sample). All of them are
  in this fork under `FtcRobotController/.../external/samples`.
- Split `RobotHardware` into `Drivetrain` and `Arm` classes (FRC subsystem style).

**For students who are struggling:**
- Let them build the idea in Blocks first, then translate it with the cheat sheet.
- Use **trace tables**: write each variable's value after every line on paper.
- Pair them with a patient buddy and have them explain each line back. Reading and explaining code counts as real programming practice.
- Lower the bar to Predict/Run/Investigate + Modify #1. Skip *Make*.

---

## Common problems and fixes

| Symptom | Likely cause |
|---|---|
| OpMode isn't on the Driver Station menu | Build didn't finish or failed; wrong annotation; looking under TeleOp for an `@Autonomous` |
| Error at INIT: "unable to find a hardware device" | Name in code doesn't match the configuration (check capitals and `_`) |
| Robot spins when it should drive straight | Missing or wrong `setDirection(REVERSE)` |
| Pushing stick up drives backward | Forgot the `-` on `left_stick_y` |
| Encoder drive never finishes | Encoder cable unplugged, or one motor's direction is wrong so it drives away from its target |
| Toggle flickers | No edge detection (see Lesson 13) |
| Robot doesn't stop when STOP is pressed | A loop is missing `opModeIsActive()` |

---

## After the 8 weeks
- Students make their own season repo from this fork and start a `teamcode` package for the real game.
- Keep the Do Now code-reading warm-ups going all season.
- FRC-bound students: read [frc-bridge.md](reference/frc-bridge.md) and then the WPILib "Zero to Robot" guide.
