package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

/*
 * LESSON 08 - Sensors and Boolean Logic
 * Big idea: combine true/false values with LOGICAL OPERATORS.
 *
 *   &&   AND   true only if BOTH sides are true
 *   ||   OR    true if EITHER side is true
 *   !    NOT   flips true <-> false
 *
 * An arm motor moves up with Y and down with A. A touch sensor acts as a LIMIT SWITCH:
 * when it's pressed, the arm is at the top and must NOT go up any farther.
 * (No arm built yet? Use any motor, and press the touch sensor with your finger.)
 *
 * ROBOT CONFIGURATION NEEDED: "arm" (DC motor), "touch_sensor" (REV Touch Sensor, digital port)
 */

@TeleOp(name = "L08 Sensors and Logic", group = "Lessons")
public class L08_SensorsAndLogic extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor arm = hardwareMap.get(DcMotor.class, "arm");
        TouchSensor topLimit = hardwareMap.get(TouchSensor.class, "touch_sensor");

        // BRAKE makes the motor hold still when power is 0 instead of coasting.
        arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        while (opModeIsActive()) {
            // Store each true/false question in a well-named variable.
            // Good names make the "if" statements below read almost like English.
            boolean upRequested = gamepad1.y;
            boolean downRequested = gamepad1.a;
            boolean atTop = topLimit.isPressed();

            String armStatus;
            if (upRequested && !atTop) {
                arm.setPower(0.4);
                armStatus = "moving UP";
            } else if (downRequested) {
                arm.setPower(-0.3);
                armStatus = "moving DOWN";
            } else {
                arm.setPower(0);
                armStatus = "holding";
            }

            // A warning only when the driver is pushing against the limit
            if (upRequested && atTop) {
                telemetry.addLine("!! Arm at top limit - can't go higher !!");
            }

            telemetry.addData("Arm", armStatus);
            telemetry.addData("Top limit pressed", atTop);
            telemetry.addData("Arm encoder", arm.getCurrentPosition());
            telemetry.update();
        }
    }
}
