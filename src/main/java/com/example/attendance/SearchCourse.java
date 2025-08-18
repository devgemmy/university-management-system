package com.example.attendance;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;

public class SearchCourse extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField txtWelcomeToThe;
	private JTextField textField;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SearchCourse frame = new SearchCourse();
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
	public SearchCourse() {
		setBounds(100, 100, 1297, 780);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		txtWelcomeToThe = new JTextField();
		txtWelcomeToThe.setHorizontalAlignment(SwingConstants.CENTER);
		txtWelcomeToThe.setFont(new Font("Tahoma", Font.BOLD, 20));
		txtWelcomeToThe.setText("    Course Attendance");
		txtWelcomeToThe.setBounds(478, 21, 433, 51);
		getContentPane().add(txtWelcomeToThe);
		txtWelcomeToThe.setColumns(10);
		
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
        JMenuItem searchStudentItem = new JMenuItem("Student Attendance");
        JMenuItem logoutItem = new JMenuItem("Logout");
        
        actionsMenu.add(dash);
        actionsMenu.add(searchStudentItem);
        actionsMenu.addSeparator(); // Optional for spacing
        actionsMenu.add(logoutItem);
        
        // Action Listeners
        searchStudentItem.addActionListener(e -> {
            System.out.println("Student Attendance Clicked");
            new Searchstud().setVisible(true);
        });

        dash.addActionListener(e -> {
        	System.out.println("Dashboard Clicked");
            new Dashboard().setVisible(true);
        });
        
        logoutItem.addActionListener(e -> {
            //JOptionPane.showMessageDialog(null, "Logout Clicked");
            logout();
        });     

        setJMenuBar(menuBar); // Attach menu bar to the frame

        setVisible(true);
		
		JButton btnNewButton = new JButton("Search Name");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("Hi!");
				
				try {
                    DefaultTableModel course = new DefaultTableModel();
                    course.addColumn("Course Name");
                    course.addColumn("Course ID");
                    course.addColumn("University Name");
                    course.addColumn("PUBUKPRN");

                    ResultSet results = SearchCourseImport.ReadFromDB1(textField.getText());

                    boolean hasResults = false; // Track if any result is found

                    while (results.next()) {
                        hasResults = true;
                        String name = results.getString("TITLE");
                        String name1 = results.getString("CourseID");
                        String name2 = results.getString("LEGAL_NAME");
                        String name3 = results.getString("PUBUKPRN");

                        String[] row = {name, name1, name2, name3}; 
                        course.addRow(row);
                    }

                    table.setModel(course);

                    // Show error message if no results were found
                    if (!hasResults) {
                        JOptionPane.showMessageDialog(null, "No matching course found. Please re enter a correct Name or ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    }		
                    
//				try {
//					DefaultTableModel course = new DefaultTableModel();
//					
//					course.addColumn("Course Name");
//				    course.addColumn("Course ID");
//				    course.addColumn("University Name");
//				    course.addColumn("PUBUKPRN");
//				    
//				    ResultSet results = SearchCourseImport.ReadFromDB1(textField.getText());
//				    
//				    while (results.next()) {
//				        String name = results.getString("TITLE");
//				        String name1 = results.getString("CourseID");
//				        String name2 = results.getString("LEGAL_NAME");
//				        String name3 = results.getString("PUBUKPRN");
//
//				        // 🛠️ Fix: Add all values in a **single row**
//				        String[] row = {name, name1, name2, name3}; 
//				        course.addRow(row);
//				    }
//				    
//				    table.setModel(course);
					
//					ResultSet results = DashboardImport.ReadFromDB(textField.getText());
//					
//					course.addColumn("Course Name");
//					
//					while (results.next())
//					{
//						String name = results.getString("TITLE");
//						System.out.println(name);
//						
//						String[] row = {name};
//						
//						course.addRow(row);
//					}
//					
//					ResultSet results1 = DashboardImport.ReadFromDB(textField.getText());
//					
//					course.addColumn("Course ID");
//					
//					while (results1.next())
//					{
//						String name1 = results1.getString("CourseID");
//						System.out.println(name1);
//						
//						String[] row1 = {name1};
//						
//						course.addRow(row1);
//					}
//					
//					ResultSet results2 = DashboardImport.ReadFromDB(textField.getText());
//					
//					course.addColumn("University Name");
//					
//					while (results2.next())
//					{
//						String name2 = results2.getString("LEGAL_NAME");
//						System.out.println(name2);
//						
//						String[] row2 = {name2};
//						
//						course.addRow(row2);
//					}
//					
//					ResultSet results3 = DashboardImport.ReadFromDB(textField.getText());
//					
//					course.addColumn("PUBUKPRN");
//					
//					while (results3.next())
//					{
//						String name2 = results3.getString("PUBUKPRN");
//						System.out.println(name2);
//						
//						String[] row2 = {name2};
//						
//						course.addRow(row2);
//					}
//					 
//					table.setModel(course);
					
//				} catch (SQLException e1) {
//				    e1.printStackTrace();
//				}
//			}
//		});

                } catch (SQLException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error retrieving data from the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnNewButton.setBounds(174, 95, 166, 36);
		getContentPane().add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(344, 95, 909, 36);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 138, 1233, 496);
		getContentPane().add(scrollPane);
		
		table = new JTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				JTable copy = (JTable)e.getSource();
				
				int row = copy.getSelectedRow();
				int col = copy.getSelectedColumn();
				String val = copy.getModel().getValueAt(row, col).toString();
				System.out.println(row + " " + col + " " + val);
		        
				Course uni = new Course(val);
				uni.setVisible(true);
			}
		});
		scrollPane.setViewportView(table);

	}

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
