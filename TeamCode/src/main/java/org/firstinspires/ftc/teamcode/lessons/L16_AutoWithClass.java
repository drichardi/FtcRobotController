package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/*
 * LESSON 16 - Autonomous Using the SAME Robot Class
 * Big idea: REUSE. TeleOp (L15) and this Autonomous share one RobotHardware class.
 * Fix a bug or tune a constant there, and BOTH programs get better.
 *
 * Plan: grab a game piece, drive forward 24", turn ~90 degrees, drive 12", drop it, back away.
 *
 * ROBOT CONFIGURATION NEEDED: everything listed in RobotHardware.init()
 */

@Autonomous(name = "L16 Auto With Class", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L16_AutoWithClass extends LinearOpMode {

    // How far each wheel travels for a 90 degree spin. Measure and tune on YOUR robot!
    static final double INCHES_PER_90_DEGREE_TURN = 11.0;

    @Override
    public void runOpMode() {
        RobotHardware robot = new RobotHardware(this);
        robot.init();
        telemetry.addData("Status", "Ready. Preload a game piece in the claw.");
        telemetry.update();

        waitForStart();

        robot.setClawOpen(false);
        sleep(300);

        robot.encoderDrive(24, 24, 0.5, 4.0);
        turnRight90(robot);
        robot.encoderDrive(12, 12, 0.5, 3.0);

        robot.setClawOpen(true);
        sleep(500);

        // encoderDrive RETURNS true or false, so we can react if something went wrong
        boolean backedAway = robot.encoderDrive(-6, -6, 0.4, 2.0);
        if (!backedAway) {
            telemetry.addLine("Back-away timed out. Is something blocking the robot?");
        }
        telemetry.addData("Status", "Autonomous complete");
        telemetry.update();
        sleep(2000);
    }

    // A method can take an OBJECT as a parameter, just like a number.
    private void turnRight90(RobotHardware robot) {
        robot.encoderDrive(INCHES_PER_90_DEGREE_TURN, -INCHES_PER_90_DEGREE_TURN, 0.4, 3.0);
    }
}
