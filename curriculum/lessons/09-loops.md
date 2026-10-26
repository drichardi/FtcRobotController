# Lesson 09 — Loops: Drive a Square

**Code:** [`L09_Loops.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L09_Loops.java)
**Big idea:** **Repetition.** `for` loops repeat a set number of times; `while` loops repeat as long as something is true.
**Hardware:** `left_drive`, `right_drive`.
**Start of class:** Pull in GitHub Desktop, copy this lesson's `.java` file into your `students/yourname` folder, and give it a unique `name` ([daily routine](00-getting-the-code.md#daily-routine)). Do everything below on **your copy**.

---

## 1. Predict
1. How many times will `Side` appear on telemetry? What numbers will it show?
2. What numbers will the countdown show, in order? Does it ever show `0`?

## 2. Run
Run it. Tune `driveTimeMs` and `turnTimeMs` until the square nearly closes. Record your best values.

## 3. Investigate
1. Change `side <= 4` to `side < 4`. How many sides now? (Programmers call this an **off-by-one** bug, and it's one of the most common bugs in the world.)
2. Change `side = 1` to `side = 0` (keep `<= 4`). How many sides now?
3. Why do both loops include `&& opModeIsActive()`? What could happen if they didn't?
4. Translate the `for` loop into the Blocks you used to use. Which block matches it best?

## 4. Modify
1. Make it drive a **triangle**. What has to change besides the number of sides?
2. Make the countdown start at 10 and count by 2s.

## 5. Make — Square spiral
Make each side **longer** than the one before by using the loop variable `side` in the math for the drive time. Drive 8 sides.

---
**Exit ticket:** When would you choose a `for` loop instead of a `while` loop? Give a robot example of each.
