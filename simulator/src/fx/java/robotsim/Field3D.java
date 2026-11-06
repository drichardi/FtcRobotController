package robotsim;

import java.awt.image.BufferedImage;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import javax.swing.JComponent;

/**
 * The field in 3D, drawn with JavaFX from the same World snapshots as the 2D view. It only draws:
 * the physics are the same in both views.
 *
 * Coordinates: 1 unit = 1 inch. The field's x is JavaFX X, the field's y is JavaFX Z, and up is
 * JavaFX -Y (JavaFX's Y points down).
 */
final class Field3D implements FieldView3D {

    private static final double FIELD = World.FIELD;
    private static final double BLOCK = World.BLOCK_SIZE;

    private static final Color TILE_A = Color.web("#b9bec6");
    private static final Color TILE_B = Color.web("#a7adb6");
    private static final Color WALL = Color.web("#8a5a2b");
    private static final Color PERIMETER = Color.web("#5f6670");
    private static final Color ROBOT_BODY = Color.web("#3d4451");
    private static final Color ZONE = Color.web("#1f6feb");
    private static final Color DONE = Color.web("#1a7f37");
    private static final Color TODO = Color.web("#e8590c");
    private static final Color BLOCK_COLOR = Color.web("#f2c200");
    private static final Color TRAIL = Color.web("#8250df");
    private static final Color BALL_COLOR = Color.web("#e5534b");

    private final JFXPanel panel = new JFXPanel();
    private final AtomicBoolean updatePending = new AtomicBoolean(false);
    private volatile World.Snapshot latest;
    private volatile boolean showTrail;
    private volatile String hint;

    // Built on the JavaFX thread
    private SubScene sub;
    private final Group world = new Group();
    private final Group fieldStatic = new Group();
    private final Group goalsGroup = new Group();
    private final Group blocksGroup = new Group();
    private final Group ballsGroup = new Group();
    private final Group trailGroup = new Group();
    private final Group robot = new Group();
    private final Rotate robotYaw = new Rotate(0, Rotate.Y_AXIS);
    private final Translate robotPos = new Translate();
    private final Rotate armPitch = new Rotate(0, Rotate.Z_AXIS);
    // The robot's sizes, from the design it was built from
    private RobotModel builtRobot;
    private double robotLength, robotWidth, wheelRadius, ticksPerWheelTurn, armPivotX, armPivotHeight, armLength;
    private final Rotate leftSpin = new Rotate(0, Rotate.Z_AXIS);
    private final Rotate rightSpin = new Rotate(0, Rotate.Z_AXIS);
    private Box fingerLeft, fingerRight, bumper, sensor;
    private final PhongMaterial bumperMaterial = new PhongMaterial(Color.web("#6e7781"));
    private final PhongMaterial sensorMaterial = new PhongMaterial(Color.web("#b0b0b0"));
    private final List<PhongMaterial> goalMaterials = new ArrayList<>();
    private Label banner, hintLabel, fpsLabel;

    private Mission builtMission;
    private Mission blocksBuiltFor;
    private List<Mission.Tape> builtTapes;
    private int builtTrailSize = -1;

    // Camera
    private enum View { OVERHEAD, CHASE, ORBIT }
    private View view = View.OVERHEAD;
    private final Translate camTarget = new Translate(FIELD / 2, 0, FIELD / 2);
    private final Rotate camYaw = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate camPitch = new Rotate(-58, Rotate.X_AXIS);
    private final Translate camBack = new Translate(0, 0, -250);
    private double dragX, dragY, userYaw, userPitch, zoom = 1;

    Field3D() {
        Platform.setImplicitExit(false);
        CountDownLatch ready = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                buildScene();
            } finally {
                ready.countDown();
            }
        });
        try {
            ready.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (sub == null) {
            throw new IllegalStateException("JavaFX didn't start");
        }
    }

    @Override
    public JComponent component() {
        return panel;
    }

    @Override
    public void show(World.Snapshot s, boolean trail, String keyHint) {
        latest = s;
        showTrail = trail;
        hint = keyHint;
        // Only one update waits at a time; it always draws the newest snapshot.
        if (updatePending.compareAndSet(false, true)) {
            Platform.runLater(() -> {
                updatePending.set(false);
                update(latest);
            });
        }
    }

    @Override
    public BufferedImage capture() {
        BufferedImage[] out = new BufferedImage[1];
        CountDownLatch done = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                update(latest);
                WritableImage img = panel.getScene().getRoot().snapshot(null, null);
                out[0] = SwingFXUtils.fromFXImage(img, null);
            } finally {
                done.countDown();
            }
        });
        try {
            done.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return out[0];
    }

    @Override
    public void close() {
        Platform.exit();
        try {
            // Give the renderer a moment to finish its last frame.
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ------------------------------------------------------------------ scene

    private void buildScene() {
        AmbientLight ambient = new AmbientLight(Color.gray(0.5));
        PointLight sun = new PointLight(Color.gray(0.45));
        sun.getTransforms().add(new Translate(FIELD * 0.3, -260, FIELD * 0.2));
        world.getChildren().addAll(fieldStatic, goalsGroup, trailGroup, blocksGroup, ballsGroup, robot, ambient, sun);
        robot.getTransforms().addAll(robotPos, robotYaw);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFieldOfView(38);
        camera.setNearClip(0.5);
        camera.setFarClip(3000);
        camera.getTransforms().addAll(camTarget, camYaw, camPitch, camBack);

        sub = new SubScene(world, 800, 700, true, SceneAntialiasing.BALANCED);
        sub.setFill(Color.web("#2b2d30"));
        sub.setCamera(camera);

        banner = overlayLabel(18, "#ffffff");
        StackPane.setAlignment(banner, Pos.TOP_CENTER);
        StackPane.setMargin(banner, new Insets(48, 0, 0, 0));
        hintLabel = overlayLabel(13, "#ffd33d");
        hintLabel.setStyle(hintLabel.getStyle() + "-fx-background-color: rgba(31,35,40,0.88);");
        StackPane.setAlignment(hintLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(hintLabel, new Insets(0, 0, 14, 0));
        fpsLabel = new Label("");
        fpsLabel.setStyle("-fx-text-fill: #8c959f; -fx-font-size: 11px;");
        StackPane.setAlignment(fpsLabel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(fpsLabel, new Insets(0, 8, 6, 0));

        HBox cameraButtons = cameraButtons();
        StackPane.setAlignment(cameraButtons, Pos.TOP_LEFT);
        StackPane.setMargin(cameraButtons, new Insets(8));

        Pane holder = new Pane(sub);
        sub.widthProperty().bind(holder.widthProperty());
        sub.heightProperty().bind(holder.heightProperty());
        StackPane root = new StackPane(holder, banner, hintLabel, fpsLabel, cameraButtons);
        root.setStyle("-fx-background-color: #2b2d30;");
        Scene scene = new Scene(root, 800, 700);
        panel.setScene(scene);
        addMouseControls(holder);
        startFpsCounter();
    }

    private static Label overlayLabel(double size, String color) {
        Label l = new Label();
        l.setVisible(false);
        l.setFont(Font.font("System", FontWeight.BOLD, size));
        l.setStyle("-fx-text-fill: " + color + "; -fx-padding: 8 16 8 16; -fx-background-radius: 10;");
        return l;
    }

    private HBox cameraButtons() {
        ToggleGroup group = new ToggleGroup();
        HBox box = new HBox(4);
        box.setMaxSize(HBox.USE_PREF_SIZE, HBox.USE_PREF_SIZE);
        String[] names = {"Overhead", "Chase", "Orbit"};
        View[] views = {View.OVERHEAD, View.CHASE, View.ORBIT};
        for (int i = 0; i < names.length; i++) {
            ToggleButton b = new ToggleButton(names[i]);
            b.setToggleGroup(group);
            b.setFocusTraversable(false);
            b.setSelected(i == 0);
            View v = views[i];
            b.setOnAction(e -> {
                view = v;
                userYaw = 0;
                userPitch = 0;
                zoom = 1;
                b.setSelected(true);
                placeCamera(latest);
            });
            box.getChildren().add(b);
        }
        return box;
    }

    private void addMouseControls(Pane target) {
        target.setOnMousePressed(e -> {
            dragX = e.getSceneX();
            dragY = e.getSceneY();
            panel.requestFocus();
        });
        target.setOnMouseDragged(e -> {
            userYaw += (e.getSceneX() - dragX) * 0.4;
            userPitch = Math.max(-30, Math.min(30, userPitch - (e.getSceneY() - dragY) * 0.3));
            dragX = e.getSceneX();
            dragY = e.getSceneY();
            placeCamera(latest);
        });
        target.setOnScroll(e -> {
            zoom = Math.max(0.35, Math.min(2.5, zoom * (e.getDeltaY() > 0 ? 0.9 : 1.1)));
            placeCamera(latest);
        });
    }

    private void startFpsCounter() {
        new AnimationTimer() {
            private long last = 0;
            private int frames = 0;

            @Override
            public void handle(long now) {
                frames++;
                if (last == 0) {
                    last = now;
                } else if (now - last >= 1_000_000_000L) {
                    fpsLabel.setText(frames + " fps");
                    frames = 0;
                    last = now;
                }
            }
        }.start();
    }

    // ------------------------------------------------------------------ robot

    /** Builds the robot's 3D shape from its design. Runs again if the design changes. */
    private void buildRobot(RobotModel m) {
        robot.getChildren().clear();
        robotLength = m.length;
        robotWidth = m.width;
        wheelRadius = m.wheelDiameter / 2;
        ticksPerWheelTurn = m.ticksPerMotorRev;
        armPivotX = m.armPivotAhead;
        armPivotHeight = m.armPivotHeight;
        armLength = m.armLength;
        armPitch.setPivotX(armPivotX);
        armPitch.setPivotY(-armPivotHeight);
        PhongMaterial body = new PhongMaterial(ROBOT_BODY);
        PhongMaterial black = new PhongMaterial(Color.web("#141414"));
        PhongMaterial metal = new PhongMaterial(Color.web("#9aa3ad"));

        Box chassis = box(robotLength - 1, 2, robotWidth - 4, body);
        chassis.setTranslateY(-(wheelRadius + 0.4));
        Box deck = box(10, 0.4, robotWidth - 6, new PhongMaterial(Color.web("#4b5361")));
        deck.setTranslateY(-(wheelRadius + 1.6));

        // Two wheels per side; they spin with the encoder ticks.
        Group wheels = new Group();
        for (int side = -1; side <= 1; side += 2) {
            for (int end = -1; end <= 1; end += 2) {
                Cylinder w = new Cylinder(wheelRadius, 1.4);
                w.setMaterial(black);
                Group g = new Group(w, spoke(metal));
                g.getTransforms().addAll(new Translate(end * 5.5, -wheelRadius, side * (robotWidth / 2 - 1.2)),
                        side < 0 ? rightSpin : leftSpin, new Rotate(90, Rotate.X_AXIS));
                wheels.getChildren().add(g);
            }
        }

        Box hub = box(4.5, 1.2, 4, new PhongMaterial(Color.web("#2a2a2a")));
        hub.getTransforms().add(new Translate(-4, -(wheelRadius + 2.4), -3));
        Box battery = box(5, 1.8, 3, new PhongMaterial(Color.web("#1f6feb")));
        battery.getTransforms().add(new Translate(-4, -(wheelRadius + 2.7), 3.5));

        // Front bumper (the touch sensor): red when pressed
        bumper = box(1.5, 2, robotWidth - 2, bumperMaterial);
        bumper.getTransforms().add(new Translate(robotLength / 2 + 0.75, -(wheelRadius + 0.2), 0));

        // Arrow on top showing the front
        Box arrow = box(6, 0.3, 1.2, new PhongMaterial(Color.WHITE));
        arrow.getTransforms().add(new Translate(1, -(wheelRadius + 1.9), 0));
        Box arrowHead = box(1.6, 0.3, 3.4, new PhongMaterial(Color.WHITE));
        arrowHead.getTransforms().add(new Translate(4.2, -(wheelRadius + 1.9), 0));

        // Color sensor, looking down at the floor ahead of the center
        sensor = box(1.6, 0.8, 1.6, sensorMaterial);
        sensor.getTransforms().add(new Translate(m.colorSensorAhead, -0.9, 0));

        // Arm on a motor at the front, with the claw at its end
        Box tower = box(1.5, armPivotHeight - wheelRadius, 1.5, metal);
        tower.getTransforms().add(new Translate(armPivotX, -(wheelRadius + armPivotHeight) / 2 - 1, 0));
        Group arm = new Group();
        Box bar = box(armLength, 1, 1.2, new PhongMaterial(Color.web("#e8590c")));
        bar.getTransforms().add(new Translate(armPivotX + armLength / 2, -armPivotHeight, 0));
        fingerLeft = box(3, 2.4, 0.6, metal);
        fingerRight = box(3, 2.4, 0.6, metal);
        arm.getChildren().addAll(bar, fingerLeft, fingerRight);
        if (m.hasHook()) {
            // A small J-shaped hook hanging under the arm
            Box shank = box(0.4, 1.4, 0.4, metal);
            shank.getTransforms().add(new Translate(armPivotX + m.hookAlongArm, -armPivotHeight + 0.7, 0));
            Box tip = box(1.0, 0.4, 0.4, metal);
            tip.getTransforms().add(new Translate(armPivotX + m.hookAlongArm + 0.3, -armPivotHeight + 1.3, 0));
            arm.getChildren().addAll(shank, tip);
        }
        arm.getTransforms().add(armPitch);

        robot.getChildren().addAll(chassis, deck, wheels, hub, battery, bumper, arrow, arrowHead, sensor, tower, arm);
    }

    private Node spoke(PhongMaterial m) {
        Box b = new Box(wheelRadius * 1.6, 1.5, 0.5);
        b.setMaterial(m);
        return b;
    }

    private static Box box(double w, double h, double d, PhongMaterial m) {
        Box b = new Box(w, h, d);
        b.setMaterial(m);
        return b;
    }

    // ------------------------------------------------------------------ field

    private void buildField(World.Snapshot s) {
        fieldStatic.getChildren().clear();
        PhongMaterial tileA = new PhongMaterial(TILE_A);
        PhongMaterial tileB = new PhongMaterial(TILE_B);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                Box t = box(23.8, 0.5, 23.8, (i + j) % 2 == 0 ? tileA : tileB);
                t.getTransforms().add(new Translate(i * 24 + 12, 0.25, j * 24 + 12));
                fieldStatic.getChildren().add(t);
            }
        }
        PhongMaterial perimeter = new PhongMaterial(PERIMETER);
        double h = 6;
        fieldStatic.getChildren().addAll(
                wall(-1, -1, FIELD + 2, 1, h, perimeter), wall(-1, FIELD, FIELD + 2, 1, h, perimeter),
                wall(-1, 0, 1, FIELD, h, perimeter), wall(FIELD, 0, 1, FIELD, h, perimeter));
        PhongMaterial wood = new PhongMaterial(WALL);
        PhongMaterial cone = new PhongMaterial(Color.web("#f08c00"));
        for (Rectangle2D r : s.mission.obstacles) {
            boolean isCone = r.getWidth() <= 3.1 && r.getHeight() <= 3.1;
            fieldStatic.getChildren().add(wall(r.getX(), r.getY(), r.getWidth(), r.getHeight(),
                    isCone ? 6 : 10, isCone ? cone : wood));
        }
        for (Mission.Tape t : s.tapes) {
            PhongMaterial m = new PhongMaterial(t.dark ? Color.web("#111111") : Color.web("#f4f5f7"));
            for (int i = 0; i + 1 < t.points.length; i++) {
                double[] a = t.points[i], b = t.points[i + 1];
                double len = Math.hypot(b[0] - a[0], b[1] - a[1]);
                Box seg = box(len + t.width, 0.06, t.width, m);
                seg.getTransforms().addAll(new Translate((a[0] + b[0]) / 2, -0.03, (a[1] + b[1]) / 2),
                        new Rotate(-Math.toDegrees(Math.atan2(b[1] - a[1], b[0] - a[0])), Rotate.Y_AXIS));
                fieldStatic.getChildren().add(seg);
            }
        }
        buildGoals(s.mission);
    }

    private static Box wall(double x, double y, double w, double d, double h, PhongMaterial m) {
        Box b = box(w, h, d, m);
        b.getTransforms().add(new Translate(x + w / 2, -h / 2, y + d / 2));
        return b;
    }

    private void buildGoals(Mission m) {
        goalsGroup.getChildren().clear();
        goalMaterials.clear();
        for (Mission.Goal goal : m.goals) {
            PhongMaterial mat = new PhongMaterial(TODO);
            goalMaterials.add(mat);
            if (goal instanceof Mission.ZoneGoal) {
                Mission.ZoneGoal z = (Mission.ZoneGoal) goal;
                addZone(z.zone, z.zoneName, mat);
            } else if (goal instanceof Mission.BallInZoneGoal) {
                Mission.BallInZoneGoal z = (Mission.BallInZoneGoal) goal;
                addZone(z.zone, z.zoneName, mat);
            } else if (goal instanceof Mission.BlockInZoneGoal) {
                Mission.BlockInZoneGoal z = (Mission.BlockInZoneGoal) goal;
                addZone(z.zone, z.zoneName, mat);
            } else if (goal instanceof Mission.WaypointGoal) {
                Mission.WaypointGoal w = (Mission.WaypointGoal) goal;
                Cylinder c = new Cylinder(w.radius, 0.08);
                c.setMaterial(mat);
                c.getTransforms().add(new Translate(w.x, -0.06, w.y));
                goalsGroup.getChildren().addAll(c, floorText(w.tag, w.x, w.y));
            } else if (goal instanceof Mission.GateGoal) {
                Line2D l = ((Mission.GateGoal) goal).line;
                double len = l.getP1().distance(l.getP2());
                Box line = box(len, 0.08, 1, mat);
                line.getTransforms().addAll(new Translate((l.getX1() + l.getX2()) / 2, -0.06, (l.getY1() + l.getY2()) / 2),
                        new Rotate(-Math.toDegrees(Math.atan2(l.getY2() - l.getY1(), l.getX2() - l.getX1())), Rotate.Y_AXIS));
                goalsGroup.getChildren().addAll(line,
                        floorText(((Mission.GateGoal) goal).tag, (l.getX1() + l.getX2()) / 2 + 6, (l.getY1() + l.getY2()) / 2));
            }
        }
    }

    private void addZone(Rectangle2D r, String name, PhongMaterial mat) {
        Box zone = box(r.getWidth(), 0.08, r.getHeight(), mat);
        zone.getTransforms().add(new Translate(r.getCenterX(), -0.06, r.getCenterY()));
        goalsGroup.getChildren().addAll(zone, floorText(name, r.getCenterX(), r.getCenterY()));
    }

    /**
     * A word lying flat on the floor, readable from the default camera. JavaFX draws 3D text
     * blurry, so the word is drawn as a sharp picture first and laid on a flat plate.
     */
    private static Node floorText(String s, double x, double y) {
        Text t = new Text(s);
        t.setFont(Font.font("System", FontWeight.BOLD, 64));
        t.setFill(Color.WHITE);
        t.setStroke(Color.web("#1f2328"));
        t.setStrokeWidth(3);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage img = t.snapshot(params, null);
        double h = 4.5;
        double w = h * img.getWidth() / img.getHeight();
        TriangleMesh mesh = new TriangleMesh();
        float x0 = (float) (x - w / 2), x1 = (float) (x + w / 2);
        float zNear = (float) (y - h / 2), zFar = (float) (y + h / 2);
        mesh.getPoints().addAll(x0, -0.15f, zFar, x1, -0.15f, zFar, x1, -0.15f, zNear, x0, -0.15f, zNear);
        mesh.getTexCoords().addAll(0, 0, 1, 0, 1, 1, 0, 1);
        mesh.getFaces().addAll(0, 0, 1, 1, 2, 2, 0, 0, 2, 2, 3, 3);
        MeshView plate = new MeshView(mesh);
        plate.setCullFace(CullFace.NONE);
        PhongMaterial m = new PhongMaterial(Color.WHITE);
        m.setDiffuseMap(img);
        m.setSelfIlluminationMap(img);
        plate.setMaterial(m);
        return plate;
    }

    /** For tests and screenshots: "overhead", "chase", or "orbit". */
    void useView(String name) {
        Platform.runLater(() -> {
            view = View.valueOf(name.toUpperCase());
            userYaw = 0;
            userPitch = 0;
            zoom = 1;
            placeCamera(latest);
        });
    }

    // ------------------------------------------------------------------ per frame

    private void update(World.Snapshot s) {
        if (s == null || sub == null) {
            return;
        }
        if (s.mission != builtMission || !s.tapes.equals(builtTapes)) {
            buildField(s);
            builtMission = s.mission;
            builtTapes = s.tapes;
            builtTrailSize = -1;
        }
        for (int i = 0; i < goalMaterials.size(); i++) {
            boolean done = i < s.goalIndex;
            boolean current = i == s.goalIndex;
            Mission.Goal g = s.mission.goals.get(i);
            Color base = done ? DONE : (g instanceof Mission.ZoneGoal ? ZONE
                    : g instanceof Mission.BlockInZoneGoal ? BLOCK_COLOR.darker()
                    : g instanceof Mission.BallInZoneGoal ? BALL_COLOR : TODO);
            goalMaterials.get(i).setDiffuseColor(base.deriveColor(0, current || done ? 1 : 0.7, 1, 1));
        }

        if (s.robot != builtRobot) {
            buildRobot(s.robot);
            builtRobot = s.robot;
        }
        robotPos.setX(s.x);
        robotPos.setZ(s.y);
        robotYaw.setAngle(-s.headingDeg);
        leftSpin.setAngle(-s.leftTicks / ticksPerWheelTurn * 360);
        rightSpin.setAngle(-s.rightTicks / ticksPerWheelTurn * 360);
        double elevation = s.robot.armElevationDeg(s.armDeg);
        armPitch.setAngle(-elevation);
        double spread = s.clawPos >= 0.5 ? 2.6 : 1.4;
        double tipX = armPivotX + armLength + 1.2;
        fingerLeft.getTransforms().setAll(new Translate(tipX, -armPivotHeight, spread));
        fingerRight.getTransforms().setAll(new Translate(tipX, -armPivotHeight, -spread));
        bumperMaterial.setDiffuseColor(s.touch ? Color.web("#cf222e") : Color.web("#6e7781"));
        double shade = Math.max(0.05, Math.min(1, s.surface * 1.1));
        sensorMaterial.setDiffuseColor(Color.color(shade, shade, shade));

        updateBlocks(s, elevation);
        updateTrail(s);
        updateOverlays(s);
        placeCamera(s);
    }

    private void updateBlocks(World.Snapshot s, double elevation) {
        if (blocksGroup.getChildren().size() != s.blocks.size() || s.mission != blocksBuiltFor) {
            blocksGroup.getChildren().clear();
            for (double[] b : s.blocks) {
                blocksGroup.getChildren().add(blockNode(b[6] > 0));
            }
            blocksBuiltFor = s.mission;
        }
        double e = Math.toRadians(elevation);
        double h = Math.toRadians(s.headingDeg);
        for (int i = 0; i < s.blocks.size(); i++) {
            double[] b = s.blocks.get(i);
            Node n = blocksGroup.getChildren().get(i);
            if (b[2] > 0) {
                // Held: hangs from the claw at the end of the arm
                double ahead = armPivotX + (armLength + 1.2) * Math.cos(e);
                double up = armPivotHeight + (armLength + 1.2) * Math.sin(e);
                n.getTransforms().setAll(new Translate(s.x + ahead * Math.cos(h), -Math.max(BLOCK / 2, up - 1), s.y + ahead * Math.sin(h)),
                        new Rotate(-s.headingDeg, Rotate.Y_AXIS));
            } else {
                // On the floor, or hanging from the hook (lifted)
                n.getTransforms().setAll(new Translate(b[0], -(BLOCK / 2 + b[4]), b[1]),
                        new Rotate(-b[3], Rotate.Y_AXIS));
            }
        }
        while (ballsGroup.getChildren().size() < s.balls.size()) {
            Sphere ball = new Sphere(1, 24);
            ball.setMaterial(new PhongMaterial(BALL_COLOR));
            ballsGroup.getChildren().add(ball);
        }
        while (ballsGroup.getChildren().size() > s.balls.size()) {
            ballsGroup.getChildren().remove(ballsGroup.getChildren().size() - 1);
        }
        for (int i = 0; i < s.balls.size(); i++) {
            double[] b = s.balls.get(i);
            Sphere ball = (Sphere) ballsGroup.getChildren().get(i);
            ball.setRadius(b[2]);
            ball.getTransforms().setAll(new Translate(b[0], -b[2], b[1]));
        }
    }

    /** A yellow block, with a metal handle on top if a hook can lift it. */
    private Node blockNode(boolean handle) {
        Box cube = box(BLOCK, BLOCK, BLOCK, new PhongMaterial(BLOCK_COLOR));
        if (!handle) {
            return new Group(cube);
        }
        PhongMaterial metal = new PhongMaterial(Color.web("#8c959f"));
        double h = World.HANDLE_HEIGHT;
        Box postA = box(0.4, h, 0.4, metal);
        postA.getTransforms().add(new Translate(-1.6, -(BLOCK / 2 + h / 2), 0));
        Box postB = box(0.4, h, 0.4, metal);
        postB.getTransforms().add(new Translate(1.6, -(BLOCK / 2 + h / 2), 0));
        Box bar = box(3.6, 0.4, 0.4, metal);
        bar.getTransforms().add(new Translate(0, -(BLOCK / 2 + h), 0));
        return new Group(cube, postA, postB, bar);
    }

    private void updateTrail(World.Snapshot s) {
        int size = s.trail.size();
        if (!showTrail) {
            trailGroup.getChildren().clear();
            builtTrailSize = -1;
            return;
        }
        if (size == builtTrailSize) {
            return;
        }
        if (size < builtTrailSize) {
            trailGroup.getChildren().clear();
            builtTrailSize = 0;
        }
        PhongMaterial m = new PhongMaterial(TRAIL);
        int start = Math.max(1, builtTrailSize);
        for (int i = start; i < size; i++) {
            double[] a = s.trail.get(i - 1), b = s.trail.get(i);
            double len = Math.hypot(b[0] - a[0], b[1] - a[1]);
            Box seg = box(len + 0.4, 0.05, 0.6, m);
            seg.getTransforms().addAll(new Translate((a[0] + b[0]) / 2, -0.12, (a[1] + b[1]) / 2),
                    new Rotate(-Math.toDegrees(Math.atan2(b[1] - a[1], b[0] - a[0])), Rotate.Y_AXIS));
            trailGroup.getChildren().add(seg);
        }
        builtTrailSize = size;
    }

    private void updateOverlays(World.Snapshot s) {
        if (s.failure != null) {
            banner.setText("MISSION FAILED: " + s.failure);
            banner.setStyle(banner.getStyle().replaceAll("-fx-background-color:[^;]*;", "")
                    + "-fx-background-color: rgba(207,34,46,0.92);");
            banner.setVisible(true);
        } else if (s.complete) {
            banner.setText(s.completeSeconds >= 0
                    ? String.format("MISSION COMPLETE!   %.1f seconds", s.completeSeconds) : "MISSION COMPLETE!");
            banner.setStyle(banner.getStyle().replaceAll("-fx-background-color:[^;]*;", "")
                    + "-fx-background-color: rgba(26,127,55,0.92);");
            banner.setVisible(true);
        } else {
            banner.setVisible(false);
        }
        String h = hint;
        hintLabel.setVisible(h != null);
        if (h != null) {
            hintLabel.setText(h);
        }
    }

    private void placeCamera(World.Snapshot s) {
        switch (view) {
            case CHASE:
                if (s != null) {
                    camTarget.setX(s.x);
                    camTarget.setY(-4);
                    camTarget.setZ(s.y);
                    camYaw.setAngle(90 - s.headingDeg + userYaw);
                }
                camPitch.setAngle(-24 + userPitch);
                camBack.setZ(-70 * zoom);
                break;
            case ORBIT:
                camTarget.setX(FIELD / 2);
                camTarget.setY(0);
                camTarget.setZ(FIELD / 2);
                camYaw.setAngle(35 + userYaw);
                camPitch.setAngle(-35 + userPitch);
                camBack.setZ(-260 * zoom);
                break;
            default:
                camTarget.setX(FIELD / 2);
                camTarget.setY(0);
                camTarget.setZ(FIELD / 2 - 6);
                camYaw.setAngle(userYaw);
                camPitch.setAngle(-62 + userPitch);
                camBack.setZ(-255 * zoom);
                break;
        }
    }
}
