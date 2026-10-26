# Lesson 06 — Arcade Drive

**Code:** [`L06_ArcadeDrive.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L06_ArcadeDrive.java)
**Big idea:** **Expressions.** Combine variables with math to create the behavior you want.
**Hardware:** `left_drive`, `right_drive`.
**Start of class:** Pull in GitHub Desktop, copy this lesson's `.java` file into your `students/yourname` folder, give it a unique `name`, and delete its `@Disabled` line ([daily routine](00-getting-the-code.md#daily-routine)). Do everything below on **your copy**.

---

## 1. Predict
Fill in the table using `leftPower = drive + turn` and `rightPower = drive - turn` (before clipping).

| drive | turn | leftPower | rightPower | What the robot does |
|---|---|---|---|---|
| 1.0 | 0.0 | | | |
| 0.0 | 1.0 | | | |
| 0.5 | 0.5 | | | |
| 1.0 | 1.0 | | | |
| -1.0 | 0.0 | | | |

## 2. Run
Drive it. Compare arcade to tank drive (Lesson 05). Which do your drivers prefer?

## 3. Investigate
1. Look at row 4 in your table. Why do we need `Range.clip()`?
2. Delete both `Range.clip` lines. Can you *feel* a difference while driving? (Try full forward + full turn.)
3. What does `"%.2f / %.2f"` do in the telemetry line? Change `2` to `4` and see.

## 4. Modify
1. Make it **one-stick** arcade: both drive and turn come from the *left* stick.
2. Make turning gentler than driving by multiplying `turn` by `0.6`.

## 5. Make — Input curves
Small stick movements should give *very* small power for precise driving.
Replace `drive` with `drive * Math.abs(drive)` (and the same for `turn`).
Fill in: stick at 0.5 → power ____. Stick at 1.0 → power ____. Why does this help?

## 🔁 Other half: Autonomous
The arcade formulas work in autonomous too. In an `@Autonomous` program, set `drive = 0.5` and
`turn = 0.25`, calculate `leftPower` and `rightPower` with the arcade formulas, set the motors,
and `sleep(1500)`.
1. What shape does the robot drive?
2. Try `turn = -0.25`. Now combine the two **arcs** to curve around the first slalom cone
   on the obstacle course and come back to the center line.

---
**Exit ticket:** Write the arcade drive formulas from memory and explain why the right side uses `-`.
