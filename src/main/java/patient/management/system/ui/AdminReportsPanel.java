package patient.management.system.ui;

import patient.management.system.dto.AppointmentReportDTO;
import patient.management.system.dto.EmergencyReportDTO;
import patient.management.system.service.AdminService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class AdminReportsPanel extends JPanel {
    private final AdminService adminService = new AdminService();

    public AdminReportsPanel() {
        setLayout(new BorderLayout());
        
        JPanel panel = AdminUI.basePanel("View Reports", "Hospital activity summary: appointments, finances, and emergency statistics");

        JPanel cards = new JPanel(new GridLayout(2, 4, 14, 14));
        cards.setBackground(AdminUI.LIGHT);

        cards.add(AdminUI.infoCard("Total Appointments", String.valueOf(adminService.getTotalBookedAppointmentsCount()), AdminUI.BLUE));
        cards.add(AdminUI.infoCard("Canceled", String.valueOf(adminService.getTotalCancelledAppointmentsCount()), AdminUI.RED));
        cards.add(AdminUI.infoCard("Rescheduled", String.valueOf(adminService.getTotalRescheduledAppointmentsCount()), AdminUI.ORANGE));
        cards.add(AdminUI.infoCard("Fees Collected", "PKR " + adminService.getTotalFeesCollected(), AdminUI.GREEN));
        cards.add(AdminUI.infoCard("Total Refunds", String.valueOf(adminService.getTotalRefundsCount()), AdminUI.PURPLE));
        cards.add(AdminUI.infoCard("Net Amount", "PKR " + adminService.getNetAmount(), AdminUI.ORANGE));
        cards.add(AdminUI.infoCard("Emergency Cases", String.valueOf(adminService.getTotalEmergencyCasesCount()), AdminUI.RED));
        cards.add(AdminUI.infoCard("Unlinked Temp Patients", String.valueOf(adminService.getTemporaryLinksCount()), AdminUI.NAVY));

        JButton appointmentReportButton = AdminUI.actionButton("Appointment Report", AdminUI.BLUE);
        JButton emergencyReportButton = AdminUI.actionButton("Emergency Report", AdminUI.BLUE);

        appointmentReportButton.addActionListener(event -> showAppointmentReportDialog());
        emergencyReportButton.addActionListener(event -> showEmergencyReportDialog());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        buttonPanel.setBackground(AdminUI.LIGHT);
        buttonPanel.add(appointmentReportButton);
        buttonPanel.add(emergencyReportButton);

        JPanel content = new JPanel(new BorderLayout(0, 24));
        content.setBackground(AdminUI.LIGHT);
        content.add(cards, BorderLayout.NORTH);
        content.add(buttonPanel, BorderLayout.CENTER);

        panel.add(content, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);
    }

    private void showAppointmentReportDialog() {
        String[] columns = {"Doctor ID", "Doctor Name", "Total Appointments", "Completed", "Canceled", "Rescheduled"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            List<AppointmentReportDTO> reports = adminService.getAppointmentReports();

            for (AppointmentReportDTO report : reports) {
                model.addRow(new Object[]{
                        report.getDoctorId(),
                        report.getDoctorName(),
                        report.getTotalAppointments(),
                        report.getCompletedAppointments(),
                        report.getCancelledAppointments(),
                        report.getRescheduledAppointments()
                });
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Appointment Report Error", JOptionPane.ERROR_MESSAGE);
        }

        showReportDialog("Appointment Report Summary", model, -1);
    }

    private void showEmergencyReportDialog() {
        String[] columns = {"Triage Color", "Total Cases", "Completed", "Moved to ICU", "Deceased"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            List<EmergencyReportDTO> reports = adminService.getEmergencyReport();

            for (EmergencyReportDTO report : reports) {
                model.addRow(new Object[]{
                        report.getTriageColor(),
                        report.getTotalCases(),
                        report.getCompleted(),
                        report.getMovedToICU(),
                        report.getDeceased()
                });
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Emergency Report Error", JOptionPane.ERROR_MESSAGE);
        }

        showReportDialog("Emergency Report Summary", model, 0);
    }

    private void showReportDialog(String title, DefaultTableModel model, int triageColumn) {
        JDialog dialog = new JDialog((java.awt.Frame) null, title, true);
        dialog.setSize(820, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTable table = AdminUI.table(model);

        if (triageColumn >= 0) {
            table.getColumnModel().getColumn(triageColumn).setCellRenderer(new AdminUI.TriageRenderer());
        }

        JScrollPane scrollPane = AdminUI.tableCard(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel header = new JPanel();
        header.setBackground(AdminUI.NAVY);
        header.setPreferredSize(new Dimension(820, 52));

        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(UITheme.HEADING_FONT);
        headerLabel.setForeground(Color.WHITE);
        header.add(headerLabel);

        dialog.add(header, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}