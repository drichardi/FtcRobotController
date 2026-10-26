# Blocks → Java Cheat Sheet

Keep this open while you code. Everything you did in Blocks has a Java twin.

## Program structure
| Blocks | Java |
|---|---|
| purple `runOpMode` block | `public void runOpMode() { ... }` |
| `call waitForStart` | `waitForStart();` |
| `repeat while opModeIsActive` | `while (opModeIsActive()) { ... }` |
| comment bubble | `// this is a comment` |

## Telemetry
| Blocks | Java |
|---|---|
| `Telemetry.addData key "Speed" number 0.5` | `telemetry.addData("Speed", 0.5);` |
| `Telemetry.update` | `telemetry.update();` |

## Motors and servos
| Blocks | Java |
|---|---|
| (hardware is found for you) | `DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");` |
| `set leftDrive.Power to 0.5` | `leftDrive.setPower(0.5);` |
| `set leftDrive.Direction to REVERSE` | `leftDrive.setDirection(DcMotor.Direction.REVERSE);` |
| `set claw.Position to 0.7` | `claw.setPosition(0.7);` |
| `leftDrive.CurrentPosition` | `leftDrive.getCurrentPosition()` |
| `call sleep 1000` | `sleep(1000);` |

## Gamepad
| Blocks | Java | Type |
|---|---|---|
| `gamepad1.LeftStickY` | `gamepad1.left_stick_y` | `double` (up is **negative**!) |
| `gamepad1.RightStickX` | `gamepad1.right_stick_x` | `double` |
| `gamepad1.A` | `gamepad1.a` | `boolean` |
| `gamepad1.DpadUp` | `gamepad1.dpad_up` | `boolean` |
| `gamepad1.RightTrigger` | `gamepad1.right_trigger` | `double` 0.0–1.0 |

## Variables
| Blocks | Java |
|---|---|
| `set speed to 0.5` (first time) | `double speed = 0.5;` |
| `set speed to 0.8` (later) | `speed = 0.8;` |
| `change count by 1` | `count++;` or `count += 1;` |

Types: `int` (whole numbers) · `double` (decimals) · `boolean` (`true`/`false`) · `String` (`"text"`)

## Logic and math
| Blocks | Java |
|---|---|
| `if … do … else` | `if (x) { ... } else { ... }` |
| `else if` | `} else if (y) { ... }` |
| `=` (compare) | `==` |
| `≠` | `!=` |
| `and` / `or` / `not` | `&&` / `\|\|` / `!` |
| `repeat 4 times` | `for (int i = 1; i <= 4; i++) { ... }` |
| `absolute of x` | `Math.abs(x)` |
| `remainder of a ÷ b` | `a % b` |
| function block | a **method**: `void driveForward(double power) { ... }` |

## The 5 most common beginner errors
1. **Missing `;`** — the error often points at the *next* line.
2. **Wrong capitalization** — `Telemetry` ≠ `telemetry`, `setpower` ≠ `setPower`.
3. **Mismatched `{ }`** — click a brace; your editor highlights its partner.
4. **`=` instead of `==`** in an `if`.
5. **Hardware name typo** — `"left_drive"` must match the robot configuration *exactly*. This one isn't caught until you press INIT.
