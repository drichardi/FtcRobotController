package robotsim;

import java.util.ArrayList;
import java.util.List;

/**
 * Works out how a robot design behaves: its size, how fast it drives, how it turns, its arm,
 * claw, hook, sensors, and what's plugged into each port. The result is the RobotModel the
 * simulation runs.
 *
 * Problems are explained for students ("The robot needs wheels on both sides..."), so the
 * simulator can show them instead of crashing.
 */
final class DesignModel {

    /**
     * A wheel's real speed under load, compared with its motor's free speed. With the course
     * robot's motor and wheels this gives 40 inches per second, the speed the lessons were
     * written for.
     */
    static final double DRIVE_LOAD_FACTOR = 0.6477613;

    private DesignModel() {
    }

    static RobotModel toModel(RobotDesign d) throws RobotDesign.DesignException {
        List<RobotDesign.Placement> parts = d.parts();
        RobotDesign.Placement chassis = first(parts, PartCatalog.Kind.CHASSIS);
        if (chassis == null) {
            throw new RobotDesign.DesignException("Every robot starts with a chassis frame. This design doesn't have one.");
        }
        RobotModel.Builder b = new RobotModel.Builder(d.name).design(d).body(chassis.part.length, chassis.part.width);

        // ---- drivetrain: wheels on each side, driven by a motor ----
        List<RobotDesign.Placement> leftWheels = new ArrayList<RobotDesign.Placement>();
        List<RobotDesign.Placement> rightWheels = new ArrayList<RobotDesign.Placement>();
        for (RobotDesign.Placement p : parts) {
            if (p.part.kind == PartCatalog.Kind.WHEEL && p.on != null
                    && d.find(p.on).part.kind == PartCatalog.Kind.MOTOR) {
                (p.y > 0 ? leftWheels : rightWheels).add(p);
            }
        }
        if (leftWheels.isEmpty() || rightWheels.isEmpty()) {
            throw new RobotDesign.DesignException("The robot needs wheels on both sides, each attached to a motor "
                    + "(a wheel's \"on\" names the motor that drives it).");
        }
        RobotDesign.Placement leftMotor = d.find(leftWheels.get(0).on);
        RobotDesign.Placement rightMotor = d.find(rightWheels.get(0).on);
        if (leftMotor == rightMotor) {
            throw new RobotDesign.DesignException("The left and right wheels need their own motors to turn.");
        }
        double track = average(leftWheels) - average(rightWheels);
        double diameter = leftWheels.get(0).part.number("diameter");
        double ticks = leftMotor.part.number("ticksPerRev");
        double rpm = leftMotor.part.number("rpm");
        double maxSpeed = rpm / 60 * Math.PI * diameter * DRIVE_LOAD_FACTOR;
        b.drivetrain(track, diameter, ticks, maxSpeed, mount(leftMotor), mount(rightMotor));

        // ---- arm, and what's on it ----
        RobotDesign.Placement arm = first(parts, PartCatalog.Kind.ARM);
        RobotDesign.Placement armMotor = arm != null && arm.on != null ? d.find(arm.on) : null;
        if (arm != null && (armMotor == null || armMotor.part.kind != PartCatalog.Kind.MOTOR)) {
            throw new RobotDesign.DesignException("The arm has to be on a motor (its \"on\" names the motor).");
        }
        if (arm != null) {
            double ticksPerDeg = armMotor.part.number("ticksPerRev") * arm.part.number("gearRatio") / 360;
            b.arm(arm.part.number("maxDeg"), arm.part.number("limitDeg"), arm.part.number("speedDeg"), ticksPerDeg,
                    arm.x, arm.z, arm.part.length);
            for (RobotDesign.Placement p : parts) {
                if (arm.id.equals(p.on) && p.part.kind == PartCatalog.Kind.HOOK) {
                    b.hook(Double.isNaN(p.along) ? arm.part.length : p.along);
                }
            }
        } else {
            // No arm: the arm motor's numbers don't matter, nothing moves.
            b.arm(0, 1000, 0, 1, 0, 0, 0);
        }
        RobotDesign.Placement claw = first(parts, PartCatalog.Kind.CLAW);
        b.claw(claw == null ? 0 : claw.part.number("speed"));
        RobotDesign.Placement color = first(parts, PartCatalog.Kind.COLOR);
        b.colorSensor(color == null ? 0 : color.x);

        // ---- wiring: what's plugged into each port ----
        for (RobotDesign.Placement p : parts) {
            if (p.port == null) {
                continue;
            }
            String[] words = p.port.trim().toLowerCase().split("\\s+");
            int number;
            try {
                number = Integer.parseInt(words[words.length - 1]);
            } catch (NumberFormatException e) {
                throw new RobotDesign.DesignException("Part \"" + p.id + "\" has port \"" + p.port
                        + "\". Ports look like \"motor 0\", \"servo 0\", \"digital 1\", or \"i2c 1\".");
            }
            String kind = words[0];
            if (p.part.portKind == null || !p.part.portKind.equals(kind)) {
                throw new RobotDesign.DesignException("Part \"" + p.id + "\" (" + p.part.name + ") can't plug into a "
                        + kind + " port" + (p.part.portKind == null ? "." : "; it needs a " + p.part.portKind + " port."));
            }
            switch (kind) {
                case "motor":
                    if (p == leftMotor) {
                        b.motorPort(number, RobotModel.Device.LEFT_WHEELS);
                    } else if (p == rightMotor) {
                        b.motorPort(number, RobotModel.Device.RIGHT_WHEELS);
                    } else if (p == armMotor) {
                        b.motorPort(number, RobotModel.Device.ARM);
                    }
                    break;
                case "servo":
                    if (p.part.kind == PartCatalog.Kind.CLAW) {
                        b.servoPort(number, RobotModel.Device.CLAW);
                    }
                    break;
                case "digital":
                    b.digitalPort(number, RobotModel.Device.TOUCH);
                    break;
                case "i2c":
                    b.i2cBus(number, RobotModel.Device.COLOR);
                    break;
                default:
                    break;
            }
        }
        return b.build();
    }

    private static RobotDesign.Placement first(List<RobotDesign.Placement> parts, PartCatalog.Kind kind) {
        for (RobotDesign.Placement p : parts) {
            if (p.part.kind == kind) {
                return p;
            }
        }
        return null;
    }

    private static double average(List<RobotDesign.Placement> wheels) {
        double sum = 0;
        for (RobotDesign.Placement w : wheels) {
            sum += w.y;
        }
        return sum / wheels.size();
    }

    /**
     * A motor's shaft points along its length. Drive motors whose shafts point to the left are
     * mirrored compared with ones pointing right, so programs have to reverse one side.
     */
    private static int mount(RobotDesign.Placement motor) {
        double shaftLeft = Math.sin(Math.toRadians(motor.turn));
        return shaftLeft > 0 ? -1 : +1;
    }
}
