# Lesson 10 — Timers: Stop Sleeping, Start Checking

**Code:** [`L10_TimerAuto.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L10_TimerAuto.java)
**Big idea:** A `while` loop with a smart condition lets the robot **keep paying attention** while it moves.
**Hardware:** `left_drive`, `right_drive`, `touch_sensor` (front bumper, or press it by hand).
**Start of class:** Pull in GitHub Desktop, copy this lesson's `.java` file into your `students/yourname` folder, give it a unique `name`, and delete its `@Disabled` line ([daily routine](00-getting-the-code.md#daily-routine)). Do everything below on **your copy**.

---

## 1. Predict
1. Nobody touches the sensor. When does the robot stop, and what does "Stopped because" say?
2. You press the sensor after 1 second. What happens?
3. In Lesson 02, the telemetry only changed between steps. What will it do here while driving?

## 2. Run
Test all three. Try pressing STOP partway through, too.

## 3. Investigate
1. The `while` loop's body only updates telemetry. So what is actually keeping the motors running?
2. Read the loop condition out loud as an English sentence.
3. After the loop, why do we need an `if / else if / else` to know *why* it ended?

## 4. Modify
1. After stopping, make the robot **back up for 1 second** using a second `while` loop and `timer.reset()` (no `sleep()` allowed!).
2. Change the time limit to 5 seconds using only one edit.

## 5. Make — Ping-pong
Make the robot drive forward until the bumper is hit, back up 1 second, and repeat **3 times**. *(Hint: you need a loop inside a loop. These are called **nested loops**.)*

## 🔁 Other half: TeleOp course timer
Add a **course timer** to your arcade drive TeleOp:
1. Create an `ElapsedTime` and reset it right after `waitForStart()`.
2. Show `timer.seconds()` on telemetry every loop.
3. When the touch sensor is pressed (tap the finish wall!), **freeze** the time. You'll need a
   `boolean finished` and a `double finalTime` declared above the loop.
4. Show `Final time` once finished. Driving should still work afterward.

Use it to time your obstacle course runs.

🏁 **Obstacle course:** **Level 4**: autonomously drive to the wall and stop on the bumper.

---
**Exit ticket:** Give two reasons why `while + timer` is better than `sleep()` for autonomous.
