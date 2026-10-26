# Lesson 01 — Hello, Robot!

**Code:** [`L01_HelloRobot.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L01_HelloRobot.java)
**Big idea:** Java programs have a strict *shape*: classes, methods, braces, semicolons.
**Hardware:** Control Hub + Driver Station only. Nothing has to be plugged in.
**Start of class:** You already copied this file into your `students/yourname` folder in [Lesson 00, Step 7](00-getting-the-code.md#step-7--copy-your-first-lesson). Do everything below on **your copy**, not the one in `lessons/`.

---

## 1. Predict (before you run anything)
Read the code with your partner. Write your answers down.
1. What text will the Driver Station show after you press **INIT**?
2. What text will it show after you press **START**?
3. One number on the screen will keep changing. Which one, and why?

## 2. Run
Deploy the code (pull first!), pick **your** copy from the TeleOp menu (for example `L01 Hello Robot - Ava`), then press INIT → START → STOP.
Were your predictions right? Circle any you got wrong.

## 3. Investigate
1. Find `waitForStart();`. Which lines run *before* it, and which run *after*?
2. What do you think `telemetry.update()` does? Comment it out (put `//` in front of it) inside the loop and run again. What changed?
3. Find the `@TeleOp(...)` line. Change `name` to something else. Where does that change show up?

## 4. Modify
1. Replace `"Your name here"` with the names of both partners.
2. Add a new line of telemetry that shows your team number.

## 5. 🔨 Break-It Lab
Professional programmers see error messages *all day*. Learning to read them is a superpower.
Make **one** change at a time, try to build, write down the error, then undo the change.

| Change you make | Did it build? | What did the error message say (in your own words)? |
|---|---|---|
| Delete a `;` at the end of a line | | |
| Change `telemetry` to `Telemetry` | | |
| Delete the very last `}` in the file | | |
| Change `"Status"` to `"Stat us"` | | |
| Change `waitForStart();` to `waitforstart();` | | |

One of these changes builds just fine. Why do you think that is?

## 6. Make
Make the Driver Station show a 3-line "robot introduction": its name, what it will do, and a fun fact.

---
**Exit ticket:** Name three rules of Java that Blocks never made you think about.
