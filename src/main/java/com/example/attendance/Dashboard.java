package com.example.attendance;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.Font;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.file.Paths;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
//
//public class Dashboard extends JFrame {
//
//	private static final long serialVersionUID = 1L;
//	private JTextField txtUniversityNames;
//	private JTextField txtTotalPresent;
//	private JTextField txtTotalAbsent;
//	private JTextField txtTotalLate;
//
//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					Dashboard frame = new Dashboard();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
//
//	/**
//	 * Create the frame.
//	 */
//	public Dashboard() {
//		setBounds(100, 100, 1297, 840);
//		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		getContentPane().setLayout(null);
//		
//		txtUniversityNames = new JTextField();
//		txtUniversityNames.setText("   Welcome to the Attendance Dashboard");
//		txtUniversityNames.setFont(new Font("Tahoma", Font.BOLD, 23));
//		txtUniversityNames.setColumns(10);
//		txtUniversityNames.setBounds(362, 21, 516, 51);
//		getContentPane().add(txtUniversityNames);
//		
//		JButton btnNewButton_1 = new JButton("Search Course");
//		btnNewButton_1.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				SearchCourse course = new SearchCourse();
//				course.setVisible(true);
//			}
//		});
//		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
//		btnNewButton_1.setBounds(10, 153, 148, 41);
//		getContentPane().add(btnNewButton_1);
//		
//		JButton btnNewButton_1_1 = new JButton("Search Student");
//		btnNewButton_1_1.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				Searchstud stud = new Searchstud();
//				stud.setVisible(true);
//			}
//		});
//		btnNewButton_1_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
//		btnNewButton_1_1.setBounds(10, 198, 148, 41);
//		getContentPane().add(btnNewButton_1_1);
//		
//		JButton btnNewButton_1_1_1_1 = new JButton("Logout");
//		btnNewButton_1_1_1_1.setForeground(Color.BLACK);
//		btnNewButton_1_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
//		btnNewButton_1_1_1_1.setBackground(Color.RED);
//		btnNewButton_1_1_1_1.setBounds(10, 597, 148, 41);
//		getContentPane().add(btnNewButton_1_1_1_1);
//		
//		txtTotalPresent = new JTextField();
//		txtTotalPresent.setFont(new Font("Tahoma", Font.PLAIN, 13));
//		txtTotalPresent.setText("Total Present:");
//		txtTotalPresent.setBounds(330, 108, 186, 41);
//		getContentPane().add(txtTotalPresent);
//		txtTotalPresent.setColumns(10);
//		
//		txtTotalAbsent = new JTextField();
//		txtTotalAbsent.setText("Total Absent:");
//		txtTotalAbsent.setFont(new Font("Tahoma", Font.PLAIN, 13));
//		txtTotalAbsent.setColumns(10);
//		txtTotalAbsent.setBounds(549, 108, 186, 41);
//		getContentPane().add(txtTotalAbsent);
//		
//		txtTotalLate = new JTextField();
//		txtTotalLate.setText("Total Late:");
//		txtTotalLate.setFont(new Font("Tahoma", Font.PLAIN, 13));
//		txtTotalLate.setColumns(10);
//		txtTotalLate.setBounds(775, 108, 186, 41);
//		getContentPane().add(txtTotalLate);
//		
//		JPanel chartPanel = createChartPanel();
//        chartPanel.setBounds(330, 180, 631, 400);
//        getContentPane().add(chartPanel);
//
//	}
//	
//	private JPanel createChartPanel() {
//        DefaultPieDataset dataset = getDatasetFromSQL();
//
//        JFreeChart chart = ChartFactory.createPieChart(
//            "Attendance Statistics",  // Chart Title
//            dataset,                  // Dataset
//            true,                     // Include Legend
//            true,
//            false
//        );
//
//        return new ChartPanel(chart);
//    }
//
//    // Method to Retrieve Data from SQL Database
//    private DefaultPieDataset getDatasetFromSQL() {
//        DefaultPieDataset dataset = new DefaultPieDataset();
//        String driver = "jdbc:sqlite";
//        String db = "C:\\Users\\mkv_2\\OneDrive\\Desktop\\Aishka\\Uni\\Group Project\\Sem 2\\UMS Data.db"; 
//        String url = driver + ":" + db;
//
//        try (Connection conn = DriverManager.getConnection(url)) {
//            String sql = "SELECT " +
//                         "SUM(CASE WHEN Attendance = 'Present' THEN 1 ELSE 0 END) AS total_present, " +
//                         "SUM(CASE WHEN Attendance = 'Absent' THEN 1 ELSE 0 END) AS total_absent, " +
//                         "SUM(CASE WHEN Attendance = 'Late' THEN 1 ELSE 0 END) AS total_late " +
//                         "FROM ATTENDANCE";
//
//            PreparedStatement stmnt = conn.prepareStatement(sql);
//            ResultSet rs = stmnt.executeQuery();
//
//            if (rs.next()) {
//                dataset.setValue("Present", rs.getInt("total_present"));
//                dataset.setValue("Absent", rs.getInt("total_absent"));
//                dataset.setValue("Late", rs.getInt("total_late"));
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return dataset;
//    }
//}

//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.image.BufferedImage;
//import javax.imageio.ImageIO;
//import java.io.File;
//import java.io.IOException;
//import java.sql.*;
//import java.util.ArrayList;
//
//import org.jfree.chart.*;
//import org.jfree.chart.plot.*;
//import org.jfree.chart.axis.*;
//import org.jfree.data.category.*;
//import org.jfree.data.general.DefaultPieDataset;
//
//public class Dashboard extends JFrame {
//
//    private JComboBox<String> courseDropdown;
//    private JTextField startDateField;
//    private JTextField endDateField;
//    private JPanel chartPanel;
//
//    public Dashboard() {
//        setBounds(100, 100, 800, 600);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLayout(null);
//
//        JLabel lblCourse = new JLabel("Select Course:");
//        lblCourse.setBounds(30, 20, 100, 25);
//        add(lblCourse);
//
//        courseDropdown = new JComboBox<>(loadCoursesFromDB());
//        courseDropdown.setBounds(150, 20, 200, 25);
//        courseDropdown.addItemListener(e -> {
//            if (e.getStateChange() == ItemEvent.SELECTED) {
//                updateChart();
//            }
//        });
//        add(courseDropdown);
//
//        JButton btnExport = new JButton("Export Chart");
//        btnExport.setBounds(370, 20, 120, 30);
//        btnExport.addActionListener(e -> exportChart());
//        add(btnExport);
//
//        chartPanel = new JPanel();
//        chartPanel.setBounds(30, 60, 700, 500);
//        add(chartPanel);
//
//        updateChart();
//    }
//
//    private String[] loadCoursesFromDB() {
//    	ArrayList<String> courses = new ArrayList<>();
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\mkv_2\\OneDrive\\Desktop\\Aishka\\Uni\\Group Project\\Sem 2\\UMS Data.db")) {
//            String sql = "SELECT TITLE FROM COURSE";
//            PreparedStatement stmnt = conn.prepareStatement(sql);
//            ResultSet rs = stmnt.executeQuery();
//
//            courses.add("All Courses");
//            while (rs.next()) {
//                courses.add(rs.getString("TITLE"));
//            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
//
//        
////            DefaultListModel<String> model = new DefaultListModel<>();
////            model.addElement("All Courses");
////            while (rs.next()) {
////                model.addElement(rs.getString("course_name"));
////            }
//            //return model.toArray(new String[0]);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        
//        if (courses.isEmpty()) {
//            return new String[]{"No Data"};
//        }
//        
//        return courses.toArray(new String[0]);
//        
//        //return new String[]{"No Data"};
//    }
//
//    private void updateChart() {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        String selectedCourse = (String) courseDropdown.getSelectedItem();
//
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\mkv_2\\OneDrive\\Desktop\\Aishka\\Uni\\Group Project\\Sem 2\\UMS Data.db")) {
//            //String sql = "SELECT Attendance, COUNT(*) AS count FROM ATTENDANCE WHERE 1=1";
//        	String sql = "SELECT a.Attendance, COUNT(*) AS count " +
//                    "FROM ATTENDANCE a " +
//                    "JOIN COURSE c ON a.course_id = c.course_id " +
//                    "WHERE 1=1";
//        	
//        	if (!selectedCourse.equals("All Courses")) {
//                sql += " AND c.course_name = ?";
//            }
////        	if (!selectedCourse.equals("All Courses")) {
////                sql += " AND course_name = ?";
////            }
//            
//        	sql += " GROUP BY a.attendance_date ORDER BY a.attendance_date";
//        	//sql += " GROUP BY attendance_date";
//
//            PreparedStatement stmnt = conn.prepareStatement(sql);
//            if (!selectedCourse.equals("All Courses")) {
//                stmnt.setString(1, selectedCourse);
//            }
//
//            ResultSet rs = stmnt.executeQuery();
//            while (rs.next()) {
//                dataset.addValue(rs.getInt("count"), "Attendance", rs.getString("attendance_date"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        if (dataset.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this, "No attendance data found for the selected course.");
//        } else {
//            JFreeChart chart = ChartFactory.createLineChart(
//                    "Attendance Trends",
//                    "Date",
//                    "Count",
//                    dataset
//            );
////        JFreeChart chart = ChartFactory.createLineChart(
////                "Attendance Trends",
////                "Date",
////                "Count",
////                dataset
////        );
//
//        chartPanel.removeAll();
//        chartPanel.add(new ChartPanel(chart));
//        chartPanel.revalidate();
//        chartPanel.repaint();
//    }
//}
//    
//    private void exportChart() {
//        try {
//            BufferedImage image = new BufferedImage(chartPanel.getWidth(), chartPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
//            chartPanel.paint(image.getGraphics());
//            ImageIO.write(image, "PNG", new File("Attendance_Trends.png"));
//            JOptionPane.showMessageDialog(this, "Chart exported successfully as 'Attendance_Trends.png'");
//        } catch (IOException ex) {
//            JOptionPane.showMessageDialog(this, "Error exporting chart: " + ex.getMessage());
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            Dashboard frame = new Dashboard();
//            frame.setVisible(true);
//        });
//    }
//}

//import java.awt.image.BufferedImage;
//import java.io.IOException;
//
//import javax.swing.JOptionPane;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.JFreeChart;
//
//public class Dashboard extends JFrame {
//
//    private JComboBox<String> courseDropdown;
//    private JTextField startDateField;
//    private JTextField endDateField;
//    private JPanel chartPanel;
//    private JTextField txtUniversityNames;
//    private JTextField txtTotalPresent;
//    private JTextField txtTotalAbsent;
//    private JTextField txtTotalLate;
//
//    public Dashboard() {
//        setBounds(100, 100, 1297, 840); // Adjusted JFrame size to accommodate all components
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        getContentPane().setLayout(null);
//
//        // University Title
//        txtUniversityNames = new JTextField();
//        txtUniversityNames.setText("   Welcome to the Attendance Dashboard");
//        txtUniversityNames.setFont(new Font("Tahoma", Font.BOLD, 23));
//        txtUniversityNames.setColumns(10);
//        txtUniversityNames.setBounds(362, 21, 516, 51);
//        getContentPane().add(txtUniversityNames);
//
//        // Search Course Button
//        JButton btnNewButton_1 = new JButton("Search Course");
//        btnNewButton_1.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                SearchCourse course = new SearchCourse();
//                course.setVisible(true);
//            }
//        });
//        btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
//        btnNewButton_1.setBounds(10, 153, 148, 41);
//        getContentPane().add(btnNewButton_1);
//
//        // Search Student Button
//        JButton btnNewButton_1_1 = new JButton("Search Student");
//        btnNewButton_1_1.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                Searchstud stud = new Searchstud();
//                stud.setVisible(true);
//            }
//        });
//        btnNewButton_1_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
//        btnNewButton_1_1.setBounds(10, 198, 148, 41);
//        getContentPane().add(btnNewButton_1_1);
//
//        // Logout Button
//        JButton btnNewButton_1_1_1_1 = new JButton("Logout");
//        btnNewButton_1_1_1_1.setForeground(Color.BLACK);
//        btnNewButton_1_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
//        btnNewButton_1_1_1_1.setBackground(Color.RED);
//        btnNewButton_1_1_1_1.setBounds(10, 597, 148, 41);
//        getContentPane().add(btnNewButton_1_1_1_1);
//
//        // Total Present TextField
//        txtTotalPresent = new JTextField();
//        txtTotalPresent.setFont(new Font("Tahoma", Font.PLAIN, 13));
//        txtTotalPresent.setText("Total Present:");
//        txtTotalPresent.setBounds(330, 108, 186, 41);
//        getContentPane().add(txtTotalPresent);
//        txtTotalPresent.setColumns(10);
//
//        // Total Absent TextField
//        txtTotalAbsent = new JTextField();
//        txtTotalAbsent.setText("Total Absent:");
//        txtTotalAbsent.setFont(new Font("Tahoma", Font.PLAIN, 13));
//        txtTotalAbsent.setColumns(10);
//        txtTotalAbsent.setBounds(549, 108, 186, 41);
//        getContentPane().add(txtTotalAbsent);
//
//        // Total Late TextField
//        txtTotalLate = new JTextField();
//        txtTotalLate.setText("Total Late:");
//        txtTotalLate.setFont(new Font("Tahoma", Font.PLAIN, 13));
//        txtTotalLate.setColumns(10);
//        txtTotalLate.setBounds(775, 108, 186, 41);
//        getContentPane().add(txtTotalLate);
//
//        // Chart Panel (Shifted down to make space for buttons and text fields)
//        chartPanel = new JPanel();
//        chartPanel.setBounds(266, 180, 824, 500);  // Adjusted bounds to start below the other components
//        getContentPane().add(chartPanel);
//
//        // Updating the chart with the available data
//
////import javax.swing.SwingUtilities;
//
//        updateChart();
//    }
//
//    private void updateChart() {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        String selectedCourse = (String) courseDropdown.getSelectedItem();
//
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\mkv_2\\OneDrive\\Desktop\\Aishka\\Uni\\Group Project\\Sem 2\\UMS Data.db")) {
//            String sql = "SELECT a.Attendance, COUNT(*) AS count " +
//                    "FROM ATTENDANCE a " +
//                    "JOIN COURSE c ON a.CourseID = c.CourseID " +
//                    "WHERE 1=1";
//
//            if (!selectedCourse.equals("All Courses")) {
//                sql += " AND c.TITLE = ?";
//            }
//
//            sql += " GROUP BY a.SessionDate ORDER BY a.SessionDate";
//
//            PreparedStatement stmnt = conn.prepareStatement(sql);
//            if (!selectedCourse.equals("All Courses")) {
//                stmnt.setString(1, selectedCourse);
//            }
//
//            ResultSet rs = stmnt.executeQuery();
//            while (rs.next()) {
//                dataset.addValue(rs.getInt("count"), "Attendance", rs.getString("SessionDate"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        if (dataset.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this, "No attendance data found for the selected course.");
//        } else {
//            JFreeChart chart = ChartFactory.createLineChart(
//                    "Attendance Trends",
//                    "Date",
//                    "Count",
//                    dataset
//            );
//
//            chartPanel.removeAll();
//            chartPanel.add(new ChartPanel(chart));
//            chartPanel.revalidate();
//            chartPanel.repaint();
//        }
//    }
//
//    private void exportChart() {
//        try {
//            BufferedImage image = new BufferedImage(chartPanel.getWidth(), chartPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
//            chartPanel.paint(image.getGraphics());
//            ImageIO.write(image, "PNG", new File("Attendance_Trends.png"));
//            JOptionPane.showMessageDialog(this, "Chart exported successfully as 'Attendance_Trends.png'");
//        } catch (IOException ex) {
//            JOptionPane.showMessageDialog(this, "Error exporting chart: " + ex.getMessage());
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            Dashboard frame = new Dashboard();
//            frame.setVisible(true);
//        });
//    }
//}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.plot.*;

//public class Dashboard extends JFrame {
//
//    private JComboBox<String> courseDropdown;
//    private JTextField startDateField;
//    private JTextField endDateField;
//    private JPanel chartPanel;
//    private JTextField txtTotalPresent;
//    private JTextField txtTotalAbsent;
//    private JTextField txtTotalLate;
//
//    public Dashboard() {
//        setBounds(100, 100, 1297, 840);  // Adjust size to accommodate all components
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        getContentPane().setLayout(null);
//
//        // Welcome Text
//        JTextField txtUniversityNames = new JTextField();
//        txtUniversityNames.setText("   Welcome to the Attendance Dashboard");
//        txtUniversityNames.setFont(new Font("Tahoma", Font.BOLD, 23));
//        txtUniversityNames.setColumns(10);
//        txtUniversityNames.setBounds(362, 21, 516, 51);
//        getContentPane().add(txtUniversityNames);
//
//        // Dropdown for Course Selection
//        JLabel lblCourse = new JLabel("Select Course:");
//        lblCourse.setBounds(30, 20, 100, 25);
//        getContentPane().add(lblCourse);
//
//        courseDropdown = new JComboBox<>(loadCoursesFromDB());
//        courseDropdown.setBounds(150, 20, 200, 25);
//        courseDropdown.addItemListener(e -> {
//            if (e.getStateChange() == ItemEvent.SELECTED) {
//                updateChart();
//            }
//        });
//        getContentPane().add(courseDropdown);
//
//        // Export Button
//        JButton btnExport = new JButton("Export Chart");
//        btnExport.setBounds(370, 20, 120, 30);
//        btnExport.addActionListener(e -> exportChart());
//        getContentPane().add(btnExport);
//
//        // Total Present, Absent, and Late Textboxes
//        txtTotalPresent = new JTextField();
//        txtTotalPresent.setFont(new Font("Tahoma", Font.PLAIN, 13));
//        txtTotalPresent.setText("Total Present:");
//        txtTotalPresent.setBounds(330, 108, 186, 41);
//        getContentPane().add(txtTotalPresent);
//        txtTotalPresent.setColumns(10);
//
//        txtTotalAbsent = new JTextField();
//        txtTotalAbsent.setText("Total Absent:");
//        txtTotalAbsent.setFont(new Font("Tahoma", Font.PLAIN, 13));
//        txtTotalAbsent.setColumns(10);
//        txtTotalAbsent.setBounds(549, 108, 186, 41);
//        getContentPane().add(txtTotalAbsent);
//
//        txtTotalLate = new JTextField();
//        txtTotalLate.setText("Total Late:");
//        txtTotalLate.setFont(new Font("Tahoma", Font.PLAIN, 13));
//        txtTotalLate.setColumns(10);
//        txtTotalLate.setBounds(775, 108, 186, 41);
//        getContentPane().add(txtTotalLate);
//
//        // Chart Panel to display the chart
//        chartPanel = new JPanel();
//        chartPanel.setBounds(199, 160, 1031, 500);  // Adjusted for space
//        getContentPane().add(chartPanel);
//        
//        JButton btnNewButton_1_1 = new JButton("Search Student");
//        btnNewButton_1_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
//        btnNewButton_1_1.setBounds(22, 244, 148, 41);
//        getContentPane().add(btnNewButton_1_1);
//        
//        JButton btnNewButton_1_1_1 = new JButton("Search Course");
//        btnNewButton_1_1_1.addActionListener(new ActionListener() {
//        	public void actionPerformed(ActionEvent e) {
//        	}
//        });
//        btnNewButton_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
//        btnNewButton_1_1_1.setBounds(22, 192, 148, 41);
//        getContentPane().add(btnNewButton_1_1_1);
//
//        updateChart(); // Ensure chart is populated on startup
//    }
//
//    private String[] loadCoursesFromDB() {
//        ArrayList<String> courses = new ArrayList<>();
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\mkv_2\\OneDrive\\Desktop\\Aishka\\Uni\\Group Project\\Sem 2\\UMS Data.db")) {
//            String sql = "SELECT TITLE FROM COURSE";
//            PreparedStatement stmnt = conn.prepareStatement(sql);
//            ResultSet rs = stmnt.executeQuery();
//
//            courses.add("All Courses");
//            while (rs.next()) {
//                courses.add(rs.getString("TITLE"));
//            }
//            System.out.println("Course selected from TITLE!");
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        if (courses.isEmpty()) {
//            return new String[]{"No Data"};
//        }
//
//        return courses.toArray(new String[0]);
//    }
//
//    private void updateChart() {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        String selectedCourse = (String) courseDropdown.getSelectedItem();
//
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\mkv_2\\OneDrive\\Desktop\\Aishka\\Uni\\Group Project\\Sem 2\\UMS Data.db")) {
//            String sql = "SELECT a.Attendance, COUNT(*) AS count " +
//                    "FROM ATTENDANCE a " +
//                    "JOIN COURSE c ON a.CourseID = c.CourseID " +
//                    "WHERE 1=1 " ;
//            
//            if (!selectedCourse.equals("All Courses")) {
//                sql += " AND c.TITLE = ?";
//            }
//            
//            sql += " GROUP BY a.SessionDate ORDER BY a.SessionDate";
//
//            PreparedStatement stmnt = conn.prepareStatement(sql);
//            if (!selectedCourse.equals("All Courses")) {
//                stmnt.setString(1, selectedCourse);
//            }
//
//            ResultSet rs = stmnt.executeQuery();
//            while (rs.next()) {
//                dataset.addValue(rs.getInt("count"), "Attendance", rs.getString("SessionDate"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        if (dataset.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this, "No attendance data found for the selected course.");
//        } else {
//            JFreeChart chart = ChartFactory.createLineChart(
//                    "Attendance Trends",
//                    "Date",
//                    "Count",
//                    dataset
//            );
//
//            chartPanel.removeAll();
//            chartPanel.add(new ChartPanel(chart));
//            chartPanel.revalidate();
//            chartPanel.repaint();
//        }
//
//        // You can implement logic here to update the "Total Present", "Total Absent", and "Total Late" textboxes
//        updateAttendanceStats(selectedCourse);
//    }
//
//    private void updateAttendanceStats(String selectedCourse) {
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\mkv_2\\OneDrive\\Desktop\\Aishka\\Uni\\Group Project\\Sem 2\\UMS Data.db")) {
//            String sql = "SELECT Attendance, COUNT(*) AS count " +
//                    "FROM ATTENDANCE a " +
//                    "JOIN COURSE c ON a.CourseID = c.CourseID " +
//                    "WHERE 1=1";
//            
//            if (!selectedCourse.equals("All Courses")) {
//                sql += " AND c.TITLE = ?";
//            }
//
//            sql += " GROUP BY a.Attendance";
//
//            PreparedStatement stmnt = conn.prepareStatement(sql);
//            if (!selectedCourse.equals("All Courses")) {
//                stmnt.setString(1, selectedCourse);
//            }
//
//            ResultSet rs = stmnt.executeQuery();
//
//            int totalPresent = 0;
//            int totalAbsent = 0;
//            int totalLate = 0;
//
//            while (rs.next()) {
//                String attendanceStatus = rs.getString("Attendance");
//                int count = rs.getInt("count");
//
//                if ("Present".equals(attendanceStatus)) {
//                    totalPresent = count;
//                } else if ("Absent".equals(attendanceStatus)) {
//                    totalAbsent = count;
//                } else if ("Late".equals(attendanceStatus)) {
//                    totalLate = count;
//                }
//            }
//
//            // Update the text fields
//            txtTotalPresent.setText("Total Present: " + totalPresent);
//            txtTotalAbsent.setText("Total Absent: " + totalAbsent);
//            txtTotalLate.setText("Total Late: " + totalLate);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void exportChart() {
//        try {
//            BufferedImage image = new BufferedImage(chartPanel.getWidth(), chartPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
//            chartPanel.paint(image.getGraphics());
//            ImageIO.write(image, "PNG", new File("Attendance_Trends.png"));
//            JOptionPane.showMessageDialog(this, "Chart exported successfully as 'Attendance_Trends.png'");
//        } catch (IOException ex) {
//            JOptionPane.showMessageDialog(this, "Error exporting chart: " + ex.getMessage());
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            Dashboard frame = new Dashboard();
//            frame.setVisible(true);
//        });
//    }
//}


public class Dashboard extends JFrame {

    private JComboBox<String> courseDropdown;
    private JTextField startDateField;
    private JTextField endDateField;
    private JPanel chartPanel;
    private JTextField txtUniversityNames;
    private JTextField txtTotalPresent;
    private JTextField txtTotalAbsent;
    private JTextField txtTotalLate;

    public Dashboard() {
        setBounds(100, 100, 1300, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        // Title label
        txtUniversityNames = new JTextField();
        txtUniversityNames.setText("   Welcome to the Attendance Dashboard");
        txtUniversityNames.setFont(new Font("Tahoma", Font.BOLD, 23));
        txtUniversityNames.setColumns(10);
        txtUniversityNames.setBounds(362, 21, 516, 51);
        getContentPane().add(txtUniversityNames);

        // Course selection dropdown
        JLabel lblCourse = new JLabel("Select Course Name:");
        lblCourse.setBounds(20, 117, 128, 25);
        getContentPane().add(lblCourse);

        courseDropdown = new JComboBox<>(loadCoursesFromDB());
        courseDropdown.setBounds(148, 117, 200, 25);
        courseDropdown.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateChart();
                updateAttendanceCounts();
            }
        });
        getContentPane().add(courseDropdown);

        // Export chart button
        JButton btnExport = new JButton("Export Chart");
        btnExport.setForeground(new Color(0, 64, 128));
        btnExport.setBounds(1049, 114, 120, 30);
        btnExport.addActionListener(e -> exportChart());
        getContentPane().add(btnExport);

        // Total present, absent, and late fields
        txtTotalPresent = new JTextField();
        txtTotalPresent.setFont(new Font("Tahoma", Font.PLAIN, 13));
        txtTotalPresent.setText("Total Present:");
        txtTotalPresent.setBounds(370, 108, 186, 41);
        getContentPane().add(txtTotalPresent);

        txtTotalAbsent = new JTextField();
        txtTotalAbsent.setText("Total Absent:");
        txtTotalAbsent.setFont(new Font("Tahoma", Font.PLAIN, 13));
        txtTotalAbsent.setColumns(10);
        txtTotalAbsent.setBounds(589, 108, 186, 41);
        getContentPane().add(txtTotalAbsent);

        txtTotalLate = new JTextField();
        txtTotalLate.setText("Total Late:");
        txtTotalLate.setFont(new Font("Tahoma", Font.PLAIN, 13));
        txtTotalLate.setColumns(10);
        txtTotalLate.setBounds(808, 108, 186, 41);
        getContentPane().add(txtTotalLate);

        // Chart panel
        chartPanel = new JPanel();
        chartPanel.setBounds(188, 160, 1042, 500);
        getContentPane().add(chartPanel);
        
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(800, 40)); // Increase height
        
        // Custom Font for Larger Text
        Font menuFont = new Font("Arial", Font.BOLD, 22);
        
        // "Actions" Menu
        JMenu actionsMenu = new JMenu("Menu");
        menuBar.add(actionsMenu);
        
        // Menu Items
        JMenuItem searchCourseItem = new JMenuItem("Course Attendance");
        JMenuItem searchStudentItem = new JMenuItem("Student Attendance");
        JMenuItem logoutItem = new JMenuItem("Logout");
        
        actionsMenu.add(searchCourseItem);
        actionsMenu.add(searchStudentItem);
        actionsMenu.addSeparator(); // Optional for spacing
        actionsMenu.add(logoutItem);        
        
        // Action Listeners
        searchStudentItem.addActionListener(e -> {
            System.out.println("Student Attendance Clicked");
            new Searchstud().setVisible(true);
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
        // Update chart initially
        updateChart();
    }

    private String[] loadCoursesFromDB() {
    	
    	System.out.println("Hi!");
    	ArrayList<String> courses = new ArrayList<>();
        //C:\Users\mkv_2\OneDrive\Desktop\Aishka\Uni\Group Project\Sem 2\
      try (Connection conn = DriverManager.getConnection("jdbc:sqlite:UMS Data.db")) {
          String sql = "SELECT TITLE FROM COURSE";
          PreparedStatement stmnt = conn.prepareStatement(sql);
          ResultSet rs = stmnt.executeQuery();
          
          System.out.println("Made connection to DB");

          courses.add("All Courses");
          while (rs.next()) {
              courses.add(rs.getString("TITLE"));
          }
          //System.out.println("Course Name selected from COURSE!");

      } catch (SQLException e) {
          e.printStackTrace();
      }

      if (courses.isEmpty()) {
          return new String[]{"No Data"};
      }

      return courses.toArray(new String[0]);
  }
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\mkv_2\\OneDrive\\Desktop\\Aishka\\Uni\\Group Project\\Sem 2\\UMS Data.db")) {
//            String sql = "SELECT course_name FROM COURSE";
//            PreparedStatement stmnt = conn.prepareStatement(sql);
//            ResultSet rs = stmnt.executeQuery();
//
//            DefaultListModel<String> model = new DefaultListModel<>();
//            model.addElement("All Courses");
//            while (rs.next()) {
//                model.addElement(rs.getString("course_name"));
//            }
//            if (courses.isEmpty()) {
//              return new String[]{"No Data"};
//          }
//           //return model.toArray(new String[0]);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return new String[]{"No Data"};
//    }

    
//    private void updateChart() {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        String selectedCourse = (String) courseDropdown.getSelectedItem();
//
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\mkv_2\\OneDrive\\Desktop\\Aishka\\Uni\\Group Project\\Sem 2\\UMS Data.db")) {
//            String sql = "SELECT Attendance, TITLE, COUNT(*) AS count FROM ATTENDANCE a " +
//                         "JOIN COURSE c ON a.CourseID = c.CourseID " +
//                         "WHERE 1=1";
//
//            // Filter by selected course
//            if (!selectedCourse.equals("All Courses")) {
//                sql += " AND c.TITLE = ?";
//            }
//
//            sql += " GROUP BY Attendance, TITLE";
//
//            PreparedStatement stmnt = conn.prepareStatement(sql);
//            if (!selectedCourse.equals("All Courses")) {
//                stmnt.setString(1, selectedCourse);
//            }
//
//            ResultSet rs = stmnt.executeQuery();
//            while (rs.next()) {
//                String attendanceDate = rs.getString("Attendance");
//                String courseName = rs.getString("TITLE");
//                int attendanceCount = rs.getInt("count");
//
//                // Add data to the dataset, grouped by course and date
//                dataset.addValue(attendanceCount, courseName, attendanceDate);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        // Create a bar chart with the dataset
//        JFreeChart chart = ChartFactory.createBarChart(
//                "Attendance Trends",        // Chart title
//                "Date",                     // X-axis label
//                "Count",                    // Y-axis label
//                dataset,                    // Dataset
//                PlotOrientation.VERTICAL,   // Orientation (vertical bar chart)
//                true,                       // Include legend
//                true,                       // Tooltips
//                false                       // URLs not required
//        );
//
//        // Add the chart to the panel
//        chartPanel.removeAll();
//        chartPanel.add(new ChartPanel(chart));
//        chartPanel.revalidate();
//        chartPanel.repaint();
//    }
    
//    private void updateChart() {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        String selectedCourse = (String) courseDropdown.getSelectedItem();
//
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\mkv_2\\OneDrive\\Desktop\\Aishka\\Uni\\Group Project\\Sem 2\\UMS Data.db")) {
//            //String sql = "SELECT Attendance, CourseID, COUNT(*) AS count FROM ATTENDANCE WHERE 1=1";
//        	
////        	String sql = "SELECT Attendance, CourseID, COUNT(*) AS count FROM ATTENDANCE a " +
////                    "JOIN COURSE c ON a.CourseID = c.CourseID " +
////                    "WHERE 1=1";
//        	if (!selectedCourse.equals("All Courses")) {
//                sql += " AND a.CourseID = ?";
//            }
//            sql += " GROUP BY Attendance, a.CourseID";
//
//            PreparedStatement stmnt = conn.prepareStatement(sql);
//            if (!selectedCourse.equals("All Courses")) {
//                stmnt.setString(1, selectedCourse);
//            }
//
//            ResultSet rs = stmnt.executeQuery();
//            while (rs.next()) {
//                String attendanceDate = rs.getString("attendance_date");
//                String courseID = rs.getString("course_id");
//                int attendanceCount = rs.getInt("count");
//                
//             // Add data to the dataset, grouped by course ID and date
//                dataset.addValue(attendanceCount, courseID, attendanceDate);
//            }
////            while (rs.next()) {
////                dataset.addValue(rs.getInt("count"), "Attendance", rs.getString("Attendance"));
////            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        // Create a bar chart instead of a line chart
//        JFreeChart chart = ChartFactory.createBarChart(
//                "Attendance Trends",        // Chart title
//                "Date",                     // X-axis label
//                "Count",                    // Y-axis label
//                dataset,                    // Dataset
//                PlotOrientation.VERTICAL,   // Orientation (vertical bar chart)
//                true,                       // Include legend
//                true,                       // Tooltips
//                false                       // URLs not required
//        );
//
//        // Add the chart to the panel
//        chartPanel.removeAll();
//        chartPanel.add(new ChartPanel(chart));
//        chartPanel.revalidate();
//        chartPanel.repaint();
//    }
//    private void updateChart() {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        String selectedCourse = (String) courseDropdown.getSelectedItem();
//
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\mkv_2\\OneDrive\\Desktop\\Aishka\\Uni\\Group Project\\Sem 2\\UMS Data.db")) {
//            String sql = "SELECT Attendance, COUNT(*) AS count FROM ATTENDANCE WHERE 1=1";
//            if (!selectedCourse.equals("All Courses")) {
//                sql += " AND course_name = ?";
//            }
//            sql += " GROUP BY Attendance";
//
//            PreparedStatement stmnt = conn.prepareStatement(sql);
//            if (!selectedCourse.equals("All Courses")) {
//                stmnt.setString(1, selectedCourse);
//            }
//
//            ResultSet rs = stmnt.executeQuery();
//            while (rs.next()) {
//                dataset.addValue(rs.getInt("count"), "Attendance", rs.getString("Attendance"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        JFreeChart chart = ChartFactory.createLineChart(
//                "Attendance Trends",
//                "Date",
//                "Count",
//                dataset
//        );
//
//        chartPanel.removeAll();
//        chartPanel.add(new ChartPanel(chart));
//        chartPanel.revalidate();
//        chartPanel.repaint();
//    }
    
//    private void updateChart() {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        String selectedCourse = (String) courseDropdown.getSelectedItem();
//
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\mkv_2\\OneDrive\\Desktop\\Aishka\\Uni\\Group Project\\Sem 2\\UMS Data.db")) {
//            String sql = "SELECT Attendance, COUNT(*) AS count FROM ATTENDANCE WHERE 1=1";
//            if (!selectedCourse.equals("All Courses")) {
//                sql += " AND course_name = ?";
//            }
//            sql += " GROUP BY Attendance";
//
//            PreparedStatement stmnt = conn.prepareStatement(sql);
//            if (!selectedCourse.equals("All Courses")) {
//                stmnt.setString(1, selectedCourse);
//            }
//
//            ResultSet rs = stmnt.executeQuery();
//            while (rs.next()) {
//                dataset.addValue(rs.getInt("count"), "Attendance", rs.getString("Attendance"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        // Create a bar chart instead of a line chart
//        JFreeChart chart = ChartFactory.createBarChart(
//                "Attendance Trends",        // Chart title
//                "Date",                     // X-axis label
//                "Count",                    // Y-axis label
//                dataset,                    // Dataset
//                PlotOrientation.VERTICAL,   // Orientation (vertical bar chart)
//                true,                       // Include legend
//                true,                       // Tooltips
//                false                       // URLs not required
//        );
//
//        // Add the chart to the panel
//        chartPanel.removeAll();
//        chartPanel.add(new ChartPanel(chart));
//        chartPanel.revalidate();
//        chartPanel.repaint();
//    }

    private void updateChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String selectedCourse = (String) courseDropdown.getSelectedItem();
        //C:\Users\mkv_2\OneDrive\Desktop\Aishka\Uni\Group Project\Sem 2\
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:UMS Data.db")) {
            String sql = "SELECT c.TITLE, a.Attendance, COUNT(*) AS count " +
                         "FROM ATTENDANCE a " +
                         "JOIN COURSE c ON a.CourseID = c.CourseID " +
                         "WHERE 1=1";

            // Apply course filter if a specific course is selected
            if (!selectedCourse.equals("All Courses")) {
                sql += " AND c.TITLE = ?";
            }
            sql += " GROUP BY c.TITLE, a.Attendance";

            PreparedStatement stmnt = conn.prepareStatement(sql);
            if (!selectedCourse.equals("All Courses")) {
                stmnt.setString(1, selectedCourse);
            }

            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) {
                String courseTitle = rs.getString("TITLE");
                String attendanceStatus = rs.getString("Attendance");
                int attendanceCount = rs.getInt("count");

                // Add data to the dataset, grouped by course title and attendance status
                dataset.addValue(attendanceCount, attendanceStatus, courseTitle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create a bar chart instead of a line chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Attendance Trends by Course",   // Chart title
                "Course Title",                  // X-axis label
                "Count",                         // Y-axis label
                dataset,                         // Dataset
                PlotOrientation.VERTICAL,        // Orientation (vertical bar chart)
                true,                            // Include legend
                true,                            // Tooltips
                false                            // URLs not required
        );
        
        System.out.println("Chart successfully uploaded!");

        // Add the chart to the panel
        chartPanel.removeAll();
        chartPanel.add(new ChartPanel(chart));
        chartPanel.revalidate();
        chartPanel.repaint();
    }
    
    private void exportChart() {
        try {
            BufferedImage image = new BufferedImage(chartPanel.getWidth(), chartPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
            chartPanel.paint(image.getGraphics());
            ImageIO.write(image, "PNG", new File("Attendance_Trends.png"));
            JOptionPane.showMessageDialog(this, "Chart exported successfully as 'Attendance_Trends.png'");
            System.out.println("Chart exported successfully as 'Attendance_Trends.png'");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error exporting chart: " + ex.getMessage());
        }
    }
    
    private void updateAttendanceCounts() {
        String selectedCourse = (String) courseDropdown.getSelectedItem();
        String sql = "SELECT Attendance, COUNT(*) AS count FROM ATTENDANCE WHERE 1=1";

        // Apply course filter if a specific course is selected
        if (!selectedCourse.equals("All Courses")) {
            sql += " AND CourseID = (SELECT CourseID FROM COURSE WHERE TITLE = ?)";
        }
        sql += " GROUP BY Attendance";

        //C:\Users\mkv_2\OneDrive\Desktop\Aishka\Uni\Group Project\Sem 2\
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:UMS Data.db")) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (!selectedCourse.equals("All Courses")) {
                stmt.setString(1, selectedCourse);
            }
            ResultSet rs = stmt.executeQuery();

            int totalPresent = 0;
            int totalAbsent = 0;
            int totalLate = 0;

            while (rs.next()) {
                String status = rs.getString("Attendance");
                int count = rs.getInt("count");

                if ("Yes".equals(status)) {
                    totalPresent = count;
                } else if ("Absent".equals(status)) {
                    totalAbsent = count;
                } else if ("Late".equals(status)) {
                    totalLate = count;
                }
            }

            // Update the textboxes
            txtTotalPresent.setText("Total Present: " + totalPresent);
            txtTotalAbsent.setText("Total Absent: " + totalAbsent);
            txtTotalLate.setText("Total Late: " + totalLate);
            
            System.out.println("Successfully displayed the number of Total Present, Late and Absent students!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
//    private void searchStudent() {
//        JOptionPane.showMessageDialog(this, "Search Student Clicked");
//    }
//
//    private void searchCourse() {
//        JOptionPane.showMessageDialog(this, "Search Course Clicked");
//    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dashboard frame = new Dashboard();
            frame.setVisible(true);
        });
    }
}