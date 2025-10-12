package pkgfinal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

/**
 * TableStyler:
 * Provides consistent minimal styling for JTables.
 * Uses only white and teal [0,102,102].
 */
public class tablestyle {

    private static final Color TEAL = new Color(0, 102, 102);
    private static final Color WHITE = Color.WHITE;

    /** Applies consistent minimal style to a JTable */
    public static void applyStyle(JTable table) {
        // Table body
        table.setBackground(WHITE);
        table.setForeground(TEAL);
        table.setSelectionBackground(TEAL);
        table.setSelectionForeground(WHITE);
        table.setGridColor(TEAL);

        table.setRowHeight(24);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setFocusable(false);
        table.setRowSelectionAllowed(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setBorder(BorderFactory.createEmptyBorder());

        // Header (column titles)
        JTableHeader header = table.getTableHeader();
        header.setBackground(WHITE);        // 🤍 white background
        header.setForeground(TEAL);         // 🟩 teal text
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setOpaque(true);
        header.setBorder(BorderFactory.createEmptyBorder());
    }
}
