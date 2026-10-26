package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * LESSON 05 - Tank Drive
 * Big idea: INPUT -> PROCESS -> OUTPUT. Read a stick, do a little math, send it to a motor.
 *
 * Left stick drives the left wheels. Right stick drives the right wheels.
 *
 * ROBOT CONFIGURATION NEEDED: "left_drive" and "right_drive" (DC motors)
 */

@TeleOp(name = "L05 Tank Drive", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L05_TankDrive extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        // A variable we can tune. 1.0 = full speed, 0.5 = half speed.
        double maxSpeed = 0.5;

        telemetry.addData("Status", "Ready");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // INPUT: read the sticks (and flip them so UP is positive)
            double leftStick = -gamepad1.left_stick_y;
            double rightStick = -gamepad1.right_stick_y;

            // PROCESS: scale the stick values by our speed limit
            double leftPower = leftStick * maxSpeed;
            double rightPower = rightStick * maxSpeed;

            // OUTPUT: send power to the motors
            leftDrive.setPower(leftPower);
            rightDrive.setPower(rightPower);

            telemetry.addData("Left power", leftPower);
            telemetry.addData("Right power", rightPower);
            telemetry.update();
        }
    }
}
