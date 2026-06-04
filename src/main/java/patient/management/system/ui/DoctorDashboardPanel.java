package patient.management.system.ui;

import patient.management.system.dto.AppointmentDTO;
import patient.management.system.dto.DoctorDTO;
import patient.management.system.model.User;
import patient.management.system.service.DoctorService;

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
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

public class DoctorDashboardPanel extends JPanel {
    private final User loggedInUser;
    private final DoctorService doctorService;
    private final JLabel todayValue;
    private final JLabel upcomingValue;
    private final JLabel completedValue;
    private final JLabel pendingValue;
    private final DefaultTableModel appointmentModel;
    private DoctorDTO loggedInDoctor;

    public DoctorDashboardPanel(User loggedInUser) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        this.doctorService = new DoctorService();
        this.todayValue = statValueLabel();
        this.upcomingValue = statValueLabel();
        this.completedValue = statValueLabel();
        this.pendingValue = statValueLabel();
        this.appointmentModel = buildAppointmentModel();
        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        refreshDashboard();
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
        stats.add(statCard("Today's Appointments", todayValue, UITheme.SOFT_BLUE, UITheme.BLUE));
        stats.add(statCard("Upcoming Appointments", upcomingValue, UITheme.SOFT_GREEN, UITheme.SUCCESS));
        stats.add(statCard("Completed Today", completedValue, UITheme.SOFT_YELLOW, UITheme.WARNING));
        stats.add(statCard("Pending Records", pendingValue, UITheme.SOFT_RED, UITheme.DANGER));
        return stats;
    }

    private JLabel statValueLabel() {
        JLabel valueLabel = new JLabel("00");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        return valueLabel;
    }

    private JPanel statCard(String title, JLabel valueLabel, Color background, Color foreground) {
        JPanel card = AppUI.cardPanel();
        card.setBackground(background);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.LABEL_FONT);
        titleLabel.setForeground(UITheme.TEXT);

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

    private DefaultTableModel buildAppointmentModel() {
        return new DefaultTableModel(
                new String[]{"Appointment ID", "Patient Name", "Date", "Time", "Patient Description", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JScrollPane buildAppointmentsTable() {
        JTable table = AppUI.table(appointmentModel);
        addStatusColors(table);
        return AppUI.tableScrollPane(table);
    }

    public void refreshDashboard() {
        try {
            if (loggedInDoctor == null) {
                loggedInDoctor = doctorService.getDoctorByUserId(loggedInUser.getUserId());
            }

            todayValue.setText(String.valueOf(doctorService.getTodayAppointmentsCount(loggedInDoctor.getName())));
            upcomingValue.setText(String.valueOf(doctorService.getUpcomingAppointmentCount(loggedInDoctor.getName())));
            completedValue.setText(String.valueOf(doctorService.getCompletedTodayCount(loggedInDoctor.getName())));
            pendingValue.setText(String.valueOf(doctorService.getPendingRecordsCount(loggedInDoctor.getName())));

            appointmentModel.setRowCount(0);
            List<AppointmentDTO> appointments = doctorService.getAppointmentsForDoctor(loggedInDoctor.getDoctorId());
            for (AppointmentDTO appointment : appointments) {
                appointmentModel.addRow(new Object[]{
                        appointment.getAppointmentId(),
                        appointment.getPatientName(),
                        appointment.getAppointmentDate(),
                        formatHour(appointment.getAppointmentHour()),
                        appointment.getPatientDescription(),
                        appointment.getStatus()
                });
            }
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Unable to Load Doctor Dashboard",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String formatHour(int hour) {
        int displayHour = hour % 12 == 0 ? 12 : hour % 12;
        String suffix = hour < 12 ? "AM" : "PM";
        return String.format("%02d:00 %s", displayHour, suffix);
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
