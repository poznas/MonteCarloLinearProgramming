package monte.controllers;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DialogChart {

    private static final double height = 700;
    public Pane chartPane;
    private List<HashMap<String,Double>> points = new ArrayList<>();

    public void addAndDraw(List<HashMap<String,Double>> resultPoints) {
        int size = 1;
        Color color = Color.BLACK;
        if( chartPane.getChildren().size() == 0 ){
            size = 2;
            color = Color.LIGHTGREEN;
        }
        points.addAll(resultPoints);
        int finalSize = size;
        Color finalColor = color;
        resultPoints.parallelStream().map(point -> {
            Circle circle = new Circle();
            List<String> vars = new ArrayList<>(point.keySet());
            circle.setCenterX(point.get(vars.get(0)));
            circle.setCenterY(700 - point.get(vars.get(1)));
            circle.setRadius(finalSize);
            circle.setFill(finalColor);
            return circle;
        }).forEach(point ->{
            Platform.runLater(() ->{
                chartPane.getChildren().add(point);
            });
        });
        //drawLines();
    }

    public void addCurrent(HashMap<String,Double> currentPoint) {
        Circle circle = new Circle();
        List<String> vars = new ArrayList<>(currentPoint.keySet());
        circle.setCenterX(currentPoint.get(vars.get(0)));
        circle.setCenterY(height - currentPoint.get(vars.get(1)));
        circle.setRadius(3);
        circle.setFill(javafx.scene.paint.Color.RED);
        Platform.runLater(() ->{
            chartPane.getChildren().add(circle);
        });
    }

    private void drawLines() {
        Line line1 = new Line(0,height-500,800,height-0);
        Line line2 = new Line(0,height-900,450,height-0);
        Platform.runLater(()->{
            chartPane.getChildren().addAll(line1,line2);
        });
    }

    public void addBluePoint(HashMap<String,Double> point) {
        Circle circle = new Circle();
        List<String> vars = new ArrayList<>(point.keySet());
        circle.setCenterX(point.get(vars.get(0)));
        circle.setCenterY(height - point.get(vars.get(1)));
        circle.setRadius(1);
        circle.setFill(Color.BLUE);
        Platform.runLater(() ->{
            chartPane.getChildren().add(circle);
        });
    }
}
