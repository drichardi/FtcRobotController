package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * LESSON 09 - Loops: Drive a Square
 * Big idea: REPETITION. Instead of copying the same code 4 times, loop it.
 *
 * A "for" loop has three parts inside the parentheses:
 *   for (int side = 1;   side <= 4;   side++) { ... }
 *        start here      keep going    after each pass,
 *                        while true    add 1
 *
 * Compare with Blocks: this is the "repeat 4 times" / "count with i from 1 to 4" block.
 *
 * ROBOT CONFIGURATION NEEDED: "left_drive" and "right_drive" (DC motors)
 */

@Autonomous(name = "L09 Loops - Square", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L09_Loops extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        // Tune these on YOUR robot until the square closes up.
        double drivePower = 0.5;
        long driveTimeMs = 1000;    // "long" is like int but holds bigger whole numbers
        long turnTimeMs = 450;

        waitForStart();

        // The "&& opModeIsActive()" part matters! Without it, pressing STOP
        // wouldn't end the loop and the robot would keep driving.
        for (int side = 1; side <= 4 && opModeIsActive(); side++) {
            telemetry.addData("Side", side + " of 4");
            telemetry.update();

            // drive straight
            leftDrive.setPower(drivePower);
            rightDrive.setPower(drivePower);
            sleep(driveTimeMs);

            // turn right about 90 degrees
            leftDrive.setPower(drivePower);
            rightDrive.setPower(-drivePower);
            sleep(turnTimeMs);
        }

        leftDrive.setPower(0);
        rightDrive.setPower(0);

        // A "while" loop repeats as long as its condition is true.
        // This one counts down on the screen before the program ends.
        int countdown = 5;
        while (countdown > 0 && opModeIsActive()) {
            telemetry.addData("Square done! Ending in", countdown);
            telemetry.update();
            sleep(1000);
            countdown--;
        }
    }
}
