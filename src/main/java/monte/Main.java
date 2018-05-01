package monte;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import monte.controllers.RootLayoutController;
import monte.linear_programming.StandardForm;

import java.io.File;


public class Main extends Application {

    public static Stage primaryStage;
    public static Scene rootScene;
    public static BorderPane rootLayout;

    public static RootLayoutController rootLayoutController;

    public static File currentFile;
    public static StandardForm standardForm;

    @Override
    public void init() throws Exception {
        super.init();
    }

    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        Main.primaryStage.setTitle("Monte");

        Main.primaryStage.getIcons().add( new Image(
                String.valueOf(getClass().getResource("/images/monte.png"))
        ));
        RootLayoutController.init();
    }
}
