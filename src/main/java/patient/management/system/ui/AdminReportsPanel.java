package patient.management.system.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class AdminReportsPanel extends JPanel {
    public AdminReportsPanel() {
        setLayout(new BorderLayout());
        JPanel panel = AdminUI.basePanel("View Reports", "Hospital activity summary: appointments, finances, and emergency statistics");

        JPanel cards = new JPanel(new GridLayout(2, 4, 14, 14));
        cards.setBackground(AdminUI.LIGHT);
        cards.add(AdminUI.infoCard("Total Appointments", "124", AdminUI.BLUE));
        cards.add(AdminUI.infoCard("Canceled", "18", AdminUI.RED));
        cards.add(AdminUI.infoCard("Rescheduled", "12", AdminUI.ORANGE));
        cards.add(AdminUI.infoCard("Fees Collected", "PKR 186,500", AdminUI.GREEN));
        cards.add(AdminUI.infoCard("Total Refunds", "8", AdminUI.PURPLE));
        cards.add(AdminUI.infoCard("Net Amount", "PKR 172,100", AdminUI.ORANGE));
        cards.add(AdminUI.infoCard("Emergency Cases", "31", AdminUI.RED));
        cards.add(AdminUI.infoCard("Unlinked Temp Patients", "7", AdminUI.NAVY));

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
        String[] columns = {"Doctor", "Total Appointments", "Completed", "Canceled", "Rescheduled"};
        Object[][] data = {
                {"Dr. Ahmed Khan", "38", "28", "6", "4"},
                {"Dr. Sara Ali", "32", "22", "5", "5"},
                {"Dr. Bilal Sheikh", "29", "20", "4", "5"},
                {"Dr. Hina Noor", "25", "18", "3", "4"}
        };
        showReportDialog("Appointment Report Summary", columns, data, -1);
    }

    private void showEmergencyReportDialog() {
        String[] columns = {"Triage Color", "Total Cases", "Completed", "Moved to ICU", "Deceased"};
        Object[][] data = {
                {"RED (Immediate)", "14", "8", "4", "2"},
                {"YELLOW (Delayed)", "9", "7", "1", "1"},
                {"GREEN (Minor)", "6", "6", "0", "0"},
                {"BLACK (Deceased)", "2", "0", "0", "2"}
        };
        showReportDialog("Emergency Report Summary", columns, data, 0);
    }

    private void showReportDialog(String title, String[] columns, Object[][] data, int triageColumn) {
        JDialog dialog = new JDialog((java.awt.Frame) null, title, true);
        dialog.setSize(820, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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
