package com.example.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GenerateNewInvoiceController implements Initializable {
    @FXML
    private Button allInvoiceButton;

    public void seeAllInvoices(ActionEvent event) throws IOException {
        Stage closingStage = (Stage) allInvoiceButton.getScene().getWindow();
        closingStage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
