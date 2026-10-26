package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * LESSON 12 - Encoders and Return Values
 * Big idea: methods can RETURN an answer, and CONSTANTS keep magic numbers in one place.
 *
 * Driving by time is unreliable: a low battery means the robot goes less far.
 * An ENCODER counts how far the motor shaft has turned, in "ticks".
 * If we know ticks per wheel rotation and the wheel's circumference, we can drive by INCHES.
 *
 *   int inchesToTicks(double inches)   <- takes a double in, gives an int back
 *
 * ROBOT CONFIGURATION NEEDED: "left_drive" and "right_drive" (DC motors WITH encoder cables)
 */

@Autonomous(name = "L12 Encoder Drive", group = "Lessons")
public class L12_EncoderDrive extends LinearOpMode {

    // CONSTANTS: "static final" means this value never changes. By convention their names
    // are ALL_CAPS. Look these numbers up for YOUR motor and wheels!
    //   goBILDA 5203 312 RPM motor:   537.7 ticks per rev
    //   REV HD Hex motor + 20:1:      560   ticks per rev
    //   REV Core Hex motor:           288   ticks per rev
    static final double TICKS_PER_MOTOR_REV = 537.7;
    static final double GEAR_RATIO = 1.0;              // > 1.0 if gears/sprockets slow the wheel down
    static final double WHEEL_DIAMETER_INCHES = 3.78;  // 96 mm goBILDA wheel
    static final double TICKS_PER_INCH =
            (TICKS_PER_MOTOR_REV * GEAR_RATIO) / (WHEEL_DIAMETER_INCHES * Math.PI);

    private DcMotor leftDrive;
    private DcMotor rightDrive;

    @Override
    public void runOpMode() {
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        // Zero the encoders, then let the motors run normally
        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Ticks per inch", "%.2f", TICKS_PER_INCH);
        telemetry.addData("24 inches =", "%d ticks", inchesToTicks(24));
        telemetry.update();
        waitForStart();

        driveInches(24, 0.5);
        sleep(500);
        driveInches(-12, 0.3);      // negative inches = drive backward
    }

    /** Converts a distance in inches into encoder ticks. */
    private int inchesToTicks(double inches) {
        // (int) chops off the decimal part, turning a double into an int. This is called a "cast".
        return (int) (inches * TICKS_PER_INCH);
    }

    /** Drives straight a set distance, then stops. Gives up after 5 seconds just in case. */
    private void driveInches(double inches, double power) {
        int ticks = inchesToTicks(inches);

        // Targets are measured from where the wheels are RIGHT NOW
        int leftTarget = leftDrive.getCurrentPosition() + ticks;
        int rightTarget = rightDrive.getCurrentPosition() + ticks;

        leftDrive.setTargetPosition(leftTarget);
        rightDrive.setTargetPosition(rightTarget);
        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // In RUN_TO_POSITION, power should be positive; the motor figures out the direction.
        leftDrive.setPower(Math.abs(power));
        rightDrive.setPower(Math.abs(power));

        ElapsedTime timeout = new ElapsedTime();
        while (opModeIsActive() && timeout.seconds() < 5.0
                && (leftDrive.isBusy() || rightDrive.isBusy())) {
            telemetry.addData("Target", "%d ticks (%.1f in)", ticks, inches);
            telemetry.addData("Left", "%d / %d", leftDrive.getCurrentPosition(), leftTarget);
            telemetry.addData("Right", "%d / %d", rightDrive.getCurrentPosition(), rightTarget);
            telemetry.update();
        }

        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
