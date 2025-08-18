package com.example.demo1;

import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;




public class ApplicantFormController {

    @FXML
    private TextField firstNameField, lastNameField, emailField, address1Field, address2Field, cityField, stateField, zipField, institutionField, qualificationField;

    @FXML
    private DatePicker dobPicker, gradDatePicker;

    @FXML
    private ComboBox<Country> countryCodeBox;

    @FXML
    private TextField contactNumberField;

    @FXML
    private TextField gradeField;

    @FXML
    private TextArea commentsArea;

    @FXML
    private Label profilePicLabel, passportLabel, certLabel;

    @FXML
    private ImageView profileImageView, passportImageView;

    @FXML
    private Button uploadProfilePicButton, uploadPassportButton, uploadCertButton, saveButton, cancelButton;

    @FXML
    private VBox uploadedFilesContainer;

    private ApplicantDetails existingApplicant;

    private final Set<String> editedFields = new HashSet<>();

    private DashboardController dashboardController; // Store reference

    // Method to validate names (only letters and spaces allowed)
    private boolean isValidName(String name) {
        return name.matches("^[A-Za-z ]+$"); // Allows only letters and spaces
    }

    // email regex (prevents 'test@com' but allows 'test@email.co.uk')
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    public ApplicantDetails getExistingApplicant() {
        return this.existingApplicant;
    }
    //Method to validate postal codes
    private boolean isValidPostalCode(String postalCode) {
        return postalCode.matches("^[A-Za-z0-9]{3,10}$"); // No spaces, only 3-10 chars
    }


    // Setter to receive DashboardController from Dashboard
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    //Main method
    @FXML
    public void initialize() {


        // Initially disable upload buttons
        uploadProfilePicButton.setDisable(true);
        uploadPassportButton.setDisable(true);

        //  Listen for changes in Name and Date of Birth to enable Image Upload
        ChangeListener<String> textListener = (obs, oldVal, newVal) -> validateFormForImageUpload();
        firstNameField.textProperty().addListener(textListener);
        lastNameField.textProperty().addListener(textListener);
        dobPicker.valueProperty().addListener((obs, oldVal, newVal) -> validateFormForImageUpload());


        // Wait until the scene is loaded before applying styles
        firstNameField.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) { // Ensure scene is not null
                newScene.getStylesheets().add(getClass().getResource("/com/example/demo1/style.css").toExternalForm());
            }
        });
        // Ensure ImageViews exist before setting default images
        if (profileImageView != null) {
            profileImageView.setImage(new Image(getClass().getResource("/com/example/demo1/images/camera-icon.png").toExternalForm()));
        }
        if (passportImageView != null) {
            passportImageView.setImage(new Image(getClass().getResource("/com/example/demo1/images/passport-icon.png").toExternalForm()));
        }


        // Image Upload Listeners
        //Added max width and max height as per requirements
        if (uploadProfilePicButton != null) {
            uploadProfilePicButton.setOnAction(event -> handleImageUpload(profileImageView, profilePicLabel, 300, 300, "profile"));
        }
        if (uploadPassportButton != null) {
            uploadPassportButton.setOnAction(event -> handleImageUpload(passportImageView, passportLabel, 500, 500, "passport"));
        }
        if (uploadCertButton != null) {
            uploadCertButton.setOnAction(event -> handlePDFUpload(certLabel));
        }

        // Load country codes into the dropdown
        populateCountryCodes();

        // Setup validation for contact number
        setupTextFieldValidation(contactNumberField, "^[0-9]{7,15}$", "Invalid contact number. Must be 7-15 digits.");

        // Setup field validation for real-time user input
        setupTextFieldValidation(firstNameField, "^[A-Za-z ]*$", "Only letters allowed in First Name");
        setupTextFieldValidation(lastNameField, "^[A-Za-z ]*$", "Only letters allowed in Last Name");
        setupTextFieldValidation(zipField, "^[A-Za-z0-9]*$", "Only letters & numbers allowed in ZIP");

        // Email validation (real-time)
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            resetErrorStyles(emailField);
            if (newVal.trim().isEmpty()) {
                showError(emailField, "Required field is empty");
            } else if (!isValidEmail(newVal)) {
                showError(emailField, "Invalid email format");
            }
        });

        // Date of Birth Validation
        dobPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            resetErrorStyles(dobPicker);
            if (newVal == null) {
                showError(dobPicker, "Date of birth is required");
            }
        });

        //Disable Save Button at the start
        saveButton.setDisable(true);


        // Track changes and validate only edited fields
        addValidationListener(firstNameField);
        addValidationListener(lastNameField);
        addValidationListener(emailField);
        addValidationListener(zipField);
        addValidationListener(contactNumberField);

        dobPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            editedFields.add("dobPicker");
            validateForm();
        });
        saveButton.setOnAction(event -> handleSave());
    }

    private void addValidationListener(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            editedFields.add(field.getId()); // Track this field as edited
            validateForm();
        });
    }

    //Enables the image upload buttons only when Name & DOB are entered
    //Reason: unique ID requires Date of birth and name of applicant and this helps avoid NullPointerException
    private void validateFormForImageUpload() {
        boolean isFormValid = !firstNameField.getText().trim().isEmpty()
                && !lastNameField.getText().trim().isEmpty()
                && dobPicker.getValue() != null;

        uploadProfilePicButton.setDisable(!isFormValid);
        uploadPassportButton.setDisable(!isFormValid);
    }

    private void populateCountryCodes() {
        countryCodeBox.getItems().addAll(
                new Country("United States", "+1", "/com/example/demo1/flags/united-states.png"),
                new Country("United Kingdom", "+44", "/com/example/demo1/flags/united-kingdom.png"),
                new Country("Germany", "+49", "/com/example/demo1/flags/germany.png"),
                new Country("France", "+33", "/com/example/demo1/flags/france.png"),
                new Country("India", "+91", "/com/example/demo1/flags/india.png"),
                new Country("Japan", "+81", "/com/example/demo1/flags/japan.png"),
                new Country("China", "+86", "/com/example/demo1/flags/china.png"),
                new Country("Brazil", "+55", "/com/example/demo1/flags/brazil.png"),
                new Country("South Africa", "+27", "/com/example/demo1/flags/south-africa.png"),
                new Country("UAE", "+971", "/com/example/demo1/flags/united-arab-emirates.png")
        );

        countryCodeBox.setValue(countryCodeBox.getItems().get(1)); // Default to UK

        // Custom cell factory to show flag + country code
        countryCodeBox.setCellFactory(listView -> new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Country country, boolean empty) {
                super.updateItem(country, empty);
                if (empty || country == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    imageView.setImage(country.getFlag());
                    imageView.setFitWidth(25);
                    imageView.setFitHeight(15);
                    setGraphic(imageView);
                    setText(country.getDialCode() + " (" + country.getName() + ")");
                }
            }
        });

        // Ensure selected item also shows flag
        countryCodeBox.setButtonCell(new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Country country, boolean empty) {
                super.updateItem(country, empty);
                if (empty || country == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    imageView.setImage(country.getFlag());
                    imageView.setFitWidth(25);
                    imageView.setFitHeight(15);
                    setGraphic(imageView);
                    setText(country.getDialCode() + " (" + country.getName() + ")");
                }
            }
        });
    }


    //Prevents invalid characters in TextFields and shows error messages
    private void setupTextFieldValidation(TextField field, String regex, String errorMessage) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            resetErrorStyles(field);

            if (newVal.trim().isEmpty()) {
                showError(field, "Required field is empty"); // Show required field error
                return; // Stop further validation
            }

            if (!newVal.matches(regex)) {
                showError(field, errorMessage);
            }
        });
    }

    @FXML
    private void handleSave() {
        resetAllErrorStyles();
        boolean isValid = validateForm();
        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Cannot save without required fields.", "Please fix errors before submitting.");
            return;
        }

        ensureApplicantExists();

        String fullName = firstNameField.getText().trim() + " " + lastNameField.getText().trim();
        String dob = (dobPicker.getValue() != null) ? dobPicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";
        String dateOfApplication = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String email = emailField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String address1 = address1Field.getText().trim();
        String address2 = address2Field.getText().trim();
        String city = cityField.getText().trim();
        String state = stateField.getText().trim();
        String zip = zipField.getText().trim();
        String institution = institutionField.getText().trim();
        String certificate = qualificationField.getText().trim();
        String grade = gradeField.getText().trim();
        String graduationDate = (gradDatePicker.getValue() != null) ? gradDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";
        String comments = commentsArea.getText().trim();

        if (dashboardController == null) {
            System.out.println("ERROR: DashboardController is null!");
            return;
        }

        ApplicantManager applicantManager = dashboardController.getApplicantManager();

        if (existingApplicant != null) {
            // Update existing applicant
            existingApplicant.setFullName(fullName);
            existingApplicant.setDateOfBirth(dob);
            existingApplicant.setDateOfApplication(dateOfApplication);
            existingApplicant.setEmail(email);
            existingApplicant.setContactNumber(contactNumber);
            existingApplicant.setAddress1(address1);
            existingApplicant.setAddress2(address2);
            existingApplicant.setCity(city);
            existingApplicant.setCounty(state);
            existingApplicant.setPostcode(zip);
            existingApplicant.setInstitution(institution);
            existingApplicant.setCertificate(certificate);
            existingApplicant.setGrade(grade);
            existingApplicant.setGraduationDate(graduationDate);
            existingApplicant.setComments(comments);

            // Generate folder name if not already set
            if (existingApplicant.getApplicantFolderName() == null || existingApplicant.getApplicantFolderName().isEmpty()) {
                String newFolderName = ApplicantDetails.generateRandomFolderName();
                existingApplicant.setApplicantFolderName(newFolderName);

                File applicantFolder = new File("applicants", newFolderName);
                if (!applicantFolder.exists()) applicantFolder.mkdirs();
            }

            // If DOB was previously empty or unknown, regenerate the Unique ID
            if (existingApplicant.getUniqueID().endsWith("-UNKNOWN") || existingApplicant.getUniqueID().endsWith("-INVALID")) {
                existingApplicant.setUniqueID(ApplicantDetails.generateUniqueID(fullName, dob));
            }

            System.out.println("✅ Updated Applicant: " + existingApplicant.toString());
            applicantManager.saveApplicants();
        } else {
            // Create new applicant
            ApplicantDetails newApplicant = new ApplicantDetails(fullName, dob, dateOfApplication, certificate, grade);
            newApplicant.setUniqueID(newApplicant.generateUniqueID(fullName, dob));
            newApplicant.setEmail(email);
            newApplicant.setContactNumber(contactNumber);
            newApplicant.setAddress1(address1);
            newApplicant.setAddress2(address2);
            newApplicant.setCity(city);
            newApplicant.setCounty(state);
            newApplicant.setPostcode(zip);
            newApplicant.setInstitution(institution);
            newApplicant.setGraduationDate(graduationDate);
            newApplicant.setComments(comments);

            //Set image paths explicitly here
            File applicantFolder = new File("applicants", newApplicant.getApplicantFolderName());
            if (!applicantFolder.exists()) applicantFolder.mkdirs();

            File profilePic = new File(applicantFolder, "profile.png");
            if (profilePic.exists()) {
                newApplicant.setProfilePicturePath(profilePic.getAbsolutePath());
            }

            File passportPic = new File(applicantFolder, "passport.png");
            if (passportPic.exists()) {
                newApplicant.setPassportPicturePath(passportPic.getAbsolutePath());
            }

            File documentPdf = new File(applicantFolder, "document.pdf");
            if (documentPdf.exists()) {
                newApplicant.setDocumentPath(documentPdf.getAbsolutePath());
            }

            System.out.println("✅ Adding New Applicant: " + newApplicant.toString());

            applicantManager.applicantList.add(newApplicant);
            applicantManager.saveApplicants();
        }

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Applicant details saved successfully.", ButtonType.OK);
        successAlert.setTitle("Success");
        successAlert.setHeaderText(null);

        successAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                goToDashboard(); // Ensure dashboard refreshes
            }
        });

        clearForm();
    }

    //Handles cancel action on applicant form
    @FXML
    private void handleCancel() {
        Alert cancelAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel?", ButtonType.YES, ButtonType.NO);
        cancelAlert.setTitle("Cancel Confirmation");
        cancelAlert.setHeaderText(null);

        cancelAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                goToDashboard();
            }
        });
    }

    private void goToDashboard() {
        try {
            // Close the current Applicant Form window
            Stage currentStage = (Stage) cancelButton.getScene().getWindow();
            currentStage.close();

            // Ensure the existing dashboard refreshes
            if (dashboardController != null) {
                dashboardController.refreshTable();
                return;
            }

            // Find existing dashboard window and refresh it
            for (Stage stage : Stage.getWindows().stream()
                    .filter(window -> window instanceof Stage)
                    .map(window -> (Stage) window)
                    .toList()) {

                if (stage.getTitle() != null && stage.getTitle().equals("Dashboard")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/Dashboard.fxml"));
                    Parent dashboardRoot = loader.load();

                    // Get controller reference
                    DashboardController dashboardController = loader.getController();
                    dashboardController.refreshTable(); // Refresh table after returning

                    stage.toFront(); // Bring existing dashboard to front
                    return;
                }
            }

            // If no dashboard exists, create a new one
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/Dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("Dashboard");
            dashboardStage.setScene(new Scene(dashboardRoot));

            // Get controller reference and refresh TableView
            DashboardController dashboardController = loader.getController();
            dashboardController.refreshTable();

            dashboardStage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the dashboard.");
            e.printStackTrace();
        }
    }

    public void setApplicantData(ApplicantDetails applicant) {
        if (applicant == null) return; // Prevent null pointer issues

        this.existingApplicant = applicant; // Store the reference

        firstNameField.setText((applicant.getFullName() != null && applicant.getFullName().contains(" "))
                ? applicant.getFullName().split(" ")[0]
                : "");

        lastNameField.setText((applicant.getFullName() != null && applicant.getFullName().contains(" "))
                ? applicant.getFullName().split(" ")[1]
                : "");

        // Assume applicant.getGraduationDate() or applicant.getDateOfBirth() returns "N/A"
        dobPicker.setValue(safeParseDate(applicant.getDateOfBirth()));
        //Safely handle graduation date
        gradDatePicker.setValue(safeParseDate(applicant.getGraduationDate()));
        emailField.setText(applicant.getEmail() != null ? applicant.getEmail() : "");
        contactNumberField.setText(applicant.getContactNumber() != null ? applicant.getContactNumber() : "");
        address1Field.setText(applicant.getAddress1() != null ? applicant.getAddress1() : "");
        address2Field.setText(applicant.getAddress2() != null ? applicant.getAddress2() : "");
        cityField.setText(applicant.getCity() != null ? applicant.getCity() : "");
        stateField.setText(applicant.getCounty() != null ? applicant.getCounty() : "");
        zipField.setText(applicant.getPostcode() != null ? applicant.getPostcode() : "");
        institutionField.setText(applicant.getInstitution() != null ? applicant.getInstitution() : "");
        qualificationField.setText(applicant.getCertificate() != null ? applicant.getCertificate() : "");
        gradeField.setText(applicant.getGrade() != null ? applicant.getGrade() : "");
        commentsArea.setText(applicant.getComments() != null ? applicant.getComments() : "");

        // Check if folder exists before loading any files
        File applicantFolder = null;

        if (applicant.getProfilePicturePath() != null && !applicant.getProfilePicturePath().isEmpty()) {
            applicantFolder = new File(applicant.getProfilePicturePath()).getParentFile();
        } else if (applicant.getPassportPicturePath() != null && !applicant.getPassportPicturePath().isEmpty()) {
            applicantFolder = new File(applicant.getPassportPicturePath()).getParentFile();
        } else if (applicant.getDocumentPath() != null && !applicant.getDocumentPath().isEmpty()) {
            applicantFolder = new File(applicant.getDocumentPath()).getParentFile();
        }

        if (applicantFolder != null && !applicantFolder.exists()) {
            showAlert(Alert.AlertType.ERROR, "Missing Folder", "The applicant folder does not exist:\n" + applicantFolder.getAbsolutePath());

            profilePicLabel.setText("Folder missing");
            passportLabel.setText("Folder missing");
            certLabel.setText("Folder missing");
            return;
        }

        // Load Profile Image
        if (applicant.getProfilePicturePath() != null && !applicant.getProfilePicturePath().isEmpty()) {
            File profileImgFile = new File(applicant.getProfilePicturePath());
            if (profileImgFile.exists()) {
                profileImageView.setImage(new Image(profileImgFile.toURI().toString()));
                profilePicLabel.setText(profileImgFile.getName());
                addFileToVBox(applicant.getProfilePicturePath(), "png", "profile");
            } else {
                //When saved profile picture is missing
                showAlert(Alert.AlertType.ERROR, "Missing File", "Profile picture not found at:\n" + applicant.getProfilePicturePath());
            }
        }

        // Load Passport Image
        if (applicant.getPassportPicturePath() != null && !applicant.getPassportPicturePath().isEmpty()) {
            File passportImgFile = new File(applicant.getPassportPicturePath());
            if (passportImgFile.exists()) {
                passportImageView.setImage(new Image(passportImgFile.toURI().toString()));
                passportLabel.setText(passportImgFile.getName());
                addFileToVBox(applicant.getPassportPicturePath(), "png", "passport");
            } else {
                //When passport picture is missing
                showAlert(Alert.AlertType.ERROR, "Missing File", "Passport image not found at:\n" + applicant.getPassportPicturePath());
                passportLabel.setText("File not found");
            }
        }

        // Load PDF Document
        if (applicant.getDocumentPath() != null && !applicant.getDocumentPath().isEmpty()) {
            File documentFile = new File(applicant.getDocumentPath());
            if (documentFile.exists()) {
                certLabel.setText(documentFile.getName());
                addFileToVBox(applicant.getDocumentPath(), "pdf", "document");
            } else {
                //When document is missing
                showAlert(Alert.AlertType.ERROR, "Missing File", "Document PDF not found at:\n" + applicant.getDocumentPath());
                certLabel.setText("File not found");
            }
        }
    }


    private LocalDate safeParseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || dateStr.equalsIgnoreCase("N/A") || dateStr.equals("-")) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }


    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        contactNumberField.clear();
        address1Field.clear();
        address2Field.clear();
        cityField.clear();
        stateField.clear();
        zipField.clear();
        institutionField.clear();
        qualificationField.clear();
        gradeField.clear();
        dobPicker.setValue(null);
        gradDatePicker.setValue(null);
        commentsArea.clear();

        // Reset validation styles
        resetAllErrorStyles();

        // Disable Save button until new input is provided
        saveButton.setDisable(true);
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validate First Name (Check if it's empty and contains only letters)
        String firstName = firstNameField.getText().trim();
        if (firstName.isEmpty()) {
            showError(firstNameField, "First name is required");
            isValid = false;
        } else if (!isValidName(firstName)) {
            showError(firstNameField, "First name can only contain letters.");
            isValid = false;
        } else {
            resetErrorStyles(firstNameField);
        }

        // Validate Last Name (Check if it's empty and contains only letters)
        String lastName = lastNameField.getText().trim();
        if (lastName.isEmpty()) {
            showError(lastNameField, "Last name is required");
            isValid = false;
        } else if (!isValidName(lastName)) {
            showError(lastNameField, "Last name can only contain letters.");
            isValid = false;
        } else {
            resetErrorStyles(lastNameField);
        }

        // Validate Email
        if (emailField.getText().trim().isEmpty()) {
            showError(emailField, "Email is required");
            isValid = false;
        } else if (!isValidEmail(emailField.getText().trim())) {
            showError(emailField, "Invalid email format");
            isValid = false;
        } else {
            resetErrorStyles(emailField);
        }

        // Validate Date of Birth
        if (dobPicker.getValue() == null) {
            showError(dobPicker, "Date of birth is required");
            isValid = false;
        } else {
            resetErrorStyles(dobPicker);
        }

        // Validate ZIP/Postcode
        String zipCode = zipField.getText().trim();
        if (zipCode.isEmpty()) {
            showError(zipField, "ZIP/Postcode is required");
            isValid = false;
        } else if (!isValidPostalCode(zipCode)) {
            showError(zipField, "ZIP/Postcode can only contain 3-10 letters/numbers, no spaces.");
            isValid = false;
        } else {
            resetErrorStyles(zipField);
        }

        // Enable or disable Save button based on validation
        saveButton.setDisable(!isValid);

        return isValid;
    }

    private void addFileToVBox(String filePath, String fileType, String category) {
        File file = new File(filePath);
        if (!file.exists()) {
            showAlert(Alert.AlertType.ERROR, "Error", "File not found: " + filePath);
            return;
        }

        // Remove the existing entry if it's a profile or passport image
        if (category.equals("profile") || category.equals("passport")) {
            uploadedFilesContainer.getChildren().removeIf(node -> {
                if (node instanceof HBox) {
                    HBox hbox = (HBox) node;
                    if (!hbox.getChildren().isEmpty() && hbox.getChildren().get(1) instanceof Label) {
                        Label label = (Label) hbox.getChildren().get(1);
                        return category.equals("profile") && label.getText().contains("Profile Picture")
                                || category.equals("passport") && label.getText().contains("Passport Image");
                    }
                }
                return false;
            });
        }

        // Create an HBox to hold file details
        HBox fileEntry = new HBox(10); // 10px spacing
        fileEntry.setAlignment(Pos.CENTER_LEFT);
        fileEntry.setPadding(new Insets(5));

        // File Icon
        ImageView fileIcon = new ImageView();
        fileIcon.setFitWidth(20);
        fileIcon.setFitHeight(20);

        if (fileType.equals("pdf")) {
            fileIcon.setImage(new Image(getClass().getResource("/com/example/demo1/images/pdf-icon.png").toExternalForm()));
        } else {
            fileIcon.setImage(new Image(getClass().getResource("/com/example/demo1/images/image-icon.png").toExternalForm()));
        }

        // Truncate File Name if too long (max 20 chars)
        String fileName = file.getName();
        String displayedName = fileName.length() > 20 ? fileName.substring(0, 17) + "..." : fileName;

        // File Name Label
        Label fileLabel = new Label(
                (category.equals("profile") ? "Profile Picture: " :
                        category.equals("passport") ? "Passport Image: " : "")
                        + displayedName);

        // Show Full Name in Tooltip
        Tooltip tooltip = new Tooltip(fileName);
        Tooltip.install(fileLabel, tooltip);

        // View Button
        Button viewButton = new Button("View");
        viewButton.setStyle("-fx-background-color: #D1D5DB; -fx-text-fill: black; -fx-font-size: 12px;");
        viewButton.setOnAction(event -> openFile(filePath)); // Pass full path to openFile()

        // Set a fixed width for the label so the button doesn’t shift
        fileLabel.setMaxWidth(180);
        fileLabel.setEllipsisString("...");

        // Add elements to HBox
        fileEntry.getChildren().addAll(fileIcon, fileLabel, viewButton);

        // Add fileEntry to VBox
        uploadedFilesContainer.getChildren().add(fileEntry);
    }

    //Opens the file depending on file type
    private void openFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            showAlert(Alert.AlertType.ERROR, "Error", "File not found: " + filePath);
            return;
        }

        // Detect file type and open accordingly
        if (filePath.toLowerCase().endsWith(".png") || filePath.toLowerCase().endsWith(".jpg") || filePath.toLowerCase().endsWith(".jpeg")) {
            previewImage(filePath);
        } else if (filePath.toLowerCase().endsWith(".pdf")) {
            previewPDF(filePath);
        } else {
            // this code activates if the filters of handleImageUpload() and handlePDFUpload() are removed.
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Opening File", "File opened in default application.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Cannot open file: " + filePath);
                e.printStackTrace();
            }
        }
    }

    //Previews PDF file using view button, which opens the system PDF viewer
    private void previewPDF(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            showAlert(Alert.AlertType.ERROR, "Error", "File not found: " + filePath);
            return;
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file); // Opens the PDF in system default viewer
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Opening File", "Your system does not support opening PDF files.");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open PDF: " + filePath);
            e.printStackTrace();
        }
    }

    //Previews png images upon selecting the view button, which opens a new ImageView window
    private void previewImage(String filePath) {
        Stage stage = new Stage();
        stage.setTitle("Image Preview");

        // Load image
        Image image = new Image(new File(filePath).toURI().toString());

        // Create an ImageView with automatic scaling
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true); // Maintain aspect ratio
        imageView.setFitWidth(600); // Adjust the size dynamically
        imageView.setFitHeight(500); // Adjust the size dynamically

        // Wrap in ScrollPane in case image is large
        ScrollPane scrollPane = new ScrollPane(imageView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Create scene and show window
        Scene scene = new Scene(scrollPane, 650, 550);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }


    private void handleImageUpload(ImageView imageView, Label label, int maxWidth, int maxHeight, String category) {
        ensureApplicantExists();
        // Generate folder name if missing BEFORE using it
        if (existingApplicant.getApplicantFolderName() == null || existingApplicant.getApplicantFolderName().isEmpty()) {
            String folderName = ApplicantDetails.generateRandomFolderName();
            existingApplicant.setApplicantFolderName(folderName);
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Images", "*.png"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(selectedFile);
                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();

                if (width > maxWidth || height > maxHeight) {
                    System.out.println("❌ Image upload failed: Size " + width + "x" + height +
                            " exceeds limit of " + maxWidth + "x" + maxHeight);
                    showAlert(Alert.AlertType.ERROR, "Invalid Image Size",
                            "Image must not exceed " + maxWidth + "x" + maxHeight + " pixels.");
                    return;
                }

                File parentFolder = new File("applicants");
                if (!parentFolder.exists()) parentFolder.mkdirs();

                File applicantFolder = new File(parentFolder, existingApplicant.getApplicantFolderName());
                if (!applicantFolder.exists()) applicantFolder.mkdirs();

                File savedFile = new File(applicantFolder, category + ".png");
                Files.copy(selectedFile.toPath(), savedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                if (category.equals("profile")) {
                    existingApplicant.setProfilePicturePath(savedFile.getAbsolutePath());
                    System.out.println("✅ Profile image saved to: " + savedFile.getAbsolutePath());
                } else if (category.equals("passport")) {
                    existingApplicant.setPassportPicturePath(savedFile.getAbsolutePath());
                    existingApplicant.setPassportPicturePath(savedFile.getAbsolutePath());
                    System.out.println("✅ Passport image saved to: " + savedFile.getAbsolutePath());
                }

                dashboardController.getApplicantManager().saveApplicants(); //  Persist immediately

                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                imageView.setImage(image);
                label.setText(savedFile.getName());

                addFileToVBox(savedFile.getAbsolutePath(), "png", category);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Image uploaded successfully.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "File Error", "Could not upload image file.");
            }
        }
    }

    private void handlePDFUpload(Label label) {
        ensureApplicantExists();

        // Generate folder name if missing before using it
        if (existingApplicant.getApplicantFolderName() == null || existingApplicant.getApplicantFolderName().isEmpty()) {
            existingApplicant.setApplicantFolderName(ApplicantDetails.generateRandomFolderName());
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Documents", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                File parentFolder = new File("applicants");
                if (!parentFolder.exists()) parentFolder.mkdirs();

                File applicantFolder = new File(parentFolder, existingApplicant.getApplicantFolderName());
                if (!applicantFolder.exists()) applicantFolder.mkdirs();

                File savedFile = new File(applicantFolder, "document.pdf");
                Files.copy(selectedFile.toPath(), savedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                existingApplicant.setDocumentPath(savedFile.getAbsolutePath());
                System.out.println("✅ PDF document saved to: " + savedFile.getAbsolutePath());

                dashboardController.getApplicantManager().saveApplicants(); // Persist immediately

                label.setText(savedFile.getName());

                addFileToVBox(savedFile.getAbsolutePath(), "pdf", "document");

                showAlert(Alert.AlertType.INFORMATION, "Success", "Document uploaded successfully.");

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "File Error", "Could not upload PDF file.");
            }
        }
    }


    //Ensure a single ApplicantDetails instance is created per applicant (to avoid Stack Overflow Error)
    private void ensureApplicantExists() {
        if (existingApplicant == null) {
            existingApplicant = new ApplicantDetails(
                    firstNameField.getText().trim() + " " + lastNameField.getText().trim(),
                    dobPicker.getValue() != null ? dobPicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-",
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    qualificationField.getText().trim(),
                    gradeField.getText().trim()
            );

            existingApplicant.setApplicantFolderName(ApplicantDetails.generateRandomFolderName());

            dashboardController.getApplicantManager().applicantList.add(existingApplicant);
        }
    }


    // Show an error message below the field
    private void showError(Control field, String message) {
        // Remove old error styles before reapplying them
        field.getStyleClass().remove("error");
        field.getStyleClass().add("error"); // Reapply red border

        /*Debugging: Check applied style classes
        System.out.println("Applying error class to: " + field.getId());
        System.out.println("Current style classes: " + field.getStyleClass());
         */

        // Ensure only ONE error message exists per field
        AnchorPane parent = (AnchorPane) field.getParent();
        parent.getChildren().removeIf(node ->
                node instanceof Label && node.getId() != null && node.getId().equals(field.getId() + "-error"));

        // Create new error message label
        Label errorLabel = new Label(message);
        errorLabel.setId(field.getId() + "-error"); // Unique ID for easy removal
        errorLabel.getStyleClass().add("error-message"); // Apply the CSS class

        // Force setting the style if CSS isn't working
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold;");

        // Position error label below the field
        errorLabel.setLayoutX(field.getLayoutX());
        errorLabel.setLayoutY(field.getLayoutY() + field.getHeight() + 15);

        //  Add to UI
        parent.getChildren().add(errorLabel);
    }

    // Reset styles and remove error messages for all fields
    private void resetAllErrorStyles() {
        resetErrorStyles(firstNameField);
        resetErrorStyles(lastNameField);
        resetErrorStyles(dobPicker);
    }

    // Reset error styles for a single field and remove its error message
    private void resetErrorStyles(Control field) {
        field.getStyleClass().remove("error"); // Remove red border

        // Remove only the error label related to this field
        ((AnchorPane) field.getParent()).getChildren().removeIf(node ->
                node instanceof Label && node.getId() != null && node.getId().equals(field.getId() + "-error"));
    }

    // Show an alert (can be success or error)
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}