# Lesson 02 — Drive in Sequence

**Code:** [`L02_DriveSequence.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L02_DriveSequence.java)
**Big idea:** **Sequence.** The computer runs one line at a time, top to bottom, and never skips ahead.
**Hardware:** `left_drive`, `right_drive`. Put the robot on a block (wheels off the ground) for the first run!
**Start of class:** Pull in GitHub Desktop, copy this lesson's `.java` file into your `students/yourname` folder, give it a unique `name`, and delete its `@Disabled` line ([daily routine](00-getting-the-code.md#daily-routine)). Do everything below on **your copy**.

---

## 1. Predict
On graph paper, draw the path you think the robot will take. Mark each numbered step.

## 2. Run
Run it on the floor. Then run your FTCSIM movement puzzle solution in Java mode and compare.

## 3. Investigate
1. `sleep(1000)` waits 1000 *milliseconds*. How many seconds is that? What would `sleep(250)` be?
2. While the program is sleeping, are the motors running or stopped? How do you know?
3. Comment out `leftDrive.setDirection(DcMotor.Direction.REVERSE);`. Predict, then test: what does step 1 do now?
4. Delete Step 4 (the two `setPower(0)` lines). What does the robot do in those last 2 seconds?

## 4. Modify
1. Make the robot drive **backward** for step 1. (Hint: what number makes a motor spin the other way?)
2. Make the turn in step 2 closer to exactly 90°. Change only one number.

## 5. Make
Program the robot to drive the shape of a capital **L**, then a capital **Z**. Draw your plan first.

## 🏁 Obstacle course connection
Driver control arrives in Lesson 05. For now, use your sequence to drive the **straightaway**
of the obstacle course. How many milliseconds does it take to reach the first cone? Write it down,
because you'll compare it with encoder driving in Lesson 12.

---
**Exit ticket:** In Blocks, what block did the job of `sleep()`? Why does a motor keep spinning during `sleep()`?
