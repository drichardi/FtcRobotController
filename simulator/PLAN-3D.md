# Plan: 3D Simulator and Robot Builder

**Branch:** `sim-3d` · **Status:** Phases 0, 1, and 3 built (Phase 2 came with Phase 0); waiting for a MacBook Air check

## The idea
Students build a robot from a parts menu, including **their own 3D-printed parts**, wire it to
the Control Hub, configure it, program it, and test it, all on a laptop. The current 2D
simulator stays as it is; **3D is a toggle**.

> Pick a part from a menu with icons → click to place it → after the first piece, parts snap
> into place. No screws.

## What we know about the class
| | |
|---|---|
| **Laptops** | MacBook Air, all 2020 or newer (Apple Silicon). This is the main target. |
| **Parts** | Generic, not brand-specific. |
| **Motors** | Includes TETRIX motors (model unknown: default 1440 ticks per turn, editable). |
| **Wheels** | Some groups use **mecanum** wheels (they can drive sideways). |
| **Challenge** | Students **3D print parts** to handle game objects: they **push a ball** and **lift a block** by hooking a metal attachment on its top. |

## Short verdict
**Worth doing, as a planning and learning tool, built in stages with a go/no-go test first.**

- **Why it's worth it:** it connects the whole process. Today students learn to code a robot
  someone else designed. With a builder, *their* design decisions (wheel size, where the color
  sensor goes, the shape of their printed scoop) change how *their* code behaves. That's the
  link between building and programming that most programs never teach.
- **It won't replace building.** No tolerances, screws, flex, layer lines, or broken parts. Use it
  to plan a design, try a printed part *before* spending hours printing it, practice at home, and
  test code before the real robot exists.
- **The biggest risks:** how real the printed-part physics needs to be (see the choice below),
  Java 3D on Macs, and letting the builder grow into a CAD program. The plan handles all three
  with a test first and a small parts list that grows only when needed.

## What stays the same
- **Student code doesn't change.** Same `hardwareMap.get`, same lessons, same missions.
- **CourseBot stays the default.** It becomes a prebuilt design, so nobody has to build anything
  to do Lessons 01–18.
- **Configure Robot stays.** The builder adds the step before it: *which port is each cable
  plugged into?* Configure Robot still decides *what each port is called*.

## What students would do
| Step | Where | What they learn |
|---|---|---|
| 1. **Build** | Builder (3D) | Pick parts, snap them together, add their own printed parts. See the parts list fill in. |
| 2. **Wire** | Builder, wiring view | Click a motor, click a hub port. Cables show up. |
| 3. **Check** | Robot Check panel | Like inspection: fits the 18" box? Hub and battery? Every motor wired? |
| 4. **Configure** | Configure Robot (exists today) | Name each port. |
| 5. **Code** | Android Studio (exists today) | Same as now. |
| 6. **Test** | Simulator, 2D or 3D | Their design changes how the robot drives, reaches, and handles blocks and balls. |

---

## Your own 3D-printed parts (STL)
**Yes, students can add their STL files as parts.** It's the same file they send to the printer.

**How it works for a student:**
1. In the builder, choose **Add my part** and pick the `.stl` file.
2. The simulator reads its size in millimeters (what Tinkercad, Onshape, and Fusion export) and
   turns it upright (CAD files are often lying on their side).
3. They choose **how it attaches**: bolted flat to a channel or plate, on a servo horn, or on a
   motor shaft. Then they click the spot on their part that touches the mount.
4. It appears in the parts menu with its own icon, and snaps like any other part.

**Checks that help them before printing:**
- **Too big?** Compared with a typical printer bed (size set by the teacher).
- **How heavy?** Estimated from its volume (PLA), and added to the robot's weight.
- **Too detailed?** Very detailed files are simplified for the simulator, with a note.

**Sharing:** the STL is copied into the repo's `robots/parts/` folder, so the whole group gets it
through GitHub Desktop, the same way they share code.

### The big choice: how real should printed parts be?
A printed part only matters if it can **touch** blocks and balls. There are two ways to do that,
and they're very different amounts of work.

| | **A. Floor-level (2D physics)** | **B. Full 3D physics** |
|---|---|---|
| What a printed part can do | Push, guide, funnel, corral, and sweep blocks and balls across the floor. Flaps on a servo can swing and trap them. | Everything in A, **plus** scoop, lift, cradle, hold a ball by its shape, drop it into a goal. |
| How it works | The part's **outline seen from above** becomes a solid wall. Blocks and balls slide and roll on the floor. | The part's **3D shape** is split into simple solid pieces. Objects fall, roll, stack, and get carried. |
| Library | [dyn4j](https://github.com/dyn4j/dyn4j) (pure Java 2D physics) | [Libbulletjme](https://github.com/stephengold/Libbulletjme) (Bullet 3D physics; runs on Apple Silicon) |
| Feels like | Predictable. Easy to understand why something happened. | Realistic, but sometimes jittery, and needs tuning ("why did the ball fall through?"). |
| Effort | Medium | Large: about double, and more testing |
| Existing missions | Keep working as they are | Keep 2D for the lessons; 3D physics is only for the 3D mode |

**My recommendation: build A first, design so B can come later.** A covers intakes, funnels,
plows, and flipper gates, which is most of what a first printed part does. If the challenge needs
lifting and holding (a scoop that carries a ball up to a goal), B is the next step, using the same
part files. **Phase 0 tests both** on a MacBook Air, so the choice is based on facts.

Either way, blocks and balls become **real physical objects** that the robot can push and
that bump into each other. Today blocks only get "grabbed" by a rule. Teachers decide in each
mission which objects are on the field and how big they are.

---

## Architecture
```
 Robot design file (parts + positions + wiring)   ← saved in robots/, shared with git
 Printed parts (STL)                              ← saved in robots/parts/
          │  derive
          ▼
 RobotModel (outline, wheels, drivetrain type, motors→mechanisms, sensor spots, weight, wiring)
          │  used by
          ▼
 World: physics for the robot AND the game objects (A: 2D, B: 3D)
          │
          ├──►  2D view (today)
          └──►  3D view (new, toggle)
          ▼
 SimDevices / hardwareMap (wiring from the design instead of fixed ports)
```
- **Design files** are JSON in a top-level `robots/` folder, outside `TeamCode`, so they never go
  on the robot.
- **RobotModel:** today the course robot's sizes, motor ports, and sensor spots are constants in
  `World`. Phase 1 moves them into a model built from a design. CourseBot's design must
  reproduce today's behavior **exactly**: the regression harness proves it.

## Technology
- **Drawing: JavaFX 3D** (OpenJFX 21 LTS, from Maven Central through Gradle). Same Java, same
  Android Studio run button, works on Apple Silicon. Clicking a 3D object tells you what was
  clicked, which the builder needs. It has no STL reader, but STL is a simple format, so the
  simulator gets a small reader of its own.
- **Physics:** dyn4j (choice A) or Libbulletjme (choice B), above.
- The simulator module moves from Java 8 to Java 17 (it runs on Android Studio's Java 21).
  **Student code is still compiled as Java 8**, like the robot.
- **Phase 0 decides** whether the 3D view lives inside the simulator window or in its own window.
  JavaFX inside Swing windows has had speed problems on Mac Retina screens in the past.

## Parts list, version 1
Generic parts in kit-like colors, simple shapes, not CAD models.

| Group | Parts |
|---|---|
| **Frame** | Chassis frame (3 sizes, **must be first**), channel (4 lengths), flat plate, 90° bracket |
| **Electronics** | Control Hub, **Expansion Hub** (more motor ports), battery, power switch |
| **Motors** | **Fast motor** (~312 RPM, 537.7 ticks per turn), **TETRIX-style motor** (~100 RPM, 1440 ticks per turn), servo |
| **Wheels** | Traction wheel (2 sizes), omni wheel, **mecanum wheel** (left and right versions) |
| **Mechanisms** | Arm (channel on a motor shaft), claw |
| **Sensors** | Touch sensor (bumper), color sensor (looking down) |
| **Yours** | Any STL you add |

Motor numbers are typical values. The motor's settings panel lets a group type in their real
motor's numbers. **The parts list** shows every part in the design with counts, plus each printed
part's estimated print weight. It can be exported as a CSV.

### How snapping works
- Every part has **connectors**: hole-grid spots, motor shafts, motor mounts, servo horns,
  sensor mounts. Each connector has a type, and only matching types connect (shaft ↔ wheel hub).
- **The first part** must be a chassis frame. It sits on the floor at the center.
- **While placing**, a see-through "ghost" part follows the mouse and jumps to the nearest
  matching free connector. **Click** to place, **R** to rotate, **Delete** to remove, **⌘Z** to undo.
- Every part connects back to the chassis. The builder doesn't allow overlapping parts.

## Mecanum drive
Mecanum robots drive forward, sideways, and diagonally, using **four** drive motors.
- **Physics:** easy in the simulator. The four wheel speeds combine into forward, sideways, and
  turning motion.
- **Ports:** four drive motors fill all four Control Hub motor ports, so an arm motor needs an
  **Expansion Hub**. The builder, wiring view, and Configure Robot gain an Expansion Hub (today
  they only know the Control Hub).
- **Code and lessons:** students need new code (four motors, the mecanum math). Plan a new lesson
  ("Mecanum Drive", after Lesson 06), a **CourseBot Mecanum** design, and a mecanum version of
  Mission 6 that needs sideways driving.

## TETRIX motors
- Configure Robot gets the **TETRIX Motor** type (the real Driver Station has it too).
- The real difference students feel is **1440 ticks per turn instead of 537.7**, so Lesson 12's
  `TICKS_PER_MOTOR_REV` changes. In the simulator, a TETRIX-style motor in the design makes that
  real: the wrong number makes the robot drive the wrong distance, just like on the real robot.
  Lesson 12 gets a short note for TETRIX groups.

## What the design changes in the simulation
| Design choice | What changes |
|---|---|
| Wheel size, motor type | Top speed, strength, encoder ticks per inch (**Lesson 12's math gets real**) |
| Wheel type and spacing | Tank turning vs. mecanum strafing; inches of wheel travel per 90° turn |
| Total weight (incl. printed parts) | How fast it speeds up and stops; how hard it pushes objects |
| Robot outline (incl. printed parts) | What it bumps into, what it pushes, whether it fits through gates |
| Arm length, motor | Reach, and how fast gravity pulls the arm down |
| Claw or printed gripper position | Where it can grab or trap an object |
| Color and touch sensor positions | Where they read the floor; what presses the bumper |
| Wiring | Which motor each port drives. Wrong wiring = wrong motor, like a real robot |

Not simulated: wheel slip details, flex, battery wear, print strength, layer adhesion.

---

## Phases
Each phase is a pull request into `sim-3d`. `master` gets the work when a phase is useful by
itself and the 2D simulator is unchanged.

| # | Phase | Delivers | Size | Done when |
|---|---|---|---|---|
| **0** | **Test (go/no-go)** | A 3D view inside the simulator window, with a 2D/3D toggle, three cameras, and an fps counter. (Choice A plus hooks is decided, so no full-3D physics test is needed.) | S–M | **Built.** 63 fps on Windows; waiting for a MacBook Air check |
| 1 | RobotModel | World, both views, and the hardware map read a `RobotModel` instead of constants; CourseBot is one model. (CourseBot's design *file* comes with Phase 4.) | M | **Built.** 18 missions and lessons match the old results (differences only within normal run-to-run timing variation) |
| 2 | 3D view | 2D/3D toggle; field, walls, tape, blocks, CourseBot in 3D; cameras: overhead, chase, orbit | L | **Built with Phase 0.** Polish (shadows, nicer parts) can come later |
| 3 | Game-object physics | Blocks and balls become real objects (dyn4j, choice A) in missions that turn them on; pinned objects stop the robot and press the bumper; the **hook rule** (hook on the arm, handle on the block); Mission 10 "Push the Ball" | M | **Built.** 16 physics checks pass; Mission 10 completes by keyboard; Missions 1–9 and the lessons unchanged |
| 4 | Parts and designs | Parts catalog with icons, design files, parts list panel | M | CourseBot loads from its file; parts list shows its parts |
| 5 | Builder | Build mode: catalog, ghost placement, snapping, rotate, undo, orbit camera | L | A student builds a driving robot from nothing in 15 minutes |
| 6 | **Printed parts (STL)** | Add my part, attach point, size and weight checks, `robots/parts/` sharing | M | A printed funnel on a robot guides a ball where it should go |
| 7 | Wiring and Robot Check | Click-to-wire; Expansion Hub; the hardware map uses the design's wiring; TETRIX Motor type; inspection panel | M | A miswired motor drives the wrong wheel; Robot Check explains every problem |
| 8 | Physics from the design | Speed, turning, weight, arm, gripper, and sensor positions come from the design; CSV parts list | M | Bigger wheels drive faster; a TETRIX motor needs 1440 ticks per turn |
| 9 | **Mecanum** | Mecanum wheels and physics; CourseBot Mecanum; "Mecanum Drive" lesson; sideways-driving mission | M | A mecanum robot strafes; the lesson works start to finish |
| 10 | Curriculum | "Build your first drivetrain", "Design a printed part" (with the challenge's objects), Lesson 18 design option, teacher notes | M | Lessons tested start to finish |
| 11 | Stretch | Choice B if we started with A; linear slides; intakes with spinning wheels | — | Only if the course needs it |

Sizes are relative: S ≈ one session, M ≈ a few, L ≈ several, plus your testing on a MacBook Air
between phases.

## Testing
- **The regression harness keeps running** on every phase: missions, lessons, Grand Challenge.
- **CourseBot equivalence (Phase 1):** same final positions, times, and goal results as today.
- **Builder tests:** snapping rules, design files save → load → same design, Robot Check messages.
- **STL tests:** binary and text STL files, millimeters and inches, files lying on their side,
  very detailed files, broken files (friendly error, no crash).
- **Physics tests:** a ball pushed straight goes straight; a funnel guides a ball; objects never
  pass through walls.
- **A MacBook Air check every phase**, plus screenshots of the 3D view from the headless renderer.

## Risks
| Risk | Plan |
|---|---|
| Printed parts need full 3D physics | Phase 0 tests both; A first, B later with the same part files |
| Too slow on a MacBook Air | Phase 0 test; simple shapes; simplified STLs; a "low graphics" switch |
| Mac problems with Java 3D | Phase 0 tests a MacBook Air; fall back to a separate 3D window |
| First Gradle sync downloads JavaFX and physics (~60 MB) | Do the first sync at school on a fast network |
| Students' STL files are huge or broken | Size limits, automatic simplifying, friendly errors |
| The builder grows into a CAD program | Version 1 parts list stays small; new parts only when a lesson needs them |
| Custom robots break missions | Missions list what they need ("needs a claw"); CourseBot always available |
| More code for a teacher to maintain | Same structure and style as today; this plan and code comments explain the design |

## Decisions
- **Printed parts: A plus hooks.** Balls are **pushed** (floor-level 2D physics, choice A). Blocks
  are **lifted by a hook** that catches a metal attachment on the block's top. That's a rule, like
  today's claw grab: when a hook point on a printed part reaches the block's handle and lifts, the
  block hangs from it until it's set down or unhooked. No full 3D physics needed. A student marks
  the hook point on their STL when they add it.
- **Laptops:** MacBook Air, 2020 or newer, so Apple Silicon only.
- **TETRIX motors:** model unknown. Default 1440 ticks per turn (common for TETRIX encoders), and
  every motor's numbers are editable in the builder.

## Questions still open
1. **The block's metal attachment:** a picture (shape, size, how high it sits) so the hook rule
   matches it. Placeholders for now: a 6-inch block, a handle 1.5 inches tall, and a hook that
   catches within 1.5 inches (`World.BLOCK_SIZE`, `HANDLE_HEIGHT`, `HOOK_CATCH`).
2. **Game objects:** the block and ball sizes (and ball weight) once the challenge is designed.

## Prior art
- [Autodesk Synthesis](https://github.com/Autodesk/synthesis): free, open-source 3D simulator for
  FTC and FRC. Robots come from Fusion CAD, which suits experienced teams but is too much for
  first-timers. This plan aims at beginners: snap-together parts plus their own printed parts,
  inside Android Studio, with the course's own code and missions.
- Snap-together builders like LEGO's digital designers are the model for the builder's feel.
