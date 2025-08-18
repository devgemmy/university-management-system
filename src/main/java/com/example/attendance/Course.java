package com.example.attendance;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.JScrollPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class Course extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private String courseID;
	private JPanel chartPanel;
	String driver = "jdbc:sqlite";
	//C:\Users\mkv_2\OneDrive\Desktop\Aishka\Uni\Group Project\Sem 2\
	String db = "UMS Data.db";
	String url = driver + ":" + db;
	//Connection c = DriverManager.getConnection(url);
	
	/**
	 * Create the frame.
	 * @param val 
	 */
	public Course(String val) {
		this.courseID = val;
		setBounds(100, 100, 1462, 737);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		chartPanel = new JPanel(); // Empty panel to start with
		chartPanel.setLayout(new BorderLayout());  // Ensures ChartPanel expands properly
		chartPanel.setBounds(264, 440, 809, 196);
		getContentPane().add(chartPanel);
		
		chartPanel.setVisible(true);
		
		JLabel lblNewLabel = new JLabel("Welcome to");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 23));
		lblNewLabel.setBounds(22, 11, 146, 48);
		getContentPane().add(lblNewLabel);
		
		JLabel coursename = new JLabel();
		coursename.setText(val);
		coursename.setFont(new Font("Tahoma", Font.BOLD, 23));
		coursename.setBounds(169, 11, 749, 48);
		getContentPane().add(coursename);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(32, 70, 1241, 594);
		getContentPane().add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		loadCourseAttendance();

	}
	
	private void loadCourseAttendance() {
	    try (Connection c = DriverManager.getConnection(url)) {
	    	if (courseID != null)
	    	{
	    		String sql = "SELECT SessionDate, " +
		                "SUM(CASE WHEN Attendance = 'Yes' THEN 1 ELSE 0 END) AS TotalPresent, " +
		                "SUM(CASE WHEN Attendance = 'Late' THEN 1 ELSE 0 END) AS TotalLate, " +
		                "SUM(CASE WHEN Attendance = 'Absent' THEN 1 ELSE 0 END) AS TotalAbsent, " +
		                "ROUND(100.0 * SUM(CASE WHEN Attendance = 'Yes' THEN 1 ELSE 0 END) / COUNT(*), 2) AS AttendancePercentage " +
		                "FROM ATTENDANCE " +
		                "WHERE CourseID = ? " +
		                "GROUP BY SessionDate " +
		                "ORDER BY SessionDate ASC";

		        PreparedStatement ps = c.prepareStatement(sql);
		        ps.setString(1, courseID);
		        ResultSet rs = ps.executeQuery();
		        
		        if (!rs.isBeforeFirst()) { // Checks if the ResultSet is empty
	                JOptionPane.showMessageDialog(this, "No attendance records found for this course!", "Information", JOptionPane.INFORMATION_MESSAGE);
	                return;
	            }

		        // Create Table Model
		        DefaultTableModel model = new DefaultTableModel();
		        model.addColumn("Session Date");
		        model.addColumn("Total Present");
		        model.addColumn("Total Late");
		        model.addColumn("Total Absent");
		        model.addColumn("Attendance %");
		        
		        int totalPresent = 0;
	            int totalLate = 0;
	            int totalAbsent = 0;

		        // Add data to table
		        while (rs.next()) {
		        	int present = rs.getInt("TotalPresent");
	                int late = rs.getInt("TotalLate");
	                int absent = rs.getInt("TotalAbsent");

	                totalPresent += present;
	                totalLate += late;
	                totalAbsent += absent;
		        	
		        	model.addRow(new Object[]{
		        			rs.getString("SessionDate"),
	                        present,
	                        late,
	                        absent,
	                        rs.getDouble("AttendancePercentage")
	                        
//		            model.addRow(new Object[]{
//		                    rs.getString("SessionDate"),
//		                    rs.getInt("TotalPresent"),
//		                    rs.getInt("TotalLate"),
//		                    rs.getInt("TotalAbsent"),
//		                    rs.getDouble("AttendancePercentage")
		            });
		        }

		        table.setModel(model);
		        
		        if (totalPresent == 0 && totalLate == 0 && totalAbsent == 0) {
		            JOptionPane.showMessageDialog(this, "No attendance data available for this student.", "Information", JOptionPane.INFORMATION_MESSAGE);
		            return;  // Skip chart creation if no data
		        }
		        
		        updateChartPanel(totalPresent, totalLate, totalAbsent);

		        rs.close();
		        ps.close();
	    	}
	    	else
	    	{
	    		JOptionPane.showMessageDialog(this, "Invalid Course ID!", "Error", JOptionPane.ERROR_MESSAGE);
	    	}
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Error fetching course attendance.", "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	private JPanel createChartPanel() {
		DefaultPieDataset dataset = new DefaultPieDataset();

		JFreeChart chart = ChartFactory.createPieChart(
				"Attendance Statistics",  // Chart Title
				dataset,                  // Dataset
				true,                     // Include Legend
				true,
				false
			);
		
		PiePlot plot = (PiePlot) chart.getPlot();
	    plot.setSectionPaint("Present", new Color(51, 204, 51));  // Green
	    plot.setSectionPaint("Late", new Color(255, 204, 51));    // Yellow
	    plot.setSectionPaint("Absent", new Color(255, 51, 51));   // Red
	    plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
	    plot.setSimpleLabels(true);  // Cleaner labels

	    return new ChartPanel(chart);
	}
	
	private void updateChartPanel(int totalPresent, int totalLate, int totalAbsent) {
	    DefaultPieDataset dataset = new DefaultPieDataset();

	    int total = totalPresent + totalLate + totalAbsent;

	    if (totalPresent > 0) {
	        dataset.setValue("Present", totalPresent);
	    }
	    if (totalLate > 0) {
	        dataset.setValue("Late", totalLate);
	    }
	    if (totalAbsent > 0) {
	        dataset.setValue("Absent", totalAbsent);
	    }

	    JFreeChart chart = ChartFactory.createPieChart(
	            "Attendance Statistics",
	            dataset,
	            true,
	            true,
	            false
	    );

	    PiePlot plot = (PiePlot) chart.getPlot();
	    if (totalPresent > 0) plot.setSectionPaint("Present", new Color(51, 204, 51));  // Green
	    if (totalLate > 0) plot.setSectionPaint("Late", new Color(255, 204, 51));        // Yellow
	    if (totalAbsent > 0) plot.setSectionPaint("Absent", new Color(255, 51, 51));     // Red

	    // Enable tooltips with exact numbers and percentages
	    plot.setToolTipGenerator((dataset1, key) -> {
	        int value = dataset1.getValue(key).intValue();
	        double percentage = (value * 100.0) / total;
	        return key + ": " + value + " (" + String.format("%.2f", percentage) + "%)";
	    });

	    plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
	    
	 // Use a custom label generator to show percentage inside the chart
//	    plot.setLabelGenerator((dataset1, key) -> {
//	        int value = dataset1.getValue(key).intValue();
//	        double percentage = (value * 100.0) / total;
//	        return String.format("%.2f%%", percentage);  // Display percentage inside the chart
//	    });
	    
	    plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
	            "{0}: {1} ({2})", // Label format: section name, value, and percentage
	            new DecimalFormat("0"),   // Value format
	            new DecimalFormat("0.00%") // Percentage format
	        ));
	    
	    plot.setSimpleLabels(true);
	    
	    System.out.println("Total Present: " + totalPresent);
	    System.out.println("Total Late: " + totalLate);
	    System.out.println("Total Absent: " + totalAbsent);

	    chartPanel.removeAll();
	    chartPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
	    chartPanel.revalidate();
	    chartPanel.repaint();
	}
}
