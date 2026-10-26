package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * LESSON 10 - Timers: Stop Sleeping, Start Checking
 * Big idea: a LOOP WITH A CONDITION lets the robot keep paying attention while it moves.
 *
 * Problem with sleep(): while the robot "sleeps" it is blind. It can't check sensors,
 * update telemetry, or notice STOP.
 * Fix: an ElapsedTime timer + a while loop that checks the clock AND sensors every pass.
 *
 * The robot drives forward until 3 seconds pass OR the touch sensor (front bumper) is hit.
 * Then it reports WHY it stopped.
 * (Testing on a bench? Press the touch sensor with your finger.)
 *
 * ROBOT CONFIGURATION NEEDED: "left_drive", "right_drive" (DC motors), "touch_sensor"
 */

@Autonomous(name = "L10 Timer Auto", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L10_TimerAuto extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        TouchSensor bumper = hardwareMap.get(TouchSensor.class, "touch_sensor");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        // ElapsedTime is a stopwatch. We'll learn what "new" means in Unit 7 (classes).
        ElapsedTime timer = new ElapsedTime();
        double timeLimitSeconds = 3.0;

        waitForStart();
        timer.reset();      // start the stopwatch at 0

        leftDrive.setPower(0.4);
        rightDrive.setPower(0.4);

        // Keep looping while ALL of these are true
        while (opModeIsActive() && timer.seconds() < timeLimitSeconds && !bumper.isPressed()) {
            telemetry.addData("Driving... time", "%.1f s", timer.seconds());
            telemetry.update();
        }

        leftDrive.setPower(0);
        rightDrive.setPower(0);

        // Why did we exit the loop? Check each possibility.
        String reason;
        if (bumper.isPressed()) {
            reason = "Bumper hit something";
        } else if (timer.seconds() >= timeLimitSeconds) {
            reason = "Time ran out";
        } else {
            reason = "Driver pressed STOP";
        }

        telemetry.addData("Stopped because", reason);
        telemetry.addData("Total time", "%.2f s", timer.seconds());
        telemetry.update();
        sleep(3000);
    }
}
