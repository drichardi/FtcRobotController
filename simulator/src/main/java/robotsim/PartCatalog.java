package robotsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Every part a robot design can use. Parts are generic (not one brand), with the sizes and
 * numbers the simulation needs. Sizes are inches: length (front to back), width (side to side),
 * and height, measured before the part is turned.
 */
final class PartCatalog {

    private PartCatalog() {
    }

    /** What a part is, which decides how it's drawn and what it does in the simulation. */
    enum Kind { CHASSIS, CHANNEL, PLATE, BRACKET, HUB, BATTERY, SWITCH, MOTOR, SERVO, WHEEL, ARM, CLAW, HOOK, TOUCH, COLOR }

    static final class PartType {
        final String id, name, category, description;
        final Kind kind;
        final double length, width, height;
        final int color;
        /** The kind of port its cable plugs into: "motor", "servo", "digital", "i2c", or null. */
        final String portKind;
        private final Map<String, Double> numbers;

        PartType(String id, String name, String category, Kind kind, double length, double width, double height,
                 int color, String portKind, String description, Map<String, Double> numbers) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.kind = kind;
            this.length = length;
            this.width = width;
            this.height = height;
            this.color = color;
            this.portKind = portKind;
            this.description = description;
            this.numbers = numbers;
        }

        /** A number about this part, like "rpm" or "diameter". NaN if it doesn't have one. */
        double number(String key) {
            Double d = numbers.get(key);
            return d == null ? Double.NaN : d;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static final String FRAME = "Frame", ELECTRONICS = "Electronics", MOTORS = "Motors", WHEELS = "Wheels",
            MECHANISMS = "Mechanisms", SENSORS = "Sensors";

    private static final Map<String, PartType> PARTS = new LinkedHashMap<String, PartType>();

    static {
        // ---- frame ----
        for (int size : new int[]{14, 16, 18}) {
            add("chassis-" + size, size + "-inch chassis frame", FRAME, Kind.CHASSIS, size, size, 2, 0x6E7781, null,
                    "The base of the robot. Every design starts with one.");
        }
        for (int len : new int[]{4, 8, 12, 16}) {
            add("channel-" + len, len + "-inch channel", FRAME, Kind.CHANNEL, len, 1.5, 1.5, 0x9AA3AD, null,
                    "Metal U-channel with a grid of holes.");
        }
        for (int len : new int[]{4, 8}) {
            add("upright-" + len, len + "-inch upright", FRAME, Kind.CHANNEL, 1.5, 1.5, len, 0x9AA3AD, null,
                    "Channel standing straight up, to hold something higher.");
        }
        add("plate", "Flat plate (8 x 6)", FRAME, Kind.PLATE, 8, 6, 0.2, 0x9AA3AD, null, "A flat mounting plate.");
        add("bracket", "90-degree bracket", FRAME, Kind.BRACKET, 1.5, 1.5, 1.5, 0x9AA3AD, null,
                "Joins two pieces at a right angle.");

        // ---- electronics ----
        add("control-hub", "Control Hub", ELECTRONICS, Kind.HUB, 5.6, 4.1, 1.2, 0x2A2A2A, null,
                "The robot's brain: 4 motor ports, 6 servo ports, 8 digital ports, 4 I2C buses.");
        add("expansion-hub", "Expansion Hub", ELECTRONICS, Kind.HUB, 5.6, 4.1, 1.2, 0x3A3A3A, null,
                "Four more motor ports (and more of everything else).");
        add("battery", "Battery (12 V)", ELECTRONICS, Kind.BATTERY, 5, 3, 1.8, 0x1F6FEB, null, "Powers everything.");
        add("power-switch", "Power switch", ELECTRONICS, Kind.SWITCH, 2, 1.6, 1, 0xCF222E, null,
                "Turns the robot on and off.");

        // ---- motors (the shaft points along the motor's length) ----
        add("motor-fast", "Fast motor (312 RPM)", MOTORS, Kind.MOTOR, 3.8, 1.5, 1.5, 0x2D2D2D, "motor",
                "537.7 encoder ticks per turn. The course robot's motor.", "rpm", 312, "ticksPerRev", 537.7);
        add("motor-tetrix", "TETRIX-style motor (100 RPM)", MOTORS, Kind.MOTOR, 4.4, 1.6, 1.6, 0x3B3B3B, "motor",
                "Slower and stronger. 1440 encoder ticks per turn (check your motor's numbers).",
                "rpm", 100, "ticksPerRev", 1440);
        add("servo", "Servo", MOTORS, Kind.SERVO, 1.6, 0.8, 1.5, 0x1F2328, "servo", "Turns to a position from 0 to 1.");

        // ---- wheels (they stand on edge; the axle points side to side) ----
        add("wheel-96", "Traction wheel (96 mm)", WHEELS, Kind.WHEEL, 3.78, 1.4, 3.78, 0x141414, null,
                "Grippy rubber wheel. The course robot's wheel.", "diameter", 3.78);
        add("wheel-140", "Traction wheel (140 mm)", WHEELS, Kind.WHEEL, 5.51, 1.4, 5.51, 0x141414, null,
                "A bigger wheel: faster, but fewer encoder ticks per inch.", "diameter", 5.51);
        add("omni-96", "Omni wheel (96 mm)", WHEELS, Kind.WHEEL, 3.78, 1.4, 3.78, 0x3A3A3A, null,
                "Rollers let it slide sideways, which makes turning easier.", "diameter", 3.78, "omni", 1);

        // ---- mechanisms ----
        add("arm-11", "Arm (11 inches)", MECHANISMS, Kind.ARM, 11, 1.2, 1, 0xE8590C, null,
                "A channel on a motor's shaft that swings up and down.",
                "gearRatio", 3.3476, "maxDeg", 100, "limitDeg", 95, "speedDeg", 120);
        add("claw", "Claw", MECHANISMS, Kind.CLAW, 3, 5.5, 2.4, 0x9AA3AD, "servo",
                "Two fingers on a servo. Goes on the end of an arm.", "speed", 2.0);
        add("hook", "Hook", MECHANISMS, Kind.HOOK, 1, 0.4, 1.8, 0x9AA3AD, null,
                "Catches a block's handle when the arm lifts. Goes on an arm.");

        // ---- sensors ----
        add("touch-sensor", "Touch sensor (bumper)", SENSORS, Kind.TOUCH, 1.5, 16, 2, 0x6E7781, "digital",
                "Pressed when the front bumper touches something (or the arm reaches the top).");
        add("color-sensor", "Color sensor", SENSORS, Kind.COLOR, 1.6, 1.6, 0.8, 0x00B4D8, "i2c",
                "Looks down at the floor and measures color and brightness.");
    }

    private static void add(String id, String name, String category, Kind kind, double length, double width,
                            double height, int color, String portKind, String description, Object... numbers) {
        Map<String, Double> map = new LinkedHashMap<String, Double>();
        for (int i = 0; i + 1 < numbers.length; i += 2) {
            map.put((String) numbers[i], ((Number) numbers[i + 1]).doubleValue());
        }
        PARTS.put(id, new PartType(id, name, category, kind, length, width, height, color, portKind, description,
                Collections.unmodifiableMap(map)));
    }

    /** The part with this id, or null. */
    static PartType get(String id) {
        return PARTS.get(id);
    }

    static List<PartType> all() {
        return Collections.unmodifiableList(new ArrayList<PartType>(PARTS.values()));
    }
}
