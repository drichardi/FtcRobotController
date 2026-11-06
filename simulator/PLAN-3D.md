# Plan: 3D Simulator and Robot Builder

**Branch:** `sim-3d` · **Status:** plan only, nothing built yet

## The idea
Students build a robot from a parts menu, wire it to the Control Hub, configure it, program it,
and test it, all on a laptop. The current 2D simulator stays as it is; **3D is a toggle**.

> Pick a part from a menu with icons → click to place it → after the first piece, parts snap
> into place. No screws.

## Short verdict
**Worth doing, as a planning and learning tool, built in stages with a go/no-go test first.**

- **Why it's worth it:** it connects the whole process. Today students learn to code a robot
  someone else designed. With a builder, *their* design decisions (wheel size, where the color
  sensor goes, how long the arm is) change how *their* code behaves. That's the link between
  the build team and the programming team that most programs never teach.
- **It won't replace building.** No tolerances, screws, flex, friction, wiring mess, or broken
  parts. Use it to plan a design, to practice at home, and to test code before the real robot
  exists. It shouldn't take over build time in an 8-week course.
- **The biggest risks** are graphics speed on school laptops, Mac support for Java 3D, and
  letting the builder grow into a CAD program. The plan handles all three: a test first, and a
  small parts list that grows only when needed.

## What stays the same
- **Student code doesn't change.** Same `hardwareMap.get`, same lessons, same missions.
- **One physics engine.** The robot drives on a flat field, so the physics stay 2D (top-down) in
  both views. 3D is a different way of *drawing* the same simulation, plus a builder. This keeps
  2D and 3D in agreement and keeps every existing test valid.
- **CourseBot stays the default.** It becomes a prebuilt design, so nobody has to build anything
  to do Lessons 01–18.
- **Configure Robot stays.** The builder adds the step before it: *which port is each cable
  plugged into?* Configure Robot still decides *what each port is called*.

## What students would do
| Step | Where | What they learn |
|---|---|---|
| 1. **Build** | Builder (3D) | Pick parts, snap them together. See the parts list fill in. |
| 2. **Wire** | Builder, wiring view | Click a motor, click a Control Hub port. Cables show up. |
| 3. **Check** | Robot Check panel | Like inspection: fits the 18" box? Hub and battery? Every motor wired? |
| 4. **Configure** | Configure Robot (exists today) | Name each port. |
| 5. **Code** | Android Studio (exists today) | Same as now. |
| 6. **Test** | Simulator, 2D or 3D | Same missions. Their design changes how the robot drives and reaches. |

## Architecture
```
 Robot design file (parts + positions + wiring)   ← saved in the repo, shared with git
          │  derive
          ▼
 RobotModel (footprint, wheels, motors→mechanisms, sensor positions, mass, wiring)
          │  used by
          ▼
 World (today's 2D physics, generalized)  ──►  2D view (today)
          │                                ──►  3D view (new, toggle)
          ▼
 SimDevices / hardwareMap (wiring from the design instead of fixed ports)
```
- **Robot design file:** JSON, one file per design, in a top-level `robots/` folder (outside
  `TeamCode`, so it never goes on the robot). Groups share designs through GitHub Desktop like code.
- **RobotModel:** today the course robot's sizes, motor ports, and sensor spots are constants in
  `World`. Step 1 moves them into a model built from a design. CourseBot's design has to
  reproduce today's behavior **exactly**: the regression harness proves it.
- **Builder** is a separate mode in the same window: *Build* ↔ *Drive*.

## Technology
Recommended: **JavaFX 3D** (OpenJFX 21 LTS, from Maven Central through Gradle).
- It's the same Java, the same Android Studio run button, and it works on Windows, Intel Macs, and
  Apple Silicon Macs. Simple shapes (boxes, cylinders) render fast, and clicking a 3D object
  tells you what was clicked, which the builder needs.
- The simulator module would move from Java 8 to Java 17 (it runs on Android Studio's Java 21).
  **Student code is still compiled as Java 8**, like the robot.

The alternative is **jMonkeyEngine**, a full game engine with physics and better graphics, but
heavier, harder to put inside the existing window, and more for a teacher to maintain.

**Phase 0 decides.** Embedding JavaFX in the Swing window has had speed problems on Mac Retina
screens in the past. The test tries both options: JavaFX inside the window, and JavaFX in its own
window.

## Parts list, version 1
Kept small on purpose. Parts are simple shapes in kit colors, not CAD models.

| Group | Parts | Snaps to |
|---|---|---|
| **Frame** | Chassis frame (3 sizes, **must be first**), channel (4 lengths), flat plate, 90° bracket | Hole grid on channels and plates |
| **Electronics** | Control Hub, battery, power switch | Plates or channels (flat side) |
| **Motion** | Motor (2 speeds: fast/torque), servo | Motor mount on a channel; servo mount |
| **Wheels** | Traction wheel (2 sizes), omni wheel | A motor's shaft, or a free axle |
| **Mechanisms** | Arm (channel on a motor shaft), claw | Motor shaft; servo horn |
| **Sensors** | Touch sensor (bumper), color sensor (looking down) | Sensor mount on a channel or plate |

**The parts list** ("BOM") shows every part in the design with counts, and can be exported as a
CSV for real-world ordering and planning (Phase 6).

### How snapping works
- Every part has **connectors**: hole-grid spots, motor shafts, motor mounts, servo horns,
  sensor mounts. Each connector has a type, and only matching types connect (shaft ↔ wheel hub).
- **The first part** must be a chassis frame. It sits on the floor at the center.
- **While placing**, a see-through "ghost" part follows the mouse and jumps to the nearest
  matching free connector. **Click** to place, **R** to rotate, **Delete** to remove,
  **Ctrl/⌘+Z** to undo.
- Parts snap to the hole grid, so they can never float or end up half-attached. Every part
  connects back to the chassis, and the builder doesn't allow overlapping parts.

## What the design changes in the simulation
| Design choice | What changes in the simulation |
|---|---|
| Wheel size, motor speed | Top speed, encoder ticks per inch (**Lesson 12's math gets real**) |
| Wheel spacing | Turning: how many inches of wheel travel make a 90° turn |
| Total weight | How fast it speeds up and stops |
| Robot outline | What it bumps into, and whether it fits through gates |
| Arm length, motor | Reach, and how fast gravity pulls the arm down |
| Claw position | Where it can grab a block |
| Color sensor position | Where it reads the floor (and how far ahead of the wheels) |
| Touch sensor position | What presses it (front bumper, back, arm top) |
| Wiring | Which motor each port drives. Wrong wiring = wrong motor, like a real robot |

Not simulated: wheel slip, flex, battery wear, friction details, 3D falls and tipping.

## Phases
Each phase is a pull request into `sim-3d`. `master` gets the work when a phase is useful by
itself and the 2D simulator is unchanged.

| # | Phase | Delivers | Size | Done when |
|---|---|---|---|---|
| **0** | **Test (go/no-go)** | A spinning 3D field and box robot, inside the window and in its own window | S | Smooth on a school Windows laptop **and** a MacBook. If not, stop or switch technology. |
| 1 | RobotModel | World reads a model instead of constants; CourseBot as a design file | M | Every mission, lesson, and harness result identical to today |
| 2 | 3D view | 2D/3D toggle; field, walls, tape, blocks, CourseBot in 3D; cameras: overhead, chase, orbit | L | Every mission plays the same in both views |
| 3 | Parts and designs | Parts catalog with icons, design file save/load, parts list panel | M | CourseBot loads from its file; parts list shows its parts |
| 4 | Builder | Build mode: catalog sidebar, ghost placement, snapping, rotate, undo, orbit camera | L | A student builds a driving robot from nothing in 15 minutes |
| 5 | Wiring and Robot Check | Click-to-wire cables; the hardware map uses the design's wiring; inspection panel | M | A miswired motor drives the wrong wheel; Robot Check explains every problem |
| 6 | Physics from the design | Speed, turning, weight, arm, claw, and sensor positions come from the design; CSV parts list | M | Bigger wheels drive faster; a longer arm reaches farther; Lesson 12 ticks-per-inch matches the design |
| 7 | Curriculum | "Build your first drivetrain" lesson, a design challenge tied to Lesson 18, teacher notes, screenshots | M | Lessons tested start to finish in the simulator |
| 8 | Stretch | Mecanum wheels (new code: 4-motor driving), linear slides, intakes, importing simple CAD shapes | — | Only if the course needs it |

Sizes are relative: S ≈ one session, M ≈ a few, L ≈ several, plus your testing on real laptops
between phases.

## Testing
- **The regression harness keeps running** on every phase: missions, lessons, Grand Challenge.
- **CourseBot equivalence (Phase 1):** same final positions, times, and goal results as today.
- **Builder tests:** snapping rules, design file save → load → same design, Robot Check messages.
- **Screenshots of the 3D view** from the headless renderer, plus a manual check on a Mac each phase.

## Risks
| Risk | Plan |
|---|---|
| Too slow on school laptops | Phase 0 test on the real laptops; low-poly parts; a "low graphics" switch |
| Mac problems with Java 3D | Phase 0 tests a MacBook; fall back to a separate 3D window |
| First Gradle sync downloads JavaFX (~50 MB) | Do the first sync at school on a fast network, or before class |
| The builder grows into a CAD program | Version 1 parts list stays small; new parts only when a lesson needs them |
| Custom robots break missions | Missions list what they need ("needs a claw"); CourseBot always available |
| More code for a teacher to maintain | Same structure and style as today; the plan and comments explain the design |

## Questions for you (before Phase 0)
1. **Which kit does your team build with?** goBILDA, REV, or both? The hole grid, part shapes, and
   parts list should match what students hold in their hands.
2. **What laptops do students use?** Model or rough age, for the Phase 0 speed test.
3. **Should the parts list use real part numbers** for ordering, or stay generic?
4. **Mecanum wheels:** does your team use them? They need new driving code and lessons, so they're
   in the stretch phase for now.

## Prior art
- [Autodesk Synthesis](https://github.com/Autodesk/synthesis): free, open-source 3D simulator for
  FTC and FRC, with fields each season. Robots come from Fusion CAD, which is great for
  experienced teams but too much for first-timers. This plan aims at beginners: snap-together
  parts, inside Android Studio, with the course's own code and missions.
- Snap-together builders like LEGO's digital designers are the model for the builder's feel.
