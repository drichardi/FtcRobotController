# Lesson 11 — Methods: Teach the Robot New Words

**Code:** [`L11_Methods.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L11_Methods.java)
**Big idea:** **Abstraction.** Give a chunk of code a name, then use the name instead of the details.
**Hardware:** `left_drive`, `right_drive`.
**Start of class:** Pull in GitHub Desktop, copy this lesson's `.java` file into your `students/yourname` folder, give it a unique `name`, and delete its `@Disabled` line ([daily routine](00-getting-the-code.md#daily-routine)). Do everything below on **your copy**.

---

## 1. Predict
Open L09 and L11 side by side. Will the robot do anything different? Which `runOpMode()` is easier to read?

## 2. Run
Run L11 and confirm it matches L09.

## 3. Investigate
1. In `driveForward(0.5, 1000)`, the values `0.5` and `1000` are called **arguments**. What are the matching **parameters** called inside the method?
2. `leftDrive` and `rightDrive` moved from inside `runOpMode()` to the top of the class. Why did they have to move? *(Try moving them back inside `runOpMode()` and read the error.)*
3. How many places in the file call `leftDrive.setPower`? Why is that a good thing if a motor gets rewired?
4. What does `void` mean?

## 4. Modify
1. Write a `turnLeft(double power, long timeMs)` method.
2. Write a `driveBackward(double power, long timeMs)` method that **calls `driveForward`** instead of `setDrivePower`. *(Hint: negative power.)*

## 5. Make
Write a method `drivePolygon(int sides, long turnTimeMs)` that drives any regular shape. Then make `runOpMode()` drive a triangle, then a square, each with **one line** of code.

---
**Exit ticket:** In Blocks, what did you call the thing Java calls a "method"? Name one method from the FTC SDK you've used that you *didn't* write.
