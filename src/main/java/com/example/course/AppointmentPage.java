package com.example.course;
import javax.swing.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

public class AppointmentPage extends JFrame {
	private static final long serialVersionUID = 1L;

	private JTextField firstNameField;
	private JTextField lastNameField;
	private JTextField emailField;
	private JTextField examDateField;
	private JTextField interviewDateField;
	private JComboBox<String> examTimeCombo, interviewTimeCombo;
	private JLabel courseIdLabel, courseTitleLabel, institutionLabel;

	private final String[] timeSlots = {
		"09:00", "10:00", "11:00", "12:00",
		"13:00", "14:00", "15:00", "16:00", "17:00"
	};

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public AppointmentPage(String courseId, String courseTitle, String institution) {
		setTitle("Book Appointment");
		setBounds(100, 100, 550, 550);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);

		JLabel lblFirstName = new JLabel("First Name:");
		lblFirstName.setBounds(30, 30, 100, 25);
		getContentPane().add(lblFirstName);

		firstNameField = new JTextField();
		firstNameField.setBounds(150, 30, 300, 25);
		getContentPane().add(firstNameField);

		JLabel lblLastName = new JLabel("Last Name:");
		lblLastName.setBounds(30, 70, 100, 25);
		getContentPane().add(lblLastName);

		lastNameField = new JTextField();
		lastNameField.setBounds(150, 70, 300, 25);
		getContentPane().add(lastNameField);

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setBounds(30, 110, 100, 25);
		getContentPane().add(lblEmail);

		emailField = new JTextField();
		emailField.setBounds(150, 110, 300, 25);
		getContentPane().add(emailField);

		JLabel lblCourse = new JLabel("Course ID:");
		lblCourse.setBounds(30, 150, 100, 25);
		getContentPane().add(lblCourse);

		courseIdLabel = new JLabel(courseId);
		courseIdLabel.setBounds(150, 150, 300, 25);
		getContentPane().add(courseIdLabel);

		JLabel lblCourseTitle = new JLabel("Course Title:");
		lblCourseTitle.setBounds(30, 190, 100, 25);
		getContentPane().add(lblCourseTitle);

		courseTitleLabel = new JLabel(courseTitle);
		courseTitleLabel.setBounds(150, 190, 300, 25);
		getContentPane().add(courseTitleLabel);

		JLabel lblInstitution = new JLabel("Institution:");
		lblInstitution.setBounds(30, 230, 100, 25);
		getContentPane().add(lblInstitution);

		institutionLabel = new JLabel(institution);
		institutionLabel.setBounds(150, 230, 300, 25);
		getContentPane().add(institutionLabel);

		// Exam date and time
		JLabel lblExamDate = new JLabel("Exam Date (yyyy-MM-dd):");
		lblExamDate.setBounds(30, 270, 180, 25);
		getContentPane().add(lblExamDate);

		examDateField = new JTextField();
		examDateField.setBounds(210, 270, 130, 25);
		getContentPane().add(examDateField);

		examTimeCombo = new JComboBox<>(timeSlots);
		examTimeCombo.setBounds(360, 270, 90, 25);
		getContentPane().add(examTimeCombo);

		// Interview date and time
		JLabel lblInterviewDate = new JLabel("Interview Date (yyyy-MM-dd):");
		lblInterviewDate.setBounds(30, 310, 180, 25);
		getContentPane().add(lblInterviewDate);

		interviewDateField = new JTextField();
		interviewDateField.setBounds(210, 310, 130, 25);
		getContentPane().add(interviewDateField);

		interviewTimeCombo = new JComboBox<>(timeSlots);
		interviewTimeCombo.setBounds(360, 310, 90, 25);
		getContentPane().add(interviewTimeCombo);

		JButton btnBook = new JButton("Book Appointment");
		btnBook.setBounds(180, 370, 180, 30);
		btnBook.addActionListener(e -> bookAppointment());
		getContentPane().add(btnBook);
	}

	private void bookAppointment() {
		String firstName = firstNameField.getText().trim();
		String lastName = lastNameField.getText().trim();
		String email = emailField.getText().trim();
		String examDateStr = examDateField.getText().trim();
		String interviewDateStr = interviewDateField.getText().trim();

		if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
				|| examDateStr.isEmpty() || interviewDateStr.isEmpty()
				|| examTimeCombo.getSelectedItem() == null
				|| interviewTimeCombo.getSelectedItem() == null) {
			showError("Please fill the whole form out.");
			return;
		}

		if (!Pattern.matches("^[a-zA-Z\\s]+$", firstName) || !Pattern.matches("^[a-zA-Z\\s]+$", lastName)) {
			showError("Name fields can only contain letters and spaces.");
			return;
		}

		if (!Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", email)) {
			showError("Invalid email format.");
			return;
		}

		try {
			Date examUtilDate = dateFormat.parse(examDateStr);
			Date interviewUtilDate = dateFormat.parse(interviewDateStr);

			LocalDate examDate = examUtilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate interviewDate = interviewUtilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			LocalTime examTime = LocalTime.parse(examTimeCombo.getSelectedItem().toString());
			LocalTime interviewTime = LocalTime.parse(interviewTimeCombo.getSelectedItem().toString());

			LocalDateTime examDateTime = LocalDateTime.of(examDate, examTime);
			LocalDateTime interviewDateTime = LocalDateTime.of(interviewDate, interviewTime);
			LocalDateTime now = LocalDateTime.now();

			if (examDateTime.isBefore(now) || interviewDateTime.isBefore(now)) {
				showError("Dates must be in the future.");
				return;
			}

			if (examDate.getDayOfWeek().getValue() >= 6 || interviewDate.getDayOfWeek().getValue() >= 6) {
				showError("Appointments cannot be booked on weekends.");
				return;
			}

			if (!interviewDateTime.isAfter(examDateTime)) {
				showError("Please book the entry exam before the career interview.");
				return;
			}

			String fullName = firstName + " " + lastName;
			String bookingId = UUID.randomUUID().toString().substring(0, 8);

			try (Connection conn = DBUtil.connect()) {
				PreparedStatement checkStmt = conn.prepareStatement(
						"SELECT COUNT(*) FROM Appointment WHERE StudentName = ? OR Email = ?");
				checkStmt.setString(1, fullName);
				checkStmt.setString(2, email);
				ResultSet rs = checkStmt.executeQuery();

				if (rs.next() && rs.getInt(1) > 0) {
					showError("An appointment already exists for this student. Please cancel it before booking a new one.");
					return;
				}

				String create = "CREATE TABLE IF NOT EXISTS Appointment (" +
						"BookingID TEXT PRIMARY KEY," +
						"StudentName TEXT," +
						"Email TEXT," +
						"CourseID TEXT," +
						"Institution TEXT," +
						"ExamDateTime TEXT," +
						"InterviewDateTime TEXT)";
				conn.createStatement().execute(create);

				PreparedStatement stmt = conn.prepareStatement(
						"INSERT INTO Appointment VALUES (?, ?, ?, ?, ?, ?, ?)");
				stmt.setString(1, bookingId);
				stmt.setString(2, fullName);
				stmt.setString(3, email);
				stmt.setString(4, courseIdLabel.getText());
				stmt.setString(5, institutionLabel.getText());
				stmt.setString(6, examDateTime.toString());
				stmt.setString(7, interviewDateTime.toString());

				int rows = stmt.executeUpdate();
				if (rows > 0) {
					JOptionPane.showMessageDialog(this, "Appointment booked! Booking ID: " + bookingId);
					dispose();
				} else {
					showError("Booking failed. Please try again.");
				}
			}
		} catch (ParseException e) {
			showError("Please enter dates in yyyy-MM-dd format.");
		} catch (SQLException ex) {
			showError("Database error: " + ex.getMessage());
		}
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
}