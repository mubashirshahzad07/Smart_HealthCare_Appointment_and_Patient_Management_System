package patient.management.system.ui;

import patient.management.system.model.User;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReceptionistFrame extends JFrame {
    private static final Color SIDEBAR_BUTTON = new Color(0x173B5C);
    private static final Color SIDEBAR_BUTTON_HOVER = new Color(0x214F78);
    private static final Color SIDEBAR_BUTTON_SHADOW = new Color(0x092238);

    private final JFrame loginFrame;
    private final User loggedInUser;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final Map<String, JButton> menuButtons;
    private ReceptionistDashboardPanel dashboardPanel;
    private RegisterPatientPanel registerPatientPanel;
    private BookAppointmentPanel bookAppointmentPanel;
    private ManageAppointmentsPanel manageAppointmentsPanel;
    private EmergencyCaseEntryPanel emergencyCaseEntryPanel;
    private LinkTemporaryPatientPanel linkTemporaryPatientPanel;

    public ReceptionistFrame(JFrame loginFrame, User user) {
        this.loginFrame = loginFrame;
        this.loggedInUser = user;
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        menuButtons = new LinkedHashMap<>();

        setTitle("Receptionist - CityCare Hospital");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(1100, 650));
        mainPanel.add(buildSidebar(user), BorderLayout.WEST);
        mainPanel.add(buildCards(), BorderLayout.CENTER);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildSidebar(User user) {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(UITheme.NAVY);
        sidebar.setPreferredSize(new Dimension(220, 650));
        sidebar.setBorder(BorderFactory.createEmptyBorder(22, 16, 22, 16));

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(UITheme.HEADING_FONT);

        JLabel roleLabel = new JLabel("Receptionist");
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
        gbc.insets = new Insets(0, 0, 26, 0);
        sidebar.add(roleLabel, gbc);

        addMenuButton(sidebar, "Dashboard", "DASHBOARD", 2);
        addMenuButton(sidebar, "Register Patient", "REGISTER_PATIENT", 3);
        addMenuButton(sidebar, "Book Appointment", "BOOK_APPOINTMENT", 4);
        addMenuButton(sidebar, "Manage Appointments", "MANAGE_APPOINTMENTS", 5);
        addMenuButton(sidebar, "Register Emergency", "CREATE_EMERGENCY", 6);
        addMenuButton(sidebar, "Link Patient", "LINK_TEMPORARY", 7);

        JButton logoutButton = sidebarButton("Logout");
        logoutButton.addActionListener(event -> logout());
        gbc.gridy = 8;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(140, 0, 0, 0);
        sidebar.add(logoutButton, gbc);

        setActiveMenu("DASHBOARD");
        return sidebar;
    }

    private JPanel buildCards() {
        dashboardPanel = new ReceptionistDashboardPanel(loggedInUser);
        registerPatientPanel = new RegisterPatientPanel(loggedInUser);
        bookAppointmentPanel = new BookAppointmentPanel(loggedInUser);
        manageAppointmentsPanel = new ManageAppointmentsPanel(loggedInUser);
        emergencyCaseEntryPanel = new EmergencyCaseEntryPanel(loggedInUser);
        linkTemporaryPatientPanel = new LinkTemporaryPatientPanel(loggedInUser);

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(registerPatientPanel, "REGISTER_PATIENT");
        contentPanel.add(bookAppointmentPanel, "BOOK_APPOINTMENT");
        contentPanel.add(manageAppointmentsPanel, "MANAGE_APPOINTMENTS");
        contentPanel.add(emergencyCaseEntryPanel, "CREATE_EMERGENCY");
        contentPanel.add(linkTemporaryPatientPanel, "LINK_TEMPORARY");
        return contentPanel;
    }

    private void addMenuButton(JPanel sidebar, String label, String cardName, int row) {
        JButton button = sidebarButton(label);
        button.addActionListener(event -> {
            refreshCard(cardName);
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

    private void refreshCard(String cardName) {
        if ("DASHBOARD".equals(cardName)) {
            dashboardPanel.refreshDashboard();
        } else if ("REGISTER_PATIENT".equals(cardName)) {
            registerPatientPanel.refreshPatients();
        } else if ("BOOK_APPOINTMENT".equals(cardName)) {
            bookAppointmentPanel.refreshPanel();
        } else if ("MANAGE_APPOINTMENTS".equals(cardName)) {
            manageAppointmentsPanel.refreshAppointments();
        } else if ("CREATE_EMERGENCY".equals(cardName)) {
            emergencyCaseEntryPanel.refreshPatients();
        } else if ("LINK_TEMPORARY".equals(cardName)) {
            linkTemporaryPatientPanel.refreshPatients();
        }
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

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                if (button.getBackground() != UITheme.BLUE) {
                    button.setBackground(SIDEBAR_BUTTON_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                if (button.getBackground() != UITheme.BLUE) {
                    button.setBackground(SIDEBAR_BUTTON);
                }
            }
        });

        return button;
    }

    private void setActiveMenu(String activeCard) {
        for (String cardName : menuButtons.keySet()) {
            JButton button = menuButtons.get(cardName);
            button.setBackground(cardName.equals(activeCard) ? UITheme.BLUE : SIDEBAR_BUTTON);
        }
    }

    private void logout() {
        dispose();
        loginFrame.setVisible(true);
    }
}
