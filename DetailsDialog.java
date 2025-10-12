package pkgfinal;
import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DetailsDialog extends JDialog {
    
    private String patientName;
    private String patientAge;
    private String patientSymptoms;
    private String patientDateEntered;
    private String patientConsultDate;
    private String patientStatus;
    private String extraDetails = "";

    public DetailsDialog(Frame parent, String name, String age, String symptoms,
                         String dateEntered, String consultDate, String status) {
        super(parent, "Patient Details", true);
        
        // Store patient data for PDF generation
        this.patientName = name;
        this.patientAge = age;
        this.patientSymptoms = symptoms;
        this.patientDateEntered = dateEntered;
        this.patientConsultDate = consultDate;
        this.patientStatus = status;
        
        setSize(480, 520);
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // ===== HEADER =====
        JLabel title = new JLabel("Patient Details", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 102));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // ===== CONTENT =====
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        infoPanel.add(createInfoLabel("Name: " + name));
        infoPanel.add(createInfoLabel("Age: " + age));
        infoPanel.add(createInfoLabel("Symptoms: " + symptoms));
        infoPanel.add(createInfoLabel("Date Entered: " + dateEntered));
        infoPanel.add(createInfoLabel("Consultation Date: " + consultDate));
        infoPanel.add(createInfoLabel("Status: " + status));

        JTextArea extraInfo = new JTextArea();
        extraInfo.setEditable(false);
        extraInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        extraInfo.setWrapStyleWord(true);
        extraInfo.setLineWrap(true);
        extraInfo.setBackground(new Color(250, 250, 250));
        extraInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(extraInfo);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 102)),
                "Detailed Notes",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(0, 102, 102)
        ));

        add(infoPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ===== FOOTER WITH BUTTONS =====
        JButton printButton = new JButton("Print to PDF");
        printButton.setBackground(new Color(0, 153, 76));
        printButton.setForeground(Color.WHITE);
        printButton.setFocusPainted(false);
        printButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        printButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        printButton.addActionListener(e -> generatePDF());

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(0, 102, 102));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        closeButton.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(printButton);
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== LOAD EXTRA DATA =====
        loadExtraDetails(name, dateEntered, extraInfo);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private void loadExtraDetails(String name, String dateEntered, JTextArea textArea) {
        try (BufferedReader br = new BufferedReader(new FileReader("history.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 9 && data[0].equalsIgnoreCase(name) && data[3].equals(dateEntered)) {
                    String severity = data[6];
                    String diagnostic = data[7].replace(";", ",");
                    String prescription = data[8].replace(";", ",");

                    extraDetails = "Severity: " + severity + "\n\n" +
                                 "Diagnostic:\n" + diagnostic + "\n\n" +
                                 "Prescription:\n" + prescription;

                    textArea.setText(
                        "Severity: " + severity + "\n\n" +
                        "Diagnostic:\n" + diagnostic + "\n\n" +
                        "Prescription:\n" + prescription
                    );
                    return;
                }
            }
            textArea.setText("No additional details found for this record.");
            extraDetails = "No additional details found for this record.";
        } catch (IOException e) {
            e.printStackTrace();
            textArea.setText("Error loading additional details.");
            extraDetails = "Error loading additional details.";
        }
    }

    private void generatePDF() {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        
        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // Set font and colors
                Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
                Font headerFont = new Font("Segoe UI", Font.BOLD, 12);
                Font bodyFont = new Font("Segoe UI", Font.PLAIN, 11);

                int y = 50;
                int lineHeight = 20;

                // Title
                g2d.setFont(titleFont);
                g2d.setColor(new Color(0, 102, 102));
                g2d.drawString("PATIENT CONSULTATION REPORT", 100, y);
                y += 30;

                // Horizontal line
                g2d.setColor(Color.GRAY);
                g2d.drawLine(50, y, 500, y);
                y += 25;

                // Patient Information
                g2d.setFont(headerFont);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Patient Information:", 50, y);
                y += lineHeight + 5;

                g2d.setFont(bodyFont);
                g2d.drawString("Name: " + patientName, 70, y);
                y += lineHeight;
                g2d.drawString("Age: " + patientAge, 70, y);
                y += lineHeight;
                g2d.drawString("Symptoms: " + patientSymptoms, 70, y);
                y += lineHeight;
                g2d.drawString("Date Entered: " + patientDateEntered, 70, y);
                y += lineHeight;
                g2d.drawString("Consultation Date: " + patientConsultDate, 70, y);
                y += lineHeight;
                g2d.drawString("Status: " + patientStatus, 70, y);
                y += 30;

                // Detailed Notes
                if (!extraDetails.isEmpty() && !extraDetails.contains("No additional details")) {
                    g2d.setFont(headerFont);
                    g2d.drawString("Detailed Notes:", 50, y);
                    y += lineHeight + 5;

                    g2d.setFont(bodyFont);
                    String[] lines = extraDetails.split("\n");
                    for (String line : lines) {
                        if (y > pageFormat.getImageableHeight() - 50) break;
                        
                        // Wrap long lines
                        if (line.length() > 70) {
                            String[] words = line.split(" ");
                            StringBuilder currentLine = new StringBuilder();
                            for (String word : words) {
                                if (currentLine.length() + word.length() > 70) {
                                    g2d.drawString(currentLine.toString(), 70, y);
                                    y += lineHeight;
                                    currentLine = new StringBuilder(word + " ");
                                } else {
                                    currentLine.append(word).append(" ");
                                }
                            }
                            if (currentLine.length() > 0) {
                                g2d.drawString(currentLine.toString(), 70, y);
                                y += lineHeight;
                            }
                        } else {
                            g2d.drawString(line, 70, y);
                            y += lineHeight;
                        }
                    }
                }

                // Footer
                y = (int) pageFormat.getImageableHeight() - 30;
                g2d.setFont(new Font("Segoe UI", Font.ITALIC, 9));
                g2d.setColor(Color.GRAY);
                g2d.drawString("Generated on: " + java.time.LocalDateTime.now().toString(), 50, y);

                return PAGE_EXISTS;
            }
        });

        // Show print dialog (user can save as PDF)
        if (printerJob.printDialog()) {
            try {
                printerJob.print();
                JOptionPane.showMessageDialog(this, 
                    "Print job sent successfully!\nChoose 'Save as PDF' in the print dialog to create a PDF file.",
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error printing: " + e.getMessage(),
                    "Print Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}