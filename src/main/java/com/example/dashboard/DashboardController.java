package com.example.dashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML private PieChart totalCostsChart;
    @FXML private Button addInvoiceButton;
    @FXML private Label costLabel;

    @FXML
    private void loadTotalCostsChart() {

        ObservableList<PieChart.Data> pieData =
                FXCollections.observableArrayList(
                new PieChart.Data("Courses", 535000),
                new PieChart.Data("Food", 35000),
                new PieChart.Data("Sports", 115000)
        );

        totalCostsChart.setData(pieData);
        totalCostsChart.setTitle("Total Costs in 2022 for Business");
        totalCostsChart.setClockwise(false);
        // totalCostsChart.setLegendSide(Side.TOP);

        costLabel.setTextFill(Color.BLACK);
        costLabel.setStyle("-fx-font: 20 Arial;");
        // costLabel.setText("£" + String.valueOf(totalCostsChart.getData().getFirst().getPieValue()));

        for (final PieChart.Data data : totalCostsChart.getData()) {
            data.getNode().addEventHandler(
                    MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent ev) {
                            // System.out.println("PieChart clicked");
                            costLabel.setTranslateX(ev.getSceneX() - costLabel.getLayoutX());
                            costLabel.setTranslateY(ev.getSceneY() - costLabel.getLayoutY());
                            costLabel.setText("£" + String.valueOf(data.getPieValue()));
                        }
                    });
        }
    }

    private void generateInvoice() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invoice");
        alert.setHeaderText(null);
        alert.setContentText("Add Invoice functionality will be implemented here.");
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTotalCostsChart();

        // Set event listeners
        addInvoiceButton.setOnAction(event -> generateInvoice());
    }
}
