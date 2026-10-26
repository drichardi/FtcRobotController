# Lesson 13 — Toggles: Remembering the Past

**Code:** [`L13_Toggle.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L13_Toggle.java)
**Big idea:** **State.** Sometimes the right action depends on what happened *before*.
**Hardware:** `claw` servo.
**Start of class:** Pull in GitHub Desktop, copy this lesson's `.java` file into your `students/yourname` folder, give it a unique `name`, and delete its `@Disabled` line ([daily routine](00-getting-the-code.md#daily-routine)). Do everything below on **your copy**.

---

## 1. Experiment first: the broken toggle
Temporarily replace the edge-detection `if` with this naive version:
```java
if (gamepad1.a) {
    clawOpen = !clawOpen;
    toggleCount++;
}
```
Tap A ten times. Write down `Times toggled`. Why is it so much bigger than 10?
Now put the real code back.

## 2. Predict
Trace the loop by hand. A is held down for 3 loops in a row, then released.

| Loop # | `aIsDownNow` | `aWasDownLastLoop` (at start) | Toggle? | `clawOpen` after |
|---|---|---|---|---|
| 1 | false | false | | false |
| 2 | true | | | |
| 3 | true | | | |
| 4 | true | | | |
| 5 | false | | | |

## 3. Run
Test it. Tune `CLAW_OPEN_POSITION` and `CLAW_CLOSED_POSITION` so the claw doesn't strain.

## 4. Investigate
1. Which single line is the program's "memory"? What happens if you delete it?
2. What happens if you move `aWasDownLastLoop = aIsDownNow;` to *above* the `if`?
3. The SDK has a shortcut: `gamepad1.aWasPressed()`. Replace your `if` condition with it. Does it behave the same? Why is it still worth knowing how to build it yourself?

## 5. Modify
Add a second toggle on **X** that switches between "fast" and "slow" mode and shows it on telemetry.

## 6. Make
Use D-pad UP and DOWN (with edge detection) to choose a number from 0 to 10, shown on telemetry. *(This is exactly how teams pick an "autonomous delay" before a match!)*

---
**Exit ticket:** Explain to a Blocks programmer why "if A is pressed, flip the claw" doesn't work.
