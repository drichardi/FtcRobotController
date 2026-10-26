# The Obstacle Course

The obstacle course gives every lesson a purpose: each new idea helps your robot get through
it a little better. Groups run the course **one at a time**, first by driving, then autonomously,
and improve their code between runs.

---

## Building the course
Keep it small, cheap, and easy to rearrange. Tape, cones, and wooden blocks are plenty.
A space of about 6 ft × 8 ft (or half an FTC field) works well.

```
 ┌──────────────────────────────────────────┐
 │  FINISH  ┌────────┐                      │
 │  (park)  │  WALL  │ ← touch-sensor tap   │
 │          └────────┘                      │
 │                          ▲   ▲   ▲       │
 │   [ PICK-UP ]            slalom cones    │
 │    block → drop zone                     │
 │                                          │
 │  ┌──────┐                                │
 │  │START │ ───── straightaway ─────▶      │
 │  └──────┘                                │
 └──────────────────────────────────────────┘
```

| Element | What it tests | Lessons that help |
|---|---|---|
| **Straightaway** | Driving straight | 02, 05, 06, 12 |
| **Slalom** (2–3 cones) | Smooth turns, slow precise driving | 06 (arcs), 07 (speed modes) |
| **Tight box turn** | Accurate turns | 09, 11, 12, 16 |
| **Wall tap** | Stopping when the touch sensor (bumper) hits something | 08, 10, 14 |
| **Pick-up and drop** | Moving a block with the arm and claw | 08, 13, 14, 15 |
| **Finish box** | Stopping where you mean to | 10, 12 |

💡 **Change the layout now and then** (move a cone, swap the direction of the slalom). Code
built on variables, methods, and constants adapts in minutes. Code full of copied numbers
doesn't, and that's a lesson worth learning by experience.

---

## Levels
Groups can attempt whatever level fits where they are. There's no need to "win."

| Level | Challenge | Ready after |
|---|---|---|
| 1 | **Drive it.** Complete the course by hand (TeleOp). | Lesson 05 or 06 |
| 2 | **Drive it with control.** Use speed modes for the slalom, and finish inside the box. | Lesson 07 |
| 3 | **First autonomous section.** From START, drive the straightaway and first turn autonomously using timers. Drivers take over for the rest. | Lesson 09 or 10 |
| 4 | **Sensor stop.** Autonomously drive to the wall and stop when the bumper touches it. | Lesson 10 |
| 5 | **Accurate autonomous.** Drive the straightaway and box turn by encoders, landing within 2" of a tape mark. | Lesson 12 |
| 6 | **Multi-step autonomous.** A state machine that does three or more sections in a row. | Lesson 14 |
| 7 | **Full autonomous.** The whole course, including the pick-up and drop. | Lessons 14–16 |

**Doing driver control and autonomous together:** for Levels 3–6, the same run starts
autonomously and then switches to driver control. The group runs their Autonomous OpMode from
START, then picks their TeleOp and finishes by hand. Both programs have to work, and the
autonomous section can grow a little longer with each lesson.

---

## Running it one group at a time
- **Set up a turn order** on the board. A turn is about 5 minutes: one practice run, then two
  timed runs.
- **Before your turn:** pull, deploy, and check that your programs are on the Driver Station menu.
  Do this *before* you reach the course.
- **While other groups run**, you aren't waiting around. Use your bench board to:
  - tune the numbers from your last run (powers, times, ticks per inch),
  - update your run log and plan the next change,
  - start the next level's code,
  - review a teammate's code and explain it back to them.
- **After your turn:** commit and push what you changed, with a summary that says what you tried.

### Course timer (optional)
The 🔁 *Other half* task in [Lesson 10](lessons/10-timer-auto.md) builds a TeleOp that shows a
running timer and freezes it when the touch sensor is pressed. Mount the sensor at the front of
the robot and finish by tapping the wall.

---

## Run log
Copy this into your engineering notebook. One row per run.

| Run | Level | Program(s) | What we changed since last run | What happened | What we'll change next |
|---|---|---|---|---|---|
| 1 | | | | | |
| 2 | | | | | |
| 3 | | | | | |

This is the **engineering design cycle**: test → observe → change **one** thing → test again.
Changing three things at once means you won't know which one helped.

---

## Working as a group in one folder
For obstacle course code, the group shares a folder: `teamcode/group/` (create it the same way
you made your own folder in Lesson 00). With more than one person in a folder, plan ahead so
nobody edits the same file at the same time:
1. **Assign an owner to each file** before writing code, for example: Ava owns
   `RobotHardware.java`, Ben owns the TeleOp, Cam and Dee own the Autonomous. Write it down.
2. **Need a change in someone else's file?** Ask the owner to make it, or wait until they've
   pushed. Then pull, make the change, and push right away.
3. Still **pull when you sit down and push before you leave**. With a shared folder, it matters
   even more.
4. If GitHub Desktop shows a **conflict** screen, stop and get your teacher. It's fixable, and
   it's a good thing to learn.

Groups that haven't reached Lessons 15–16 yet can each keep their own course programs in their
own folders. That works fine too.

---

## Wrapping up (optional)
If you want a final share-out, have each group:
1. Run its best version of the course.
2. Show its run log, and explain the change that helped the most.
3. Have **each member** explain one piece of code they wrote.
4. Name one thing they'd improve with more time.

### What good code looks like
- [ ] TeleOp and Autonomous both run reliably.
- [ ] Numbers you tune (powers, times, distances) are named variables or constants, not scattered through the code.
- [ ] Clear names and comments, so a teammate could fix it without asking you.
- [ ] Autonomous loops check `opModeIsActive()` and have timeouts, so STOP always works.
- [ ] Every member can explain the code they wrote.
