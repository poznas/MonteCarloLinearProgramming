package monte.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import monte.linear_programming.StandardForm;
import monte.parse.Detector;

import java.util.List;


public class StandardFormDialog {
    public AnchorPane dialogRoot;
    public VBox mainBox;
    public TextField objectiveFunctionField;
    public Button okButton;
    public CheckBox nonnegativeConstraints;
    private StandardForm standardForm;
    private boolean okClicked = false;
    private Stage dialogStage;



    public void handleCancel(ActionEvent actionEvent) {
        dialogStage.close();
    }

    public void handleOK(ActionEvent actionEvent) {
        handleOK();
    }

    private void handleOK() {
        if( isInputValid() ){
            okClicked = true;
            dialogStage.close();
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        String expression = objectiveFunctionField.getText();
        List<String> variables = new Detector().variables(expression);
        if (expression == null || variables.size() <= 1) {
            errorMessage += "Objective Function should have two or more decision variables!\n";
        }else{
            standardForm.objectiveFunction = expression;
            standardForm.decisionVariables = variables;
            if( nonnegativeConstraints.isSelected() ){
                standardForm.addNonnegativeVarConstraints();
            }
        }
        return RootLayoutController.showErrorAlert(errorMessage,dialogStage);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        standardForm = new StandardForm();
        dialogRoot.setOnKeyPressed((event) -> {
            if(event.getCode() == KeyCode.ENTER) {
                handleOK();
            }
        });
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public StandardForm getStandardForm() {
        return standardForm;
    }

    public void setStandardForm(StandardForm standardForm) {
        this.standardForm = standardForm;
    }
}
