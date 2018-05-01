package monte.viewer;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import monte.Main;
import monte.controllers.ChartDialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static monte.controllers.RootLayoutController.getDialogStage;

public class Chart2D {

    ChartDialog controller;

    public void display() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/fxml/dialog_chart.fxml"));
            AnchorPane dialogPane = loader.load();

            Stage dialogStage = getDialogStage("Chart2D", dialogPane);

            controller = loader.getController();

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(List<HashMap<String,Double>> resultPoints) {
        controller.addAndDraw(resultPoints);
    }

    public void addCurrentPoint(HashMap<String,Double> currentPoint) {
        controller.addCurrent(currentPoint);
    }
}
