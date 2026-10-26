# Lesson 16 — Reuse: Autonomous With the Same Robot Class

**Code:** [`L16_AutoWithClass.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L16_AutoWithClass.java) (uses `RobotHardware.java`)
**Big idea:** **Reuse.** One well-built class powers both TeleOp and Autonomous.
**Hardware:** everything in `RobotHardware.init()`.
**Start of class:** Pull, then copy `L16_AutoWithClass.java` into the **same** folder as your `RobotHardware.java` from Lesson 15 and give it a unique `name`. Delete any `import org.firstinspires.ftc.teamcode.lessons.RobotHardware;` line so it uses **your** robot class.

---

## 1. Predict
Draw the path on graph paper. Mark where the claw opens.

## 2. Run
Run it. Then tune `INCHES_PER_90_DEGREE_TURN` until the turn is a clean 90°. Record your value.

## 3. Investigate
1. Change `CLAW_OPEN_POSITION` in `RobotHardware.java`. Run **both** L15 and L16. What changed in each? Why is this powerful?
2. `encoderDrive` returns a `boolean`. What does `true` mean? What does `false` mean? Where does L16 use it?
3. `turnRight90(RobotHardware robot)` takes an **object** as a parameter. Why does it need one?

## 4. Modify
1. Add `turnLeft90(robot)`.
2. If the first `encoderDrive` times out, skip the rest of the routine and show a warning.

## 5. Make
Move turning **into** `RobotHardware` as `turnDegrees(double degrees, double power)`. Calculate the inches from the degrees using your tuned value (90° → `INCHES_PER_90_DEGREE_TURN`). Now any OpMode can turn any angle.

---
**Exit ticket:** You find a bug in `encoderDrive`. How many files do you need to fix? Before this unit, how many would it have been?
