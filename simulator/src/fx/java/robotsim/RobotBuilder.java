package robotsim;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * The robot builder: pick a part from the menu, point at the robot, click to place it. Parts
 * snap where they belong (the rules are in DesignEditor). Saves designs to the robots folder.
 *
 * Mouse: drag to look around, scroll to zoom. Keys: R turns the part, Delete removes the
 * selected part, Esc puts the part down, Ctrl/⌘+Z undoes, Ctrl/⌘+Y redoes.
 */
final class RobotBuilder implements RobotBuilderLauncher {

    private static final PhongMaterial GHOST_OK = new PhongMaterial(Color.rgb(46, 160, 67, 0.55));
    private static final PhongMaterial GHOST_BAD = new PhongMaterial(Color.rgb(207, 34, 46, 0.55));

    private static RobotBuilder open;

    private JFrame frame;
    private final JFXPanel panel = new JFXPanel();
    private File robotsFolder;
    private Consumer<String> onUse;
    private volatile boolean dirty = false;

    // ---- on the JavaFX thread ----
    private DesignEditor editor;
    private RobotDesign savedDesign;
    private PartCatalog.PartType tool;
    private int quarterTurns = 0;
    private String selected;
    private DesignEditor.Proposal proposal;
    /** Where the mouse last pointed, so R can turn the part without moving it. */
    private Aim lastAim;
    private final Group partsGroup = new Group();
    private final Group ghostGroup = new Group();
    private final Group selectionGroup = new Group();
    private final ToggleGroup toolButtons = new ToggleGroup();
    private final Map<String, ToggleButton> toolButtonById = new LinkedHashMap<String, ToggleButton>();
    private TextField nameField;
    private Label status, summary, selectedTitle, selectedDetails;
    private VBox selectedBox;
    private ComboBox<String> portBox;
    private TextField rpmField, ticksField;
    private VBox motorRow;
    private MenuButton openMenu;
    private boolean updatingPort = false;

    // camera
    private final Translate camTarget = new Translate(0, -3, 0);
    private final Rotate camYaw = new Rotate(-35, Rotate.Y_AXIS);
    private final Rotate camPitch = new Rotate(-30, Rotate.X_AXIS);
    private final Translate camBack = new Translate(0, 0, -60);
    private double pressX, pressY, lastX, lastY;
    private boolean dragged;

    @Override
    public void open(Window owner, File folder, RobotDesign start, Consumer<String> use) {
        if (open != null && open.frame != null && open.frame.isDisplayable()) {
            open.frame.toFront();
            return;
        }
        open = this;
        robotsFolder = folder;
        onUse = use;
        frame = new JFrame("Robot Builder");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (dirty && javax.swing.JOptionPane.showConfirmDialog(frame,
                        "Close the builder without saving your changes?", "Robot Builder",
                        javax.swing.JOptionPane.YES_NO_OPTION) != javax.swing.JOptionPane.YES_OPTION) {
                    return;
                }
                frame.dispose();
            }
        });
        frame.getContentPane().add(panel);
        frame.setSize(1240, 800);
        frame.setLocationRelativeTo(owner);
        Platform.setImplicitExit(false);
        CountDownLatch ready = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                build(start);
            } finally {
                ready.countDown();
            }
        });
        try {
            ready.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        frame.setVisible(true);
    }

    // ------------------------------------------------------------------ layout

    private void build(RobotDesign start) {
        editor = new DesignEditor(start);
        savedDesign = start;

        // 3D view
        Group world = new Group();
        Box floor = new Box(80, 0.2, 80);
        floor.setMaterial(new PhongMaterial(Color.web("#c9cdd3")));
        floor.setTranslateY(0.1);
        floor.setUserData("floor");
        Group grid = new Group();
        PhongMaterial line = new PhongMaterial(Color.web("#aeb4bc"));
        for (int i = -36; i <= 36; i += 6) {
            Box a = new Box(72, 0.02, 0.08);
            a.setMaterial(line);
            a.getTransforms().add(new Translate(0, -0.01, i));
            Box b = new Box(0.08, 0.02, 72);
            b.setMaterial(line);
            b.getTransforms().add(new Translate(i, -0.01, 0));
            grid.getChildren().addAll(a, b);
        }
        grid.setMouseTransparent(true);
        ghostGroup.setMouseTransparent(true);
        selectionGroup.setMouseTransparent(true);
        PointLight sun = new PointLight(Color.gray(0.55));
        sun.getTransforms().add(new Translate(20, -60, -30));
        world.getChildren().addAll(floor, grid, partsGroup, ghostGroup, selectionGroup,
                new AmbientLight(Color.gray(0.55)), sun);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFieldOfView(40);
        camera.setNearClip(0.3);
        camera.setFarClip(1000);
        camera.getTransforms().addAll(camTarget, camYaw, camPitch, camBack);
        SubScene sub = new SubScene(world, 700, 600, true, SceneAntialiasing.BALANCED);
        sub.setFill(Color.web("#2b2d30"));
        sub.setCamera(camera);
        Pane holder = new Pane(sub);
        sub.widthProperty().bind(holder.widthProperty());
        sub.heightProperty().bind(holder.heightProperty());
        addMouse(sub);

        BorderPane root = new BorderPane();
        root.setTop(toolbar());
        root.setLeft(catalog());
        root.setCenter(holder);
        root.setRight(sidePanel());
        status = new Label();
        status.setPadding(new Insets(6, 10, 6, 10));
        status.setMaxWidth(Double.MAX_VALUE);
        status.setStyle("-fx-background-color: #f6f8fa; -fx-border-color: #d0d7de; -fx-border-width: 1 0 0 0;");
        root.setBottom(status);

        Scene scene = new Scene(root, 1240, 780);
        scene.setOnKeyPressed(e -> {
            boolean shortcut = e.isShortcutDown();
            if (e.getCode() == KeyCode.R && !shortcut && !(scene.getFocusOwner() instanceof TextField)) {
                rotate();
            } else if ((e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE)
                    && !(scene.getFocusOwner() instanceof TextField)) {
                deleteSelected();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                setTool(null);
            } else if (shortcut && e.getCode() == KeyCode.Z) {
                if (e.isShiftDown()) {
                    redo();
                } else {
                    undo();
                }
            } else if (shortcut && e.getCode() == KeyCode.Y) {
                redo();
            }
        });
        panel.setScene(scene);
        loadDesign(start);
    }

    private ToolBar toolbar() {
        Button newButton = new Button("New");
        newButton.setOnAction(e -> {
            if (confirmDiscard()) {
                loadDesign(null);
            }
        });
        openMenu = new MenuButton("Open");
        openMenu.setOnShowing(e -> fillOpenMenu());
        Button save = new Button("Save");
        save.setOnAction(e -> save());
        Button undo = new Button("Undo");
        undo.setOnAction(e -> undo());
        Button redo = new Button("Redo");
        redo.setOnAction(e -> redo());
        Button turn = new Button("Turn (R)");
        turn.setOnAction(e -> rotate());
        Button delete = new Button("Delete");
        delete.setOnAction(e -> deleteSelected());
        nameField = new TextField();
        nameField.setPrefColumnCount(14);
        nameField.setPromptText("Robot name");
        Button drive = new Button("Save and drive it ▶");
        drive.setStyle("-fx-font-weight: bold;");
        drive.setOnAction(e -> {
            String name = save();
            if (name != null && onUse != null) {
                SwingUtilities.invokeLater(() -> onUse.accept(name));
            }
        });
        Region grow = new Region();
        HBox.setHgrow(grow, Priority.ALWAYS);
        return new ToolBar(newButton, openMenu, save, new Separator(), undo, redo, new Separator(), turn, delete,
                new Separator(), new Label("Name:"), nameField, grow, drive);
    }

    /** The parts menu: a picture button for every part, by category. */
    private ScrollPane catalog() {
        VBox box = new VBox(4);
        box.setPadding(new Insets(8));
        String category = null;
        FlowPane row = null;
        for (PartCatalog.PartType t : PartCatalog.all()) {
            if (!t.category.equals(category)) {
                category = t.category;
                Label heading = new Label(category);
                heading.setFont(Font.font("System", FontWeight.BOLD, 13));
                heading.setTextFill(Color.web("#6d28d9"));
                heading.setPadding(new Insets(6, 0, 0, 0));
                row = new FlowPane(4, 4);
                row.setPrefWrapLength(210);
                box.getChildren().addAll(heading, row);
            }
            ToggleButton b = new ToggleButton(shortName(t),
                    new ImageView(SwingFXUtils.toFXImage(PartIcons.draw(t, 40), null)));
            b.setContentDisplay(ContentDisplay.TOP);
            b.setPrefSize(100, 78);
            b.setWrapText(true);
            b.setStyle("-fx-font-size: 10px;");
            b.setToggleGroup(toolButtons);
            b.setTooltip(new Tooltip(t.name + "\n" + PartsListDialog.details(t)));
            b.setOnAction(e -> setTool(b.isSelected() ? t : null));
            toolButtonById.put(t.id, b);
            row.getChildren().add(b);
        }
        ScrollPane scroll = new ScrollPane(box);
        scroll.setPrefWidth(236);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private static String shortName(PartCatalog.PartType t) {
        return t.name.replace(" chassis frame", " frame").replace("Traction wheel", "Wheel")
                .replace("TETRIX-style motor", "TETRIX motor").replace(" (bumper)", "");
    }

    private ScrollPane sidePanel() {
        selectedTitle = new Label();
        selectedTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        selectedTitle.setWrapText(true);
        selectedDetails = new Label();
        selectedDetails.setWrapText(true);
        portBox = new ComboBox<String>();
        portBox.setOnAction(e -> {
            if (!updatingPort && selected != null) {
                String chosen = portBox.getValue();
                String problem = editor.setPort(selected, "(not plugged in)".equals(chosen) ? null : chosen);
                if (problem != null) {
                    say(problem, true);
                }
                refresh();
            }
        });
        rpmField = new TextField();
        rpmField.setPrefColumnCount(5);
        ticksField = new TextField();
        ticksField.setPrefColumnCount(6);
        Button apply = new Button("Set");
        apply.setOnAction(e -> applyMotorNumbers());
        javafx.scene.layout.GridPane numbers = new javafx.scene.layout.GridPane();
        numbers.setHgap(6);
        numbers.setVgap(6);
        numbers.addRow(0, new Label("RPM:"), rpmField);
        numbers.addRow(1, new Label("Ticks per turn:"), ticksField, apply);
        Label numbersNote = new Label("Type your real motor's numbers if they're different.");
        numbersNote.setWrapText(true);
        numbersNote.setStyle("-fx-font-size: 11px; -fx-text-fill: #59636e;");
        motorRow = new VBox(4, numbers, numbersNote);
        Button turn = new Button("Turn (R)");
        turn.setOnAction(e -> rotate());
        Button delete = new Button("Delete");
        delete.setOnAction(e -> deleteSelected());
        selectedBox = new VBox(8, selectedTitle, selectedDetails, new HBox(6, new Label("Port:"), portBox), motorRow,
                new HBox(6, turn, delete));
        selectedBox.setPadding(new Insets(10));
        selectedBox.setStyle("-fx-background-color: #f6f8fa; -fx-background-radius: 6;");

        Label robotTitle = new Label("Your robot");
        robotTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        summary = new Label();
        summary.setWrapText(true);
        VBox side = new VBox(12, selectedBox, robotTitle, summary);
        side.setPadding(new Insets(10));
        side.setPrefWidth(290);
        ScrollPane scroll = new ScrollPane(side);
        scroll.setFitToWidth(true);
        scroll.setPrefWidth(300);
        return scroll;
    }

    // ------------------------------------------------------------------ mouse

    private void addMouse(SubScene sub) {
        sub.setOnMousePressed(e -> {
            pressX = lastX = e.getSceneX();
            pressY = lastY = e.getSceneY();
            dragged = false;
            panel.requestFocus();
        });
        sub.setOnMouseDragged(e -> {
            if (Math.hypot(e.getSceneX() - pressX, e.getSceneY() - pressY) > 4) {
                dragged = true;
            }
            camYaw.setAngle(camYaw.getAngle() + (e.getSceneX() - lastX) * 0.4);
            camPitch.setAngle(Math.max(-85, Math.min(-5, camPitch.getAngle() - (e.getSceneY() - lastY) * 0.3)));
            lastX = e.getSceneX();
            lastY = e.getSceneY();
        });
        sub.setOnScroll(e -> camBack.setZ(Math.max(-200, Math.min(-15, camBack.getZ() * (e.getDeltaY() > 0 ? 0.9 : 1.1)))));
        sub.setOnMouseMoved(e -> hover(e.getPickResult()));
        sub.setOnMouseClicked(e -> {
            if (dragged || e.getButton() != MouseButton.PRIMARY) {
                return;
            }
            click(e.getPickResult());
        });
        sub.setOnMouseExited(e -> ghostGroup.getChildren().clear());
    }

    /** What the mouse points at: a part's id (null = the floor) and the spot in robot coordinates. */
    private static final class Aim {
        final String targetId;
        final double x, y, z;

        Aim(String targetId, double x, double y, double z) {
            this.targetId = targetId;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private Aim aim(PickResult pr) {
        Node n = pr.getIntersectedNode();
        if (n == null) {
            return null;
        }
        Point3D hit = n.localToScene(pr.getIntersectedPoint());
        String id = null;
        for (Node k = n; k != null; k = k.getParent()) {
            if (k.getUserData() instanceof String) {
                id = (String) k.getUserData();
                break;
            }
        }
        return new Aim("floor".equals(id) ? null : id, hit.getX(), hit.getZ(), -hit.getY());
    }

    private void hover(PickResult pr) {
        ghostGroup.getChildren().clear();
        if (tool == null) {
            return;
        }
        Aim a = aim(pr);
        if (a == null) {
            return;
        }
        lastAim = a;
        showProposal(editor.propose(tool, a.targetId, a.x, a.y, a.z, quarterTurns));
    }

    private void showProposal(DesignEditor.Proposal p) {
        proposal = p;
        ghostGroup.getChildren().clear();
        if (p.placement != null) {
            List<RobotDesign.Placement> withGhost = new java.util.ArrayList<RobotDesign.Placement>(editor.design().parts());
            withGhost.add(p.placement);
            RobotDesign preview = new RobotDesign(editor.design().name, withGhost);
            ghostGroup.getChildren().add(PartShapes.placed(p.placement, preview, p.ok() ? GHOST_OK : GHOST_BAD));
        }
        if (p.ok()) {
            say("Click to place the " + tool.name.toLowerCase() + ". R turns it. Esc when you're done.", false);
        } else {
            say(p.problem, true);
        }
    }

    private void click(PickResult pr) {
        if (tool != null) {
            Aim a = aim(pr);
            if (a == null) {
                return;
            }
            DesignEditor.Proposal p = editor.propose(tool, a.targetId, a.x, a.y, a.z, quarterTurns);
            place(p);
            return;
        }
        Aim a = aim(pr);
        select(a == null ? null : a.targetId);
    }

    private void place(DesignEditor.Proposal p) {
        if (!p.ok()) {
            say(p.problem, true);
            return;
        }
        editor.place(p);
        selected = p.placement.id;
        refresh();
        say("Placed " + p.placement.part.name + " (" + p.placement.id + ")"
                + (p.placement.port != null ? ", plugged into " + p.placement.port : "") + ".", false);
        // The chassis is placed once; then help pick the next part.
        if (p.placement.part.kind == PartCatalog.Kind.CHASSIS) {
            setTool(null);
            say("Now add wheels to the sides, and a motor on each side to drive them.", false);
        }
    }

    // ------------------------------------------------------------------ actions

    private void setTool(PartCatalog.PartType t) {
        tool = t;
        quarterTurns = 0;
        ghostGroup.getChildren().clear();
        for (Map.Entry<String, ToggleButton> e : toolButtonById.entrySet()) {
            e.getValue().setSelected(t != null && e.getKey().equals(t.id));
        }
        if (t == null) {
            say("Pick a part on the left, or click a part on the robot to change it. Drag to look around.", false);
        } else {
            say("Point at the robot to place the " + t.name.toLowerCase() + ".", false);
        }
    }

    private void rotate() {
        if (tool != null) {
            quarterTurns = (quarterTurns + 1) % 4;
            if (lastAim != null) {
                showProposal(editor.propose(tool, lastAim.targetId, lastAim.x, lastAim.y, lastAim.z, quarterTurns));
            }
        } else if (selected != null) {
            String problem = editor.rotate(selected);
            if (problem != null) {
                say(problem, true);
            }
            refresh();
        }
    }

    private void deleteSelected() {
        if (selected == null) {
            say("Click a part on the robot first, then Delete.", true);
            return;
        }
        String msg = editor.remove(selected);
        selected = null;
        refresh();
        say(msg + " (Undo brings it back.)", false);
    }

    private void undo() {
        if (editor.undo()) {
            refresh();
            say("Undone.", false);
        }
    }

    private void redo() {
        if (editor.redo()) {
            refresh();
            say("Redone.", false);
        }
    }

    private void select(String id) {
        selected = id;
        refresh();
    }

    private void applyMotorNumbers() {
        try {
            double rpm = Double.parseDouble(rpmField.getText().trim());
            double ticks = Double.parseDouble(ticksField.getText().trim());
            if (rpm <= 0 || ticks <= 0) {
                throw new NumberFormatException();
            }
            editor.setSetting(selected, "rpm", rpm);
            editor.setSetting(selected, "ticksPerRev", ticks);
            refresh();
            say("Motor numbers set. The robot's speed and ticks per inch come from these.", false);
        } catch (NumberFormatException ex) {
            say("RPM and ticks per turn need to be numbers bigger than 0.", true);
        }
    }

    // ------------------------------------------------------------------ files

    private File fileFor(String name) {
        return new File(robotsFolder, name + ".robot.json");
    }

    /** Saves the design. Returns its name, or null if it couldn't be saved. */
    private String save() {
        String name = nameField.getText().trim();
        if (!RobotConfig.isValidName(name)) {
            say("Give your robot a name first (letters, numbers, spaces, - or _).", true);
            nameField.requestFocus();
            return null;
        }
        if (name.equalsIgnoreCase(RobotConfig.COURSE_BOT)) {
            say("CourseBot is the course robot that every lesson uses. Save your version with a new name.", true);
            nameField.requestFocus();
            return null;
        }
        try {
            DesignModel.toModel(editor.design());
        } catch (RobotDesign.DesignException e) {
            // Saving an unfinished robot is fine; it just can't drive yet.
        }
        File f = fileFor(name);
        boolean sameAsOpened = savedDesign != null && name.equals(savedDesign.name);
        if (f.exists() && !sameAsOpened) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, name + " already exists. Replace it?",
                    ButtonType.YES, ButtonType.NO);
            a.setHeaderText(null);
            if (a.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
                return null;
            }
        }
        editor.rename(name);
        try {
            if (!robotsFolder.isDirectory()) {
                robotsFolder.mkdirs();
            }
            editor.design().save(f);
        } catch (IOException e) {
            say("Couldn't save: " + e.getMessage(), true);
            return null;
        }
        savedDesign = editor.design();
        dirty = false;
        say("Saved to robots/" + f.getName() + ". Commit and push it in GitHub Desktop to share it.", false);
        return name;
    }

    private void fillOpenMenu() {
        openMenu.getItems().clear();
        File[] files = robotsFolder.listFiles((dir, n) -> n.endsWith(".robot.json"));
        if (files == null || files.length == 0) {
            MenuItem none = new MenuItem("(no saved designs yet)");
            none.setDisable(true);
            openMenu.getItems().add(none);
            return;
        }
        Arrays.sort(files);
        for (File f : files) {
            MenuItem item = new MenuItem(f.getName().replace(".robot.json", ""));
            item.setOnAction(e -> {
                if (!confirmDiscard()) {
                    return;
                }
                try {
                    loadDesign(RobotDesign.load(f));
                } catch (RobotDesign.DesignException ex) {
                    say(ex.getMessage(), true);
                }
            });
            openMenu.getItems().add(item);
        }
    }

    private void loadDesign(RobotDesign d) {
        editor = new DesignEditor(d);
        savedDesign = d;
        selected = null;
        lastAim = null;
        refresh();
        dirty = false;
        // Students can start from CourseBot, but save it under their own name.
        boolean courseBot = d != null && d.name.equals(RobotConfig.COURSE_BOT);
        nameField.setText(d == null || courseBot ? "" : d.name);
        if (d == null) {
            setTool(PartCatalog.get("chassis-18"));
            say("A new robot. Click the floor to put down the chassis frame (or pick another frame size first).", false);
        } else {
            setTool(null);
            if (courseBot) {
                say("This is CourseBot. Change it, then save it with your own name.", false);
            }
        }
    }

    private boolean confirmDiscard() {
        if (!dirty) {
            return true;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Throw away the changes to this robot?",
                ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    // ------------------------------------------------------------------ drawing

    /** Redraws the robot, the selection, and the side panel from the editor's design. */
    private void refresh() {
        RobotDesign d = editor.design();
        dirty = savedDesign == null ? !d.parts().isEmpty() : !d.toJsonText().equals(savedDesign.toJsonText());
        partsGroup.getChildren().clear();
        for (RobotDesign.Placement p : d.parts()) {
            Group g = PartShapes.placed(p, d, null);
            g.setUserData(p.id);
            partsGroup.getChildren().add(g);
        }
        if (selected != null && d.find(selected) == null) {
            selected = null;
        }
        drawSelection();
        updateSidePanel();
        updateSummary();
    }

    private void drawSelection() {
        selectionGroup.getChildren().clear();
        if (selected == null) {
            return;
        }
        RobotDesign.Placement p = editor.design().find(selected);
        Group shape = PartShapes.placed(p, editor.design(), null);
        javafx.geometry.Bounds b = shape.getBoundsInParent();
        Box outline = new Box(b.getWidth() + 0.4, b.getHeight() + 0.4, b.getDepth() + 0.4);
        outline.setDrawMode(DrawMode.LINE);
        outline.setMaterial(new PhongMaterial(Color.web("#ffd33d")));
        outline.getTransforms().add(new Translate(b.getCenterX(), b.getCenterY(), b.getCenterZ()));
        selectionGroup.getChildren().add(outline);
    }

    private void updateSidePanel() {
        RobotDesign.Placement p = selected == null ? null : editor.design().find(selected);
        selectedBox.setVisible(p != null);
        selectedBox.setManaged(p != null);
        if (p == null) {
            return;
        }
        selectedTitle.setText(p.part.name + "  (" + p.id + ")");
        String on = p.on == null ? "" : "\nOn: " + p.on;
        selectedDetails.setText(PartsListDialog.details(p.part) + on);
        updatingPort = true;
        portBox.getItems().clear();
        if (p.part.portKind != null) {
            portBox.getItems().add("(not plugged in)");
            portBox.getItems().addAll(DesignEditor.ports(p.part.portKind));
            portBox.setValue(p.port == null ? "(not plugged in)" : p.port);
            portBox.setDisable(false);
        } else {
            portBox.setValue(null);
            portBox.setDisable(true);
        }
        updatingPort = false;
        boolean motor = p.part.kind == PartCatalog.Kind.MOTOR;
        motorRow.setVisible(motor);
        motorRow.setManaged(motor);
        if (motor) {
            rpmField.setText(trim(p.number("rpm")));
            ticksField.setText(trim(p.number("ticksPerRev")));
        }
    }

    private void updateSummary() {
        RobotDesign d = editor.design();
        StringBuilder sb = new StringBuilder();
        boolean problem = false;
        try {
            RobotModel m = DesignModel.toModel(d);
            sb.append(String.format("Ready to drive!%nTop speed: %.0f inches per second%nEncoder: %.1f ticks per inch"
                            + "%nWheels %.1f inches apart%nSize: %.0f x %.0f inches", m.maxSpeed, m.ticksPerInch(),
                    m.trackWidth, m.length, m.width));
            if (m.motorPort(0) != null || m.motorPort(1) != null || m.motorPort(2) != null || m.motorPort(3) != null) {
                sb.append("\n\nPorts:");
                for (RobotDesign.Placement p : d.parts()) {
                    if (p.port != null) {
                        sb.append("\n  ").append(p.port).append(": ").append(p.id);
                    }
                }
            }
        } catch (RobotDesign.DesignException e) {
            problem = true;
            sb.append(d.parts().isEmpty() ? "Start with a chassis frame (top of the parts menu)." : e.getMessage());
        }
        for (RobotDesign.Placement p : d.parts()) {
            if (p.part.kind == PartCatalog.Kind.MOTOR && p.port == null) {
                sb.append("\n\n⚠ ").append(p.id).append(" isn't plugged into a port.");
            }
        }
        double[] size = overallSize(d);
        if (size[0] > 18.01 || size[1] > 18.01 || size[2] > 18.01) {
            sb.append(String.format("%n%nNote: at the start it's %.1f x %.1f x %.1f inches. A real FTC robot "
                    + "has to fit in an 18-inch cube when a match starts (arms can fold up to fit).",
                    size[0], size[1], size[2]));
        }
        summary.setText(sb.toString());
        summary.setTextFill(problem ? Color.web("#cf222e") : Color.web("#1f2328"));
    }

    /** The robot's length, width, and height, with everything on it (arm resting level). */
    private static double[] overallSize(RobotDesign d) {
        double c = Math.cos(Math.toRadians(PartShapes.ARM_REST_DEG));
        double minX = 0, maxX = 0, minY = 0, maxY = 0, maxZ = 0;
        boolean any = false;
        for (RobotDesign.Placement p : d.parts()) {
            double[] b;
            RobotDesign.Placement on = p.on == null ? null : d.find(p.on);
            if (p.part.kind == PartCatalog.Kind.ARM) {
                b = new double[]{p.x, -0.6, p.z - 0.5, p.x + p.part.length * c, 0.6, p.z + 0.5};
            } else if (on != null && on.part.kind == PartCatalog.Kind.ARM) {
                double x = on.x + (Double.isNaN(p.along) ? on.part.length : p.along) * c;
                b = new double[]{x, -p.part.width / 2, on.z - 1.2, x + p.part.length * c, p.part.width / 2, on.z + 1.2};
            } else {
                b = DesignEditor.bounds(p);
            }
            if (!any) {
                minX = b[0];
                minY = b[1];
                maxX = b[3];
                maxY = b[4];
                maxZ = b[5];
                any = true;
            }
            minX = Math.min(minX, b[0]);
            minY = Math.min(minY, b[1]);
            maxX = Math.max(maxX, b[3]);
            maxY = Math.max(maxY, b[4]);
            maxZ = Math.max(maxZ, b[5]);
        }
        return new double[]{maxX - minX, maxY - minY, maxZ};
    }

    private void say(String text, boolean problem) {
        status.setText(text);
        status.setTextFill(problem ? Color.web("#cf222e") : Color.web("#1f2328"));
    }

    private static String trim(double d) {
        return d == Math.rint(d) ? String.valueOf((long) d) : String.valueOf(d);
    }

    // ------------------------------------------------------------------ for tests

    static RobotBuilder current() {
        return open;
    }

    /** Runs on the JavaFX thread and waits. */
    void onFx(Runnable r) {
        CountDownLatch done = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                r.run();
            } finally {
                done.countDown();
            }
        });
        try {
            done.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Does what a click does with this part picked, pointing at this spot. Returns the problem, or null. */
    String testClick(String partId, String targetId, double x, double y, double z) {
        String[] out = new String[1];
        onFx(() -> {
            setTool(PartCatalog.get(partId));
            DesignEditor.Proposal p = editor.propose(tool, targetId, x, y, z, quarterTurns);
            out[0] = p.problem;
            place(p);
        });
        return out[0];
    }

    /** Shows the ghost for this part pointing at this spot, like moving the mouse there. */
    void testHover(String partId, String targetId, double x, double y, double z) {
        onFx(() -> {
            setTool(PartCatalog.get(partId));
            showProposal(editor.propose(tool, targetId, x, y, z, quarterTurns));
        });
    }

    void testNew() {
        onFx(() -> loadDesign(null));
    }

    void testSelect(String id) {
        onFx(() -> {
            setTool(null);
            select(id);
        });
    }

    String testSave(String name) {
        String[] out = new String[1];
        onFx(() -> {
            nameField.setText(name);
            out[0] = save();
        });
        return out[0];
    }

    String testStatus() {
        String[] out = new String[1];
        onFx(() -> out[0] = status.getText());
        return out[0];
    }

    String testSummary() {
        String[] out = new String[1];
        onFx(() -> out[0] = summary.getText());
        return out[0];
    }

    RobotDesign testDesign() {
        RobotDesign[] out = new RobotDesign[1];
        onFx(() -> out[0] = editor.design());
        return out[0];
    }

    BufferedImage testCapture() {
        BufferedImage[] out = new BufferedImage[1];
        onFx(() -> {
            WritableImage img = panel.getScene().getRoot().snapshot(null, null);
            out[0] = SwingFXUtils.fromFXImage(img, null);
        });
        return out[0];
    }

    void testClose() {
        dirty = false;
        SwingUtilities.invokeLater(() -> frame.dispose());
    }
}
