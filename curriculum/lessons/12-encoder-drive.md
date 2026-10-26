# Lesson 12 — Encoders and Return Values

**Code:** [`L12_EncoderDrive.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L12_EncoderDrive.java)
**Big idea:** Methods can **return** an answer. **Constants** (`static final`) keep important numbers in one place.
**Hardware:** `left_drive`, `right_drive`, **with encoder cables plugged in.**
**Start of class:** Pull in GitHub Desktop, copy this lesson's `.java` file into your `students/yourname` folder, give it a unique `name`, and delete its `@Disabled` line ([daily routine](00-getting-the-code.md#daily-routine)). Do everything below on **your copy**.

---

## 0. Before you start
Look up **your** motor's ticks per revolution and measure **your** wheel diameter. Update the constants at the top of the file.

## 1. Predict (do the math by hand with a calculator)
1. `TICKS_PER_INCH = (TICKS_PER_MOTOR_REV × GEAR_RATIO) ÷ (WHEEL_DIAMETER × π)` = ______
2. `inchesToTicks(24)` returns ______
3. Where will the robot end up compared to where it started?

## 2. Run
Put a strip of tape at the start. Measure where the robot ends up after the first move. Was it 24"?

## 3. Investigate
1. Why does `inchesToTicks` have `int` in front of its name instead of `void`?
2. What does `(int)` do to `1086.7`? Is that rounding?
3. Change the battery to a low one (or set power to 0.3). Does the robot still go 24"? Compare to driving by time in Lesson 09.
4. What is the 5-second `timeout` for? Describe an obstacle-course situation where it saves you.

## 4. Modify — Calibrate
Drive 48". Measure the actual distance. Adjust `WHEEL_DIAMETER_INCHES` until it's within ½". Record the before/after in your engineering notebook.

## 5. Make
Write `double ticksToInches(int ticks)`, the reverse of `inchesToTicks`. Use it to show "inches traveled so far" on telemetry while driving.

## 🔁 Other half: TeleOp arm presets
Encoders make driver control easier too. In a TeleOp, make two **arm preset** buttons:
A sends the arm to `0` ticks (down) and Y sends it to a "carry" height you measure (try `300`).
```java
arm.setTargetPosition(300);
arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
arm.setPower(0.4);
```
Reset the arm encoder once in INIT with `STOP_AND_RESET_ENCODER`, with the arm all the way
down. Watch the encoder value on telemetry. Why does the arm need to start in the same place
every time?

🏁 **Obstacle course:** **Level 5**: drive the straightaway and box turn by encoders and land on the tape mark.

---
**Exit ticket:** Why are constants written in `ALL_CAPS`, and why is it better to use `TICKS_PER_INCH` than to type `45.28` everywhere?
