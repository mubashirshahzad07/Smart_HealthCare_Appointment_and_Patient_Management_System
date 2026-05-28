package pms.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Font;

public class AdminTeamsPanel extends JPanel {
    public AdminTeamsPanel() {
        setLayout(new BorderLayout());
        JPanel panel = AdminUI.basePanel("Emergency Teams", "View emergency response teams and triage assignments");

        String[] columns = {"Team ID", "Team Name", "Username", "Triage Color"};
        Object[][] data = {
                {"ET-001", "Red Emergency Team", "red_team", "RED"},
                {"ET-002", "Yellow Emergency Team", "yellow_team", "YELLOW"},
                {"ET-003", "Green Emergency Team", "green_team", "GREEN"},
                {"ET-004", "Black Emergency Team", "black_team", "BLACK"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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
