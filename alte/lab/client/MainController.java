package alte.lab.client;

import alte.lab.Human;
import alte.lab.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MainController {
    @FXML private Canvas main_canvas;
    @FXML private TextArea logs;
    private double position_x = 1125, position_y = 1275, position_z = 0;

    @FXML private TextField nick, x, y, age;

    private Callable updateList;
    private List<Human> humans;

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

    final private double man_size = 20.0;
    void redraw() {
        double side = 3000 * getSize();
        gc.drawImage(map, position_x, position_y, side, side, 0, 0, 3000, 3000);

        double new_man_size = Math.max(man_size, man_size/getSize());
        for(Human human : humans) {
            if(human.getAge() < 10) gc.setFill(Color.RED);
            else if(human.getAge() < 20) gc.setFill(Color.GREEN);
            else if(human.getAge() < 30) gc.setFill(Color.PURPLE);
            else if(human.getAge() < 40) gc.setFill(Color.CYAN);
            else if(human.getAge() < 50) gc.setFill(Color.YELLOW);
            else if(human.getAge() < 60) gc.setFill(Color.WHITE);
            else gc.setFill(Color.BLACK);
            gc.fillRect(-position_x/getSize()+(1500 + human.getPosX())/getSize(), -position_y/getSize()+(1500 + human.getPosY())/getSize(), new_man_size, new_man_size);
        }
    }

    @FXML protected void addMan(ActionEvent event) {
        humans.add(new Human("Ыыы", Integer.parseInt(age.getText()), Integer.parseInt(x.getText()), Integer.parseInt(y.getText())));
        redraw();
    }

    void drawCanvas(Callable onUpdate) {
        updateList = onUpdate;
        humans = new ArrayList<>();
        humans.add(new Human("Ыыы", 20, -200, 750));
        humans.add(new Human("Ыыы", 11, 790, -200));
        humans.add(new Human("Ыыы", 30, 900, 100));
        humans.add(new Human("Ыыы", 40, 200, 200));
        humans.add(new Human("Ыыы", 50, -500, -500));
        humans.add(new Human("Ыыы", 0, -1000, -1000));
        humans.add(new Human("Ыыы", 5, 1000,1000));
        humans.add(new Human("Ыыы", 55, 500, -300));
        humans.add(new Human("Ыыы", 70, 10, 10));

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

    @FXML protected void keyPressed(KeyEvent event) {
        if(event.getCode() == KeyCode.TAB) {
            try {
                updateList.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setPlayers(List<Human> humans) {
        this.humans = humans;
        redraw();
    }

    void addLogs(String log_string) {
        logs.setText(log_string+"\r\n"+logs.getText());
    }

}