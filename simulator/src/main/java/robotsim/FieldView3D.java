package robotsim;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * The 3D view of the field. It lives in the simulator's "fx" part (src/fx), which needs Java 17
 * and JavaFX, so this Java 8 part only knows it through this interface. The 2D view keeps
 * working when the 3D part isn't there.
 */
interface FieldView3D {

    /** The Swing component that shows the 3D view. */
    JComponent component();

    /** Draws one frame: the same snapshot the 2D view gets. */
    void show(World.Snapshot snapshot, boolean trail, String hint);

    /** A picture of the current view (for tests and the docs). Waits for the frame to finish. */
    BufferedImage capture();

    /** Stops drawing before the program quits, so closing the window doesn't print errors. */
    void close();

    /** Builds the 3D view, or explains why it can't. */
    final class Factory {
        private static String problem;

        private Factory() {
        }

        static FieldView3D create() {
            try {
                Class<?> type = Class.forName("robotsim.Field3D");
                return (FieldView3D) type.getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                problem = "The 3D part of the simulator isn't installed. Start the simulator from "
                        + "Android Studio with the \"Robot Simulator\" run configuration.";
            } catch (Throwable e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                problem = "The 3D view couldn't start on this computer: " + cause;
            }
            return null;
        }

        static String problem() {
            return problem;
        }
    }
}
