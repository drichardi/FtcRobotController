package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/*
 * LESSON 06 - Arcade Drive
 * Big idea: EXPRESSIONS. Combine variables with math to get the behavior you want.
 *
 * Left stick up/down = drive forward/back. Right stick left/right = turn.
 *   leftPower  = drive + turn
 *   rightPower = drive - turn
 * Try it on paper: what happens when drive = 1 and turn = 0? drive = 0 and turn = 1?
 *
 * ROBOT CONFIGURATION NEEDED: "left_drive" and "right_drive" (DC motors)
 */

@TeleOp(name = "L06 Arcade Drive", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L06_ArcadeDrive extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        double maxSpeed = 0.6;

        waitForStart();

        while (opModeIsActive()) {
            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;

            double leftPower = drive + turn;
            double rightPower = drive - turn;

            // drive + turn could be as big as 2.0, but motors only accept -1.0 to 1.0.
            // Range.clip() "clips" a number so it stays between a min and a max.
            leftPower = Range.clip(leftPower, -1.0, 1.0);
            rightPower = Range.clip(rightPower, -1.0, 1.0);

            leftDrive.setPower(leftPower * maxSpeed);
            rightDrive.setPower(rightPower * maxSpeed);

            // "%.2f" means "show this decimal number with 2 digits after the point"
            telemetry.addData("Drive / Turn", "%.2f / %.2f", drive, turn);
            telemetry.addData("Left / Right", "%.2f / %.2f", leftPower, rightPower);
            telemetry.update();
        }
    }
}
