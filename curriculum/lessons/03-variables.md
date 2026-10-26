# Lesson 03 — Variables and Types

**Code:** [`L03_Variables.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L03_Variables.java)
**Big idea:** A **variable** is a named box. In Java, every box has a **type**: `int`, `double`, `boolean`, or `String`.
**Hardware:** None. Runs on any Control Hub, or in FTCSIM's OnBot Java.
**Start of class:** Pull in GitHub Desktop, copy this lesson's `.java` file into your `students/yourname` folder, give it a unique `name`, and delete its `@Disabled` line ([daily routine](00-getting-the-code.md#daily-routine)). Do everything below on **your copy**.

---

## 1. Predict
Fill in what you think each telemetry line will show **before** you run it.

| Telemetry label | Your prediction | Actual |
|---|---|---|
| Wheel circumference (in) | | |
| 7 / 2 with ints | | |
| 7 % 2 remainder | | |
| 7.0 / 2.0 with doubles | | |
| Score | | |

## 2. Run
Run it and fill in the "Actual" column. Which surprised you?

## 3. Investigate
1. Why does `7 / 2` give a different answer than `7.0 / 2.0`?
2. Change `int cookies = 7;` to `int cookies = 7.5;`. What error do you get? What is Java protecting you from?
3. Remove the quotes: `String robotName = Sparky;`. What error do you get? Why?
4. Trace `score` line by line. Write its value after each of the 4 lines that change it.

## 4. Modify
1. Change the wheel diameter to match **your** robot's wheels. (Measure them! 96 mm = 3.78 in.)
2. Add a variable `double targetInches = 100;` and calculate **how many wheel rotations** it takes to drive that far. Show it on telemetry.

## 5. Make
Create a "robot stat card" that uses at least one variable of each of the 4 types and at least two math operations.

## 🔁 Other half: Autonomous
Open your copy of **L02 Drive Sequence**. The numbers `0.5` and `1000` appear several times.
1. Create `double drivePower = 0.5;` and `long driveTimeMs = 1000;` above `waitForStart()`.
2. Replace the matching numbers in the steps with your variables.
3. Now speed up the whole routine by changing **one** line. Why is that better than hunting for every `0.5`?

---
**Exit ticket:** Which type would you use for: (a) the number of cones the robot drove around, (b) motor power, (c) whether the claw is open, (d) your robot's name? Why?
