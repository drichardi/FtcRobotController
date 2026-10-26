package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
 * LESSON 03 - Variables and Types
 * Big idea: a VARIABLE is a named box that holds a value. In Java every box has a TYPE
 * that says what kind of value can go inside.
 *
 *   int      whole numbers              int teamNumber = 12345;
 *   double   numbers with decimals      double wheelDiameter = 3.78;
 *   boolean  true or false              boolean isRedAlliance = true;
 *   String   text (capital S!)          String robotName = "Sparky";
 *
 * In Blocks you made a variable once and could put anything in it.
 * Java is stricter: you must say the type, and you can't put text in an int box.
 * That strictness is a GOOD thing: the computer catches mistakes before the robot runs.
 *
 * NO HARDWARE NEEDED - this runs on any Control Hub (or in FTCSIM's OnBot Java).
 */

@TeleOp(name = "L03 Variables", group = "Lessons")
public class L03_Variables extends LinearOpMode {

    @Override
    public void runOpMode() {
        // --- Declaring variables:  type  name  =  value; ---
        String robotName = "Sparky";
        int teamNumber = 12345;
        boolean isRedAlliance = true;
        double wheelDiameterInches = 3.78;

        // --- Doing math with variables ---
        // Math.PI is a value Java already knows (3.14159...)
        double wheelCircumference = Math.PI * wheelDiameterInches;
        double inchesPerTenRotations = wheelCircumference * 10;

        // --- Integer division surprise! ---
        int cookies = 7;
        int friends = 2;
        int cookiesEach = cookies / friends;    // What will this be? Predict before you run!
        int leftover = cookies % friends;       // % is "remainder" (called modulo)
        double exactShare = 7.0 / 2.0;

        // --- Changing a variable after it's made ---
        int score = 0;
        score = score + 5;      // score is now 5
        score += 10;            // shortcut for score = score + 10
        score++;                // shortcut for score = score + 1

        telemetry.addData("Robot", robotName);
        telemetry.addData("Team", teamNumber);
        telemetry.addData("Red alliance?", isRedAlliance);
        telemetry.addData("Wheel circumference (in)", wheelCircumference);
        telemetry.addData("Distance for 10 rotations (in)", inchesPerTenRotations);
        telemetry.addData("7 / 2 with ints", cookiesEach);
        telemetry.addData("7 % 2 remainder", leftover);
        telemetry.addData("7.0 / 2.0 with doubles", exactShare);
        telemetry.addData("Score", score);
        // + between Strings "glues" them together (called concatenation)
        telemetry.addLine("Hello from " + robotName + " on team " + teamNumber);
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            idle();     // keep the screen up until STOP
        }
    }
}
