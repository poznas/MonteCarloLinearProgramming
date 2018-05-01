package monte.viewer;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import monte.Main;
import monte.linear_programming.Constraint;
import monte.linear_programming.StandardForm;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.util.ArrayList;
import java.util.List;

public class Viewer {

    static Label objectiveFunctionLabel;
    static FlowPane flowPane;
    static Color mainColor = Color.grayRgb(0, 0.702);
    private static final Background unfocusedViewerBackground
            = new Background( new BackgroundFill( Color.rgb(255, 255, 255), null, null ) );
    private static final Background focusedViewerBackground
            = new Background( new BackgroundFill( Color.rgb(157, 178, 255), null, null ) );

    private static final double textSize = 2.0;


    public static void show() {
        if( flowPane == null ){
            flowPane = Main.rootLayoutController.overlay;
            objectiveFunctionLabel = Main.rootLayoutController.objectiveFunctionLabel;
        }
        flowPane.getChildren().clear();
        flowPane.setPadding(new Insets(10,10,10,10));
        //flowPane.setOrientation(Orientation.VERTICAL);

        flowPane.getChildren().addAll(getNodes(Main.standardForm));
    }

    private static List<Node> getNodes(StandardForm standardForm) {
        objectiveFunctionLabel.setText(standardForm.objectiveFunction);
        objectiveFunctionLabel.setStyle("-fx-font-size:40;");
        List<Node> nodes = new ArrayList<>();
        for(Constraint constraint : standardForm.constraints){
            nodes.add(getConstraintBox(constraint));
        }
        return nodes;
    }

    private static HBox getConstraintBox(Constraint constraint){
        Canvas canvas = getLatexCanvas(constraint.toString() + "  ",textSize);

        HBox box = new HBox(canvas,getDeleteImageView(constraint));
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(5,5,5,5));

        box.backgroundProperty().bind(Bindings
                .when( box.focusedProperty() )
                .then( focusedViewerBackground )
                .otherwise(unfocusedViewerBackground)
        );
        return box;
    }

    private static ImageView getDeleteImageView(Constraint constraint) {
        ImageView deleteImage = new ImageView(new Image(String.valueOf(
                Main.class.getResource("/images/image_delete.png"))));
        deleteImage.setSmooth(true);
        deleteImage.setPreserveRatio(true);
        deleteImage.setFitWidth(30);
        deleteImage.setFitHeight(30);

        deleteImage.setOnMouseClicked(event -> {
            if( event.getButton() == MouseButton.PRIMARY ){
                Main.standardForm.constraints.remove(constraint);
                Viewer.show();
            }
        });
        return deleteImage;
    }


    private static Canvas getLatexCanvas(String latex, double size ){
        TeXFormula formula = new TeXFormula(latex);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);
        Canvas latexCanvas = new Canvas(icon.getIconWidth(),icon.getIconHeight());
        icon.paintInCanvas(latexCanvas,0,0, mainColor, Color.TRANSPARENT);
        return latexCanvas;
    }
}
