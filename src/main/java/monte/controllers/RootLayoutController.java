package monte.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import monte.Main;
import monte.carlo.MonteCarlo;
import monte.linear_programming.Constraint;
import monte.linear_programming.StandardForm;
import monte.viewer.Chart2D;
import monte.viewer.Viewer;

import java.io.File;
import java.io.IOException;


public class RootLayoutController {

    public FlowPane overlay;
    public Label objectiveFunctionLabel;

    public void handleOpen(ActionEvent actionEvent) {
        FileChooser fileChooser = getFileChooser();
        fileChooser.setTitle("Open Standard Form");
        setExtension(fileChooser,"JSON files (*.json)", "*.json");
        File file = fileChooser.showOpenDialog(Main.primaryStage);
        if( file != null ){
            Main.currentFile = file;
            StandardForm.loadFromJson();
        }
    }

    public void handleSaveAs(ActionEvent actionEvent) {
        FileChooser fileChooser = getFileChooser();
        setExtension(fileChooser,"JSON files (*.json)", "*.json");
        fileChooser.setTitle("Save Standard Form");

        if( Main.currentFile != null ){
            fileChooser.setInitialFileName(Main.currentFile.getName());
        }else{
            fileChooser.setInitialFileName("StandardForm.json");
        }
        File file = fileChooser.showSaveDialog(Main.primaryStage);
        if( file != null ){
            Main.currentFile = file;
            StandardForm.saveToJson();
        }
    }

    private static void setExtension( FileChooser fileChooser, String description, String ext ){
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter(description, ext);
        fileChooser.getExtensionFilters().add(extensionFilter);
    }

    private static FileChooser getFileChooser() {
        return new FileChooser();
    }

    public static void init() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/fxml/root_layout.fxml"));

        try {
            Main.rootLayout = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Main.rootScene = new Scene(Main.rootLayout);
        Main.primaryStage.setScene(Main.rootScene);
        Main.rootLayoutController = loader.getController();
        Main.primaryStage.show();
    }

    public static Stage getDialogStage(String title, AnchorPane dialogPane){
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(Main.primaryStage);
        Scene dialogScene = new Scene(dialogPane);
        dialogStage.setScene(dialogScene);

        return dialogStage;
    }

    public static boolean showErrorAlert(String errorMessage, Stage dialogStage) {
        if( errorMessage.equals("")){
            return true;
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }

    public void handleAddConstraint(ActionEvent actionEvent) {
        Constraint newConstraint = showAddConstraintDialog();
        if( newConstraint != null ){
            Main.standardForm.constraints.add(newConstraint);
            Viewer.show();
        }
    }

    public void handleNewStandardForm(ActionEvent actionEvent) {
        StandardForm standardForm = showNewStandardFormDialog();
        if( standardForm != null ){
            Main.standardForm = standardForm;
            Viewer.show();
        }
    }

    private Constraint showAddConstraintDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/fxml/dialog_add_constraint.fxml"));
            AnchorPane dialogPane = loader.load();

            Stage dialogStage = getDialogStage("Add Constraint", dialogPane);

            AddConstraintDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            if(controller.isOkClicked()){
                return controller.getConstraint();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private StandardForm showNewStandardFormDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/fxml/dialog_standard_form.fxml"));
            AnchorPane dialogPane = loader.load();

            Stage dialogStage = getDialogStage("New Standard Form", dialogPane);

            StandardFormDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            if(controller.isOkClicked()){
                return controller.getStandardForm();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void handleGetMin(ActionEvent actionEvent) {
        runMonteCarlo(false);
    }

    public void handleGetMax(ActionEvent actionEvent) {
        runMonteCarlo(true);
    }

    private void runMonteCarlo(boolean searchForMax) {
        if (Main.standardForm != null) {
            Chart2D chart = null;
            if (Main.standardForm.decisionVariables.size() == 2) {
                chart = new Chart2D();
                chart.display();
            }
            Chart2D finalChart = chart;
            new Thread(() -> {
                System.out.println(MonteCarlo.get(Main.standardForm, MonteCarlo.INIT_SIZE, searchForMax, finalChart));
            }).start();
        }
    }
}
