// File: src/pkgfinal/chartmanager.java
package pkgfinal;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.*;
import java.awt.Color;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * ChartManager: builds a 7-day bar chart showing how many patients
 * are scheduled per day based on the Consultation Date column (index 5)
 * in data.txt. Uses dark green bars, integer Y-axis ticks, and no negatives.
 * 
 * Shows the coming 7 days starting from today (today + next 6 days).
 */
public class chartmanager {

    private static final String FILE_PATH = "data.txt";
    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("MMM dd");

    /** Displays the upcoming weekly schedule bar graph in the given panel */
  public static void showWeeklyScheduleGraph(JPanel panel) {
    Map<LocalDate, Integer> dateCount = new LinkedHashMap<>();

    // ✅ Read file data and count patients per date
    try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length >= 6) {
                try {
                    String datePart = data[4].split(" ")[0]; // ✅ Consultation Date|Time at index 4
                    LocalDate date = LocalDate.parse(datePart);
                    dateCount.put(date, dateCount.getOrDefault(date, 0) + 1);
                } catch (Exception ignored) {
                    // skip malformed dates
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
        System.err.println("⚠️ Error reading schedule file: " + e.getMessage());
    }

    // ✅ Build dataset for the next 7 days (Today + Next 6)
    LocalDate today = LocalDate.now();
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("MMM d"); // 🟢 "Oct 11"

    for (int i = 0; i < 7; i++) {
        LocalDate date = today.plusDays(i);
        int count = dateCount.getOrDefault(date, 0);
        dataset.addValue(count, "Scheduled Patients", date.format(labelFmt)); // 🟢 Use short label
    }

    // ✅ Create the chart
    JFreeChart chart = ChartFactory.createBarChart(
            "Weekly Schedule",
            "Date",
            "Number of Patients",
            dataset
    );
    chart.setBackgroundPaint(Color.WHITE);

    // ✅ Customize chart style
    CategoryPlot plot = chart.getCategoryPlot();
    plot.setBackgroundPaint(Color.WHITE);
    plot.setOutlinePaint(Color.WHITE);
    plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
    plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
    plot.getRenderer().setSeriesPaint(0, new Color(0, 153, 76)); // Green bars

    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    rangeAxis.setLowerBound(0);

    // ✅ Add chart to the panel
    panel.removeAll();
    panel.setLayout(new BorderLayout());
    panel.add(new ChartPanel(chart), BorderLayout.CENTER);
    panel.revalidate();
    panel.repaint();
}

    /**
     * Automatically refresh the graph every few seconds (e.g., 30s = 30000ms).
     */
    public static javax.swing.Timer attachAutoRefresh(JPanel panel, int intervalMs) {
        javax.swing.Timer timer = new javax.swing.Timer(intervalMs, e -> showWeeklyScheduleGraph(panel));
        timer.setRepeats(true);
        timer.start();
        return timer;
    }
}
