# Lesson 14 — State Machines

**Code:** [`L14_StateMachineAuto.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L14_StateMachineAuto.java)
**Big idea:** Break a complicated job into named **states** and the **transitions** between them. This is how real FTC and FRC autonomous code is built.
**Hardware:** `left_drive`, `right_drive`, `touch_sensor`.
**Start of class:** Pull in GitHub Desktop, copy this lesson's `.java` file into your `students/yourname` folder, and give it a unique `name` ([daily routine](00-getting-the-code.md#daily-routine)). Do everything below on **your copy**.

---

## 1. Draw it
On paper, draw the state diagram: one bubble per state, one arrow per transition. Label every arrow with *what makes it happen*.

## 2. Predict
List the states in the order telemetry will show them. How long will the robot spend in each one if the bumper is never pressed?

## 3. Run
Run it once with a wall in front of the robot and once without.

## 4. Investigate
1. What is an `enum`? Why is `AutoState.BACK_UP` better than using the number `1` to mean "back up"?
2. Delete `stateTimer.reset();` from the DRIVE_TO_WALL transition. Predict, then test: what happens to BACK_UP?
3. Delete one `break;`. What happens? *(Look up "switch fall-through".)*
4. There is no `sleep()` anywhere. Why does that matter if you wanted to raise an arm *while* driving?

## 5. Modify
1. Add a `WAIT` state at the very beginning that waits 1 second before driving. Real teams use this so they don't crash into their alliance partner!
2. Change the `TURN` state to turn the other way.

## 6. Make
Design a state machine for a task in **this season's game** that your robot will actually do in autonomous. Draw the diagram first, have another team check it, then code it.

---
**Exit ticket:** Name the states and transitions for a microwave oven (door, start button, timer, beep).
