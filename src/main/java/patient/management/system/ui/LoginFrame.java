package patient.management.system.ui;

import patient.management.system.model.Role;
import patient.management.system.model.User;
import patient.management.system.model.EmergencyTeam;
import patient.management.system.service.LoginService;
import patient.management.system.service.EmergencyTeamService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class LoginFrame extends JFrame {
    private final JComboBox<Role> roleComboBox;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginFrame() {
        roleComboBox = new JComboBox<>(Role.values());
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");

        setTitle("CityCare Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel page = new LoginBackgroundPanel();
        page.setLayout(new GridBagLayout());
        page.setPreferredSize(new Dimension(920, 560));

        JPanel brandPanel = buildBrandPanel();
        JPanel loginCard = buildLoginCard();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.45;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        page.add(brandPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.55;
        gbc.insets = new Insets(0, 18, 0, 42);
        page.add(loginCard, gbc);

        add(page);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildBrandPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel brand = new JPanel(new GridBagLayout());
        brand.setOpaque(false);

        JPanel logo = new HospitalLogoPanel();
        logo.setPreferredSize(new Dimension(96, 96));

        JLabel title = new JLabel("CityCare Hospital");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(UITheme.NAVY);

        JLabel subtitle = new JLabel("Management System");
        subtitle.setFont(UITheme.HEADING_FONT);
        subtitle.setForeground(UITheme.TEXT);

        JLabel footer = new JLabel("Patient Management System");
        footer.setFont(UITheme.BODY_FONT);
        footer.setForeground(UITheme.MUTED_TEXT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 18, 0);
        brand.add(logo, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 3, 0);
        brand.add(title, gbc);

        gbc.gridy = 2;
        brand.add(subtitle, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(24, 0, 0, 0);
        brand.add(footer, gbc);

        wrapper.add(brand);
        return wrapper;
    }

    private JPanel buildLoginCard() {
        JPanel card = new RoundedPanel(16);
        card.setLayout(new GridBagLayout());
        card.setBackground(UITheme.CARD);
        card.setBorder(BorderFactory.createEmptyBorder(32, 34, 32, 34));
        card.setPreferredSize(new Dimension(370, 430));

        JLabel heading = new JLabel("Welcome Back");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setForeground(UITheme.NAVY);

        JLabel hint = new JLabel("Sign in with your hospital account");
        hint.setFont(UITheme.BODY_FONT);
        hint.setForeground(UITheme.MUTED_TEXT);

        styleComboBox(roleComboBox);
        styleTextField(usernameField, "Enter username");
        styleTextField(passwordField, "Enter password");
        styleButton(loginButton);
        loginButton.addActionListener(event -> login());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 2, 0);
        card.add(heading, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 28, 0);
        card.add(hint, gbc);

        addLabeledField(card, "Role", roleComboBox, 2);
        addLabeledField(card, "Username", usernameField, 4);
        addLabeledField(card, "Password", passwordField, 6);

        gbc.gridy = 8;
        gbc.insets = new Insets(18, 0, 0, 0);
        card.add(loginButton, gbc);

        JPanel credentialsHint = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        credentialsHint.setOpaque(false);
        JLabel hintText = new JLabel("Try: admin/admin123, receptionist/recep123, doctor/doc123");
        hintText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hintText.setForeground(UITheme.MUTED_TEXT);
        credentialsHint.add(hintText);

        gbc.gridy = 9;
        gbc.insets = new Insets(16, 0, 0, 0);
        card.add(credentialsHint, gbc);

        return card;
    }

    private void addLabeledField(JPanel parent, String labelText, java.awt.Component field, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.LABEL_FONT);
        label.setForeground(UITheme.TEXT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 6, 0);
        parent.add(label, gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        parent.add(field, gbc);
    }

    private void styleComboBox(JComboBox<Role> comboBox) {
        comboBox.setFont(UITheme.BODY_FONT);
        comboBox.setBackground(UITheme.FIELD_BACKGROUND);
        comboBox.setForeground(UITheme.TEXT);
        comboBox.setFocusable(false);
        comboBox.setPreferredSize(new Dimension(280, 38));
        comboBox.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
    }

    private void styleTextField(JTextField field, String tooltip) {
        field.setFont(UITheme.BODY_FONT);
        field.setBackground(UITheme.FIELD_BACKGROUND);
        field.setForeground(UITheme.TEXT);
        field.setCaretColor(UITheme.BLUE);
        field.setToolTipText(tooltip);
        field.setPreferredSize(new Dimension(280, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
    }

    private void styleButton(JButton button) {
        button.setFont(UITheme.BUTTON_FONT);
        button.setBackground(UITheme.BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(280, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
    }

    private void login() {
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (selectedRole == null || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a role and enter username/password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        /*
         * Backend integration point:
         *
         */
        User loggedInUser;

        try {
            LoginService loginService = new LoginService();
            loggedInUser = loginService.login( username, password, selectedRole.toString());

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (loggedInUser.getRole().equalsIgnoreCase("ADMIN")) {
            setVisible(false);
            new AdminFrame(this, loggedInUser);
        } else if (loggedInUser.getRole().equalsIgnoreCase("RECEPTIONIST")) {
            setVisible(false);
            new ReceptionistFrame(this, loggedInUser);
        } else if (loggedInUser.getRole().equalsIgnoreCase("EMERGENCY_TEAM")) {
            try {
                EmergencyTeamService emergencyTeamService = new EmergencyTeamService();
                EmergencyTeam team = emergencyTeamService.getEmergencyTeam(
                        loggedInUser.getUserId()
                );

                setVisible(false);
                new EmergencyTeamFrame(this, loggedInUser, team);

            } catch (RuntimeException exception) {
                JOptionPane.showMessageDialog(
                        this,
                        exception.getMessage(),
                        "Emergency Team Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else if (loggedInUser.getRole().equalsIgnoreCase("DOCTOR")) {
            setVisible(false);
            new DoctorFrame(this, loggedInUser);
        }

        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocusInWindow();
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;

        RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillRoundRect(4, 8, getWidth() - 8, getHeight() - 10, radius, radius);
            g2.setPaint(new GradientPaint(0, 0, UITheme.CARD, 0, getHeight(), UITheme.CARD_BOTTOM));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(UITheme.BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class LoginBackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, new Color(0xF7FBFF), getWidth(), getHeight(), new Color(0xEAF3FB)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(0xDDECF7));
            g2.fillOval(-80, 380, 220, 220);
            g2.setColor(new Color(0xE7F6F4));
            g2.fillOval(250, -70, 190, 190);
            g2.dispose();
        }
    }

    private static class HospitalLogoPanel extends JPanel {
        HospitalLogoPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 18));
            g2.fill(new RoundRectangle2D.Double(7, 9, 82, 82, 24, 24));
            g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(0xEFF8FA)));
            g2.fill(new RoundRectangle2D.Double(5, 4, 82, 82, 24, 24));
            g2.setColor(new Color(0xB8D5F5));
            g2.setStroke(new BasicStroke(3f));
            g2.draw(new RoundRectangle2D.Double(5, 4, 82, 82, 24, 24));

            g2.setColor(UITheme.TEAL);
            g2.fillRoundRect(41, 22, 11, 42, 6, 6);
            g2.fillRoundRect(25, 38, 43, 11, 6, 6);

            g2.dispose();
        }
    }
}
