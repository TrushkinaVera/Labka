package alte.lab.client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class MainController {
    @FXML
    private Canvas main_canvas;
    private double oldx = 0, oldy = 0, oldz = 0;

    @FXML
    protected void ResetParams(ActionEvent event) {
        oldx = 0;
        oldy = 0;
        oldz = 0;
        redraw();
    }

    private GraphicsContext gc;
    private Image map;

    final private double min_zoom = 0.2, max_zoom = 5, zoom_value = 620.0;

    private double getSize() {
        return (min_zoom + (oldz + zoom_value) / (zoom_value * 2) * (max_zoom - min_zoom));
    }

    void redraw() {
        double side = 3000 * getSize();
        gc.drawImage(map, oldx, oldy, side, side, 0, 0, 3000, 3000);
    }

    void drawCanvas() {
        gc = main_canvas.getGraphicsContext2D();
        map = new Image("/map.png");

        redraw();

        main_canvas.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            double dX, dY;

            @Override
            public void handle(MouseEvent event) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    dX = event.getX();
                    dY = event.getY();
                } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {

                    oldx = (-event.getX() + dX) * getSize() + oldx;
                    oldy = (-event.getY() + dY) * getSize() + oldy;

                    redraw();

                    dX = event.getX();
                    dY = event.getY();
                }
            }
        });
        main_canvas.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getEventType() == ScrollEvent.SCROLL) {
                    oldz += event.getDeltaY() / 2;
                    oldz = Math.max(-zoom_value, Math.min(zoom_value, oldz));
                    redraw();
                }
            }
        });
    }

    /* --------------------------- */

    void setPlayers() {
    }

}