package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
 * LESSON 04 - Reading the Gamepad
 * Big idea: INPUT. Programs react to the world by reading values into variables.
 *
 * The gamepad is just a bunch of variables the SDK updates for you, many times per second:
 *   gamepad1.left_stick_y    double, from -1.0 to 1.0   (careful: UP is NEGATIVE!)
 *   gamepad1.right_stick_x   double, from -1.0 to 1.0   (right is positive)
 *   gamepad1.right_trigger   double, from  0.0 to 1.0
 *   gamepad1.a               boolean, true while the A button is held
 *
 * NO MOTORS NEEDED - just a Control Hub, Driver Station, and gamepad.
 */

@TeleOp(name = "L04 Gamepad Input", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L04_GamepadInput extends LinearOpMode {

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Press START, then move the sticks and press buttons.");
        telemetry.update();
        waitForStart();

        // THE GAME LOOP: every robot program you write from now on has one.
        // Each time through: read inputs -> decide -> act -> show telemetry. Then repeat, fast.
        while (opModeIsActive()) {
            // Read: copy the gamepad values into our own variables
            double leftY = gamepad1.left_stick_y;
            double rightX = gamepad1.right_stick_x;
            double rightTrigger = gamepad1.right_trigger;
            boolean aPressed = gamepad1.a;

            // Flip the sign so that pushing UP gives a POSITIVE number
            double forward = -leftY;

            telemetry.addData("left stick Y (raw)", leftY);
            telemetry.addData("forward (flipped)", forward);
            telemetry.addData("right stick X", rightX);
            telemetry.addData("right trigger", rightTrigger);
            telemetry.addData("A button", aPressed);
            telemetry.update();
        }
    }
}
