package alte.lab.client;

import alte.lab.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.List;

public class MainController {
    @FXML private Canvas main_canvas;
    @FXML private TextArea logs;
    private double position_x = 1125, position_y = 1275, position_z = 0;

    private List<User> users;

    @FXML
    protected void ResetParams(ActionEvent event) {
        position_z = 0;

        double new_center_x = 375 * getSize();
        double new_center_y = 225 * getSize();

        position_x = 1500-new_center_x;
        position_y = 1500-new_center_y;
        redraw();
    }

    private GraphicsContext gc;
    private Image map;

    final private double min_zoom = 0.2, max_zoom = 5, zoom_value = 620.0;

    private double getSize() {
        return (min_zoom + (position_z + zoom_value) / (zoom_value * 2) * (max_zoom - min_zoom));
    }

    void redraw() {
        double side = 3000 * getSize();
        gc.drawImage(map, position_x, position_y, side, side, 0, 0, 3000, 3000);
    }

    void drawCanvas() {
        gc = main_canvas.getGraphicsContext2D();
        map = new Image("/map.png");
        ResetParams(null);

        main_canvas.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            double dX, dY;

            @Override
            public void handle(MouseEvent event) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    dX = event.getX();
                    dY = event.getY();
                } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {

                    position_x = (-event.getX() + dX) * getSize() + position_x;
                    position_y = (-event.getY() + dY) * getSize() + position_y;

                    redraw();

                    dX = event.getX();
                    dY = event.getY();
                }
            }
        });
        main_canvas.addEventFilter(ScrollEvent.ANY, event -> {
            if (event.getEventType() == ScrollEvent.SCROLL) {

                double current_center_x = position_x + 375 * getSize();
                double current_center_y = position_y + 225 * getSize();

                position_z += event.getDeltaY() / 2;
                position_z = Math.max(-zoom_value, Math.min(zoom_value, position_z));

                double new_center_x = position_x + 375 * getSize();
                double new_center_y = position_y + 225 * getSize();

                position_x += current_center_x-new_center_x;
                position_y += current_center_y-new_center_y;

                redraw();
            }
        });
    }

    /* --------------------------- */

    void setPlayers() {

    }

    void addLogs(String log_string) {
        logs.setText(log_string+"\r\n"+logs.getText());
    }

}