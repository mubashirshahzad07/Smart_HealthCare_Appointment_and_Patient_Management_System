package patient.management.system.ui;

import patient.management.system.model.User;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminFrame extends JFrame {
    private final JFrame loginFrame;
    private final User loggedInUser;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final Map<String, JButton> menuButtons;

    public AdminFrame(JFrame loginFrame, User loggedInUser) {
        this.loginFrame = loginFrame;
        this.loggedInUser = loggedInUser;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        this.menuButtons = new LinkedHashMap<>();

        setTitle("Admin - CityCare Hospital");
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
        sidebar.setBackground(AdminUI.NAVY);
        sidebar.setPreferredSize(new Dimension(220, 650));
        sidebar.setBorder(BorderFactory.createEmptyBorder(22, 16, 22, 16));

        JLabel nameLabel = new JLabel(loggedInUser.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(UITheme.HEADING_FONT);

        JLabel roleLabel = new JLabel("Administrator");
        roleLabel.setForeground(new Color(0xBFD9E4));
        roleLabel.setFont(UITheme.BODY_FONT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 2, 0);
        sidebar.add(nameLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        sidebar.add(roleLabel, gbc);

        addMenuButton(sidebar, "Dashboard", "DASHBOARD", 2);
        addMenuButton(sidebar, "Manage Doctors", "DOCTORS", 3);
        addMenuButton(sidebar, "Manage Receptionists", "RECEPTIONISTS", 4);
        addMenuButton(sidebar, "Emergency Teams", "TEAMS", 5);
        addMenuButton(sidebar, "View Reports", "REPORTS", 6);

        JButton logoutButton = sidebarButton("Logout");
        logoutButton.addActionListener(event -> logout());
        gbc.gridy = 7;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(110, 0, 0, 0);
        sidebar.add(logoutButton, gbc);

        setActiveMenu("DASHBOARD");
        return sidebar;
    }

    private JPanel buildCards() {
        contentPanel.add(new AdminDashboardPanel(), "DASHBOARD");
        contentPanel.add(new AdminDoctorsPanel(), "DOCTORS");
        contentPanel.add(new AdminReceptionistsPanel(), "RECEPTIONISTS");
        contentPanel.add(new AdminTeamsPanel(), "TEAMS");
        contentPanel.add(new AdminReportsPanel(), "REPORTS");
        return contentPanel;
    }

    private void addMenuButton(JPanel sidebar, String label, String cardName, int row) {
        JButton button = sidebarButton(label);
        button.addActionListener(event -> {
            cardLayout.show(contentPanel, cardName);
            setActiveMenu(cardName);
        });
        menuButtons.put(cardName, button);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        sidebar.add(button, gbc);
    }

    private JButton sidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(AdminUI.SIDEBAR_BUTTON);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(190, 44));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 3, AdminUI.SIDEBAR_SHADOW),
                BorderFactory.createEmptyBorder(10, 13, 9, 12)
        ));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                if (button.getBackground() != AdminUI.BLUE) {
                    button.setBackground(AdminUI.SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                if (button.getBackground() != AdminUI.BLUE) {
                    button.setBackground(AdminUI.SIDEBAR_BUTTON);
                }
            }
        });
        return button;
    }

    private void setActiveMenu(String activeCard) {
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            entry.getValue().setBackground(entry.getKey().equals(activeCard) ? AdminUI.BLUE : AdminUI.SIDEBAR_BUTTON);
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            loginFrame.setVisible(true);
        }
    }
}
