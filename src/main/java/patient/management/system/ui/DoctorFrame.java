package patient.management.system.ui;

import patient.management.system.model.User;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class DoctorFrame extends JFrame {
    private static final Color SIDEBAR_BUTTON = new Color(0x173B5C);
    private static final Color SIDEBAR_BUTTON_SHADOW = new Color(0x092238);

    private final JFrame loginFrame;
    private final User loggedInUser;

    public DoctorFrame(JFrame loginFrame, User loggedInUser) {
        this.loginFrame = loginFrame;
        this.loggedInUser = loggedInUser;

        setTitle("Doctor - CityCare Hospital");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(1100, 650));
        mainPanel.add(buildSidebar(), BorderLayout.WEST);
        mainPanel.add(new DoctorDashboardPanel(loggedInUser), BorderLayout.CENTER);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(UITheme.NAVY);
        sidebar.setPreferredSize(new Dimension(220, 650));
        sidebar.setBorder(BorderFactory.createEmptyBorder(22, 16, 22, 16));

        JLabel nameLabel = new JLabel(loggedInUser.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(UITheme.HEADING_FONT);

        JLabel roleLabel = new JLabel("Doctor");
        roleLabel.setForeground(new Color(0xBFD9E4));
        roleLabel.setFont(UITheme.BODY_FONT);

        JButton dashboardButton = sidebarButton("Dashboard");
        dashboardButton.setBackground(UITheme.BLUE);

        JButton logoutButton = sidebarButton("Logout");
        logoutButton.addActionListener(event -> logout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 2, 0);
        sidebar.add(nameLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 26, 0);
        sidebar.add(roleLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(4, 0, 4, 0);
        sidebar.add(dashboardButton, gbc);

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(140, 0, 0, 0);
        sidebar.add(logoutButton, gbc);

        return sidebar;
    }

    private JButton sidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(SIDEBAR_BUTTON);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 3, SIDEBAR_BUTTON_SHADOW),
                BorderFactory.createEmptyBorder(10, 13, 9, 12)
        ));
        return button;
    }

    private void logout() {
        dispose();
        loginFrame.setVisible(true);
    }
}
