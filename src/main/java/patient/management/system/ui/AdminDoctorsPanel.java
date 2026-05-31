package patient.management.system.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class AdminDoctorsPanel extends JPanel {
    private final DefaultTableModel doctorModel;

    private static final String[] FULL_DAYS = {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    private static final String[] SHIFTS = {"MORNING", "EVENING"};

    public AdminDoctorsPanel() {
        setLayout(new BorderLayout());
        JPanel panel = AdminUI.basePanel("Manage Doctors", "Add, update schedule/fee, and remove doctor records");

        String[] columns = {"Doctor ID", "Name", "Specialization", "Schedule", "Fee (PKR)", "Status"};
        Object[][] data = {
                {"D001", "Dr. Ahmed Khan",   "Cardiology",  "Monday - Friday   | MORNING", "2500", "Available"},
                {"D002", "Dr. Sara Ali",     "Pediatrics",  "Monday - Saturday | MORNING", "2000", "Available"},
                {"D003", "Dr. Bilal Sheikh", "Orthopedics", "Monday - Friday   | EVENING", "2200", "Busy"},
                {"D004", "Dr. Hina Noor",    "Gynecology",  "Monday - Saturday | MORNING", "2300", "Inactive"}
        };

        doctorModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = AdminUI.table(doctorModel);
        table.getColumnModel().getColumn(5).setCellRenderer(new AdminUI.StatusRenderer());

        JButton addButton        = AdminUI.actionButton("Add Doctor",          AdminUI.GREEN);
        JButton updateButton     = AdminUI.actionButton("Update Schedule/Fee", AdminUI.ORANGE);
        JButton deactivateButton = AdminUI.actionButton("Deactivate Doctor",   AdminUI.RED);
        JButton clearButton      = AdminUI.actionButton("Clear",               AdminUI.BLUE);

        addButton.addActionListener(event    -> openAddDoctorForm());
        updateButton.addActionListener(event -> updateSelectedDoctor(table));
        deactivateButton.addActionListener(event -> deactivateSelectedDoctor(table));
        clearButton.addActionListener(event  -> table.clearSelection());

        panel.add(AdminUI.searchBar("Search doctor by name or ID", table, 0, 1), BorderLayout.BEFORE_FIRST_LINE);
        panel.add(AdminUI.tableCard(table),                                       BorderLayout.CENTER);
        panel.add(AdminUI.buttonBar(addButton, updateButton, deactivateButton, clearButton), BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
    }

    // -----------------------------------------------------------------------
    // Add Doctor Form
    // -----------------------------------------------------------------------
    private void openAddDoctorForm() {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Add Doctor", true);
        dialog.setSize(500, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        form.setBackground(AdminUI.LIGHT);

        JTextField    usernameField      = new JTextField();
        JPasswordField passwordField     = new JPasswordField();
        JTextField    nameField          = new JTextField();
        JTextField    specializationField = new JTextField();
        JTextField    feeField           = new JTextField();

        JComboBox<String> fromDayBox = new JComboBox<>(FULL_DAYS);
        JComboBox<String> toDayBox   = new JComboBox<>(FULL_DAYS);
        toDayBox.setSelectedIndex(4);

        JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        dayPanel.setBackground(AdminUI.LIGHT);
        dayPanel.add(fromDayBox);
        dayPanel.add(new JLabel("to"));
        dayPanel.add(toDayBox);

        JComboBox<String> shiftBox = new JComboBox<>(SHIFTS);

        form.add(AdminUI.label("Username:"));        form.add(usernameField);
        form.add(AdminUI.label("Password:"));        form.add(passwordField);
        form.add(AdminUI.label("Full Name:"));       form.add(nameField);
        form.add(AdminUI.label("Specialization:"));  form.add(specializationField);
        form.add(AdminUI.label("Appointment Fee:")); form.add(feeField);
        form.add(AdminUI.label("Days:"));            form.add(dayPanel);
        form.add(AdminUI.label("Shift:"));           form.add(shiftBox);

        JButton submitButton = AdminUI.actionButton("Add Doctor", AdminUI.GREEN);
        submitButton.addActionListener(event -> {
            String username       = usernameField.getText().trim();
            String password       = new String(passwordField.getPassword()).trim();
            String name           = nameField.getText().trim();
            String specialization = specializationField.getText().trim();
            String fee            = feeField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty()
                    || specialization.isEmpty() || fee.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String fromDay  = (String) fromDayBox.getSelectedItem();
            String toDay    = (String) toDayBox.getSelectedItem();
            String shift    = (String) shiftBox.getSelectedItem();
            String schedule = fromDay + " - " + toDay + " | " + shift;

            String doctorId = "D" + String.format("%03d", doctorModel.getRowCount() + 1);
            doctorModel.addRow(new Object[]{doctorId, name, specialization, schedule, fee, "Available"});
            JOptionPane.showMessageDialog(this, "Doctor added successfully.");
            dialog.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(AdminUI.LIGHT);
        buttonPanel.add(submitButton);

        dialog.add(form,        BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // -----------------------------------------------------------------------
    // Update Schedule / Fee
    // -----------------------------------------------------------------------
    private void updateSelectedDoctor(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor row to update.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        openUpdateScheduleFeeForm(table.convertRowIndexToModel(row));
    }

    private void openUpdateScheduleFeeForm(int modelRow) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Update Schedule / Fee", true);
        dialog.setSize(460, 310);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        form.setBackground(AdminUI.LIGHT);

        JLabel doctorName = new JLabel(doctorModel.getValueAt(modelRow, 1).toString());
        doctorName.setFont(UITheme.BUTTON_FONT);
        doctorName.setForeground(AdminUI.NAVY);

        JComboBox<String> fromDayBox = new JComboBox<>(FULL_DAYS);
        JComboBox<String> toDayBox   = new JComboBox<>(FULL_DAYS);
        toDayBox.setSelectedIndex(4);

        String existingSchedule = doctorModel.getValueAt(modelRow, 3).toString();
        preselectDayBoxes(existingSchedule, fromDayBox, toDayBox);

        JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        dayPanel.setBackground(AdminUI.LIGHT);
        dayPanel.add(fromDayBox);
        dayPanel.add(new JLabel("to"));
        dayPanel.add(toDayBox);

        JComboBox<String> shiftBox = new JComboBox<>(SHIFTS);
        preselectShift(existingSchedule, shiftBox);

        JTextField feeField = new JTextField(doctorModel.getValueAt(modelRow, 4).toString());

        form.add(AdminUI.label("Doctor:"));    form.add(doctorName);
        form.add(AdminUI.label("Days:"));      form.add(dayPanel);
        form.add(AdminUI.label("Shift:"));     form.add(shiftBox);
        form.add(AdminUI.label("Fee (PKR):")); form.add(feeField);

        JButton submitButton = AdminUI.actionButton("Update", AdminUI.ORANGE);
        submitButton.addActionListener(event -> {
            String fee = feeField.getText().trim();
            if (fee.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Fee is required.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String fromDay  = (String) fromDayBox.getSelectedItem();
            String toDay    = (String) toDayBox.getSelectedItem();
            String shift    = (String) shiftBox.getSelectedItem();
            String schedule = fromDay + " - " + toDay + " | " + shift;

            doctorModel.setValueAt(schedule, modelRow, 3);
            doctorModel.setValueAt(fee,      modelRow, 4);
            JOptionPane.showMessageDialog(this, "Schedule and fee updated successfully.");
            dialog.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(AdminUI.LIGHT);
        buttonPanel.add(submitButton);

        dialog.add(form,        BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Deactivate Doctor 
    private void deactivateSelectedDoctor(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor row to deactivate.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        String name  = doctorModel.getValueAt(modelRow, 1).toString();
        int confirm  = JOptionPane.showConfirmDialog(this,
                "Deactivate " + name + "?", "Confirm Deactivation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            doctorModel.setValueAt("Inactive", modelRow, 5);
            JOptionPane.showMessageDialog(this, "Doctor deactivated successfully.");
        }
    }

    
    // Helpers
    private void preselectDayBoxes(String schedule,
                                    JComboBox<String> fromBox,
                                    JComboBox<String> toBox) {
        try {
            String daysPart = schedule.split("\\|")[0].trim();
            String[] days   = daysPart.split("-");
            if (days.length >= 2) {
                String from = days[0].trim();
                String to   = days[1].trim();
                for (int i = 0; i < FULL_DAYS.length; i++) {
                    if (FULL_DAYS[i].equalsIgnoreCase(from)) fromBox.setSelectedIndex(i);
                    if (FULL_DAYS[i].equalsIgnoreCase(to))   toBox.setSelectedIndex(i);
                }
            }
        } catch (Exception ignored) {}
    }

    private void preselectShift(String schedule, JComboBox<String> shiftBox) {
        try {
            String shiftPart = schedule.split("\\|")[1].trim();
            for (int i = 0; i < SHIFTS.length; i++) {
                if (SHIFTS[i].equalsIgnoreCase(shiftPart)) {
                    shiftBox.setSelectedIndex(i);
                    break;
                }
            }
        } catch (Exception ignored) {}
    }
}