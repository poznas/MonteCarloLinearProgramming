package monte.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import monte.Main;
import monte.linear_programming.Constraint;
import monte.parse.Detector;

import java.util.List;

public class AddConstraintDialog {
    public VBox mainBox;
    public Label variablesLabel;
    public TextField leftSideField;
    public TextField rightSideField;
    public RadioButton geqRadio;
    public RadioButton leqRadio;
    public Button okButton;
    public AnchorPane dialogRoot;

    private boolean okClicked = false;
    private Stage dialogStage;
    private Constraint constraint;

    public void handleCancel(ActionEvent actionEvent) {
        dialogStage.close();
    }

    public void handleOK(ActionEvent actionEvent) {
        handleOK();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        dialogRoot.setOnKeyPressed((event) -> {
            if(event.getCode() == KeyCode.ENTER) {
                handleOK();
            }
        });
        variablesLabel.setText(Main.standardForm.decisionVariables.toString());
        geqRadio.selectedProperty().addListener((observable, wasOn, isOn) -> {
            if( isOn ){
                leqRadio.setSelected(false);
            }
        });
        leqRadio.selectedProperty().addListener((observable, wasOn, isOn) -> {
            if( isOn ){
                geqRadio.setSelected(false);
            }
        });
    }

    private void handleOK() {
        if( isInputValid() ){
            okClicked = true;
            constraint = new Constraint(
                    leftSideField.getText(),
                    rightSideField.getText(),
                    geqRadio.isSelected()
            );
            dialogStage.close();
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        String right = rightSideField.getText();
        String left = leftSideField.getText();

        List<String> leftSideVariables = new Detector().variables(left);
        List<String> rightSideVariables = new Detector().variables(right);

        if(leftSideVariables.size() < 1){
            errorMessage += "Left side of constraint should contain" +
                    " at least one decision variable!\n";
        }
        errorMessage += areValidVariables(leftSideVariables);
        if( left == null || left.length() == 0 ){
            errorMessage += "Invalid right side!\n";
        }
        errorMessage += areValidVariables(rightSideVariables);

        return RootLayoutController.showErrorAlert(errorMessage,dialogStage);
    }

    private static String areValidVariables(List<String> leftSideVariables) {
        String errorMessage = "";
        for( String variable : leftSideVariables ){
            if( !Main.standardForm.decisionVariables.contains(variable)){
                errorMessage += "Decision variable: [" + variable + "]" +
                        " does not exist in object function!\n";
            }
        }
        return errorMessage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public Constraint getConstraint() {
        return constraint;
    }
}
