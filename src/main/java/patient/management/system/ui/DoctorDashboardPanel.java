package patient.management.system.ui;

import patient.management.system.model.User;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

public class DoctorDashboardPanel extends JPanel {
    private final User loggedInUser;

    public DoctorDashboardPanel(User loggedInUser) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = AppUI.pageTitle("Doctor Dashboard");
        JLabel hint = new JLabel("Today's appointment overview");
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
        content.add(buildAppointmentsCard(), BorderLayout.CENTER);
        return content;
    }

    private JPanel buildStatsPanel() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 14));
        stats.setOpaque(false);
        stats.add(statCard("Today's Appointments", "09", UITheme.SOFT_BLUE, UITheme.BLUE));
        stats.add(statCard("Upcoming Appointments", "18", UITheme.SOFT_GREEN, UITheme.SUCCESS));
        stats.add(statCard("Completed Today", "04", UITheme.SOFT_YELLOW, UITheme.WARNING));
        stats.add(statCard("Pending Records", "05", UITheme.SOFT_RED, UITheme.DANGER));
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

    private JPanel buildAppointmentsCard() {
        JPanel card = AppUI.cardPanel();
        card.add(new JLabel("Appointments"), BorderLayout.NORTH);
        card.add(buildAppointmentsTable(), BorderLayout.CENTER);
        return card;
    }

    private JScrollPane buildAppointmentsTable() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Appointment ID", "Patient Name", "Date", "Time", "Patient Description", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        /*
         * Backend integration point:
         * Later, load this doctor's SCHEDULED / RESCHEDULED / COMPLETED appointments.
         */
        model.addRow(new Object[]{"A101", "Ali Raza", "2026-05-28", "09:00 AM", "Chest pain", "SCHEDULED"});
        model.addRow(new Object[]{"A102", "Sara Ali", "2026-05-28", "10:00 AM", "Follow-up", "RESCHEDULED"});
        model.addRow(new Object[]{"A103", "Usman Shah", "2026-05-28", "11:00 AM", "Severe headache", "COMPLETED"});
        model.addRow(new Object[]{"A104", "Hina Noor", "2026-05-29", "12:00 PM", "Consultation", "SCHEDULED"});

        JTable table = AppUI.table(model);
        addStatusColors(table);
        return AppUI.tableScrollPane(table);
    }

    private void addStatusColors(JTable table) {
        int statusColumn = 5;

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
                } else if (status.equals("RESCHEDULED")) {
                    component.setBackground(UITheme.SOFT_YELLOW);
                    component.setForeground(new Color(0x9A6A00));
                } else if (status.equals("SCHEDULED")) {
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
}
