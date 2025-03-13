package com.example.dashboard;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ViewInvoiceController {
    @FXML
    private Button backToDashboardButton;

    public void backToDashboard(ActionEvent event) throws IOException {
        Stage closingStage = (Stage) backToDashboardButton.getScene().getWindow();
        closingStage.close();
    }
}
