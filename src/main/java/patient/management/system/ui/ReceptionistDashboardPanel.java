package patient.management.system.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import patient.management.system.model.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

public class ReceptionistDashboardPanel extends JPanel {
    public ReceptionistDashboardPanel() {
        this(null);
    }

    public ReceptionistDashboardPanel(User loggedInUser) {
        super(new BorderLayout(18, 18));
        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        add(AppUI.pageTitle("Receptionist Dashboard"), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(18, 18));
        center.setOpaque(false);

        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 14));
        stats.setOpaque(false);
        stats.add(statCard("Today's Appointments", "24", UITheme.SOFT_BLUE, UITheme.BLUE));
        stats.add(statCard("Upcoming Appointments", "56", UITheme.SOFT_GREEN, UITheme.SUCCESS));
        stats.add(statCard("Emergency Cases", "07", UITheme.SOFT_RED, UITheme.DANGER));
        stats.add(statCard("Temporary Links", "03", UITheme.SOFT_YELLOW, UITheme.WARNING));

        center.add(stats, BorderLayout.NORTH);
        center.add(appointmentsTable(), BorderLayout.CENTER);
        return center;
    }

    private JPanel statCard(String title, String value, Color background, Color accent) {
        JPanel card = AppUI.cardPanel();
        card.setBackground(background);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.LABEL_FONT);
        titleLabel.setForeground(UITheme.TEXT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        valueLabel.setForeground(accent);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JScrollPane appointmentsTable() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Patient Name", "Doctor", "Time", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        /*
         * Backend integration point:
         * Later, load today's appointments from AppointmentFileHandler/AppointmentService.
         */
        model.addRow(new Object[]{"A101", "Ali Raza", "Dr. Ahmed", "09:00 AM", "SCHEDULED"});
        model.addRow(new Object[]{"A102", "Sara Ali", "Dr. Khan", "10:00 AM", "RESCHEDULED"});
        model.addRow(new Object[]{"A103", "Usman Shah", "Dr. Ahmed", "11:00 AM", "COMPLETED"});
        model.addRow(new Object[]{"A104", "Hina Noor", "Dr. Fatima", "12:00 PM", "CANCELLED"});
        model.addRow(new Object[]{"A105", "Bilal Ahmed", "Dr. Khan", "01:00 PM", "SCHEDULED"});

        JTable table = AppUI.table(model);
        addStatusColors(table);
        return AppUI.tableScrollPane(table);
    }

    private void addStatusColors(JTable table) {
        int statusColumn = 4;

        table.getColumnModel().getColumn(statusColumn).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                String status = value == null ? "" : value.toString().toUpperCase();
                if (status.equals("COMPLETED")) {
                    component.setBackground(UITheme.SOFT_GREEN);
                    component.setForeground(UITheme.SUCCESS);
                } else if (status.equals("CANCELLED")) {
                    component.setBackground(UITheme.SOFT_RED);
                    component.setForeground(UITheme.DANGER);
                } else if (status.equals("RESCHEDULED")) {
                    component.setBackground(UITheme.SOFT_YELLOW);
                    component.setForeground(new Color(0x9A6A00));
                } else if (status.equals("SCHEDULED")) {
                    component.setBackground(UITheme.SOFT_BLUE);
                    component.setForeground(UITheme.BLUE);
                } else {
                    component.setBackground(isSelected ? UITheme.SOFT_BLUE : Color.WHITE);
                    component.setForeground(UITheme.TEXT);
                }

                setHorizontalAlignment(CENTER);
                setFont(UITheme.LABEL_FONT);
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return component;
            }
        });
    }
}
