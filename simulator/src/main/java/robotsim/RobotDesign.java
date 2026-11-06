package robotsim;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A robot design: which parts it has, where they are, what each one is attached to, and which
 * port each cable plugs into. Saved as a .robot.json file in the repo's robots/ folder, so a group
 * shares designs through GitHub Desktop like code.
 *
 * Positions are inches from the robot's center on the floor: x forward, y to the left, z up.
 */
final class RobotDesign {

    /** One part placed on the robot. */
    static final class Placement {
        /** A name for this part on this robot, like "left-motor". */
        final String id;
        final PartCatalog.PartType part;
        final double x, y, z;
        /** Turned this many degrees to the left (seen from above). */
        final double turn;
        /** The part this one is attached to: the motor that drives a wheel or an arm, or the arm under a claw. */
        final String on;
        /** For parts on an arm: how far from the arm's pivot, in inches. */
        final double along;
        /** The port its cable plugs into, like "motor 0", "servo 0", "digital 1", "i2c 1". */
        final String port;

        Placement(String id, PartCatalog.PartType part, double x, double y, double z, double turn, String on,
                  double along, String port) {
            this.id = id;
            this.part = part;
            this.x = x;
            this.y = y;
            this.z = z;
            this.turn = turn;
            this.on = on;
            this.along = along;
            this.port = port;
        }
    }

    /** Something wrong with a design file, explained for students. */
    static final class DesignException extends Exception {
        DesignException(String message) {
            super(message);
        }
    }

    final String name;
    private final List<Placement> parts;

    RobotDesign(String name, List<Placement> parts) {
        this.name = name;
        this.parts = Collections.unmodifiableList(new ArrayList<Placement>(parts));
    }

    List<Placement> parts() {
        return parts;
    }

    Placement find(String id) {
        for (Placement p : parts) {
            if (p.id.equals(id)) {
                return p;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ CourseBot

    /** The course robot, part by part. robots/CourseBot.robot.json is a copy of this. */
    static RobotDesign courseBot() {
        List<Placement> p = new ArrayList<Placement>();
        p.add(at("frame", "chassis-18", 0, 0, 2.3, 0, null, null));
        p.add(at("hub", "control-hub", -4, -3, 4.1, 0, null, null));
        p.add(at("battery", "battery", -4, 3.5, 4.2, 0, null, null));
        p.add(at("switch", "power-switch", -7.5, 0, 3.8, 0, null, null));
        // Drive motors point their shafts outward, so the left one is mirrored (programs reverse it).
        p.add(at("left-motor", "motor-fast", -1.5, 5, 2.2, 90, null, "motor 0"));
        p.add(at("right-motor", "motor-fast", -1.5, -5, 2.2, -90, null, "motor 1"));
        p.add(at("left-front-wheel", "wheel-96", 5.5, 7.5, 1.89, 0, "left-motor", null));
        p.add(at("left-back-wheel", "wheel-96", -5.5, 7.5, 1.89, 0, "left-motor", null));
        p.add(at("right-front-wheel", "wheel-96", 5.5, -7.5, 1.89, 0, "right-motor", null));
        p.add(at("right-back-wheel", "wheel-96", -5.5, -7.5, 1.89, 0, "right-motor", null));
        // The arm swings on a motor at the front; the claw is on the end of the arm.
        p.add(at("arm-post", "upright-4", 4, -1.6, 4.5, 0, null, null));
        p.add(at("arm-motor", "motor-fast", 4, -1.6, 7, -90, null, "motor 2"));
        p.add(at("arm", "arm-11", 4, 0, 7, 0, "arm-motor", null));
        p.add(new Placement("claw", PartCatalog.get("claw"), 0, 0, 0, 0, "arm", 11, "servo 0"));
        p.add(at("bumper", "touch-sensor", 9.75, 0, 2.1, 0, null, "digital 1"));
        p.add(at("floor-sensor", "color-sensor", 7, 0, 0.9, 0, null, "i2c 1"));
        return new RobotDesign(RobotConfig.COURSE_BOT, p);
    }

    private static Placement at(String id, String partId, double x, double y, double z, double turn, String on,
                                String port) {
        return new Placement(id, PartCatalog.get(partId), x, y, z, turn, on, Double.NaN, port);
    }

    // ------------------------------------------------------------------ files

    Map<String, Object> toJson() {
        Map<String, Object> root = new LinkedHashMap<String, Object>();
        root.put("name", name);
        root.put("format", 1.0);
        List<Object> list = new ArrayList<Object>();
        for (Placement p : parts) {
            Map<String, Object> m = new LinkedHashMap<String, Object>();
            m.put("id", p.id);
            m.put("part", p.part.id);
            if (Double.isNaN(p.along)) {
                List<Object> at = new ArrayList<Object>();
                at.add(p.x);
                at.add(p.y);
                at.add(p.z);
                m.put("at", at);
            }
            if (p.turn != 0) {
                m.put("turn", p.turn);
            }
            if (p.on != null) {
                m.put("on", p.on);
            }
            if (!Double.isNaN(p.along)) {
                m.put("along", p.along);
            }
            if (p.port != null) {
                m.put("port", p.port);
            }
            list.add(m);
        }
        root.put("parts", list);
        return root;
    }

    String toJsonText() {
        return Json.write(toJson());
    }

    @SuppressWarnings("unchecked")
    static RobotDesign fromJsonText(String text, String fallbackName) throws DesignException {
        Object root;
        try {
            root = Json.parse(text);
        } catch (Json.ParseException e) {
            throw new DesignException("The file isn't valid JSON (" + e.getMessage() + ").");
        }
        if (!(root instanceof Map)) {
            throw new DesignException("The file should start with { and hold a \"parts\" list.");
        }
        Map<String, Object> map = (Map<String, Object>) root;
        String name = map.get("name") instanceof String ? (String) map.get("name") : fallbackName;
        if (!(map.get("parts") instanceof List)) {
            throw new DesignException("The file has no \"parts\" list.");
        }
        List<Placement> parts = new ArrayList<Placement>();
        int n = 0;
        for (Object o : (List<Object>) map.get("parts")) {
            n++;
            if (!(o instanceof Map)) {
                throw new DesignException("Part " + n + " should be { ... }.");
            }
            Map<String, Object> m = (Map<String, Object>) o;
            String id = m.get("id") instanceof String ? (String) m.get("id") : "part-" + n;
            Object partId = m.get("part");
            PartCatalog.PartType type = partId instanceof String ? PartCatalog.get((String) partId) : null;
            if (type == null) {
                throw new DesignException("Part \"" + id + "\" uses \"" + partId + "\", which isn't a known part.");
            }
            double[] at = {0, 0, 0};
            if (m.get("at") instanceof List) {
                List<Object> list = (List<Object>) m.get("at");
                for (int i = 0; i < 3 && i < list.size(); i++) {
                    at[i] = list.get(i) instanceof Number ? ((Number) list.get(i)).doubleValue() : 0;
                }
            }
            double turn = m.get("turn") instanceof Number ? ((Number) m.get("turn")).doubleValue() : 0;
            String on = m.get("on") instanceof String ? (String) m.get("on") : null;
            double along = m.get("along") instanceof Number ? ((Number) m.get("along")).doubleValue() : Double.NaN;
            String port = m.get("port") instanceof String ? (String) m.get("port") : null;
            parts.add(new Placement(id, type, at[0], at[1], at[2], turn, on, along, port));
        }
        for (Placement p : parts) {
            if (p.on != null && findIn(parts, p.on) == null) {
                throw new DesignException("Part \"" + p.id + "\" is on \"" + p.on + "\", but there's no part with that id.");
            }
        }
        return new RobotDesign(name, parts);
    }

    private static Placement findIn(List<Placement> parts, String id) {
        for (Placement p : parts) {
            if (p.id.equals(id)) {
                return p;
            }
        }
        return null;
    }

    static RobotDesign load(File file) throws DesignException {
        try {
            String text = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            String fallback = file.getName().replace(".robot.json", "");
            return fromJsonText(text, fallback);
        } catch (IOException e) {
            throw new DesignException("Couldn't read " + file.getName() + ": " + e.getMessage());
        }
    }

    void save(File file) throws IOException {
        Files.write(file.toPath(), toJsonText().getBytes(StandardCharsets.UTF_8));
    }

    // ------------------------------------------------------------------ parts list

    /** One line of the parts list: a part and how many the design uses. */
    static final class PartCount {
        final PartCatalog.PartType part;
        final int count;
        final List<String> ports;

        PartCount(PartCatalog.PartType part, int count, List<String> ports) {
            this.part = part;
            this.count = count;
            this.ports = ports;
        }
    }

    /** Every kind of part in the design, in catalog order, with counts. */
    List<PartCount> partsList() {
        List<PartCount> out = new ArrayList<PartCount>();
        for (PartCatalog.PartType type : PartCatalog.all()) {
            int count = 0;
            List<String> ports = new ArrayList<String>();
            for (Placement p : parts) {
                if (p.part == type) {
                    count++;
                    if (p.port != null) {
                        ports.add(p.port);
                    }
                }
            }
            if (count > 0) {
                out.add(new PartCount(type, count, ports));
            }
        }
        return out;
    }
}
