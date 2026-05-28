/*package patient.management.system.ui;

import patient.management.system.model.User;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

public class EmergencyTeamDashboardPanel extends JPanel {
    private final User loggedInUser;
    private final Color accentColor;

    public EmergencyTeamDashboardPanel(User loggedInUser, Color accentColor) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        this.accentColor = accentColor;
        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = AppUI.pageTitle(loggedInUser.getTriageColor() + " Team Dashboard");

        JLabel hint = new JLabel("Emergency case overview for this triage team.");
        hint.setFont(UITheme.BODY_FONT);
        hint.setForeground(UITheme.MUTED_TEXT);

        header.add(title, BorderLayout.WEST);
        header.add(hint, BorderLayout.EAST);
        return header;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(18, 18));
        content.setOpaque(false);
        content.add(buildStatsPanel(), BorderLayout.NORTH);
        content.add(buildCasesCard(), BorderLayout.CENTER);
        return content;
    }

    private JPanel buildStatsPanel() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 14));
        stats.setOpaque(false);
        stats.add(statCard("Active Cases", "08", UITheme.SOFT_BLUE, UITheme.BLUE));
        stats.add(statCard("Completed Today", "05", UITheme.SOFT_GREEN, UITheme.SUCCESS));
        stats.add(statCard("Moved to ICU", "02", UITheme.SOFT_YELLOW, UITheme.WARNING));
        stats.add(statCard("Moved to Ward", "03", UITheme.SOFT_RED, UITheme.DANGER));
        return stats;
    }

    private JPanel statCard(String title, String value, Color background, Color foreground) {
        JPanel card = AppUI.cardPanel();
        card.setBackground(background);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.LABEL_FONT);
        titleLabel.setForeground(UITheme.TEXT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(foreground);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildCasesCard() {
        JPanel card = AppUI.cardPanel();
        card.add(new JLabel("Emergency Cases"), BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Case ID", "Patient Name", "Age", "Gender", "Initial Complaint", "Arrival Time", "Status"},
                0
        );

        /*
         * Backend integration point:
         * Later, load cases for loggedInUser.getTriageColor().
         */
/*        model.addRow(new Object[]{"E101", "Unknown Male", "45", "Male", "Chest pain", "2026-05-28 13:10", "ACTIVE"});
        model.addRow(new Object[]{"E102", "Usman Shah", "33", "Male", "Severe headache", "2026-05-28 13:45", "ACTIVE"});
        model.addRow(new Object[]{"E099", "Sara Ali", "24", "Female", "High fever", "2026-05-28 09:20", "COMPLETE"});

        JTable table = AppUI.table(model);
        addStatusColors(table);
        card.add(AppUI.tableScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private void addStatusColors(JTable table) {
        int statusColumn = 6;

        table.getColumnModel().getColumn(statusColumn).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                String status = value == null ? "" : value.toString().toUpperCase();
                if (status.equals("COMPLETE") || status.equals("COMPLETED")) {
                    component.setBackground(UITheme.SOFT_GREEN);
                    component.setForeground(UITheme.SUCCESS);
                } else if (status.equals("ACTIVE")) {
                    component.setBackground(UITheme.SOFT_BLUE);
                    component.setForeground(UITheme.BLUE);
                } else {
                    component.setBackground(Color.WHITE);
                    component.setForeground(UITheme.TEXT);
                }

                setHorizontalAlignment(CENTER);
                setFont(UITheme.LABEL_FONT);
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return component;
            }
        });
    }

}*/
