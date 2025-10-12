    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package pkgfinal;

    import java.io.BufferedReader;
    import java.io.BufferedWriter;
    import java.io.File;
    import java.io.FileNotFoundException;
    import java.io.FileReader;
    import java.io.FileWriter;
    import java.io.IOException;
    import java.time.LocalDate;
    import java.util.ArrayList;
    import javax.swing.JOptionPane;
    import javax.swing.JTable;
    import javax.swing.table.DefaultTableModel;
    import pkgfinal.tablestyle;

    /**
     *
     * @author gabgab8608
     */
    public class datamanagement {
        private static final String FILE_PATH = "data.txt";
        private static final String HISTORY_PATH = "history.txt";

        // 🔢 Merge Sort Implementation
        private static void mergeSort(Object[][] array, int left, int right) {
            if (left < right) {
                int middle = (left + right) / 2;
                mergeSort(array, left, middle);
                mergeSort(array, middle + 1, right);
                merge(array, left, middle, right);
            }
        }

        private static void merge(Object[][] array, int left, int middle, int right) {
            int n1 = middle - left + 1;
            int n2 = right - middle;

            Object[][] leftArray = new Object[n1][array[0].length];
            Object[][] rightArray = new Object[n2][array[0].length];

            for (int i = 0; i < n1; i++)
                System.arraycopy(array[left + i], 0, leftArray[i], 0, array[0].length);

            for (int j = 0; j < n2; j++)
                System.arraycopy(array[middle + 1 + j], 0, rightArray[j], 0, array[0].length);

            int i = 0, j = 0, k = left;

            while (i < n1 && j < n2) {
                String dateTime1 = (String) leftArray[i][4];
                String dateTime2 = (String) rightArray[j][4];
                if (dateTime1.compareTo(dateTime2) <= 0)
                    array[k++] = leftArray[i++];
                else
                    array[k++] = rightArray[j++];
            }

            while (i < n1) array[k++] = leftArray[i++];
            while (j < n2) array[k++] = rightArray[j++];
        }

        // 📊 Load & sort Schedule Table
        public static void loadScheduleDataSorted(JTable table) {
            loadTableDataWithSort("data.txt", table, new String[]{
                "Name", "Age", "Symptoms", "Date Entered", "Date|Time", "Consultation Status"
            });
        }

        // 📜 Load & sort History Table
        public static void loadHistoryDataSorted(JTable table) {
             loadTableDataWithSortStack("history.txt", table, new String[]{
        "Name", "Age", "Symptoms", "Date Entered", "Date|Time", "Consultation Status"
    });
        }

        private static void loadTableDataWithSort(String filePath, JTable table, String[] columns) {
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            table.setModel(model);
            tablestyle.applyStyle(table);

            ArrayList<Object[]> dataList = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = line.split(",", -1); // preserve empty trailing fields

                    // ✅ File structure: Name, Age, Symptoms, Date Entered, Consultation Date|Time, Status
                    if (data.length >= 6) {
                        Object[] row = new Object[]{
                            data[0].trim(), // Name
                            data[1].trim(), // Age
                            data[2].trim(), // Symptoms
                            data[3].trim(), // Date Entered
                            data[4].trim(), // Consultation Date|Time
                            data[5].trim()  // Consultation Status
                        };
                        dataList.add(row);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Error reading file: " + e.getMessage(),
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dataList.isEmpty()) return;

            Object[][] dataArray = dataList.toArray(new Object[0][columns.length]);

            long startTime = System.nanoTime();
            mergeSort(dataArray, 0, dataArray.length - 1);
            long endTime = System.nanoTime();
            double timeMs = (endTime - startTime) / 1_000_000.0;

            for (Object[] row : dataArray) {
                model.addRow(row);
            }

            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            table.setFillsViewportHeight(true);

            printComplexity(dataArray.length, timeMs);
        }

        // 🧮 Print Complexity & Timing
        public static void printComplexity(int n, double timeMs) {
            System.out.println("___________________________-____");
            System.out.println("? MERGE SORT PERFORMANCE ANALYSIS");
            System.out.println("___________________________________");
            System.out.println("Records (n): " + n);
            System.out.println("Time Complexity: O(n log n)");
            System.out.printf("Execution Time: %.3f ms%n", timeMs);
            System.out.println("_____________________________________");
            System.out.println("Theoretical Ops: " + n + " × log₂(" + n + ") ≈ " +
                    (int) (n * (Math.log(n) / Math.log(2))) + " ops");
            System.out.println("____________________________________\n");
        }

        //widget 1
        public static int countPatientsToday() {
            int count = 0;
            try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
                String line;
                String today = LocalDate.now().toString();
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    // ✅ Consultation Date|Time is at index 4 - check if appointment is today
                    if (parts.length >= 5 && parts[4].contains(today)) {
                        count++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return count;
        }

        ////widget 2
        public static int countPendingConsultations() {
            int count = 0;
            try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    // ✅ Consultation Status is at index 5
                  if (parts.length >= 6) {
            String status = parts[5].trim().toLowerCase();
            if (status.equals("scheduled") || status.equals("re-scheduled")) {
                count++;
            }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return count;
        }

        ///widget 3
        public static int countCompletedConsultations() {
            int count = 0;
            try (BufferedReader br = new BufferedReader(new FileReader("history.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    // ✅ Consultation Status is at index 5
                    if (parts.length >= 6 && parts[5].equalsIgnoreCase("Completed")) {
                        count++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return count;
        }

       public static void moveToHistory(String patientName, String dateEntered) {
        File dataFile = new File("data.txt");
        File tempFile = new File("data_temp.txt");
        File historyFile = new File("history.txt");

        try (
            BufferedReader reader = new BufferedReader(new FileReader(dataFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, false));
            BufferedWriter historyWriter = new BufferedWriter(new FileWriter(historyFile, true))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 6) continue;

                String name = data[0].trim();
                String date = data[3].trim();

                // ✅ Match by name and date
                if (name.equalsIgnoreCase(patientName) && date.equals(dateEntered)) {
                    // ➕ Append to history.txt before skipping
                    historyWriter.write(line);
                    historyWriter.newLine();
                    continue;
                }

                // Keep non-matching lines in temp file
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error moving patient to history: " + e.getMessage(), 
                "File Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ Replace original data file
        if (dataFile.delete()) {
            if (!tempFile.renameTo(dataFile)) {
                System.err.println("⚠️ Failed to rename temp file.");
            }
        } else {
            System.err.println("⚠️ Failed to delete data.txt.");
        }
    }

    private static void loadTableDataWithSortStack(String filePath, JTable table, String[] columnNames) {
    // ✅ Create the table model using columnNames
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);
    table.setModel(model);
    tablestyle.applyStyle(table);

    ArrayList<Object[]> dataList = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;

            String[] data = line.split(",", -1); // keep empty values
            if (data.length >= 6) {
                Object[] row = new Object[]{
                    data[0].trim(), // Name
                    data[1].trim(), // Age
                    data[2].trim(), // Symptoms
                    data[3].trim(), // Date Entered
                    data[4].trim(), // Date|Time
                    data[5].trim()  // Consultation Status
                };
                dataList.add(row);
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null,
            "Error reading file: " + e.getMessage(),
            "File Error",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    // 🪜 Stack behavior — show newest entries first
    for (int i = dataList.size() - 1; i >= 0; i--) {
        model.addRow(dataList.get(i));
    }

    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    table.setFillsViewportHeight(true);
}

    }