package com.example.attendance;

import java.awt.EventQueue;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import com.toedter.calendar.JDateChooser;

public class Searchstud extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtStudentAttendance;
	private JTextField textField;
	private JTable table;
	private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    // Achievement Badges and Attendance Streaks
    private JPanel achievementPanel;
    private JComboBox<String> courseDropdown;
    private JTextField txtStartDate;
    private JTextField txtEndDate;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Searchstud frame = new Searchstud();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Searchstud() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1297, 888);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Start Date field
        startDateChooser = new JDateChooser();
        startDateChooser.setBounds(728, 95, 150, 36);
        contentPane.add(startDateChooser);
        
        // End Date field
        endDateChooser = new JDateChooser();
        endDateChooser.setBounds(986, 95, 150, 36);
        contentPane.add(endDateChooser);
		
		txtStudentAttendance = new JTextField();
		txtStudentAttendance.setText("              Student Attendance");
		txtStudentAttendance.setFont(new Font("Tahoma", Font.BOLD, 23));
		txtStudentAttendance.setColumns(10);
		txtStudentAttendance.setBounds(467, 21, 411, 51);
		contentPane.add(txtStudentAttendance);
		
		JButton btnSearchStudentId = new JButton("Search Student ID");
		btnSearchStudentId.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("Hi!");
				
				try {
//					//Retrieve selected start and end dates from the JDateChooser
//                    Date startDate = startDateChooser.getDate();
//                    Date endDate = endDateChooser.getDate();
//                    
//                    // Check if both start and end dates are selected
//                    if (startDate == null || endDate == null) {
//                        JOptionPane.showMessageDialog(Searchstud.this, "Please select both start and end dates.", "Error", JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//                    SimpleDateFormat dbFormat = new SimpleDateFormat("dd/MM/yyyy");
//				    String formattedStartDate = dbFormat.format(startDateChooser.getDate());
//				    String formattedEndDate = dbFormat.format(endDateChooser.getDate());
//				    
//				    System.out.println("Start Date for Query: " + formattedStartDate);
//				    System.out.println("End Date for Query: " + formattedEndDate);
//				    
//				    System.out.println("Formatted Start Date for Query: '" + formattedStartDate + "'");
//				    System.out.println("Formatted End Date for Query: '" + formattedEndDate + "'");
//
//				    ResultSet results = SearchstudImport.ReadFromDBWithDateRange(textField.getText(), formattedStartDate, formattedEndDate);
//
//				    DefaultTableModel course1 = new DefaultTableModel();
//				    course1.setRowCount(0); // Clear previous data
//				    course1.addColumn("Student ID");
//				    course1.addColumn("Course ID");
//				    course1.addColumn("Session Date");
//				    course1.addColumn("Attendance");
//				    
//				    if (!results.isBeforeFirst()) {  // Checks if the result set is empty
//				        System.out.println("No matching data found for the given criteria.");
//				    } else {
//				        while (results.next()) {
//				            String name = results.getString("StudentID");
//				            String name1 = results.getString("CourseID");
//				            String name2 = results.getString("SessionDate");
//				            String name3 = results.getString("Attendance");
//				            
//				            System.out.println("Student ID for Query: '" + textField.getText() + "'");
//
//					        System.out.println("Retrieved Row: " + name + " | " + name1 + " | " + name2 + " | " + name3);
//
//					        String[] row = {name, name1, name2, name3}; 
//					        course1.addRow(row);
//					        
//					        table.setModel(course1);
//					    }
//					   }
//					}catch (SQLException e1) {
//						e1.printStackTrace();
//					 }
//				    }
                    
					DefaultTableModel course = new DefaultTableModel();
					
					course.addColumn("Student ID");
				    course.addColumn("Course ID");
				    course.addColumn("Session Date");
				    course.addColumn("Attendance");

				    ResultSet results = SearchstudImport.ReadFromDB2(textField.getText());
				    
				    boolean hasResults = false; // Track if any result is found
				    
				    while (results.next()) {
				    	hasResults = true;
				        String name = results.getString("StudentID");
				        String name1 = results.getString("CourseID");
				        String name2 = results.getString("SessionDate");
				        String name3 = results.getString("Attendance");

				        // 🛠️ Fix: Add all values in a **single row**
				        String[] row = {name, name1, name2, name3}; 
				      
				        course.addRow(row);
				    }
				    
				    table.setModel(course);
				    
				    // Show error message if no results were found
                    if (!hasResults) {
                        JOptionPane.showMessageDialog(null, "No matching result found. Please re enter a correct Student ID!", "Error", JOptionPane.ERROR_MESSAGE);
                    }		
				       
//                    // Convert start and end dates to `java.sql.Date` to query database
//					java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
//                    java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
//
//                    // Query the database for records between start and end dates
//                    ResultSet results = SearchstudImport.ReadFromDBWithDateRange(textField.getText(), sqlStartDate, sqlEndDate);

//				    // Format for display as dd-MM-yyyy
//				    SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
//				    System.out.println("Start Date (Display Format): " + displayFormat.format(startDate));
//				    System.out.println("End Date (Display Format): " + displayFormat.format(endDate));

//				    // Format for SQL as yyyy-MM-dd
//				    SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
//				    String formattedStartDate = sqlFormat.format(startDate);
//				    String formattedEndDate = sqlFormat.format(endDate);
//
//				    // Convert the formatted date string back to java.sql.Date
//				    java.sql.Date sqlStartDate = java.sql.Date.valueOf(formattedStartDate);
//				    java.sql.Date sqlEndDate = java.sql.Date.valueOf(formattedEndDate);
//
//				    // Query the database for records between start and end dates
//				    ResultSet results = SearchstudImport.ReadFromDBWithDateRange(textField.getText(), sqlStartDate, sqlEndDate);
				    
//				    // Format for database as dd/MM/yyyy (since database stores dates in this format)
//				    SimpleDateFormat dbFormat = new SimpleDateFormat("dd/MM/yyyy");
//				    String formattedStartDate = dbFormat.format(startDate);
//				    String formattedEndDate = dbFormat.format(endDate);
//
//				    // Use these formatted dates in the query
//				    ResultSet results = SearchstudImport.ReadFromDBWithDateRange(textField.getText(), formattedStartDate, formattedEndDate);
//				    			    

//				    while (results.next()) {
//				        String name = results.getString("StudentID");
//				        String name1 = results.getString("CourseID");
//				        String name2 = results.getString("SessionDate");
//				        String name3 = results.getString("Attendance");
				            
				} catch (SQLException e1) {
					e1.printStackTrace();
			  }
			}
		});
		
		btnSearchStudentId.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnSearchStudentId.setBounds(174, 95, 166, 36);
		contentPane.add(btnSearchStudentId);
		
		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(344, 95, 294, 36);
		contentPane.add(textField);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(25, 138, 1228, 496);
		contentPane.add(scrollPane);
		
		table = new JTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				JTable copy = (JTable)e.getSource();
				
				int row = copy.getSelectedRow();
				int col = copy.getSelectedColumn();
				String val1 = copy.getModel().getValueAt(row, col).toString();
				System.out.println(row + " " + col + " " + val1);
		        
				StudAttendance atten = new StudAttendance(val1);
				atten.setVisible(true);
			}
		});
		scrollPane.setViewportView(table);
		
		JButton btnNewButton = new JButton("Import CSV");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnNewButton.setBounds(1110, 27, 137, 36);
		contentPane.add(btnNewButton);
		
		txtStartDate = new JTextField();
		txtStartDate.setText("Start date:");
		txtStartDate.setBounds(659, 95, 65, 36);
		contentPane.add(txtStartDate);
		txtStartDate.setColumns(10);
		
		txtEndDate = new JTextField();
		txtEndDate.setText("End date:");
		txtEndDate.setColumns(10);
		txtEndDate.setBounds(916, 95, 65, 36);
		contentPane.add(txtEndDate);
		btnNewButton.addActionListener(e -> importCSV()); 
		
		// Menu Bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(800, 40)); // Increase height

        // Custom Font for Larger Text
        Font menuFont = new Font("Arial", Font.BOLD, 22);

        // "Actions" Menu
        JMenu actionsMenu = new JMenu("Menu");
        menuBar.add(actionsMenu);
        
        // Menu Items
        JMenuItem dash = new JMenuItem("Dashboard");
        JMenuItem searchCourseItem = new JMenuItem("Course Attendance");
        JMenuItem logoutItem = new JMenuItem("Logout");
        
        actionsMenu.add(dash);
        actionsMenu.add(searchCourseItem);
        actionsMenu.addSeparator(); // Optional for spacing
        actionsMenu.add(logoutItem);
        
//        // Action Listeners
//        dash.addActionListener(e -> new Dashboard().setVisible(true));
//        searchCourseItem.addActionListener(e -> new SearchCourse().setVisible(true));
//        logoutItem.addActionListener(e -> logout());
        
        // Action Listeners
        dash.addActionListener(e -> {
            System.out.println("Dashboard Clicked");
            new Dashboard().setVisible(true);
        });

        searchCourseItem.addActionListener(e -> {
        	System.out.println("Course Attendance Clicked");
            new SearchCourse().setVisible(true);
        });
        
        logoutItem.addActionListener(e -> {
            //JOptionPane.showMessageDialog(null, "Logout Clicked");
            logout();
        });

        setJMenuBar(menuBar); // Attach menu bar to the frame

        setVisible(true);
	}
	
	private void importCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Check if the selected file has a .csv extension
            if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
                JOptionPane.showMessageDialog(this, "Invalid file type! Please select a CSV file.", "File Error", JOptionPane.ERROR_MESSAGE);
                return; // Exit method early
            }
            SearchstudImport exampleObj = new SearchstudImport();
            boolean success = exampleObj.processCSV(selectedFile);

            if (success) {
                JOptionPane.showMessageDialog(this, "CSV Imported Successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error importing CSV.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
	
//	public static Connection getConnection() throws SQLException {
//	    String url = "jdbc:mysql://localhost:3306/your_database_name"; // Modify with your database URL
//	    String username = "your_username"; // Modify with your database username
//	    String password = "your_password"; // Modify with your database password
//
//	    return DriverManager.getConnection(url, username, password);
//	}

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
