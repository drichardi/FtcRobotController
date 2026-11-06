package robotsim;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * 3D shapes for each kind of part, centered on the part, for the robot builder. JavaFX axes:
 * X = the part's length (forward before turning), Y = down, Z = its width.
 * An arm's shape starts at its pivot and reaches along +X.
 */
final class PartShapes {

    private PartShapes() {
    }

    static Color color(PartCatalog.PartType t) {
        return Color.rgb((t.color >> 16) & 255, (t.color >> 8) & 255, t.color & 255);
    }

    /** The part's shape. If override isn't null, every piece uses it (for the see-through ghost). */
    static Group shape(PartCatalog.PartType t, PhongMaterial override) {
        PhongMaterial main = override != null ? override : new PhongMaterial(color(t));
        PhongMaterial metal = override != null ? override : new PhongMaterial(Color.web("#9aa3ad"));
        PhongMaterial dark = override != null ? override : new PhongMaterial(Color.web("#1f2328"));
        Group g = new Group();
        switch (t.kind) {
            case CHASSIS: {
                double rail = 1.2;
                g.getChildren().addAll(
                        at(box(rail, t.height, t.width, main), t.length / 2 - rail / 2, 0, 0),
                        at(box(rail, t.height, t.width, main), -t.length / 2 + rail / 2, 0, 0),
                        at(box(t.length, t.height, rail, main), 0, 0, t.width / 2 - rail / 2),
                        at(box(t.length, t.height, rail, main), 0, 0, -t.width / 2 + rail / 2),
                        at(box(t.length - 2, 0.3, t.width - 2,
                                override != null ? override : new PhongMaterial(Color.web("#4b5361"))), 0, 0.6, 0));
                PhongMaterial white = override != null ? override : new PhongMaterial(Color.WHITE);
                g.getChildren().addAll(at(box(6, 0.3, 1.2, white), 1, -t.height / 2 - 0.2, 0),
                        at(box(1.6, 0.3, 3.4, white), 4.2, -t.height / 2 - 0.2, 0));
                break;
            }
            case MOTOR: {
                Cylinder can = new Cylinder(t.width / 2, t.length * 0.65);
                can.setMaterial(main);
                can.getTransforms().addAll(new Translate(-t.length * 0.17, 0, 0), new Rotate(90, Rotate.Z_AXIS));
                Box gearbox = box(t.length * 0.3, t.width, t.width,
                        override != null ? override : new PhongMaterial(Color.web("#c9a227")));
                gearbox.getTransforms().add(new Translate(t.length * 0.33, 0, 0));
                g.getChildren().addAll(can, gearbox);
                break;
            }
            case WHEEL: {
                double r = t.number("diameter") / 2;
                Cylinder w = new Cylinder(r, t.width);
                w.setMaterial(main);
                w.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
                Box spoke = box(r * 1.6, 0.5, t.width + 0.1, metal);
                g.getChildren().addAll(w, spoke);
                break;
            }
            case HUB: {
                Box led = box(0.5, 0.2, 0.5, override != null ? override : new PhongMaterial(Color.web("#2ea043")));
                led.getTransforms().add(new Translate(t.length / 2 - 0.6, -t.height / 2 - 0.1, t.width / 2 - 0.6));
                g.getChildren().addAll(box(t.length, t.height, t.width, main), led);
                break;
            }
            case ARM:
                g.getChildren().add(at(box(t.length, t.height, t.width, main), t.length / 2, 0, 0));
                break;
            case CLAW:
                g.getChildren().addAll(at(box(1.2, 1.4, 2.0, dark), 0, -0.6, 0),
                        at(box(3, 2.4, 0.6, metal), 1.2, 0, 2.6), at(box(3, 2.4, 0.6, metal), 1.2, 0, -2.6));
                break;
            case HOOK:
                g.getChildren().addAll(at(box(0.4, 1.4, 0.4, metal), 0, 0.7, 0), at(box(1.0, 0.4, 0.4, metal), 0.3, 1.3, 0));
                break;
            default:
                g.getChildren().add(box(t.length, t.height, t.width, main));
                break;
        }
        return g;
    }

    /** The arm's starting angle: resting down in front, like in the simulator. */
    static final double ARM_REST_DEG = 35;

    /**
     * A part placed on the robot, in robot-builder coordinates: robot (x, y, z) becomes JavaFX
     * (x, -z, y). The arm rests down in front; parts on it go "along" it from the pivot.
     */
    static Group placed(RobotDesign.Placement p, RobotDesign design, PhongMaterial override) {
        Group g = shape(p.part, override);
        RobotDesign.Placement on = p.on == null ? null : design.find(p.on);
        if (on != null && on.part.kind == PartCatalog.Kind.ARM) {
            double along = Double.isNaN(p.along) ? on.part.length : p.along;
            g.getTransforms().addAll(new Translate(on.x, -on.z, 0), new Rotate(ARM_REST_DEG, Rotate.Z_AXIS),
                    new Translate(along, 0, 0));
        } else if (p.part.kind == PartCatalog.Kind.ARM) {
            g.getTransforms().addAll(new Translate(p.x, -p.z, 0), new Rotate(ARM_REST_DEG, Rotate.Z_AXIS));
        } else if (p.part.kind == PartCatalog.Kind.WHEEL) {
            g.getTransforms().addAll(new Translate(p.x, -p.part.number("diameter") / 2, p.y),
                    new Rotate(-p.turn, Rotate.Y_AXIS));
        } else {
            g.getTransforms().addAll(new Translate(p.x, -p.z, p.y), new Rotate(-p.turn, Rotate.Y_AXIS));
        }
        return g;
    }

    private static Box box(double l, double h, double w, PhongMaterial m) {
        Box b = new Box(l, h, w);
        b.setMaterial(m);
        return b;
    }

    private static Node at(Node n, double x, double y, double z) {
        n.getTransforms().add(new Translate(x, y, z));
        return n;
    }
}
