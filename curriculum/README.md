# Blocks to Java: A Robotics Programming Guide

A set of lessons, handouts, and working robot code to help students move from FTC Blocks to
confident Java. It isn't a fixed schedule. Use the lessons in order, at whatever pace your
build time allows, and skip or stretch as needed.

Everything points toward one shared goal: **each group gets its robot through a small
obstacle course**, first by driving it, then autonomously.

---

## How to use this guide
- **Lessons build on each other**, so go in order. Each one is a self-contained chunk of about
  30–45 minutes that can be taught by the teacher or worked through by students on their own.
- **Each handout uses the same five steps (PRIMM):** **P**redict what the code does → **R**un
  it → **I**nvestigate with guided questions → **M**odify it → **M**ake something new.
  Students always start from code that works, which is the gentlest move up from Blocks.
- **Each handout pairs driver control (TeleOp) with autonomous.** Every lesson teaches its idea
  in one mode, and the **🔁 Other half** section applies the same idea in the other mode. So
  both kinds of program grow together, and both show up on the obstacle course.
- **You don't need to finish every lesson.** The **core path** (Lessons 00–10) is enough to
  drive the course by hand and get through part of it autonomously. Lessons 11–16 make the
  code more precise and better organized.

### Habits that make it work
- **Every student codes.** Every student has their own laptop and their own folder in the
  group repo, so every line they commit is theirs. Students sit with a **buddy** working on
  the same lesson, and compare predictions and explain their code to each other before moving on.
- **Programming doesn't wait for the robot.** Give each group a [bench board](#the-bench-board)
  so code can be tested while the robot is being built. Several lessons need nothing but a
  Control Hub and a gamepad.
- **Start with reading, end with a reflection.** A good warm-up is 5–8 lines of code on the board
  with students predicting what it does. A good finish is the handout's one-question exit
  ticket plus a quick engineering-notebook entry.

---

## Learning path

| # | Lesson | CS idea | Robot skill | Main mode · 🔁 other half | Hardware |
|---|---|---|---|---|---|
| 00 | [Getting the Code](lessons/00-getting-the-code.md) | Version control, packages | GitHub Desktop, Android Studio, deploying | — | Control Hub |
| 01 | [Hello Robot](lessons/01-hello-robot.md) | Syntax, comments, errors | Telemetry | TeleOp · Auto | Hub only |
| 02 | [Drive Sequence](lessons/02-drive-sequence.md) | Sequence | Motors, `sleep` | Auto | Drive motors |
| 03 | [Variables](lessons/03-variables.md) | Variables, types, operators | — | (none) · Auto | Hub only |
| 04 | [Gamepad Input](lessons/04-gamepad-input.md) | Input, the game loop | Gamepad | TeleOp · Auto | Hub + gamepad |
| 05 | [Tank Drive](lessons/05-tank-drive.md) | Input → process → output | Driving by hand | TeleOp · Auto | Drive motors |
| 06 | [Arcade Drive](lessons/06-arcade-drive.md) | Expressions | Arcade drive, arcs | TeleOp · Auto | Drive motors |
| 07 | [Buttons and If](lessons/07-buttons-and-if.md) | `if`/`else`, scope | Speed modes, choosing a route | TeleOp · Auto | Drive motors |
| 08 | [Sensors and Logic](lessons/08-sensors-and-logic.md) | Boolean logic | Touch sensor, arm limit | TeleOp · Auto | Arm + touch sensor |
| 09 | [Loops](lessons/09-loops.md) | `for`/`while`, off-by-one | Repeated moves | Auto · TeleOp | Drive motors |
| 10 | [Timer Auto](lessons/10-timer-auto.md) | Loop conditions | Timers, stop on a sensor | Auto · TeleOp | Drive + touch sensor |
| 11 | [Methods](lessons/11-methods.md) | Methods, parameters | Reusable moves | Auto · TeleOp | Drive motors |
| 12 | [Encoder Drive](lessons/12-encoder-drive.md) | Return values, constants | Driving by inches | Auto · TeleOp | Drive motors with encoders |
| 13 | [Toggle](lessons/13-toggle.md) | State, edge detection | Claw toggle, picking a route during INIT | TeleOp · Auto | Claw servo |
| 14 | [State Machine](lessons/14-state-machine-auto.md) | Enums, `switch` | Multi-step routines | Auto · TeleOp | Drive + touch sensor |
| 15 | [Robot Class: TeleOp](lessons/15-robot-class-teleop.md) | Classes, objects | One hardware class for all programs | TeleOp · Auto | Everything |
| 16 | [Robot Class: Auto](lessons/16-robot-class-auto.md) | Reuse | Shared code across programs | Auto · TeleOp | Everything |
| 🏁 | [Obstacle Course](obstacle-course.md) | Design → test → improve | Run the course by hand and autonomously | Both | Everything |

**Optional warm-up before Lesson 01:** [FTCSIM](#ftcsim). Students solve a few puzzles in Blocks,
then switch to the Java view to see the same program as text.

**Good times for an obstacle course run:** after **Lesson 07** (drive it by hand with speed
modes), after **Lesson 10** (first autonomous section using timers), and after **Lesson 12 or 14**
(more accurate autonomous). See [Obstacle Course](obstacle-course.md) for levels and logistics.

---

## What's in this repo

```
curriculum/
├── README.md                     ← this guide
├── obstacle-course.md            ← course ideas, levels, run log, one-group-at-a-time logistics
├── lessons/00 … 16-*.md          ← student handouts
└── reference/
    ├── blocks-to-java.md         ← cheat sheet (print it!)
    ├── deploying-and-managing.md ← deploying (USB/Wi-Fi), cleaning up the menu, deploy troubleshooting
    └── frc-bridge.md             ← how all of this carries over to FRC

TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/
├── L01_HelloRobot.java … L16_AutoWithClass.java   ← one working OpMode per lesson
└── RobotHardware.java                              ← the robot class (Lessons 15–16)
```

All 17 Java files compile against FTC SDK 12.0 (this fork's version). The original lesson
OpModes are marked **`@Disabled`** so they don't fill up the Driver Station menu. Students delete
that line in their own copies (Lesson 00, Step 7). To demo an original yourself, remove
`@Disabled` locally, deploy, and don't commit that change. The answer key and optional skill
checks are kept **outside** this repository so students can't see them.

---

## Setup

### How students get and share code
Students use **Android Studio** and **GitHub Desktop**. [Lesson 00](lessons/00-getting-the-code.md)
walks groups through it step by step. Here's how it fits together:

```
your fork (drichardi/FtcRobotController)      ← you add and update lessons here
   └── fork → captain/robotics-group-#        ← one per group of 3–4
                 └── clone → each student's laptop
                               └── TeamCode/.../teamcode/students/<name>/   ← each student's own folder
```

- **Each student copies lessons into their own folder** (`students/<name>`), gives each copy a
  unique name, and deletes `@Disabled`. Nobody ever edits the same file as someone else, so
  beginners never hit merge conflicts. Obstacle-course code is the one exception: groups share
  a folder and split the files by owner.
- **Pushing new or fixed lessons to groups:** commit to your fork. Each group's Captain opens
  their repo on GitHub.com and clicks **Sync fork → Update branch**, and then everyone pulls.
  Because students never edit `lessons/`, this always goes through cleanly.
- **Checking participation:** each group repo's **Insights → Contributors** page and commit
  history show who committed what.

### Before Lesson 00 (allow a week)
- [ ] **GitHub accounts** for every student. GitHub requires users to be **13 or older**.
  Have students sign up ahead of time, because verification emails can get stuck in school
  filters. Collect usernames so Captains can invite teammates quickly.
- [ ] **Install Android Studio** (the fork's README requires *Narwhal 3 Feature Drop* or later)
  and **GitHub Desktop** on every student laptop.
- [ ] **Pre-warm Gradle.** The first time each laptop opens the project, Android Studio
  downloads a large amount of tools and libraries. If every laptop does this at once on school
  Wi-Fi, it can take a whole period. Open the project once on each machine beforehand. If
  students log in with their own profiles, do it once per profile or plan extra time.
- [ ] **Network:** ask IT to allow `github.com`, `dl.google.com`, `maven.google.com`,
  `repo.maven.apache.org`, `services.gradle.org`, and `plugins.gradle.org`.
- [ ] **Clone folder:** pick a short local path that isn't synced to the cloud (for example
  `C:\Robotics`). OneDrive-synced folders cause random build failures.
- [ ] **USB-C data cables** for deploying to each Control Hub. Some cables only charge.
- [ ] **Clear old Blocks and OnBot Java programs** off each Control Hub. An Android Studio deploy
  doesn't remove them, so they'd stay on the menu. Download any worth keeping first. Steps are in
  [deploying-and-managing.md](reference/deploying-and-managing.md#leftover-blocks-and-onbot-java-programs).
- [ ] Write your fork's URL in Step 1 of the Lesson 00 handout (or on the board).

*Alternative:* GitHub Classroom group assignments can create group repos for you and skip
Steps 1–2 of Lesson 00. However, a Classroom repo isn't a fork, so the one-click **Sync fork**
for lesson updates won't be there.

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

### The bench board
Programming usually gets left behind because the robot isn't done yet. To fix that, give each
group a **bench board**: a piece of plywood or a Tetrix/goBILDA plate with a Control Hub,
battery, switch, 2 motors with wheels or flags taped on, 1 servo, and 1 touch sensor, all wired
to the configuration above. Students can do every lesson on it while the real robot is being
built, then move their code to the robot for obstacle course runs. Programmers learn wiring
along the way, and groups waiting for the course have something real to test on.

### FTCSIM
[FTCSIM](https://ftcsim.org) (from FIRST Robotics Canada) runs in a browser and supports Blocks
and OnBot Java. It has Intro, Movement, Sensors, and Puzzle tracks. Create a teacher account so
students can save work. It's useful as a warm-up before Lesson 01, and as extra practice for
anyone waiting on hardware. The simulated robots use their own hardware names, so check them in
each puzzle's Java view before pasting lesson code in.

---

## Checking understanding
- **Exit tickets:** one question at the bottom of each handout. Use them to plan the next warm-up.
- **Optional skill checks** (in the teacher answer key): two short individual checks, each with a
  hands-on task and a code-reading quiz.
  - **Ready to drive** (after Lesson 08): write arcade drive with a slow mode from a blank
    OpMode. Consider requiring it before a student drives on the obstacle course.
  - **Ready for autonomous** (after Lesson 13): drive a square using methods and a timer loop.
- **Obstacle course run log:** every group records what it changed between runs and why
  (template in [obstacle-course.md](obstacle-course.md)). This is the clearest picture of how
  well a group is thinking like engineers.

---

## Supporting different learners

**For students who are flying:**
- Every *Make* and *🔁 Other half* section is open-ended. Push for clean code, not just working code.
- Extensions: IMU-based turning (`RobotAutoDriveByGyro_Linear` sample), mecanum drive
  (`BasicOmniOpMode_Linear` sample), AprilTags (`ConceptAprilTagEasy` sample). All of them are
  in this fork under `FtcRobotController/.../external/samples`.
- Split `RobotHardware` into `Drivetrain` and `Arm` classes (FRC subsystem style).

**For students who are struggling:**
- Let them build the idea in Blocks first, then translate it with the cheat sheet.
- Use **trace tables**: write each variable's value after every line on paper.
- Pair them with a patient buddy and have them explain each line back. Reading and explaining
  code counts as real programming practice.
- Lower the bar to Predict/Run/Investigate + Modify #1. Skip *Make* and *Other half*.

---

## Common problems and fixes

| Symptom | Likely cause |
|---|---|
| OpMode isn't on the Driver Station menu | Student didn't delete `@Disabled` in their copy; build didn't finish or failed; looking under TeleOp for an `@Autonomous` |
| Teammate's program vanished, or a deleted one came back | Someone deployed without pulling first. See [deploying-and-managing.md](reference/deploying-and-managing.md) |
| Nobody in a group can deploy | Someone pushed code that doesn't build. The **Build** tab shows whose file. |
| Error at INIT: "unable to find a hardware device" | Name in code doesn't match the configuration (check capitals and `_`) |
| Robot spins when it should drive straight | Missing or wrong `setDirection(REVERSE)` |
| Pushing stick up drives backward | Forgot the `-` on `left_stick_y` |
| Encoder drive never finishes | Encoder cable unplugged, or one motor's direction is wrong so it drives away from its target |
| Toggle flickers | No edge detection (see Lesson 13) |
| Robot doesn't stop when STOP is pressed | A loop is missing `opModeIsActive()` |
| Timed autonomous worked yesterday, overshoots today | Battery charge changed. That's the reason for Lesson 12 (encoders). |

---

## Going further
- Keep the code-reading warm-ups going after the lessons end.
- When students start on a real FTC robot, they can start a fresh package for that robot and
  copy in their `RobotHardware` class as a starting point.
- FRC-bound students: read [frc-bridge.md](reference/frc-bridge.md) and then the WPILib
  "Zero to Robot" guide.
