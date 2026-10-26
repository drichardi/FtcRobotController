# Lesson 08 — Sensors and Boolean Logic

**Code:** [`L08_SensorsAndLogic.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L08_SensorsAndLogic.java)
**Big idea:** Combine true/false values with `&&` (AND), `||` (OR), and `!` (NOT).
**Hardware:** `arm` motor, `touch_sensor`. No arm yet? Use any spare motor and press the sensor with your finger.
**Start of class:** Pull in GitHub Desktop, copy this lesson's `.java` file into your `students/yourname` folder, give it a unique `name`, and delete its `@Disabled` line ([daily routine](00-getting-the-code.md#daily-routine)). Do everything below on **your copy**.

---

## 1. Truth table warm-up
| upRequested | atTop | `!atTop` | `upRequested && !atTop` | `upRequested \|\| !atTop` |
|---|---|---|---|---|
| false | false | | | |
| false | true | | | |
| true | false | | | |
| true | true | | | |

## 2. Predict
1. You hold Y while the touch sensor is pressed. What does the arm do? What does telemetry say?
2. You hold Y **and** A at the same time (sensor not pressed). Up or down? Why?

## 3. Run
Test both predictions.

## 4. Investigate
1. Change `upRequested && !atTop` to `upRequested || !atTop`. Use your truth table to predict what happens when **nobody touches the gamepad**. Then test it carefully (wheels/arm clear of hands!). Change it back.
2. Why does the code store `gamepad1.y` in a variable called `upRequested` instead of using `gamepad1.y` directly in the `if`?
3. Watch `Arm encoder` as the arm moves. Which direction makes the number go up?

## 5. Modify
1. Add a **software lower limit**: don't let the arm go down if `arm.getCurrentPosition()` is below `0`. (You'll need `&&` again.)
2. Make the arm move slower when the left bumper is held.

## 6. Make
Use `gamepad1.rumble(200);` to make the gamepad buzz when the driver tries to go past the top limit. (Bonus: how do you stop it from buzzing nonstop?)

## 🔁 Other half: Autonomous
Write an autonomous that raises the arm **until the limit switch is pressed**, then stops:
```java
waitForStart();
while (opModeIsActive() && !topLimit.isPressed()) {
    arm.setPower(0.4);
}
arm.setPower(0);
```
1. Read the `while` condition out loud. When does the loop end?
2. What happens if the touch sensor is unplugged or never gets pressed? Why is that dangerous?
   *(Lesson 10 fixes this with a timer.)*

---
**Exit ticket:** A rule says "the arm may go up only if the driver presses Y AND the arm is NOT at the top AND the claw is closed." Write it as one Java boolean expression.
