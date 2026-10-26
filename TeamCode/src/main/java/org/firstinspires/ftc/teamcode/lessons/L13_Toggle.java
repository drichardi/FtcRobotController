package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * LESSON 13 - Toggles: Remembering the Past
 * Big idea: STATE. Sometimes the right action depends on what happened BEFORE.
 *
 * Goal: tap A once to open the claw, tap A again to close it.
 *
 * The naive way is broken:
 *     if (gamepad1.a) { clawOpen = !clawOpen; }
 * The loop runs ~50+ times per second. A "quick" tap lasts ~0.1 s, so the claw would
 * flip open/closed about 5 times and land randomly. Try it and see!
 *
 * The fix is EDGE DETECTION: only act at the exact moment the button goes from
 * NOT pressed (last loop) to pressed (this loop).
 *
 *      button:   ___|‾‾‾‾‾‾‾‾‾|___
 *                   ^ rising edge: the only moment we toggle
 *
 * ROBOT CONFIGURATION NEEDED: "claw" (servo)
 */

@TeleOp(name = "L13 Toggle", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L13_Toggle extends LinearOpMode {

    // Constants for the servo. Servo positions go from 0.0 to 1.0. Tune for YOUR claw.
    static final double CLAW_OPEN_POSITION = 0.7;
    static final double CLAW_CLOSED_POSITION = 0.3;

    @Override
    public void runOpMode() {
        Servo claw = hardwareMap.get(Servo.class, "claw");

        // STATE variables. They live outside the loop so they survive between passes.
        boolean clawOpen = false;
        boolean aWasDownLastLoop = false;
        int toggleCount = 0;

        waitForStart();

        while (opModeIsActive()) {
            boolean aIsDownNow = gamepad1.a;

            // Rising edge: down now, but NOT down last loop
            if (aIsDownNow && !aWasDownLastLoop) {
                clawOpen = !clawOpen;
                toggleCount++;
            }
            // Remember for next time. This line is the "memory".
            aWasDownLastLoop = aIsDownNow;

            if (clawOpen) {
                claw.setPosition(CLAW_OPEN_POSITION);
            } else {
                claw.setPosition(CLAW_CLOSED_POSITION);
            }

            // "condition ? A : B" is a mini if/else that picks a value: A if true, B if false
            telemetry.addData("Claw", clawOpen ? "OPEN" : "CLOSED");
            telemetry.addData("Times toggled", toggleCount);
            telemetry.update();
        }
    }
}
