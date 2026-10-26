# Lesson 04 — Reading the Gamepad

**Code:** [`L04_GamepadInput.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L04_GamepadInput.java)
**Big idea:** **Input.** The gamepad is just a set of variables the robot updates many times per second.
**Hardware:** Control Hub, Driver Station, gamepad. No motors.
**Start of class:** Pull in GitHub Desktop, copy this lesson's `.java` file into your `students/yourname` folder, give it a unique `name`, and delete its `@Disabled` line ([daily routine](00-getting-the-code.md#daily-routine)). Do everything below on **your copy**.

---

## 1. Predict
1. When you push the left stick all the way **up**, what number will `left stick Y (raw)` show?
2. What type of value will the `A button` line show?

## 2. Run
Move every stick and press every button that the code reads. Fill in this table:

| Input | Value at rest | Smallest value | Largest value | Type |
|---|---|---|---|---|
| left_stick_y | | | | |
| right_stick_x | | | | |
| right_trigger | | | | |
| a | | | | |

## 3. Investigate
1. Why do we write `double forward = -leftY;`? What would be confusing if we didn't?
2. Do the sticks read exactly `0.0` when you let go? Why might that matter for a robot?
3. This program has a `while (opModeIsActive())` loop. What would happen if the 5 `double`/`boolean` lines were **above** the loop instead of inside it? Try it.

## 4. Modify
1. Add telemetry for `gamepad1.left_stick_x`, `gamepad1.b`, and `gamepad1.left_trigger`.
2. Show the right trigger as a percentage (0 to 100) instead of 0.0 to 1.0.

## 5. Make
Create a variable `double throttle` that equals `forward` multiplied by the right trigger. Show it on telemetry. What would this be useful for on a real robot?

---
**Exit ticket:** In your own words, what is "the game loop" and why does every TeleOp need one?
