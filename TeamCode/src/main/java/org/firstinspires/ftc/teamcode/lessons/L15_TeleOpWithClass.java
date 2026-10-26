package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
 * LESSON 15 - TeleOp Using a Robot Class
 * Big idea: USE an object. All the hardware details live in RobotHardware.java.
 * This file only says WHAT the driver's buttons do, not HOW the motors work.
 *
 * Controls (gamepad 1):
 *   left stick Y = drive, right stick X = turn, hold left bumper = precision mode
 *   Y = arm up, A = arm down, tap B = toggle claw
 *
 * Compare this file to L07 + L08 + L13 combined. Count the lines!
 *
 * ROBOT CONFIGURATION NEEDED: everything listed in RobotHardware.init()
 */

@TeleOp(name = "L15 TeleOp With Class", group = "Lessons")
public class L15_TeleOpWithClass extends LinearOpMode {

    @Override
    public void runOpMode() {
        // "this" means "this OpMode". We hand ourselves to the robot so it can use our hardwareMap.
        RobotHardware robot = new RobotHardware(this);
        robot.init();
        telemetry.update();

        boolean clawOpen = false;
        boolean bWasDownLastLoop = false;

        waitForStart();

        while (opModeIsActive()) {
            // ----- Drive -----
            double speed = gamepad1.left_bumper ? 0.25 : 0.7;
            robot.arcadeDrive(-gamepad1.left_stick_y, gamepad1.right_stick_x, speed);

            // ----- Arm ----- (the limit switch safety rule lives inside robot.setArmPower)
            if (gamepad1.y) {
                robot.setArmPower(RobotHardware.ARM_UP_POWER);
            } else if (gamepad1.a) {
                robot.setArmPower(RobotHardware.ARM_DOWN_POWER);
            } else {
                robot.setArmPower(0);
            }

            // ----- Claw toggle (edge detection from Lesson 13) -----
            if (gamepad1.b && !bWasDownLastLoop) {
                clawOpen = !clawOpen;
                robot.setClawOpen(clawOpen);
            }
            bWasDownLastLoop = gamepad1.b;

            telemetry.addData("Claw", clawOpen ? "OPEN" : "CLOSED");
            telemetry.addData("Arm at top", robot.isArmAtTop());
            telemetry.update();
        }
    }
}
