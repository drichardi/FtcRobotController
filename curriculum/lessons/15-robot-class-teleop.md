# Lesson 15 — Classes and Objects: One Robot, Many Programs

**Code:** [`RobotHardware.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/RobotHardware.java) and [`L15_TeleOpWithClass.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L15_TeleOpWithClass.java)
**Big idea:** A **class** is a blueprint; an **object** is a thing built from it. **Encapsulation** hides the details behind public methods.
**Hardware:** everything in `RobotHardware.init()`.
**Start of class:** Pull, then copy **both** `RobotHardware.java` and `L15_TeleOpWithClass.java` into your `students/yourname` folder (select both, then copy, so they stay linked). Give L15 a unique `name` and delete its `@Disabled` line. **Check:** if your L15 copy has a line `import org.firstinspires.ftc.teamcode.lessons.RobotHardware;`, delete it. Your L15 should use **your** `RobotHardware`, because you will be changing it.

---

## 1. Predict
1. `RobotHardware.java` has no `@TeleOp` line. Will it appear on the Driver Station menu?
2. In L15, find every line that starts with `robot.`. What do you think each one does?

## 2. Run
Drive L15. Test drive, arm (Y/A), limit switch, and claw toggle (B).

## 3. Investigate
1. Find the **constructor** in `RobotHardware`. How is it different from a normal method?
2. In L15, try adding `robot.leftDrive.setPower(1);`. Read the error. What does `private` protect?
3. The arm limit-switch rule used to be in the OpMode (Lesson 08). Where is it now? Why is that **safer**?
4. Count the lines in L15. Now count L07 + L08 + L13 together. Which would you rather debug at a competition?

## 4. Modify
1. Let a second driver control the arm and claw with `gamepad2`.
2. Add a public method `getArmPosition()` to `RobotHardware` that **returns** the arm encoder value, and show it in L15's telemetry.

## 5. Make
Move the claw toggle **into** `RobotHardware`: write `toggleClaw()` and `isClawOpen()` methods. The object should remember its own claw state, so L15 no longer needs the `clawOpen` variable.

---
**Exit ticket:** Explain class vs. object using something in this room. (Example: a cookie cutter and cookies.)
