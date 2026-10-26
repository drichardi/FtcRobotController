# From FTC to FRC: You Already Know More Than You Think

FRC robots are programmed in Java with **WPILib**. The libraries are different, but every
idea from this course carries straight over.

| What you learned (FTC) | Where it shows up in FRC (WPILib) |
|---|---|
| `runOpMode()` and the game loop | `teleopPeriodic()` / `autonomousPeriodic()`, called about every 20 ms |
| `hardwareMap.get(DcMotor.class, "left_drive")` | `new SparkMax(1, MotorType.kBrushless)` (motors are found by CAN ID instead of name) |
| `motor.setPower(0.5)` | `motor.set(0.5)` |
| `gamepad1.left_stick_y` | `controller.getLeftY()` |
| `telemetry.addData(...)` | `SmartDashboard.putNumber(...)` |
| `ElapsedTime` timers (Lesson 10) | `Timer` |
| Constants in `ALL_CAPS` (Lesson 12) | A whole `Constants.java` file |
| Edge detection / toggles (Lesson 13) | `Trigger.onTrue(...)`, `toggleOnTrue(...)` |
| State machines (Lesson 14) | Commands and command groups (`.andThen(...)`) |
| `RobotHardware` class (Lessons 15–16) | **Subsystems** (`DriveSubsystem`, `ArmSubsystem`, …) |
| Private fields + public methods | Exactly how every subsystem is written |
| Limit-switch rule inside the class | Safety logic lives inside the subsystem |

## Next steps if you're hooked
- **Game Manual 0** (gm0.org): the community FTC guide, including the software sections.
- **WPILib docs** (docs.wpilib.org): "Zero to Robot" and the command-based tutorial.
- Rewrite `RobotHardware` as two classes: `Drivetrain` and `Arm`. That is one step from FRC command-based design.
