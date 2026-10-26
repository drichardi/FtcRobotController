# Lesson 05 — Tank Drive

**Code:** [`L05_TankDrive.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L05_TankDrive.java)
**Big idea:** **Input → Process → Output.** Read a stick, do a little math, send the result to a motor.
**Hardware:** `left_drive`, `right_drive`.
**Start of class:** Pull in GitHub Desktop, copy this lesson's `.java` file into your `students/yourname` folder, and give it a unique `name` ([daily routine](00-getting-the-code.md#daily-routine)). Do everything below on **your copy**.

---

## 1. Predict
1. You push both sticks all the way up. What power goes to each motor?
2. You push the left stick up and the right stick down. What does the robot do?

## 2. Run
Drive the robot around. Find the **Input**, **Process**, and **Output** sections in the code and label them on your printout.

## 3. Investigate
1. Remove the `-` from `-gamepad1.left_stick_y`. What happens when you push the left stick up?
2. Set `maxSpeed = 2.0`. Is the robot faster? Is it easier or harder to drive slowly? Why?
3. Why is `maxSpeed` a variable instead of typing `0.5` in two places?

## 4. Modify
1. Find the `maxSpeed` that your drive team likes best.
2. Make the robot drive **backward** when sticks are pushed up (a "reverse mode" for driving back to the start).

## 5. Make
Add telemetry that shows the robot's *average* power: `(leftPower + rightPower) / 2`. When is it 0 even though the wheels are moving?

---
**Exit ticket:** Write the Input, Process, and Output for something that isn't a robot (a microwave, a video game, a thermostat…).
