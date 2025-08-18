package com.example.attendance;//import javax.swing.*;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import javax.imageio.ImageIO;
//
//public class ExportToPNG {
//    
//    private JTable table;
//    private JPanel chartPanel;  // Panel containing the pie chart
//
//    public ExportToPNG(JTable table, JPanel chartPanel) {
//        this.table = table;
//        this.chartPanel = chartPanel;
//    }
//
//    public void exportDataToPNG() {
//        // Create a new panel that combines data table and pie chart
//        JPanel combinedPanel = new JPanel(new BorderLayout());
//        combinedPanel.add(new JScrollPane(table), BorderLayout.NORTH);
//        combinedPanel.add(chartPanel, BorderLayout.SOUTH);
//
//        // Render the panel as an image
//        BufferedImage image = new BufferedImage(combinedPanel.getWidth(), 
//                                                 combinedPanel.getHeight(), 
//                                                 BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2 = image.createGraphics();
//        combinedPanel.paint(g2);
//
//        try {
//            File outputFile = new File("Attendance_Report.png");
//            ImageIO.write(image, "png", outputFile);
//            JOptionPane.showMessageDialog(null, "Data exported successfully to Attendance_Report.png");
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null, "Error exporting data: " + e.getMessage());
//        }
//    }
//}

//import javax.swing.*;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import javax.imageio.ImageIO;
//
//public class ExportToPNG {
//    
//    private JTable table;
//    private JPanel chartPanel;
//
//    public ExportToPNG(JTable table, JPanel chartPanel) {
//        this.table = table;
//        this.chartPanel = chartPanel;
//    }
//
//    public void exportDataToPNG() {
//        // Create a new panel that combines data table and pie chart
//        JPanel combinedPanel = new JPanel(new BorderLayout());
//        combinedPanel.add(new JScrollPane(table), BorderLayout.NORTH);
//        combinedPanel.add(chartPanel, BorderLayout.SOUTH);
//
//        // Force size calculation before rendering
//        combinedPanel.setSize(800, 600);  // Set an appropriate size
//        combinedPanel.doLayout();
//
//        // Render the panel as an image
//        BufferedImage image = new BufferedImage(combinedPanel.getWidth(),
//                                                 combinedPanel.getHeight(),
//                                                 BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2 = image.createGraphics();
//        combinedPanel.paint(g2);
//        g2.dispose();
//
//        try {
//            File outputFile = new File("Attendance_Report.png");
//            ImageIO.write(image, "png", outputFile);
//            JOptionPane.showMessageDialog(null, "Data exported successfully to Attendance_Report.png");
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null, "Error exporting data: " + e.getMessage());
//        }
//    }
//}

//import javax.swing.*;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import javax.imageio.ImageIO;
//
//public class ExportToPNG {
//    
//    private JTable table;
//    private JPanel chartPanel;
//
//    public ExportToPNG(JTable table, JPanel chartPanel) {
//        this.table = table;
//        this.chartPanel = chartPanel;
//    }
//
//    public void exportDataToPNG() {
//        JPanel combinedPanel = new JPanel(new BorderLayout());
//        combinedPanel.add(new JScrollPane(table), BorderLayout.NORTH);
//        combinedPanel.add(chartPanel, BorderLayout.SOUTH);
//
//        combinedPanel.setSize(800, 600);  // Ensure proper dimensions
//        combinedPanel.doLayout();
//
//        BufferedImage image = new BufferedImage(combinedPanel.getWidth(),
//                                                 combinedPanel.getHeight(),
//                                                 BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2 = image.createGraphics();
//        combinedPanel.paint(g2);
//        g2.dispose();
//
//        // File chooser for saving
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Attendance Report");
//        fileChooser.setSelectedFile(new File("Attendance_Report.png"));
//
//        int userSelection = fileChooser.showSaveDialog(null);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            File outputFile = fileChooser.getSelectedFile();
//            try {
//                ImageIO.write(image, "png", outputFile);
//                JOptionPane.showMessageDialog(null, "Data exported successfully to " + outputFile.getAbsolutePath());
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(null, "Error exporting data: " + e.getMessage());
//            }
//        }
//    }
//}

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ExportToPNG {

    private JTable table;
    private JPanel chartPanel;

    public ExportToPNG(JTable table, JPanel chartPanel) {
        this.table = table;
        this.chartPanel = chartPanel;
    }

//    public void exportDataToPNG() {
//        JPanel combinedPanel = new JPanel(new BorderLayout());
//        combinedPanel.add(new JScrollPane(table), BorderLayout.NORTH);
//        combinedPanel.add(chartPanel, BorderLayout.SOUTH);
//
//        // Calculate preferred size based on components
//        int width = 800;
//        int height = table.getPreferredSize().height + chartPanel.getPreferredSize().height + 50;
//        combinedPanel.setPreferredSize(new Dimension(width, height));
//        combinedPanel.revalidate();  // Ensures layout is calculated
//
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2 = image.createGraphics();
//        combinedPanel.paint(g2);
//        g2.dispose();
//
//        // File chooser for saving
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Attendance Report");
//        fileChooser.setSelectedFile(new File("Attendance_Report.png"));
//
//        int userSelection = fileChooser.showSaveDialog(null);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            File outputFile = fileChooser.getSelectedFile();
//            try {
//                ImageIO.write(image, "png", outputFile);
//                JOptionPane.showMessageDialog(null, "Data exported successfully to " + outputFile.getAbsolutePath());
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(null, "Error exporting data: " + e.getMessage());
//            }
//        }
//    }
    
//    public void exportDataToPNG() {
//        JPanel combinedPanel = new JPanel(new BorderLayout());
//        combinedPanel.add(new JScrollPane(table), BorderLayout.NORTH);
//        combinedPanel.add(chartPanel, BorderLayout.SOUTH);
//
//        // Force the combined panel to layout and render correctly
//        combinedPanel.setPreferredSize(new Dimension(800, table.getPreferredSize().height + chartPanel.getPreferredSize().height + 50));
//        combinedPanel.doLayout();     // Ensures layout calculations
//        combinedPanel.revalidate();   // Ensures the layout is updated
//        combinedPanel.repaint();      // Forces repaint to ensure content is drawn
//
//        // Create image with alpha channel support
//        BufferedImage image = new BufferedImage(
//            combinedPanel.getPreferredSize().width,
//            combinedPanel.getPreferredSize().height,
//            BufferedImage.TYPE_INT_ARGB
//        );
//
//        Graphics2D g2 = image.createGraphics();
//        combinedPanel.paint(g2);  // Correct way to render Swing components to an image
//        g2.dispose();
//
//        // File chooser for saving
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Attendance Report");
//        fileChooser.setSelectedFile(new File("Attendance_Report.png"));
//
//        int userSelection = fileChooser.showSaveDialog(null);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            File outputFile = fileChooser.getSelectedFile();
//            try {
//                ImageIO.write(image, "png", outputFile);
//                JOptionPane.showMessageDialog(null, "Data exported successfully to " + outputFile.getAbsolutePath());
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(null, "Error exporting data: " + e.getMessage());
//            }
//        }
//    }
    
//    public void exportDataToPNG() {
//        JPanel combinedPanel = new JPanel(new BorderLayout());
//        combinedPanel.add(new JScrollPane(table), BorderLayout.NORTH);
//        combinedPanel.add(chartPanel, BorderLayout.SOUTH);
//        
//        // Ensure preferred size is correctly set
//        combinedPanel.setPreferredSize(new Dimension(800, table.getPreferredSize().height + chartPanel.getPreferredSize().height + 50));
//
//        // 🔹 Fix: Add to an off-screen frame to force proper rendering
//        JFrame tempFrame = new JFrame();
//        tempFrame.add(combinedPanel);
//        tempFrame.pack();  // Forces layout calculations
//        tempFrame.setVisible(true);
//
//        // Create BufferedImage
//        BufferedImage image = new BufferedImage(
//            combinedPanel.getWidth(),
//            combinedPanel.getHeight(),
//            BufferedImage.TYPE_INT_ARGB
//        );
//
//        Graphics2D g2 = image.createGraphics();
//        combinedPanel.printAll(g2);  // Use printAll instead of paint
//        g2.dispose();
//
//        // Hide temporary frame after rendering
//        tempFrame.dispose();
//
//        // File chooser for saving
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Attendance Report");
//        fileChooser.setSelectedFile(new File("Attendance_Report.png"));
//
//        int userSelection = fileChooser.showSaveDialog(null);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            File outputFile = fileChooser.getSelectedFile();
//            try {
//                ImageIO.write(image, "png", outputFile);
//                JOptionPane.showMessageDialog(null, "Data exported successfully to " + outputFile.getAbsolutePath());
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(null, "Error exporting data: " + e.getMessage());
//            }
//        }
//    }
    
//    public void exportDataToPNG() {
//        JPanel combinedPanel = new JPanel(new BorderLayout());
//        combinedPanel.add(new JScrollPane(table), BorderLayout.NORTH);
//        combinedPanel.add(chartPanel, BorderLayout.SOUTH);
//
//        // Force proper size calculation
//        combinedPanel.setPreferredSize(new Dimension(800, table.getPreferredSize().height + chartPanel.getPreferredSize().height + 100));
//        chartPanel.revalidate();  // Ensure layout updates
//        chartPanel.repaint();    // Redraw the chart
//
//        // Temporary frame for correct rendering
//        JFrame tempFrame = new JFrame();
//        tempFrame.add(combinedPanel);
//        tempFrame.pack();  // Important for correct sizing
//        tempFrame.setVisible(true);
//
//        // Create BufferedImage
//        BufferedImage image = new BufferedImage(
//            combinedPanel.getWidth(),
//            combinedPanel.getHeight(),
//            BufferedImage.TYPE_INT_ARGB
//        );
//
//        Graphics2D g2 = image.createGraphics();
//        combinedPanel.printAll(g2);
//        g2.dispose();
//
//        tempFrame.dispose();  // Hide temp frame after rendering
//
//        // File chooser for saving
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Attendance Report");
//        fileChooser.setSelectedFile(new File("Attendance_Report.png"));
//
//        int userSelection = fileChooser.showSaveDialog(null);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            File outputFile = fileChooser.getSelectedFile();
//            try {
//                ImageIO.write(image, "png", outputFile);
//                JOptionPane.showMessageDialog(null, "Data exported successfully to " + outputFile.getAbsolutePath());
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(null, "Error exporting data: " + e.getMessage());
//            }
//        }
//    }
    
//    public void exportDataToPNG() {
//        JPanel combinedPanel = new JPanel(new BorderLayout());
//        combinedPanel.add(new JScrollPane(table), BorderLayout.NORTH);
//        combinedPanel.add(chartPanel, BorderLayout.SOUTH);
//
//        // Force the chart panel to expand and calculate size
//        chartPanel.setPreferredSize(new Dimension(800, 400)); 
//        chartPanel.revalidate();  
//        chartPanel.repaint();     
//
//        // Ensure correct combined panel size
//        combinedPanel.setPreferredSize(new Dimension(800, table.getPreferredSize().height + chartPanel.getPreferredSize().height + 50));
//
//        // Temporary frame for rendering
//        JFrame tempFrame = new JFrame();
//        tempFrame.add(combinedPanel);
//        tempFrame.pack();
//        tempFrame.setVisible(true);
//
//        // Create BufferedImage
//        BufferedImage image = new BufferedImage(
//            combinedPanel.getWidth(),
//            combinedPanel.getHeight(),
//            BufferedImage.TYPE_INT_ARGB
//        );
//
//        Graphics2D g2 = image.createGraphics();
//        combinedPanel.printAll(g2);
//        g2.dispose();
//
//        tempFrame.dispose();
//
//        // File chooser for saving
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Attendance Report");
//        fileChooser.setSelectedFile(new File("Attendance_Report.png"));
//
//        int userSelection = fileChooser.showSaveDialog(null);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            File outputFile = fileChooser.getSelectedFile();
//            try {
//                ImageIO.write(image, "png", outputFile);
//                JOptionPane.showMessageDialog(null, "Data exported successfully to " + outputFile.getAbsolutePath());
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(null, "Error exporting data: " + e.getMessage());
//            }
//        }
//    }
    
//    public void exportDataToPNG() {
//        JPanel combinedPanel = new JPanel(new BorderLayout());
//        combinedPanel.add(new JScrollPane(table), BorderLayout.NORTH);
//        combinedPanel.add(chartPanel, BorderLayout.SOUTH);
//
//        // Force chartPanel size with a margin for labels
//        chartPanel.setPreferredSize(new Dimension(800, 500)); 
//        chartPanel.doLayout();  // Ensures all components are arranged properly
//        chartPanel.revalidate();  
//        chartPanel.repaint();
//
//        // Combined panel size calculation
//        combinedPanel.setPreferredSize(new Dimension(
//            800, 
//            table.getPreferredSize().height + chartPanel.getPreferredSize().height + 100
//        ));
//
//        // Temporary frame for rendering
//        JFrame tempFrame = new JFrame();
//        tempFrame.add(combinedPanel);
//        tempFrame.pack();
//        tempFrame.setVisible(true);
//
//        // Create BufferedImage
//        BufferedImage image = new BufferedImage(
//            combinedPanel.getWidth(),
//            combinedPanel.getHeight(),
//            BufferedImage.TYPE_INT_ARGB
//        );
//
//        Graphics2D g2 = image.createGraphics();
//        combinedPanel.printAll(g2);
//        g2.dispose();
//
//        tempFrame.dispose();
//
//        // File chooser for saving
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Attendance Report");
//        fileChooser.setSelectedFile(new File("Attendance_Report.png"));
//
//        int userSelection = fileChooser.showSaveDialog(null);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            File outputFile = fileChooser.getSelectedFile();
//            try {
//                ImageIO.write(image, "png", outputFile);
//                JOptionPane.showMessageDialog(null, "Data exported successfully to " + outputFile.getAbsolutePath());
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(null, "Error exporting data: " + e.getMessage());
//            }
//        }
//    }
    
//    public void exportDataToPNG() {
//        JPanel combinedPanel = new JPanel(new BorderLayout());
//        combinedPanel.add(new JScrollPane(table), BorderLayout.NORTH);
//        combinedPanel.add(chartPanel, BorderLayout.SOUTH);
//
//        // Manually set dimensions for accurate rendering
//        int chartHeight = 600;
//        int combinedHeight = table.getPreferredSize().height + chartHeight + 50;
//
//        combinedPanel.setPreferredSize(new Dimension(800, combinedHeight));
//
//        // Temporary frame for layout calculation
//        JFrame tempFrame = new JFrame();
//        tempFrame.add(combinedPanel);
//        tempFrame.pack();
//        tempFrame.setVisible(true);
//
//        // Create BufferedImage with exact dimensions
//        BufferedImage image = new BufferedImage(
//            combinedPanel.getWidth(),
//            combinedHeight,
//            BufferedImage.TYPE_INT_ARGB
//        );
//
//        Graphics2D g2 = image.createGraphics();
//
//        // Draw table and chart separately for better control
//        table.printAll(g2);
//
//        // Draw chart directly with margins
//        g2.translate(0, table.getPreferredSize().height + 10); // Offset for the chart
//        chartPanel.setSize(800, chartHeight);  // Force chart size
//        chartPanel.paint(g2);  // Directly render chart into image
//        g2.dispose();
//
//        tempFrame.dispose();
//
//        // File chooser for saving
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Attendance Report");
//        fileChooser.setSelectedFile(new File("Attendance_Report.png"));
//
//        int userSelection = fileChooser.showSaveDialog(null);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            File outputFile = fileChooser.getSelectedFile();
//            try {
//                ImageIO.write(image, "png", outputFile);
//                JOptionPane.showMessageDialog(null, "Data exported successfully to " + outputFile.getAbsolutePath());
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(null, "Error exporting data: " + e.getMessage());
//            }
//        }
//    }
    
//    public void exportDataToPNG() {
//        JPanel combinedPanel = new JPanel(new BorderLayout());
//        combinedPanel.add(new JScrollPane(table), BorderLayout.NORTH);
//        combinedPanel.add(chartPanel, BorderLayout.CENTER);
//
//        // Adjust dimensions to reduce empty space
//        int tableHeight = table.getPreferredSize().height;
//        int chartHeight = 400;  // Reduced chart height for better fit
//        int combinedHeight = tableHeight + chartHeight;
//
//        combinedPanel.setPreferredSize(new Dimension(800, combinedHeight));
//
//        // Temporary frame for layout calculation
//        JFrame tempFrame = new JFrame();
//        tempFrame.add(combinedPanel);
//        tempFrame.pack();
//        tempFrame.setVisible(true);
//
//        // Create BufferedImage with reduced height
//        BufferedImage image = new BufferedImage(
//            combinedPanel.getWidth(),
//            combinedHeight,
//            BufferedImage.TYPE_INT_ARGB
//        );
//
//        Graphics2D g2 = image.createGraphics();
//
//        // Draw table and chart with minimal gaps
//        table.printAll(g2);
//
//        // Draw chart directly with better spacing
//        g2.translate(0, tableHeight);  // Adjust chart position
//        chartPanel.setSize(800, chartHeight);
//        chartPanel.paint(g2);
//        g2.dispose();
//
//        tempFrame.dispose();
//
//        // File chooser for saving
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Attendance Report");
//        fileChooser.setSelectedFile(new File("Attendance_Report.png"));
//
//        int userSelection = fileChooser.showSaveDialog(null);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            File outputFile = fileChooser.getSelectedFile();
//            try {
//                ImageIO.write(image, "png", outputFile);
//                JOptionPane.showMessageDialog(null, "Data exported successfully to " + outputFile.getAbsolutePath());
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(null, "Error exporting data: " + e.getMessage());
//            }
//        }
//    }
    
    public void exportDataToPNG() {
        JPanel combinedPanel = new JPanel(new BorderLayout());

        // Panel to add spacing between table and chart
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(800, 20));  // Adds space

        combinedPanel.add(new JScrollPane(table), BorderLayout.NORTH);
        combinedPanel.add(spacerPanel, BorderLayout.CENTER);  // Spacer for gap
        combinedPanel.add(chartPanel, BorderLayout.SOUTH);

        // Adjust dimensions to reduce empty space
        int tableHeight = table.getPreferredSize().height;
        int chartHeight = 400;  
        int spacerHeight = 20;   // Controlled spacing
        int combinedHeight = tableHeight + chartHeight + spacerHeight;

        combinedPanel.setPreferredSize(new Dimension(800, combinedHeight));

        // Temporary frame for layout calculation
        JFrame tempFrame = new JFrame();
        tempFrame.add(combinedPanel);
        tempFrame.pack();
        tempFrame.setVisible(true);

        // Create BufferedImage with adjusted height
        BufferedImage image = new BufferedImage(
            combinedPanel.getWidth(),
            combinedHeight,
            BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = image.createGraphics();

        // Draw table
        table.printAll(g2);

        // Add spacing before chart
        g2.translate(0, tableHeight + spacerHeight);
        chartPanel.setSize(800, chartHeight);
        chartPanel.paint(g2);

        g2.dispose();
        tempFrame.dispose();

        // File chooser for saving
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Attendance Report");
        fileChooser.setSelectedFile(new File("Attendance_Report.png"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            try {
                ImageIO.write(image, "png", outputFile);
                JOptionPane.showMessageDialog(null, "Data exported successfully to " + outputFile.getAbsolutePath());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error exporting data: " + e.getMessage());
            }
        }
    }
}