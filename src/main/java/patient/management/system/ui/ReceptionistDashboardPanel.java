package patient.management.system.ui;

import patient.management.system.dto.AppointmentDTO;
import patient.management.system.model.User;
import patient.management.system.service.NotificationService;
import patient.management.system.service.ReceptionistService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

public class ReceptionistDashboardPanel extends JPanel {
    private final User loggedInUser;
    private final ReceptionistService receptionistService;
    private final NotificationService notificationService;
    private JPanel centerPanel;

    public ReceptionistDashboardPanel() {
        this(null);
    }

    public ReceptionistDashboardPanel(User loggedInUser) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        this.receptionistService = new ReceptionistService();
        this.notificationService = new NotificationService();

        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        add(buildHeader(), BorderLayout.NORTH);
        centerPanel = buildCenter();
        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshDashboard() {
        remove(centerPanel);
        centerPanel = buildCenter();
        add(centerPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(AppUI.pageTitle("Receptionist Dashboard"), BorderLayout.WEST);

        JButton reminderButton = AppUI.primaryButton("Send Tomorrow's Reminders");
        reminderButton.addActionListener(event -> sendReminders());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(reminderButton);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(18, 18));
        center.setOpaque(false);

        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 14));
        stats.setOpaque(false);
        try {
            stats.add(statCard("Today's Appointments", receptionistService.getTodayAppointmentsCount(),
                    UITheme.SOFT_BLUE, UITheme.BLUE));
            stats.add(statCard("Upcoming Appointments", receptionistService.getUpcomingAppointmentsCount(),
                    UITheme.SOFT_GREEN, UITheme.SUCCESS));
            stats.add(statCard("Emergency Cases", receptionistService.getTotalEmergencyCasesCount(),
                    UITheme.SOFT_RED, UITheme.DANGER));
            stats.add(statCard("Temporary Links", receptionistService.getTemporaryLinksCount(),
                    UITheme.SOFT_YELLOW, UITheme.WARNING));
        } catch (RuntimeException exception) {
            stats.add(statCard("Today's Appointments", 0, UITheme.SOFT_BLUE, UITheme.BLUE));
            stats.add(statCard("Upcoming Appointments", 0, UITheme.SOFT_GREEN, UITheme.SUCCESS));
            stats.add(statCard("Emergency Cases", 0, UITheme.SOFT_RED, UITheme.DANGER));
            stats.add(statCard("Temporary Links", 0, UITheme.SOFT_YELLOW, UITheme.WARNING));
            showError(exception.getMessage(), "Unable to Load Dashboard");
        }

        center.add(stats, BorderLayout.NORTH);
        center.add(appointmentsTable(), BorderLayout.CENTER);
        return center;
    }

    private JPanel statCard(String title, int value, Color background, Color accent) {
        JPanel card = AppUI.cardPanel();
        card.setBackground(background);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.LABEL_FONT);
        titleLabel.setForeground(UITheme.TEXT);

        JLabel valueLabel = new JLabel(String.format("%02d", value));
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

        try {
            String today = LocalDate.now().toString();
            for (AppointmentDTO appointment : receptionistService.getAppointments()) {
                if (!today.equals(appointment.getAppointmentDate())) {
                    continue;
                }

                model.addRow(new Object[]{
                        appointment.getAppointmentId(),
                        appointment.getPatientName(),
                        appointment.getDoctorName(),
                        formatHour(appointment.getAppointmentHour()),
                        appointment.getStatus()
                });
            }
        } catch (RuntimeException exception) {
            showError(exception.getMessage(), "Unable to Load Appointments");
        }

        JTable table = AppUI.table(model);
        addStatusColors(table);
        return AppUI.tableScrollPane(table);
    }

    private void sendReminders() {
        try {
            notificationService.sendReminder();
            JOptionPane.showMessageDialog(
                    this,
                    "Reminders sent for tomorrow's appointments.",
                    "Reminders Sent",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (RuntimeException exception) {
            showError(exception.getMessage(), "Unable to Send Reminders");
        }
    }

    private String formatHour(int hour) {
        int displayHour = hour % 12 == 0 ? 12 : hour % 12;
        String suffix = hour < 12 ? "AM" : "PM";
        return String.format("%02d:00 %s", displayHour, suffix);
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
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
                } else if (status.equals("MISSED")) {
                    component.setBackground(UITheme.SOFT_ORANGE);
                    component.setForeground(UITheme.ORANGE);
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
