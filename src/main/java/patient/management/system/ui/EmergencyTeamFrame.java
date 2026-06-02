package patient.management.system.ui;

import patient.management.system.model.User;
import patient.management.system.model.EmergencyTeam;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.Map;

public class EmergencyTeamFrame extends JFrame {
    private static final Color SIDEBAR_BUTTON_SHADOW = new Color(0x092238);

    private final JFrame loginFrame;
    private final User loggedInUser;
    private final EmergencyTeam loggedInTeam;
    private final Color accentColor;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final Map<String, JButton> menuButtons;
    private EmergencyTeamDashboardPanel dashboardPanel;

    public EmergencyTeamFrame(JFrame loginFrame, User loggedInUser, EmergencyTeam loggedInTeam) {
        this.loginFrame = loginFrame;
        this.loggedInUser = loggedInUser;
        this.loggedInTeam = loggedInTeam;
        this.accentColor = colorForTriage(loggedInTeam.getTriageColor());
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        this.menuButtons = new LinkedHashMap<>();

        setTitle(loggedInUser.getName() + " - Emergency Team");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(1100, 650));
        mainPanel.add(buildSidebar(), BorderLayout.WEST);
        mainPanel.add(buildCards(), BorderLayout.CENTER);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(sidebarColorForTriage(loggedInTeam.getTriageColor()));
        sidebar.setPreferredSize(new Dimension(220, 650));
        sidebar.setBorder(BorderFactory.createEmptyBorder(22, 16, 22, 16));

        JLabel nameLabel = new JLabel(loggedInUser.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(UITheme.HEADING_FONT);

        JLabel triageLabel = new JLabel(loggedInTeam.getTriageColor() + " Emergency Team");
        triageLabel.setForeground(new Color(0xBFD9E4));
        triageLabel.setFont(UITheme.BODY_FONT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 2, 0);
        sidebar.add(nameLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 26, 0);
        sidebar.add(triageLabel, gbc);

        addMenuButton(sidebar, "Dashboard", "DASHBOARD", 2);
        addMenuButton(sidebar, "Medical Records", "MEDICAL_RECORDS", 3);

        JButton logoutButton = sidebarButton("Logout");
        logoutButton.addActionListener(event -> logout());
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(140, 0, 0, 0);
        sidebar.add(logoutButton, gbc);

        setActiveMenu("DASHBOARD");
        return sidebar;
    }

    private JPanel buildCards() {
        dashboardPanel = new EmergencyTeamDashboardPanel(loggedInUser, loggedInTeam, accentColor);
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(new EmergencyMedicalRecordsPanel(loggedInUser, loggedInTeam, accentColor), "MEDICAL_RECORDS");
        return contentPanel;
    }

    private void addMenuButton(JPanel sidebar, String label, String cardName, int row) {
        JButton button = sidebarButton(label);
        button.addActionListener(event -> {
            if ("DASHBOARD".equals(cardName) && dashboardPanel != null) {
                dashboardPanel.refreshDashboard();
            }
            cardLayout.show(contentPanel, cardName);
            setActiveMenu(cardName);
        });
        menuButtons.put(cardName, button);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        sidebar.add(button, gbc);
    }

    private JButton sidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(accentColor);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 3, SIDEBAR_BUTTON_SHADOW),
                BorderFactory.createEmptyBorder(10, 13, 9, 12)
        ));
        return button;
    }

    private void setActiveMenu(String activeCard) {
        for (String cardName : menuButtons.keySet()) {
            JButton button = menuButtons.get(cardName);
            button.setBackground(accentColor);
        }
    }

    private void logout() {
        dispose();
        loginFrame.setVisible(true);
    }

    static Color colorForTriage(String triageColor) {
        if ("RED".equalsIgnoreCase(triageColor)) {
            return new Color(0xE14B4B);
        }
        if ("YELLOW".equalsIgnoreCase(triageColor)) {
            return new Color(0xC99012);
        }
        if ("GREEN".equalsIgnoreCase(triageColor)) {
            return new Color(0x24B77A);
        }
        if ("BLACK".equalsIgnoreCase(triageColor)) {
            return new Color(0x343A40);
        }
        return UITheme.BLUE;
    }

    private Color sidebarColorForTriage(String triageColor) {
        if ("RED".equalsIgnoreCase(triageColor)) {
            return new Color(0xD85A5A);
        }
        if ("YELLOW".equalsIgnoreCase(triageColor)) {
            return new Color(0xB98419);
        }
        if ("GREEN".equalsIgnoreCase(triageColor)) {
            return new Color(0x36B989);
        }
        return UITheme.NAVY;
    }
}
