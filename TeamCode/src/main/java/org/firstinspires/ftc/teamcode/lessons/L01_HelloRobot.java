package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
 * LESSON 01 - Hello, Robot!
 * Big idea: the SHAPE of a Java program.
 *
 * This OpMode doesn't move anything. It only shows text on the Driver Station.
 * That's on purpose: before we drive, we learn how Java code is put together.
 *
 * BLOCKS -> JAVA
 *   purple "runOpMode" block        ->  public void runOpMode() { ... }
 *   "call waitForStart"             ->  waitForStart();
 *   "call Telemetry.addData"        ->  telemetry.addData("key", value);
 *   "call Telemetry.update"         ->  telemetry.update();
 *
 * JAVA RULES TO NOTICE
 *   - Every instruction ends with a semicolon ;
 *   - Curly braces { } group code together, like the "mouth" of a Blocks block.
 *   - Java is case-sensitive: telemetry and Telemetry are NOT the same thing.
 *   - Lines starting with // (or between slash-star and star-slash) are comments.
 *     The robot ignores them. They are notes for humans.
 */

// Lines starting with @ are "annotations". @TeleOp puts this program on the Driver Station
// menu: "name" is what you'll see on the menu, and "group" sorts it under a heading.
// @Disabled takes it back OFF the menu, so the teacher's originals don't crowd the list.
@TeleOp(name = "L01 Hello Robot", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L01_HelloRobot extends LinearOpMode {

    @Override
    public void runOpMode() {
        // Everything before waitForStart() runs when the driver presses INIT.
        telemetry.addData("Status", "Hello, robot! Press START.");
        telemetry.update();

        waitForStart();

        // Everything after waitForStart() runs when the driver presses START.
        // This "while" loop repeats until the driver presses STOP.
        // We'll study loops in Unit 4. For now, read it as "keep doing this until STOP."
        while (opModeIsActive()) {
            telemetry.addData("Status", "Running!");
            telemetry.addData("Programmer", "Your name here");
            telemetry.addData("Seconds since INIT", getRuntime());
            telemetry.update();
        }
    }
}
