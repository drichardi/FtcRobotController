# Capstone: Competition-Ready Code (Sessions 19–20)

Your team will deliver the code your robot would take to a real FTC qualifier.

## Requirements
1. **A team `RobotHardware` class** (copy and adapt from Lesson 15) for *your* robot's mechanisms.
2. **A TeleOp OpMode** that:
   - uses arcade drive with at least one speed mode,
   - controls every mechanism on your robot,
   - uses at least one toggle with edge detection,
   - shows useful telemetry for the drive team.
3. **An Autonomous OpMode** that:
   - is written as a **state machine** (enum + switch),
   - uses **encoders** for at least one move,
   - scores or parks for points in this season's game.
4. **Every team member** must be able to point to code they wrote and explain it.

## Working in one shared folder
Capstone code goes in a shared team folder: `teamcode/team/`. Now that more than one person
works in the same folder, plan ahead so nobody edits the same file at the same time:
1. **Assign an owner to each file** before writing code, for example: Ava owns
   `RobotHardware.java`, Ben owns the TeleOp, Cam and Dee own the Autonomous. Write it in your
   engineering notebook.
2. **Need a change in someone else's file?** Ask the owner to make it, or wait until they've
   pushed. Then pull, make the change, and push right away.
3. Still **pull when you sit down and push before you leave**. With a shared folder, it matters
   even more.
4. If GitHub Desktop shows a **conflict** screen, stop and get your teacher. It's fixable, and
   it's a good thing to learn.

## Showcase (Session 20)
Each team gets ~5 minutes:
1. Run the autonomous live.
2. Show your state diagram and walk through one state in the code.
3. Each member explains one part they wrote.
4. One thing you'd improve with more time.

## Rubric (20 points)
| Criterion | 4 – Competition ready | 3 – Solid | 2 – Developing | 1 – Beginning |
|---|---|---|---|---|
| **Works** | TeleOp + Auto run reliably, 3 runs in a row | Minor issues | Works sometimes | Doesn't run |
| **Structure** | Clean `RobotHardware` class; OpModes contain no raw hardware calls | Mostly uses the class | Some hardware calls duplicated | Everything in one OpMode |
| **Readability** | Clear names, constants, helpful comments | Mostly clear | Hard to follow in places | Hard to follow |
| **Autonomous design** | State machine with timeouts and a state diagram | State machine, no timeouts | Sequence with `sleep()` | Incomplete |
| **Explanation** | Every member explains their code confidently | Most can | Some can | One person explains everything |
