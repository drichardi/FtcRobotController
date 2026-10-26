package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * LESSON 11 - Methods: Teach the Robot New Words
 * Big idea: ABSTRACTION. A method gives a name to a chunk of code so you can reuse it.
 *
 *   void driveForward(double power, long timeMs) { ... }
 *    |       |              |               |
 *    |      name        PARAMETERS: inputs the caller fills in
 *   "void" = gives nothing back
 *
 * In Blocks, these are the "function" blocks you could create and name yourself.
 *
 * Compare runOpMode() below with Lesson 09. Same robot behavior, but which is easier to read?
 *
 * ROBOT CONFIGURATION NEEDED: "left_drive" and "right_drive" (DC motors)
 */

@Autonomous(name = "L11 Methods", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L11_Methods extends LinearOpMode {

    // These are FIELDS: variables that belong to the whole class, not one method.
    // Every method in this class can use them.
    private DcMotor leftDrive;
    private DcMotor rightDrive;

    @Override
    public void runOpMode() {
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        // Now the plan reads like a set of instructions to a human:
        for (int side = 1; side <= 4 && opModeIsActive(); side++) {
            driveForward(0.5, 1000);
            turnRight(0.5, 450);
        }
        stopDriving();
    }

    /** Drive straight at the given power for the given number of milliseconds. */
    private void driveForward(double power, long timeMs) {
        telemetry.addData("Action", "forward at %.2f for %d ms", power, timeMs);
        telemetry.update();
        setDrivePower(power, power);
        sleep(timeMs);
    }

    /** Spin clockwise in place. */
    private void turnRight(double power, long timeMs) {
        telemetry.addData("Action", "turn right at %.2f for %d ms", power, timeMs);
        telemetry.update();
        setDrivePower(power, -power);
        sleep(timeMs);
    }

    private void stopDriving() {
        setDrivePower(0, 0);
    }

    /** Methods can call other methods! This is the only place that touches the motors. */
    private void setDrivePower(double leftPower, double rightPower) {
        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);
    }
}
