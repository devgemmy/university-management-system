package com.example.course;
import javax.swing.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

public class BookingManager extends JFrame {
	private static final long serialVersionUID = 1L;

	private JTextField bookingIdField, examDateField, interviewDateField;
	private JLabel nameLabel, emailLabel, courseLabel, institutionLabel;
	private JComboBox<String> examTimeCombo, interviewTimeCombo;
	private JButton updateButton, cancelButton;

	private final String[] timeSlots = {
		"09:00", "10:00", "11:00", "12:00",
		"13:00", "14:00", "15:00", "16:00", "17:00"
	};

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public BookingManager() {
		setTitle("Manage Your Booking");
		setSize(600, 450);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(null);

		JLabel lblBookingId = new JLabel("Enter Booking ID:");
		lblBookingId.setBounds(30, 20, 120, 25);
		add(lblBookingId);

		bookingIdField = new JTextField();
		bookingIdField.setBounds(160, 20, 200, 25);
		add(bookingIdField);

		JButton searchButton = new JButton("Search");
		searchButton.setBounds(370, 20, 100, 25);
		searchButton.addActionListener(e -> searchBooking());
		add(searchButton);

		nameLabel = new JLabel("Name: ");
		nameLabel.setBounds(30, 60, 500, 25);
		add(nameLabel);

		emailLabel = new JLabel("Email: ");
		emailLabel.setBounds(30, 90, 500, 25);
		add(emailLabel);

		courseLabel = new JLabel("Course: ");
		courseLabel.setBounds(30, 120, 500, 25);
		add(courseLabel);

		institutionLabel = new JLabel("Institution: ");
		institutionLabel.setBounds(30, 150, 500, 25);
		add(institutionLabel);

		JLabel lblExam = new JLabel("Exam Date (yyyy-MM-dd):");
		lblExam.setBounds(30, 190, 180, 25);
		add(lblExam);

		examDateField = new JTextField();
		examDateField.setBounds(210, 190, 140, 25);
		add(examDateField);

		examTimeCombo = new JComboBox<>(timeSlots);
		examTimeCombo.setBounds(370, 190, 100, 25);
		add(examTimeCombo);

		JLabel lblInterview = new JLabel("Interview Date (yyyy-MM-dd):");
		lblInterview.setBounds(30, 230, 180, 25);
		add(lblInterview);

		interviewDateField = new JTextField();
		interviewDateField.setBounds(210, 230, 140, 25);
		add(interviewDateField);

		interviewTimeCombo = new JComboBox<>(timeSlots);
		interviewTimeCombo.setBounds(370, 230, 100, 25);
		add(interviewTimeCombo);

		updateButton = new JButton("Update Dates");
		updateButton.setBounds(100, 300, 160, 30);
		updateButton.addActionListener(e -> updateDates());
		updateButton.setEnabled(false);
		add(updateButton);

		cancelButton = new JButton("Cancel Booking");
		cancelButton.setBounds(300, 300, 160, 30);
		cancelButton.addActionListener(e -> cancelBooking());
		cancelButton.setEnabled(false);
		add(cancelButton);
	}

	private void searchBooking() {
		String bookingId = bookingIdField.getText().trim();
		if (bookingId.isEmpty()) {
			showError("Please enter a Booking ID.");
			return;
		}

		try (Connection conn = DBUtil.connect()) {
			String sql = "SELECT * FROM Appointment WHERE BookingID = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, bookingId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				nameLabel.setText("Name: " + rs.getString("StudentName"));
				emailLabel.setText("Email: " + rs.getString("Email"));
				courseLabel.setText("Course: " + rs.getString("CourseID"));
				institutionLabel.setText("Institution: " + rs.getString("Institution"));

				LocalDateTime examDT = LocalDateTime.parse(rs.getString("ExamDateTime"));
				LocalDateTime interviewDT = LocalDateTime.parse(rs.getString("InterviewDateTime"));

				examDateField.setText(examDT.toLocalDate().toString());
				examTimeCombo.setSelectedItem(examDT.toLocalTime().toString().substring(0, 5));

				interviewDateField.setText(interviewDT.toLocalDate().toString());
				interviewTimeCombo.setSelectedItem(interviewDT.toLocalTime().toString().substring(0, 5));

				updateButton.setEnabled(true);
				cancelButton.setEnabled(true);
			} else {
				showError("No booking found with that ID.");
				clearFields();
			}

		} catch (SQLException e) {
			showError("Database error: " + e.getMessage());
		}
	}

	private void updateDates() {
		String bookingId = bookingIdField.getText().trim();

		try {
			Date examUtilDate = dateFormat.parse(examDateField.getText());
			Date interviewUtilDate = dateFormat.parse(interviewDateField.getText());

			LocalDate examDate = examUtilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate interviewDate = interviewUtilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			LocalTime examTime = LocalTime.parse(examTimeCombo.getSelectedItem().toString());
			LocalTime interviewTime = LocalTime.parse(interviewTimeCombo.getSelectedItem().toString());

			LocalDateTime examDT = LocalDateTime.of(examDate, examTime);
			LocalDateTime interviewDT = LocalDateTime.of(interviewDate, interviewTime);

			if (examDate.getDayOfWeek().getValue() >= 6 || interviewDate.getDayOfWeek().getValue() >= 6) {
				showError("Appointments cannot be on weekends.");
				return;
			}

			if (!interviewDT.isAfter(examDT)) {
				showError("Interview must be after the entry exam.");
				return;
			}

			if (examDT.isBefore(LocalDateTime.now()) || interviewDT.isBefore(LocalDateTime.now())) {
				showError("Dates and times must be in the future.");
				return;
			}

			try (Connection conn = DBUtil.connect()) {
				String sql = "UPDATE Appointment SET ExamDateTime = ?, InterviewDateTime = ? WHERE BookingID = ?";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, examDT.toString());
				stmt.setString(2, interviewDT.toString());
				stmt.setString(3, bookingId);
				stmt.executeUpdate();

				JOptionPane.showMessageDialog(this, "Appointment updated successfully!");
			}

		} catch (ParseException e) {
			showError("Please enter valid dates in yyyy-MM-dd format.");
		} catch (SQLException e) {
			showError("Error updating appointment: " + e.getMessage());
		}
	}

	private void cancelBooking() {
		String bookingId = bookingIdField.getText().trim();

		int confirm = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to cancel this booking?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			try (Connection conn = DBUtil.connect()) {
				String sql = "DELETE FROM Appointment WHERE BookingID = ?";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, bookingId);
				stmt.executeUpdate();

				JOptionPane.showMessageDialog(this, "Booking cancelled.");
				clearFields();
			} catch (SQLException e) {
				showError("Error cancelling booking: " + e.getMessage());
			}
		}
	}

	private void clearFields() {
		nameLabel.setText("Name: ");
		emailLabel.setText("Email: ");
		courseLabel.setText("Course: ");
		institutionLabel.setText("Institution: ");
		examDateField.setText("");
		interviewDateField.setText("");
		examTimeCombo.setSelectedIndex(0);
		interviewTimeCombo.setSelectedIndex(0);
		updateButton.setEnabled(false);
		cancelButton.setEnabled(false);
	}

	private void showError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new BookingManager().setVisible(true));
	}
}