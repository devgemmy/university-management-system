package com.example.demo1;

import com.example.course.CourseAssigner;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardController implements Initializable {

    @FXML
    private TableView<ApplicantDetails> table;

    @FXML
    private TableColumn<ApplicantDetails, String> grade;

    @FXML
    private TableColumn<ApplicantDetails, String> uniqueIDColumn;

    @FXML
    private TableColumn<ApplicantDetails, String> dobOfApplicant;

    @FXML
    private TableColumn<ApplicantDetails, String> nameofApplicant;

    @FXML
    private TableColumn<ApplicantDetails, String> certificate;

    @FXML
    private TableColumn<ApplicantDetails, String> dateOfApplication;

    @FXML
    private TableColumn<ApplicantDetails, String> status;

    @FXML
    private TableColumn<ApplicantDetails, Void> actions;

    @FXML
    private TextField searchField;

    @FXML
    private Button addApplicantButton;

    private ApplicantManager applicantManager = new ApplicantManager();
    public ApplicantManager getApplicantManager() {
        return applicantManager;
    }

    public TableView<ApplicantDetails> getTable() {
        return this.table;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set buttons
        addApplicantButton.setOnAction(event -> openApplicantForm(null));

        //Process Applicants from original APPLICANTS.csv file
        applicantManager.processApplicants();
        //Display invalid applicants from original file
        applicantManager.displayApplicants();
        //Loads valid applicants from VALID_APPLICANTS.csv file
        applicantManager.loadValidApplicants();



        // Bind TableView directly
        table.setItems(applicantManager.applicantList);

        //Set table columns
        uniqueIDColumn.setCellValueFactory(new PropertyValueFactory<ApplicantDetails, String>("uniqueID"));
        nameofApplicant.setCellValueFactory(new PropertyValueFactory<ApplicantDetails, String>("fullName"));
        dobOfApplicant.setCellValueFactory(new PropertyValueFactory<ApplicantDetails, String>("dateOfBirth"));
        dateOfApplication.setCellValueFactory(new PropertyValueFactory<ApplicantDetails, String>("dateOfApplication"));
        certificate.setCellValueFactory(new PropertyValueFactory<ApplicantDetails, String>("certificate"));
        grade.setCellValueFactory(new PropertyValueFactory<ApplicantDetails, String>("grade"));

        status.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus())); //getStatus() is a computed value, not a field.

        status.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    HBox statusContainer = new HBox();
                    statusContainer.setSpacing(5);

                    // Split status (e.g., "active, incomplete")
                    String[] statuses = item.split(", ");
                    for (String statusText : statuses) {
                        Label statusLabel = new Label(statusText.trim());
                        statusLabel.setStyle(getStatusStyle(statusText));
                        statusContainer.getChildren().add(statusLabel);
                    }

                    setGraphic(statusContainer);
                }
            }
        });


        //Actions column
        if (actions == null) {
            System.out.println("ERROR: actions column is null!");
        }
        actions.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            private final Button submitButton = new Button("Submit");
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(viewButton, submitButton, editButton, deleteButton);

            {
                buttonBox.setSpacing(5); // Add spacing between buttons
                // Apply inline styles for the buttons to match the UI
                String buttonStyle = "-fx-background-color: #007A7A; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-padding: 5px 15px; " +
                        "-fx-background-radius: 8px; -fx-font-weight: bold;";

                viewButton.setStyle(buttonStyle);
                // View Action (Popup Window)
                viewButton.setOnAction(event -> {
                    ApplicantDetails applicant = getTableView().getItems().get(getIndex());
                    openViewApplicantPopup(applicant);
                });
                submitButton.setStyle(buttonStyle);
                editButton.setStyle(buttonStyle);
                deleteButton.setStyle(buttonStyle);

                // Submit Action
                submitButton.setOnAction(event -> {
                    ApplicantDetails applicant = getTableView().getItems().get(getIndex());

                    // Validate required fields before submission
                    if (applicant.getFullName() == null || applicant.getFullName().isBlank() ||
                            applicant.getDateOfBirth() == null || applicant.getDateOfBirth().isBlank() ||
                            applicant.getEmail() == null || applicant.getEmail().isBlank() ||
                            applicant.getContactNumber() == null || applicant.getContactNumber().isBlank() ||
                            applicant.getAddress1() == null || applicant.getAddress1().isBlank() ||
                            applicant.getCity() == null || applicant.getCity().isBlank() ||
                            applicant.getCounty() == null || applicant.getCounty().isBlank() ||
                            applicant.getPostcode() == null || applicant.getPostcode().isBlank() ||
                            applicant.getInstitution() == null || applicant.getInstitution().isBlank() ||
                            applicant.getCertificate() == null || applicant.getCertificate().isBlank() ||
                            applicant.getGrade() == null || applicant.getGrade().isBlank() ||
                            applicant.getGraduationDate() == null || applicant.getGraduationDate().isBlank() || applicant.getGraduationDate().equals("-") ||
                            applicant.getDateOfApplication() == null || applicant.getDateOfApplication().isBlank() ||
                            applicant.getProfilePicturePath() == null || applicant.getProfilePicturePath().isBlank() ||
                            applicant.getPassportPicturePath() == null || applicant.getPassportPicturePath().isBlank() ||
                            applicant.getDocumentPath() == null || applicant.getDocumentPath().isBlank()) {

                        showAlert(Alert.AlertType.WARNING, "Incomplete Application",
                                "Cannot submit applicant. Please ensure all required fields are filled and files are uploaded.");
                        return; // Prevent submission
                    }

                    // Mark applicant as submitted
                    applicant.setSubmitted(true);

                    // Save updates back to CSV
                    applicantManager.saveApplicants();
                    //Write submitted applicants to new file for Course selection
                    applicantManager.writeSubmittedApplicantsToFile();

                    // Call CourseAssigner.assignCourses to update course assignments for new submissions
                    CourseAssigner.assignCourses(
                            "src/main/resources/com/example/demo1/SUBMITTED_APPLICANTS.csv",
                            "src/main/resources/com/example/course/COURSE.csv",
                            "src/main/resources/com/example/course/ASSIGNED_COURSES.csv"
                    );

                    // Refresh table
                    table.refresh();

                    System.out.println("Submitted: " + applicant.getFullName() + " (Status Updated)");
                });



                // Edit Action
                editButton.setOnAction(event -> {
                    ApplicantDetails applicant = getTableView().getItems().get(getIndex());
                    openApplicantForm(applicant);
                });

                // Delete Action
                deleteButton.setOnAction(event -> {
                    ApplicantDetails applicant = getTableView().getItems().get(getIndex());

                    // Confirmation Alert
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete " + applicant.getFullName() + "?",
                            ButtonType.YES, ButtonType.NO);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            // Remove from observable list directly
                            //table.getItems().remove(applicant);

                            //Log applicant before deletion
                            applicantManager.logDeletedApplicant(applicant.getUniqueID(), applicant.getFullName());

                            // Remove from master list
                            applicantManager.applicantList.remove(applicant);

                            if (applicant.isSubmitted()) { //Checks whether applicant belongs to submitted list
                                // Remove from submitted list
                                applicantManager.deleteFromSubmittedApplicants(applicant.getUniqueID());

                                // Delete assignment in CourseAssigner
                                CourseAssigner.deleteAssignment(applicant.getUniqueID(), "src/main/resources/com/example/course/ASSIGNED_COURSES.csv");

                            }

                            // Remove uploaded folder associated with applicant
                            applicantManager.deleteApplicantFolder(applicant.getApplicantFolderName());

                            // Save updates to CSV
                            applicantManager.saveApplicants();

                            System.out.println("Deleted: " + applicant.getFullName());
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });
        //Add search functionality
        setupSearch();
    }






    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header, just the message
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Opens a popup showing Applicant Details (Read-Only)
    private void openViewApplicantPopup(ApplicantDetails applicant) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Applicant Details");

        // Main Layout: Horizontal Box (HBox)
        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(20));

        // Left Side: Applicant Details
        VBox detailsLayout = new VBox(10);
        detailsLayout.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Applicant Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Personal Details
        Label idLabel = new Label("Unique ID: " + applicant.getUniqueID());
        Label nameLabel = new Label("Name: " + applicant.getFullName());
        Label dobLabel = new Label("Date of Birth: " + applicant.getDateOfBirth());
        Label emailLabel = new Label("Email: " + applicant.getEmail());
        Label contactLabel = new Label("Contact Number: " + applicant.getContactNumber());
        Label addressLabel = new Label("Address: " + applicant.getAddress1() + ", " + applicant.getAddress2());
        Label cityLabel = new Label("City: " + applicant.getCity() + ", " + applicant.getCounty());
        Label postcodeLabel = new Label("Postcode: " + applicant.getPostcode());

        // Academic Details
        Label institutionLabel = new Label("Institution: " + applicant.getInstitution());
        Label certificateLabel = new Label("Certificate: " + applicant.getCertificate());
        Label gradeLabel = new Label("Grade: " + applicant.getGrade());
        Label graduationDateLabel = new Label("Graduation Date: " + applicant.getGraduationDate());
        Label commentsLabel = new Label("Comments: " + applicant.getComments());

        Label statusLabel = new Label("Status: " + applicant.getStatus());

        // PDF View Button (Only show if document exists)
        Button viewDocumentButton = new Button("View Document");
        viewDocumentButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        if (applicant.getDocumentPath() == null || applicant.getDocumentPath().isEmpty()) {
            viewDocumentButton.setDisable(true);
        } else {
            viewDocumentButton.setOnAction(e -> openFile(applicant.getDocumentPath()));
        }

        detailsLayout.getChildren().addAll(
                title, idLabel, nameLabel, dobLabel, emailLabel, contactLabel,
                addressLabel, cityLabel, postcodeLabel,
                institutionLabel, certificateLabel, gradeLabel, graduationDateLabel,
                commentsLabel, statusLabel, viewDocumentButton
        );

        // Right Section: Profile & Passport Image
        VBox imageLayout = new VBox(10);
        imageLayout.setAlignment(Pos.CENTER_RIGHT); // Move section towards right
        imageLayout.setMinWidth(250);
        imageLayout.setPadding(new Insets(0, 20, 0, 40)); // Add left padding to move right

        // Profile Picture
        ImageView profileImageView = new ImageView();
        profileImageView.setFitWidth(150);
        profileImageView.setFitHeight(150);
        profileImageView.setPreserveRatio(true);

        // Passport Picture
        ImageView passportImageView = new ImageView();
        passportImageView.setFitWidth(150);
        passportImageView.setFitHeight(150);
        passportImageView.setPreserveRatio(true);

        // Load Profile Picture (if exists)
        if (applicant.getProfilePicturePath() != null && !applicant.getProfilePicturePath().isEmpty()) {
            profileImageView.setImage(new Image("file:" + applicant.getProfilePicturePath()));
        } else {
            profileImageView.setImage(new Image(getClass().getResource("/com/example/demo1/images/no-image.png").toExternalForm()));
        }

        // Load Passport Picture (if exists)
        if (applicant.getPassportPicturePath() != null && !applicant.getPassportPicturePath().isEmpty()) {
            passportImageView.setImage(new Image("file:" + applicant.getPassportPicturePath()));
        } else {
            passportImageView.setImage(new Image(getClass().getResource("/com/example/demo1/images/no-image.png").toExternalForm()));
        }

        // Buttons for Viewing Profile & Passport Image
        Button viewProfileButton = new Button("View Profile Picture");
        viewProfileButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        if (applicant.getProfilePicturePath() == null || applicant.getProfilePicturePath().isEmpty()) {
            viewProfileButton.setDisable(true);
        } else {
            viewProfileButton.setOnAction(e -> openImagePopup(applicant.getProfilePicturePath()));
        }

        Button viewPassportButton = new Button("View Passport Image");
        viewPassportButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        if (applicant.getPassportPicturePath() == null || applicant.getPassportPicturePath().isEmpty()) {
            viewPassportButton.setDisable(true);
        } else {
            viewPassportButton.setOnAction(e -> openImagePopup(applicant.getPassportPicturePath()));
        }

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #ff3b30; -fx-text-fill: white; -fx-font-size: 14px;");
        closeButton.setOnAction(e -> popupStage.close());

        imageLayout.getChildren().addAll(
                new Label("Profile Picture"), profileImageView, viewProfileButton,
                new Label("Passport Image"), passportImageView, viewPassportButton,
                closeButton
        );

        // Add both sections to main layout
        mainLayout.getChildren().addAll(detailsLayout, imageLayout);

        Scene scene = new Scene(mainLayout, 600, 500); // Adjusted width for better spacing
        popupStage.setScene(scene);
        popupStage.setResizable(false);
        popupStage.centerOnScreen();
        popupStage.show();
    }

    //Opens PDF
    private void openFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            showAlert(Alert.AlertType.ERROR, "Error", "File not found: " + filePath);
            return;
        }

        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open file.");
            e.printStackTrace();
        }
    }

    //Opens Image
    // Opens an image in a popup window
    private void openImagePopup(String imagePath) {
        Stage imageStage = new Stage();
        imageStage.setTitle("Image Preview");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(500);
        imageView.setFitHeight(500);
        imageView.setPreserveRatio(true);

        if (imagePath != null && !imagePath.isEmpty()) {
            imageView.setImage(new Image("file:" + imagePath));
        } else {
            imageView.setImage(new Image(getClass().getResource("/com/example/demo1/images/no-image.png").toExternalForm()));
        }

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #ff3b30; -fx-text-fill: white; -fx-font-size: 14px;");
        closeButton.setOnAction(e -> imageStage.close());

        VBox layout = new VBox(10, imageView, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 550, 550);
        imageStage.setScene(scene);
        imageStage.show();
    }

    //Function to open applicant form for EDITING basis only.
    private void openApplicantForm(ApplicantDetails applicant) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo1/applicantForm.fxml"));
            Parent root = fxmlLoader.load();

            // Get the controller of the Applicant Form
            ApplicantFormController controller = fxmlLoader.getController();

            // Pass the DashboardController reference
            controller.setDashboardController(this);

            // If editing, pass the existing applicant data
            if (applicant != null) {
                controller.setApplicantData(applicant);
            }

            Stage stage = new Stage();
            stage.setTitle(applicant == null ? "Add Applicant" : "Edit Applicant"); // Update title accordingly
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    // Function to style statuses
    private String getStatusStyle(String status) {
        return switch (status.toLowerCase()) {
            case "active" -> "-fx-background-color: #E9F7EF; -fx-text-fill: #1E7D2D; -fx-padding: 5px; -fx-background-radius: 10px;";
            case "inactive" -> "-fx-background-color: #F2F3F4; -fx-text-fill: #566573; -fx-padding: 5px; -fx-background-radius: 10px;";
            case "complete" -> "-fx-background-color: #E9F7EF; -fx-text-fill: #1E7D2D; -fx-padding: 5px; -fx-background-radius: 10px;";
            case "incomplete" -> "-fx-background-color: #FDEDEC; -fx-text-fill: #C0392B; -fx-padding: 5px; -fx-background-radius: 10px;";
            default -> "-fx-background-color: #D5DBDB; -fx-text-fill: black; -fx-padding: 5px; -fx-background-radius: 10px;";
        };
    }

    private void setupSearch() {
        FilteredList<ApplicantDetails> filteredData = new FilteredList<>(applicantManager.applicantList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(applicant -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true; // Show all if search field is empty
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return applicant.getUniqueID().toLowerCase().contains(lowerCaseFilter);
                //The fields below can also be used to search for applicant also
                        /*
                        || applicant.getFullName().toLowerCase().contains(lowerCaseFilter)
                        || applicant.getDateOfApplication().toLowerCase().contains(lowerCaseFilter)
                        || applicant.getCertificate().toLowerCase().contains(lowerCaseFilter)
                        || applicant.getGrade().toLowerCase().contains(lowerCaseFilter);
                         */
            });
        });

        SortedList<ApplicantDetails> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedData); // Now filters the actual data source!
    }

    public void refreshTable() {
        applicantManager.loadValidApplicants(); // Reloads valid applicants from CSV
        table.setItems(applicantManager.applicantList); // Update TableView
        setupSearch();
        table.refresh(); // Ensure UI updates
    }
}
