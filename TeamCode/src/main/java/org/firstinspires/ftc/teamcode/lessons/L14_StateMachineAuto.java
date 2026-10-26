package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * LESSON 14 - State Machines
 * Big idea: break a complicated job into named STATES and the rules for moving between them.
 * This is how real competition autonomous programs (FTC and FRC) are built.
 *
 *   DRIVE_TO_WALL --(bumper hit OR 4 s)--> BACK_UP --(0.5 s)--> TURN --(0.6 s)--> DONE
 *
 * An "enum" is a type YOU create with a fixed list of possible values.
 * A "switch" picks which block of code to run based on the current value.
 *
 * Notice: there is only ONE loop, and nothing in it ever sleeps. Every pass, we check
 * "what state am I in?" and "is it time to move to the next state?" That means we could
 * also run an arm, update telemetry, or check other sensors AT THE SAME TIME.
 *
 * ROBOT CONFIGURATION NEEDED: "left_drive", "right_drive" (DC motors), "touch_sensor"
 */

@Autonomous(name = "L14 State Machine Auto", group = "Lessons")
public class L14_StateMachineAuto extends LinearOpMode {

    // Our own type. A variable of type AutoState can ONLY hold one of these four values.
    enum AutoState {
        DRIVE_TO_WALL,
        BACK_UP,
        TURN,
        DONE
    }

    private DcMotor leftDrive;
    private DcMotor rightDrive;

    @Override
    public void runOpMode() {
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        TouchSensor bumper = hardwareMap.get(TouchSensor.class, "touch_sensor");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        AutoState state = AutoState.DRIVE_TO_WALL;
        ElapsedTime stateTimer = new ElapsedTime();     // how long we've been in this state

        waitForStart();
        stateTimer.reset();

        while (opModeIsActive() && state != AutoState.DONE) {
            switch (state) {
                case DRIVE_TO_WALL:
                    setDrivePower(0.4, 0.4);
                    if (bumper.isPressed() || stateTimer.seconds() > 4.0) {
                        state = AutoState.BACK_UP;
                        stateTimer.reset();
                    }
                    break;      // "break" means: done with this case, skip the rest of the switch

                case BACK_UP:
                    setDrivePower(-0.3, -0.3);
                    if (stateTimer.seconds() > 0.5) {
                        state = AutoState.TURN;
                        stateTimer.reset();
                    }
                    break;

                case TURN:
                    setDrivePower(0.4, -0.4);
                    if (stateTimer.seconds() > 0.6) {
                        state = AutoState.DONE;
                        stateTimer.reset();
                    }
                    break;

                case DONE:
                    break;
            }

            telemetry.addData("State", state);
            telemetry.addData("Time in state", "%.2f s", stateTimer.seconds());
            telemetry.update();
        }

        setDrivePower(0, 0);
    }

    private void setDrivePower(double leftPower, double rightPower) {
        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);
    }
}
