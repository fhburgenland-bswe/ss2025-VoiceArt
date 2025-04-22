package at.fh.burgenland.coordinatesystemtest;

import at.fh.burgenland.coordinatesystem.CoordinateSystemDrawer;
import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CoordinateSystemDrawer class.
 * This test ensures that the drawAxes() method executes without throwing exceptions
 * and that the canvas is correctly initialized for drawing. 
 */
class CoordinateSystemDrawerTest {

    /**
     * Verifies that drawAxes() runs without any exceptions and that
     * the Canvas object has a valid GraphicsContext. 
     */
    @Test
    void testDrawAxesRunsWithoutError() {
        // creates Canvas with fixed dimensions
        Canvas canvas = new Canvas(800, 600);

        // drawing should not throw any exceptions
        assertDoesNotThrow(() ->
            CoordinateSystemDrawer.drawAxes(canvas, 50, 2000, -60, 0),
            "drawAxes() should run without throwing an exception"
        );

        // additional check: the canvas must be valid for drawing
        assertNotNull(canvas.getGraphicsContext2D(), "Canvas must have a GraphicsContext");
    }
}
