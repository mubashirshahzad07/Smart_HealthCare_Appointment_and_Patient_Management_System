package patient.management.system.ui;

import patient.management.system.dto.EmergencyCaseDTO;
import patient.management.system.model.EmergencyTeam;
import patient.management.system.model.TriageColor;
import patient.management.system.model.User;
import patient.management.system.service.EmergencyTeamService;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
    private final EmergencyTeam loggedInTeam;
    private final Color accentColor;
    private final EmergencyTeamService emergencyTeamService;
    private final JLabel activeCasesValue;
    private final JLabel completedTodayValue;
    private final JLabel movedToICUValue;
    private final JLabel movedToWardValue;
    private final DefaultTableModel caseModel;

    public EmergencyTeamDashboardPanel(User loggedInUser, EmergencyTeam loggedInTeam, Color accentColor) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        this.loggedInTeam = loggedInTeam;
        this.accentColor = accentColor;
        this.emergencyTeamService = new EmergencyTeamService();
        this.activeCasesValue = statValueLabel();
        this.completedTodayValue = statValueLabel();
        this.movedToICUValue = statValueLabel();
        this.movedToWardValue = statValueLabel();
        this.caseModel = buildCaseModel();
        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        refreshDashboard();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = AppUI.pageTitle(loggedInTeam.getTriageColor() + " Team Dashboard");

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
        stats.add(statCard("Active Cases", activeCasesValue, UITheme.SOFT_BLUE, UITheme.BLUE));
        stats.add(statCard("Completed Today", completedTodayValue, UITheme.SOFT_GREEN, UITheme.SUCCESS));
        stats.add(statCard("Moved to ICU", movedToICUValue, UITheme.SOFT_YELLOW, UITheme.WARNING));
        stats.add(statCard("Moved to Ward", movedToWardValue, UITheme.SOFT_RED, UITheme.DANGER));
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

    private JPanel buildCasesCard() {
        JPanel card = AppUI.cardPanel();
        card.add(new JLabel("Emergency Cases"), BorderLayout.NORTH);

        JTable table = AppUI.table(caseModel);
        addStatusColors(table);
        card.add(AppUI.tableScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private DefaultTableModel buildCaseModel() {
        return new DefaultTableModel(
                new String[]{"Case ID", "Patient Name", "Age", "Gender", "Initial Complaint", "Arrival Time", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public void refreshDashboard() {
        try {
            TriageColor triageColor = TriageColor.valueOf(loggedInTeam.getTriageColor());

            activeCasesValue.setText(String.valueOf(emergencyTeamService.getActiveEmergencyCasesCount(triageColor)));
            completedTodayValue.setText(String.valueOf(emergencyTeamService.getCompletedCasesTodayCount(triageColor)));
            movedToICUValue.setText(String.valueOf(emergencyTeamService.getMovedToICUCount(triageColor)));
            movedToWardValue.setText(String.valueOf(emergencyTeamService.getMovedToWardCount(triageColor)));

            caseModel.setRowCount(0);
            for (EmergencyCaseDTO emergencyCase : emergencyTeamService.getEmergencyCasesByTriageColor(triageColor)) {
                caseModel.addRow(new Object[]{
                        emergencyCase.getEmergencyCaseId(),
                        emergencyCase.getPatientName(),
                        emergencyCase.getAge(),
                        emergencyCase.getGender(),
                        emergencyCase.getInitialComplaint(),
                        emergencyCase.getArrivalTime(),
                        emergencyCase.getStatus()
                });
            }
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Unable to Load Dashboard",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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

}
