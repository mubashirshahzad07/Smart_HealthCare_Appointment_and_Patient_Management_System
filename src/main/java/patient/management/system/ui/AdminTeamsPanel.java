package patient.management.system.ui;

import patient.management.system.dto.EmergencyTeamDTO;
import patient.management.system.service.AdminService;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

public class AdminTeamsPanel extends JPanel {
    private final AdminService adminService = new AdminService();

    public AdminTeamsPanel() {
        setLayout(new BorderLayout());
        JPanel panel = AdminUI.basePanel("Emergency Teams", "View emergency response teams and triage assignments");

        String[] columns = {"Team ID", "Team Name", "Username", "Triage Color"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            List<EmergencyTeamDTO> teams = adminService.getEmergencyTeams();

            for (EmergencyTeamDTO team : teams) {
                model.addRow(new Object[]{
                        team.getTeamId(),
                        team.getName(),
                        team.getUsername(),
                        team.getTriageColor()
                });
            }

        } catch (RuntimeException e) {
            model.addRow(new Object[]{"", "Unable to load teams", e.getMessage(), ""});
        }

        JTable table = AdminUI.table(model);
        table.getColumnModel().getColumn(3).setCellRenderer(new AdminUI.TriageRenderer());

        JLabel note = new JLabel("Teams are assigned triage colors by the emergency workflow.");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        note.setForeground(UITheme.MUTED_TEXT);

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(AdminUI.LIGHT);
        south.add(note, BorderLayout.WEST);

        panel.add(AdminUI.tableCard(table), BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
    }
}