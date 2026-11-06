package robotsim;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The robot builder's rules: where a part snaps when you point at something, what it attaches
 * to, which port it gets, and undo/redo. The builder window only turns the mouse into "pointing
 * at this part, at this spot" and draws the result.
 *
 * Coordinates are inches from the robot's center on the floor: x forward, y to the left, z up.
 */
final class DesignEditor {

    /** Parts snap to a half-inch grid. */
    static final double GRID = 0.5;
    /** Where the chassis frame sits: its center is this high, 1.3 inches of clearance under it. */
    static final double CHASSIS_Z = 2.3;
    /** A color sensor looks down from just above the floor. */
    static final double FLOOR_SENSOR_Z = 0.9;

    /** Where a part would go, or why it can't go there. */
    static final class Proposal {
        final RobotDesign.Placement placement;
        final String problem;

        Proposal(RobotDesign.Placement placement, String problem) {
            this.placement = placement;
            this.problem = problem;
        }

        boolean ok() {
            return problem == null;
        }
    }

    private RobotDesign design;
    private final Deque<RobotDesign> undo = new ArrayDeque<RobotDesign>();
    private final Deque<RobotDesign> redo = new ArrayDeque<RobotDesign>();

    DesignEditor(RobotDesign start) {
        design = start != null ? start : new RobotDesign("MyRobot", new ArrayList<RobotDesign.Placement>());
    }

    RobotDesign design() {
        return design;
    }

    private void change(RobotDesign next) {
        undo.push(design);
        redo.clear();
        design = next;
    }

    boolean undo() {
        if (undo.isEmpty()) {
            return false;
        }
        redo.push(design);
        design = undo.pop();
        return true;
    }

    boolean redo() {
        if (redo.isEmpty()) {
            return false;
        }
        undo.push(design);
        design = redo.pop();
        return true;
    }

    void rename(String name) {
        if (!name.equals(design.name)) {
            change(new RobotDesign(name, design.parts()));
        }
    }

    // ------------------------------------------------------------------ placing

    /**
     * Where a part would go if dropped where the mouse points: at this spot (x, y, z) on the part
     * with this id, or on the floor if targetId is null. quarterTurns turns it 90° at a time.
     */
    Proposal propose(PartCatalog.PartType type, String targetId, double hx, double hy, double hz, int quarterTurns) {
        RobotDesign.Placement chassis = first(PartCatalog.Kind.CHASSIS);
        if (type.kind == PartCatalog.Kind.CHASSIS) {
            if (chassis != null) {
                return problem("This robot already has a chassis frame. Every robot has just one.");
            }
            return check(new RobotDesign.Placement(unique("chassis"), type, 0, 0, CHASSIS_Z, 0, null, Double.NaN, null),
                    null);
        }
        if (chassis == null) {
            return problem("Start with a chassis frame: it's the base everything else goes on.");
        }
        RobotDesign.Placement target = targetId == null ? null : design.find(targetId);
        double halfL = chassis.part.length / 2, halfW = chassis.part.width / 2;
        switch (type.kind) {
            case WHEEL: {
                if (target != null && target != chassis && target.part.kind != PartCatalog.Kind.WHEEL) {
                    return problem("Wheels go on the sides of the chassis frame. Point at the frame.");
                }
                double r = type.number("diameter") / 2;
                int side = hy >= 0 ? 1 : -1;
                double y = side * (halfW - 1.5);
                double x = clamp(snap(hx), -(halfL - r), halfL - r);
                String name = (side > 0 ? "left" : "right") + (x >= 0 ? "-front" : "-back") + "-wheel";
                RobotDesign.Placement motor = driveMotorOn(side);
                return check(new RobotDesign.Placement(unique(name), type, x, y, r, 0,
                        motor == null ? null : motor.id, Double.NaN, null), null);
            }
            case ARM: {
                if (target == null || target.part.kind != PartCatalog.Kind.MOTOR) {
                    return problem("An arm goes on a motor's shaft. Point at a motor.");
                }
                if (drives(target)) {
                    return problem("That motor drives wheels. Give the arm its own motor.");
                }
                if (hasOn(target.id, PartCatalog.Kind.ARM)) {
                    return problem("That motor already has an arm.");
                }
                return check(new RobotDesign.Placement(unique("arm"), type, target.x, 0, target.z, 0, target.id,
                        Double.NaN, null), target);
            }
            case CLAW:
            case HOOK: {
                if (target == null || target.part.kind != PartCatalog.Kind.ARM) {
                    return problem("A " + type.name.toLowerCase() + " goes on an arm. Point at the arm.");
                }
                if (type.kind == PartCatalog.Kind.CLAW && hasOn(target.id, PartCatalog.Kind.CLAW)) {
                    return problem("That arm already has a claw.");
                }
                double along = type.kind == PartCatalog.Kind.CLAW ? target.part.length
                        : clamp(snap(hx - target.x), 2, target.part.length);
                String port = type.portKind == null ? null : freePort(type.portKind);
                return new Proposal(new RobotDesign.Placement(unique(type.id), type, 0, 0, 0, 0, target.id, along,
                        port), null);
            }
            case TOUCH: {
                if (first(PartCatalog.Kind.TOUCH) != null) {
                    return problem("This robot already has a bumper (touch sensor).");
                }
                double x = halfL + type.length / 2;
                return check(new RobotDesign.Placement(unique("bumper"), type, x, 0, chassis.z - 0.2, 0, null,
                        Double.NaN, freePort("digital")), null);
            }
            case COLOR: {
                double x = clamp(snap(hx), -(halfL - type.length / 2), halfL - type.length / 2);
                double y = clamp(snap(hy), -(halfW - type.width / 2), halfW - type.width / 2);
                return check(new RobotDesign.Placement(unique("floor-sensor"), type, x, y, FLOOR_SENSOR_Z, 0, null,
                        Double.NaN, freePort("i2c")), null);
            }
            default: {
                // Everything else sits on top of the chassis or of another part.
                if (target == null) {
                    return problem("Put it on the chassis frame or on top of another part.");
                }
                if (!canHold(target)) {
                    return problem("Parts can't sit on a " + target.part.name.toLowerCase() + ".");
                }
                double turn = 90 * quarterTurns;
                if (type.kind == PartCatalog.Kind.MOTOR) {
                    // Point the shaft toward the nearest side, like a drive motor.
                    turn += hy >= 0 ? 90 : -90;
                }
                turn = normalize(turn);
                boolean sideways = Math.abs(Math.sin(Math.toRadians(turn))) > 0.5;
                double l2 = (sideways ? type.width : type.length) / 2;
                double w2 = (sideways ? type.length : type.width) / 2;
                if (l2 > halfL || w2 > halfW) {
                    return problem("That part is bigger than the chassis frame. Turn it (R) or use a bigger frame.");
                }
                double x = clamp(snap(hx), -(halfL - l2), halfL - l2);
                double y = clamp(snap(hy), -(halfW - w2), halfW - w2);
                double z = top(target) + type.height / 2;
                String port = type.portKind == null ? null : freePort(type.portKind);
                // A sideways motor near a side is probably a drive motor; others are just "motor".
                boolean nearSide = Math.abs(y) > halfW / 3;
                String name = type.kind != PartCatalog.Kind.MOTOR ? type.id
                        : nearSide && (turn == 90 || turn == -90) ? (y >= 0 ? "left-motor" : "right-motor") : "motor";
                return check(new RobotDesign.Placement(unique(name), type, x, y, z, turn, null, Double.NaN, port),
                        target);
            }
        }
    }

    /** Adds a proposed part. A new drive motor picks up the unattached wheels on its side. */
    void place(Proposal p) {
        if (!p.ok()) {
            return;
        }
        List<RobotDesign.Placement> parts = new ArrayList<RobotDesign.Placement>(design.parts());
        RobotDesign.Placement placed = p.placement;
        parts.add(placed);
        if (placed.part.kind == PartCatalog.Kind.MOTOR && (placed.turn == 90 || placed.turn == -90)) {
            int side = placed.y >= 0 ? 1 : -1;
            for (int i = 0; i < parts.size(); i++) {
                RobotDesign.Placement w = parts.get(i);
                if (w.part.kind == PartCatalog.Kind.WHEEL && w.on == null && (w.y >= 0 ? 1 : -1) == side) {
                    parts.set(i, with(w, placed.id, w.port, w.turn, w.settings));
                }
            }
        }
        change(new RobotDesign(design.name, parts));
    }

    /**
     * Removes a part and anything mounted on it (an arm's claw, a motor's arm). Wheels on a removed
     * motor stay, but nothing drives them. Returns what was removed, for the status bar.
     */
    String remove(String id) {
        RobotDesign.Placement p = design.find(id);
        if (p == null) {
            return "";
        }
        List<String> gone = new ArrayList<String>();
        collectMounted(id, gone);
        List<RobotDesign.Placement> parts = new ArrayList<RobotDesign.Placement>();
        for (RobotDesign.Placement q : design.parts()) {
            if (gone.contains(q.id)) {
                continue;
            }
            parts.add(q.on != null && gone.contains(q.on) ? with(q, null, q.port, q.turn, q.settings) : q);
        }
        change(new RobotDesign(design.name, parts));
        return gone.size() == 1 ? "Removed " + p.part.name + "." : "Removed " + p.part.name + " and "
                + (gone.size() - 1) + " part" + (gone.size() > 2 ? "s" : "") + " on it.";
    }

    private void collectMounted(String id, List<String> out) {
        out.add(id);
        for (RobotDesign.Placement q : design.parts()) {
            if (id.equals(q.on) && q.part.kind != PartCatalog.Kind.WHEEL && !out.contains(q.id)) {
                collectMounted(q.id, out);
            }
        }
    }

    /** Turns a part 90° in place, if it still fits. Returns a problem, or null. */
    String rotate(String id) {
        RobotDesign.Placement p = design.find(id);
        if (p == null || !turnable(p.part.kind)) {
            return p == null ? null : "That part only goes one way.";
        }
        RobotDesign.Placement turned = new RobotDesign.Placement(p.id, p.part, p.x, p.y, p.z, normalize(p.turn + 90),
                p.on, p.along, p.port, p.settings);
        String problem = overlapOrOutside(turned, null, p.id);
        if (problem != null) {
            return problem;
        }
        replace(turned);
        return null;
    }

    /** Plugs a part into a port, like "motor 2". Returns a problem, or null. */
    String setPort(String id, String port) {
        RobotDesign.Placement p = design.find(id);
        if (p == null || p.part.portKind == null) {
            return "That part doesn't plug into a port.";
        }
        if (port != null) {
            for (RobotDesign.Placement q : design.parts()) {
                if (q != p && port.equals(q.port)) {
                    return port + " is already used by " + q.id + ".";
                }
            }
        }
        replace(with(p, p.on, port, p.turn, p.settings));
        return null;
    }

    /** Changes one of a part's numbers, like a motor's ticks per turn. */
    void setSetting(String id, String key, double value) {
        RobotDesign.Placement p = design.find(id);
        if (p == null) {
            return;
        }
        Map<String, Double> settings = new LinkedHashMap<String, Double>(p.settings);
        if (value == p.part.number(key)) {
            settings.remove(key);
        } else {
            settings.put(key, value);
        }
        replace(with(p, p.on, p.port, p.turn, settings));
    }

    private void replace(RobotDesign.Placement updated) {
        List<RobotDesign.Placement> parts = new ArrayList<RobotDesign.Placement>();
        for (RobotDesign.Placement q : design.parts()) {
            parts.add(q.id.equals(updated.id) ? updated : q);
        }
        change(new RobotDesign(design.name, parts));
    }

    private static RobotDesign.Placement with(RobotDesign.Placement p, String on, String port, double turn,
                                              Map<String, Double> settings) {
        return new RobotDesign.Placement(p.id, p.part, p.x, p.y, p.z, turn, on, p.along, port, settings);
    }

    // ------------------------------------------------------------------ ports

    /** The ports a kind of part can use, in the order they're handed out. */
    static List<String> ports(String kind) {
        List<String> out = new ArrayList<String>();
        if ("motor".equals(kind)) {
            for (int i = 0; i < 4; i++) {
                out.add("motor " + i);
            }
        } else if ("servo".equals(kind)) {
            for (int i = 0; i < 6; i++) {
                out.add("servo " + i);
            }
        } else if ("digital".equals(kind)) {
            // A REV touch sensor uses the second channel of a port, so odd numbers first.
            for (int i : new int[]{1, 3, 5, 7, 0, 2, 4, 6}) {
                out.add("digital " + i);
            }
        } else if ("i2c".equals(kind)) {
            // Bus 0 is the Control Hub's built-in IMU.
            for (int i = 1; i < 4; i++) {
                out.add("i2c " + i);
            }
        }
        return out;
    }

    /** The first port of this kind nobody uses yet, or null if they're all taken. */
    String freePort(String kind) {
        for (String port : ports(kind)) {
            boolean used = false;
            for (RobotDesign.Placement q : design.parts()) {
                used |= port.equals(q.port);
            }
            if (!used) {
                return port;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ rules

    private Proposal check(RobotDesign.Placement p, RobotDesign.Placement sittingOn) {
        String problem = overlapOrOutside(p, sittingOn, null);
        return problem == null ? new Proposal(p, null) : new Proposal(p, problem);
    }

    /** A problem if the part would overlap another part (other than what it sits on), or null. */
    private String overlapOrOutside(RobotDesign.Placement p, RobotDesign.Placement sittingOn, String ignoreId) {
        if (!solid(p)) {
            return null;
        }
        double[] a = bounds(p);
        for (RobotDesign.Placement q : design.parts()) {
            if (q == sittingOn || q.id.equals(ignoreId) || !solid(q) || q.part.kind == PartCatalog.Kind.CHASSIS) {
                continue;
            }
            double[] b = bounds(q);
            if (a[0] < b[3] - 0.01 && b[0] < a[3] - 0.01 && a[1] < b[4] - 0.01 && b[1] < a[4] - 0.01
                    && a[2] < b[5] - 0.01 && b[2] < a[5] - 0.01) {
                return "That spot is taken by " + q.part.name.toLowerCase() + " (" + q.id + ").";
            }
        }
        return null;
    }

    /** Parts that take up room. Arms and what's on them swing, so they don't block spots. */
    private boolean solid(RobotDesign.Placement p) {
        PartCatalog.Kind k = p.part.kind;
        return k != PartCatalog.Kind.ARM && k != PartCatalog.Kind.CLAW && k != PartCatalog.Kind.HOOK
                && k != PartCatalog.Kind.CHASSIS;
    }

    /** Min x, y, z and max x, y, z of a part's box, with its turn. */
    static double[] bounds(RobotDesign.Placement p) {
        boolean sideways = Math.abs(Math.sin(Math.toRadians(p.turn))) > 0.5;
        double l2 = (sideways ? p.part.width : p.part.length) / 2;
        double w2 = (sideways ? p.part.length : p.part.width) / 2;
        double h2 = p.part.height / 2;
        return new double[]{p.x - l2, p.y - w2, p.z - h2, p.x + l2, p.y + w2, p.z + h2};
    }

    private static boolean canHold(RobotDesign.Placement target) {
        switch (target.part.kind) {
            case CHASSIS:
            case CHANNEL:
            case PLATE:
            case BRACKET:
            case HUB:
            case BATTERY:
            case MOTOR:
            case SERVO:
                return true;
            default:
                return false;
        }
    }

    private static boolean turnable(PartCatalog.Kind k) {
        return k == PartCatalog.Kind.CHANNEL || k == PartCatalog.Kind.PLATE || k == PartCatalog.Kind.BRACKET
                || k == PartCatalog.Kind.HUB || k == PartCatalog.Kind.BATTERY || k == PartCatalog.Kind.SWITCH
                || k == PartCatalog.Kind.MOTOR || k == PartCatalog.Kind.SERVO;
    }

    /** The top of a part: where something stacked on it sits. */
    private static double top(RobotDesign.Placement p) {
        return p.z + p.part.height / 2;
    }

    /** A sideways motor on this side (1 = left, -1 = right) that could drive wheels. */
    private RobotDesign.Placement driveMotorOn(int side) {
        for (RobotDesign.Placement q : design.parts()) {
            if (q.part.kind == PartCatalog.Kind.MOTOR && (q.y >= 0 ? 1 : -1) == side
                    && (q.turn == 90 || q.turn == -90) && !hasOn(q.id, PartCatalog.Kind.ARM)) {
                return q;
            }
        }
        return null;
    }

    private boolean drives(RobotDesign.Placement motor) {
        return hasOn(motor.id, PartCatalog.Kind.WHEEL);
    }

    private boolean hasOn(String id, PartCatalog.Kind kind) {
        for (RobotDesign.Placement q : design.parts()) {
            if (id.equals(q.on) && q.part.kind == kind) {
                return true;
            }
        }
        return false;
    }

    private RobotDesign.Placement first(PartCatalog.Kind kind) {
        for (RobotDesign.Placement q : design.parts()) {
            if (q.part.kind == kind) {
                return q;
            }
        }
        return null;
    }

    /** A name nobody on this robot uses yet: "arm", then "arm-2", "arm-3"... */
    private String unique(String base) {
        if (design.find(base) == null) {
            return base;
        }
        for (int i = 2; ; i++) {
            if (design.find(base + "-" + i) == null) {
                return base + "-" + i;
            }
        }
    }

    private static Proposal problem(String text) {
        return new Proposal(null, text);
    }

    static double snap(double v) {
        return Math.round(v / GRID) * GRID;
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static double normalize(double turn) {
        double t = turn % 360;
        if (t > 180) {
            t -= 360;
        } else if (t <= -180) {
            t += 360;
        }
        return t;
    }
}
