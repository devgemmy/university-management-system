package com.example.course;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class MySwingGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField courseField;
	private JTable table;
	private DefaultTableModel tableModel;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				MySwingGUI frame = new MySwingGUI();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public MySwingGUI() {
		setTitle("Course Search");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1000, 400);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.decode("#02B0B0"));
		topPanel.setLayout(new FlowLayout());
		getContentPane().add(topPanel, BorderLayout.NORTH);

		courseField = new JTextField();
		courseField.setColumns(30);
		topPanel.add(new JLabel("Enter Course Title:"));
		topPanel.add(courseField);

		JButton searchButton = new JButton("Search");
		topPanel.add(searchButton);

		tableModel = new DefaultTableModel(new Object[][] {}, new String[] {
			"Course ID", "Course Title", "Institution Name", "Full Time", "Sandwich Year", "Year Abroad", "Stages"
		}) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		searchButton.addActionListener(e -> searchCourses());

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
					int row = table.getSelectedRow();
					String courseId = tableModel.getValueAt(row, 0).toString();
					String title = tableModel.getValueAt(row, 1).toString();
					String institution = tableModel.getValueAt(row, 2).toString();
					new AppointmentPage(courseId, title, institution).setVisible(true);
				}
			}
		});
		 
		JButton manageBtn = new JButton("Manage My Booking");
		manageBtn.setBackground(new Color(255, 128, 64));
	    manageBtn.setBounds(300, 450, 200, 30);
	    manageBtn.addActionListener(e -> new BookingManager().setVisible(true));
	    getContentPane().add(manageBtn, BorderLayout.WEST);

	}

	private void searchCourses() {
		tableModel.setRowCount(0);

		String sql = "SELECT c.KISCOURSEID, c.TITLE, i.LEGAL_NAME, c.KISMODE, c.SANDWICH, c.YEARABROAD, c.NUMSTAGE " +
				"FROM Course c JOIN Institution i ON c.PUBUKPRN = i.PUBUKPRN " +
				"WHERE c.TITLE LIKE ?";

		try (Connection conn = DBUtil.connect();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, "%" + courseField.getText().trim() + "%");

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Vector<Object> row = new Vector<>();
				row.add(rs.getString("KISCOURSEID"));
				row.add(rs.getString("TITLE"));
				row.add(rs.getString("LEGAL_NAME"));
				row.add(rs.getInt("KISMODE") == 1 ? "Full-Time" : "Part-Time");
				row.add(rs.getInt("SANDWICH") == 1 ? "Yes" : "No");
				row.add(rs.getInt("YEARABROAD") == 1 ? "Yes" : "No");
				row.add(rs.getInt("NUMSTAGE"));
				tableModel.addRow(row);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
		}
	}
}
