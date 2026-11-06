package robotsim;

import java.awt.Window;
import java.io.File;
import java.util.function.Consumer;

/**
 * Opens the robot builder. The builder lives in the simulator's "fx" part (JavaFX 3D), so this
 * Java 8 part only knows it through this interface, like the 3D view.
 */
interface RobotBuilderLauncher {

    /**
     * Opens the builder with a design to start from (null = a new, empty robot). Designs are saved
     * in robotsFolder. onUse gets a design's name when the student presses "Drive it".
     */
    void open(Window owner, File robotsFolder, RobotDesign start, Consumer<String> onUse);

    /** Finds the builder, or explains why it isn't available. */
    final class Factory {
        private static String problem;

        private Factory() {
        }

        static RobotBuilderLauncher create() {
            try {
                return (RobotBuilderLauncher) Class.forName("robotsim.RobotBuilder").getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                problem = "The robot builder needs the 3D part of the simulator. Start the simulator from "
                        + "Android Studio with the \"Robot Simulator\" run configuration.";
            } catch (Throwable e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                problem = "The robot builder couldn't start on this computer: " + cause;
            }
            return null;
        }

        static String problem() {
            return problem;
        }
    }
}
