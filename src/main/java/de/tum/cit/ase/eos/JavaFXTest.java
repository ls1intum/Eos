package de.tum.cit.ase.eos;

import static de.tum.cit.ase.eos.HelperClass.createInstance;
import static de.tum.cit.ase.eos.HelperClass.invokeMethod;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.service.query.NodeQuery;

/**
 * Base class for JavaFX tests.
 * <p>
 * This class provides a common setup for JavaFX tests. It starts the JavaFX application and provides utility methods for
 * testing.
 */
@TestFXAnnotations
public abstract class JavaFXTest extends ApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(JavaFXTest.class);

    /**
     * Returns the name of the JavaFX application class. This method should return the fully qualified class name of
     * the JavaFX application class under test.
     *
     * @return The name of the JavaFX application class.
     */
    public abstract String getAppClassName();

    /**
     * Starts the JavaFX application.
     *
     * @param stage The primary stage of the JavaFX application.
     */
    @Override
    public final void start(Stage stage) {
        Object app = createInstance(getAppClassName(), null);
        invokeMethod(app, "start", false, new Class<?>[] { Stage.class }, stage);
    }

    /**
     * Captures a screenshot of the current window and saves it to a file in the <code>screenshots</code> directory.
     *
     * @param fileName The name of the file to save the screenshot to.
     */
    protected void captureAndSaveScreenshot(String fileName) {
        Scene scene = robotContext().getWindowFinder().listWindows().get(0).getScene();
        Bounds bounds = scene.getRoot().localToScreen(scene.getRoot().getBoundsInLocal());
        Rectangle2D region = new Rectangle2D(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
        Image screenshot = robotContext().getCaptureSupport().captureRegion(region);

        saveScreenshot(fileName, screenshot);
    }

    private void saveScreenshot(String fileName, Image screenshot) {
        final String screenshotPath = "screenshots";
        final String fileExtension = "png";

        File screenshotFile = new File(screenshotPath + "/" + normalizeFileName(fileName, fileExtension));
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(screenshot, null), fileExtension, screenshotFile);
        }
        catch (IOException e) {
            LOG.error("Failed to save screenshot", e);
        }
    }

    private String normalizeFileName(String fileName, String fileExtension) {
        fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        return fileName.endsWith("." + fileExtension) ? fileName : fileName + "." + fileExtension;
    }

    /**
     * Checks if the given nodes have a common VBox as a parent. This provides a simple way to check if the nodes have
     * a somewhat consistent layout.
     * If no common VBox is found, the test fails.
     *
     * @param nodes The nodes to check.
     */
    protected void checkForCommonVBox(Node... nodes) {
        HashMap<VBox, Integer> vBoxCount = new HashMap<>();

        for (Node node : nodes) {
            do {
                if (node instanceof VBox) {
                    vBoxCount.put((VBox) node, vBoxCount.getOrDefault(node, 0) + 1);
                }
                node = node.getParent();
            }
            while (node != null);
        }

        if (!vBoxCount.containsValue(nodes.length)) {
            fail("No common VBox found");
        }
    }

    /**
     * Returns all nodes of the given type that match the given query.
     *
     * @param type  The type of the nodes to return.
     * @param query The query to match.
     * @return A set of nodes of the given type that match the query.
     * @param <T> The type of the nodes to return.
     * @see NodeQuery#lookup(String)
     */
    protected <T extends Node> Set<T> getNodesOfType(Class<T> type, String query) {
        return lookup(query).queryAllAs(type);
    }

    /**
     * Returns the node of the given type that matches the given query. If no or multiple nodes are found, the test fails.
     *
     * @param type  The type of the node to return.
     * @param query The query to match.
     * @return The node of the given type that matches the query.
     * @param <T> The type of the node to return.
     * @see NodeQuery#lookup(String)
     * @see #getNodesOfType(Class, String)
     */
    protected <T extends Node> T getNodeOfType(Class<T> type, String query) {
        var nodeSet = getNodesOfType(type, query);
        if (nodeSet.isEmpty()) {
            fail("Node of type " + type.getTypeName() + " not found.");
        }
        else if (nodeSet.size() > 1) {
            fail("Multiple nodes of type " + type.getTypeName() + " found.");
        }
        return nodeSet.iterator().next();
    }
}
