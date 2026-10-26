# Lesson 07 — Buttons and `if` / `else`

**Code:** [`L07_ButtonsAndIf.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L07_ButtonsAndIf.java)
**Big idea:** **Decisions.** The program asks a true/false question and chooses what to do.
**Hardware:** `left_drive`, `right_drive`.
**Start of class:** Pull in GitHub Desktop, copy this lesson's `.java` file into your `students/yourname` folder, give it a unique `name`, and delete its `@Disabled` line ([daily routine](00-getting-the-code.md#daily-routine)). Do everything below on **your copy**.

---

## 1. Predict
1. You tap D-pad UP once and let go. What gear is the robot in one second later?
2. You hold the left bumper while in FAST gear. What is the speed limit? What happens when you let go?
3. You press D-pad UP and D-pad DOWN at the **same time**. Which gear wins? Why?

## 2. Run
Test all three predictions.

## 3. Investigate
1. Move the line `double gearSpeed = 0.6;` (and `gearName`) **inside** the `while` loop. What breaks? Explain why. *(This idea is called **scope**: where a variable lives.)*
2. Why is there no `else` at the end of the D-pad `if` chain? What would happen if you added `else { gearSpeed = 0.6; }`?
3. `=` and `==` are different in Java. Which one *stores* a value? Which one *compares*?

## 4. Modify
1. Add a 4th gear on D-pad LEFT called `"TURBO"` at `0.85`.
2. Make precision mode use the **right** bumper instead.

## 5. Make
Replace the gears with an *analog* speed: the right trigger controls max speed from 0.3 (not pressed) to 1.0 (fully pressed). Write the formula on paper first.

---
**Exit ticket:** Rewrite this in Java: "If the A button is pressed, show 'Hello'. Otherwise, show 'Press A'."
