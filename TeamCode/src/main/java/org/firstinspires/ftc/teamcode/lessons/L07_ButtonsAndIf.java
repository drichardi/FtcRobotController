package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/*
 * LESSON 07 - Buttons and if / else
 * Big idea: DECISIONS. The program chooses what to do based on a true/false question.
 *
 *   if (question) {
 *       do this when true
 *   } else if (another question) {
 *       do this when the first was false and this one is true
 *   } else {
 *       do this when everything above was false
 *   }
 *
 * Arcade drive with speed "gears":
 *   D-pad UP = fast, D-pad RIGHT = medium, D-pad DOWN = slow  (the robot REMEMBERS the gear)
 *   Hold LEFT BUMPER for "precision mode" (very slow while held)
 *
 * ROBOT CONFIGURATION NEEDED: "left_drive" and "right_drive" (DC motors)
 */

@TeleOp(name = "L07 Buttons and If", group = "Lessons")
public class L07_ButtonsAndIf extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        // Declared OUTSIDE the loop, so it keeps its value from one loop to the next.
        // (Investigate question: what would happen if we moved this line inside the loop?)
        double gearSpeed = 0.6;
        String gearName = "MEDIUM";

        waitForStart();

        while (opModeIsActive()) {
            // --- Choose a gear. Only ONE of these branches can run each time through. ---
            if (gamepad1.dpad_up) {
                gearSpeed = 1.0;
                gearName = "FAST";
            } else if (gamepad1.dpad_right) {
                gearSpeed = 0.6;
                gearName = "MEDIUM";
            } else if (gamepad1.dpad_down) {
                gearSpeed = 0.3;
                gearName = "SLOW";
            }
            // No "else" here: if no D-pad button is pressed, we keep the old gear.

            // --- Precision mode overrides the gear, but only while the bumper is held ---
            double speed;
            if (gamepad1.left_bumper) {
                speed = 0.2;
            } else {
                speed = gearSpeed;
            }

            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;
            double leftPower = Range.clip(drive + turn, -1.0, 1.0) * speed;
            double rightPower = Range.clip(drive - turn, -1.0, 1.0) * speed;

            leftDrive.setPower(leftPower);
            rightDrive.setPower(rightPower);

            telemetry.addData("Gear", gearName);
            telemetry.addData("Precision mode", gamepad1.left_bumper);
            telemetry.addData("Speed limit", speed);
            telemetry.update();
        }
    }
}
