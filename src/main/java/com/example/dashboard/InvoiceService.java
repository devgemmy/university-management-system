package com.example.dashboard;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class InvoiceService {
    private static InvoiceService instance;
    private final DatabaseModel databaseModel;

    private InvoiceService() {
        this.databaseModel = DatabaseModel.getInstance();
    }

    public static InvoiceService getInstance() {
        if (instance == null) {
            instance = new InvoiceService();
        }
        return instance;
    }

    /**
     * Shows a confirmation dialog and deletes the invoice if confirmed.
     * 
     * @param invoice The invoice to delete
     * @return true if the invoice was deleted successfully, false otherwise
     */
    public boolean deleteInvoiceWithConfirmation(Invoice invoice) {
        if (invoice == null)
            return false;

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Invoice");
        confirmDialog.setHeaderText("Delete Invoice");
        confirmDialog.setContentText("Are you sure you want to delete this invoice? This action cannot be undone.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = databaseModel.deleteInvoice(invoice.getInvoiceID());
            if (deleted) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Invoice was successfully deleted.");
                successAlert.showAndWait();
            }
            return deleted;
        }
        return false;
    }
}
