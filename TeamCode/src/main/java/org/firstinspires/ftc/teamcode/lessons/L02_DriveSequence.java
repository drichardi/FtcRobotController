package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * LESSON 02 - Drive in Sequence
 * Big idea: SEQUENCE. A program runs one line at a time, top to bottom.
 *
 * The robot drives forward, spins, drives forward again, then stops.
 * Each step is: set motor powers, then sleep() (wait) while the motors keep running.
 *
 * BLOCKS -> JAVA
 *   "set leftDrive.Power to 0.5"    ->  leftDrive.setPower(0.5);
 *   "call sleep 1000 milliseconds"  ->  sleep(1000);
 *
 * ROBOT CONFIGURATION NEEDED: "left_drive" and "right_drive" (DC motors)
 */

@Autonomous(name = "L02 Drive Sequence", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L02_DriveSequence extends LinearOpMode {

    @Override
    public void runOpMode() {
        // Find the motors by the names in the robot configuration.
        // The name in quotes must match the configuration EXACTLY (capital letters too).
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");

        // The two motors face opposite directions, so one must be reversed
        // or the robot will spin when we want it to go straight.
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Ready. Press START.");
        telemetry.update();
        waitForStart();

        // Step 1: drive forward at half power for 1 second (1000 milliseconds)
        telemetry.addData("Step", "1 - forward");
        telemetry.update();
        leftDrive.setPower(0.5);
        rightDrive.setPower(0.5);
        sleep(1000);

        // Step 2: spin right (left wheel forward, right wheel backward)
        telemetry.addData("Step", "2 - spin right");
        telemetry.update();
        leftDrive.setPower(0.5);
        rightDrive.setPower(-0.5);
        sleep(500);

        // Step 3: drive forward again
        telemetry.addData("Step", "3 - forward");
        telemetry.update();
        leftDrive.setPower(0.5);
        rightDrive.setPower(0.5);
        sleep(1000);

        // Step 4: stop. Motors keep their last power until you change it!
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        telemetry.addData("Step", "4 - done");
        telemetry.update();
        sleep(2000);
    }
}
