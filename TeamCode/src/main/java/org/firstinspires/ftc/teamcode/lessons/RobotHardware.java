package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/*
 * LESSONS 15 & 16 - Classes and Objects
 * Big idea: a CLASS is a blueprint. An OBJECT is one thing built from that blueprint.
 *
 * Until now, every OpMode found its own motors and had its own copy of the driving math.
 * If a motor name changed, we had to fix it in 10 files! This class puts ALL of the
 * robot's hardware and skills in ONE place. TeleOp and Autonomous both use it.
 *
 *   RobotHardware robot = new RobotHardware(this);   // build an object from the blueprint
 *   robot.init();                                    // ask the object to do something
 *   robot.arcadeDrive(drive, turn);
 *
 * Notice this file has NO @TeleOp or @Autonomous. It is not a program you run.
 * It's a tool that programs use.
 *
 * ENCAPSULATION: the motors are "private" so no other file can touch them directly.
 * Other files must use our "public" methods. That way, safety rules (like the arm limit
 * switch) can't be skipped by accident.
 *
 * FRC CONNECTION: in WPILib command-based programming, this idea is called a "Subsystem".
 */
public class RobotHardware {

    // ---------- Constants: tune these for YOUR robot ----------
    public static final double TICKS_PER_MOTOR_REV = 537.7;
    public static final double GEAR_RATIO = 1.0;
    public static final double WHEEL_DIAMETER_INCHES = 3.78;
    public static final double TICKS_PER_INCH =
            (TICKS_PER_MOTOR_REV * GEAR_RATIO) / (WHEEL_DIAMETER_INCHES * Math.PI);

    public static final double CLAW_OPEN_POSITION = 0.7;
    public static final double CLAW_CLOSED_POSITION = 0.3;
    public static final double ARM_UP_POWER = 0.4;
    public static final double ARM_DOWN_POWER = -0.3;

    // ---------- Fields: the object's private parts ----------
    // The OpMode that is using this robot. We need it for hardwareMap, telemetry, and opModeIsActive().
    private final LinearOpMode opMode;

    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private DcMotor arm;
    private Servo claw;
    private TouchSensor armTopLimit;

    /**
     * CONSTRUCTOR: runs once when someone writes "new RobotHardware(this)".
     * It has the same name as the class and no return type.
     */
    public RobotHardware(LinearOpMode opMode) {
        // "this.opMode" means "MY field called opMode", to tell it apart from the parameter.
        this.opMode = opMode;
    }

    /** Finds all the hardware and sets it up. Call this before waitForStart(). */
    public void init() {
        leftDrive = opMode.hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = opMode.hardwareMap.get(DcMotor.class, "right_drive");
        arm = opMode.hardwareMap.get(DcMotor.class, "arm");
        claw = opMode.hardwareMap.get(Servo.class, "claw");
        armTopLimit = opMode.hardwareMap.get(TouchSensor.class, "touch_sensor");

        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        setClawOpen(false);

        opMode.telemetry.addData("RobotHardware", "Initialized");
    }

    // ===================== DRIVING =====================

    /** Arcade drive: drive = forward/back (-1 to 1), turn = left/right (-1 to 1). */
    public void arcadeDrive(double drive, double turn, double maxSpeed) {
        double leftPower = Range.clip(drive + turn, -1.0, 1.0) * maxSpeed;
        double rightPower = Range.clip(drive - turn, -1.0, 1.0) * maxSpeed;
        setDrivePower(leftPower, rightPower);
    }

    public void setDrivePower(double leftPower, double rightPower) {
        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);
    }

    public void stopDriving() {
        setDrivePower(0, 0);
    }

    public int inchesToTicks(double inches) {
        return (int) (inches * TICKS_PER_INCH);
    }

    /**
     * Moves each side of the drivetrain a set distance using encoders.
     * Same distance on both sides = drive straight. Opposite distances = spin in place.
     * Returns true if it finished, or false if it gave up because of the timeout.
     */
    public boolean encoderDrive(double leftInches, double rightInches, double power, double timeoutSeconds) {
        int leftTarget = leftDrive.getCurrentPosition() + inchesToTicks(leftInches);
        int rightTarget = rightDrive.getCurrentPosition() + inchesToTicks(rightInches);

        leftDrive.setTargetPosition(leftTarget);
        rightDrive.setTargetPosition(rightTarget);
        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        setDrivePower(Math.abs(power), Math.abs(power));

        ElapsedTime timer = new ElapsedTime();
        while (opMode.opModeIsActive() && timer.seconds() < timeoutSeconds
                && (leftDrive.isBusy() || rightDrive.isBusy())) {
            opMode.telemetry.addData("Left", "%d / %d", leftDrive.getCurrentPosition(), leftTarget);
            opMode.telemetry.addData("Right", "%d / %d", rightDrive.getCurrentPosition(), rightTarget);
            opMode.telemetry.update();
        }
        // If neither motor is still busy, we reached the target (instead of timing out or STOP)
        boolean finished = !leftDrive.isBusy() && !rightDrive.isBusy();

        stopDriving();
        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        return finished;
    }

    // ===================== ARM =====================

    /** Positive power = up, negative = down. Refuses to go up past the limit switch. */
    public void setArmPower(double power) {
        if (power > 0 && isArmAtTop()) {
            power = 0;
        }
        arm.setPower(power);
    }

    public boolean isArmAtTop() {
        return armTopLimit.isPressed();
    }

    // ===================== CLAW =====================

    public void setClawOpen(boolean open) {
        if (open) {
            claw.setPosition(CLAW_OPEN_POSITION);
        } else {
            claw.setPosition(CLAW_CLOSED_POSITION);
        }
    }
}
