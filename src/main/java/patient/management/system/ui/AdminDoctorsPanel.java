package pms.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

    public AdminDoctorsPanel() {
        setLayout(new BorderLayout());
        JPanel panel = AdminUI.basePanel("Manage Doctors", "Add, update schedule/fee, and remove doctor records");

        String[] columns = {"Doctor ID", "Name", "Specialization", "Schedule", "Fee (PKR)", "Status"};
        Object[][] data = {
                {"D001", "Dr. Ahmed Khan", "Cardiology", "Mon-Fri 10:00-15:00", "2500", "Available"},
                {"D002", "Dr. Sara Ali", "Pediatrics", "Mon-Sat 09:00-14:00", "2000", "Available"},
                {"D003", "Dr. Bilal Sheikh", "Orthopedics", "Mon-Fri 14:00-19:00", "2200", "Busy"},
                {"D004", "Dr. Hina Noor", "Gynecology", "Mon-Sat 11:00-17:00", "2300", "Inactive"}
        };

        doctorModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = AdminUI.table(doctorModel);
        table.getColumnModel().getColumn(5).setCellRenderer(new AdminUI.StatusRenderer());

        JButton addButton = AdminUI.actionButton("Add Doctor", AdminUI.GREEN);
        JButton updateButton = AdminUI.actionButton("Update Schedule/Fee", AdminUI.ORANGE);
        JButton removeButton = AdminUI.actionButton("Remove Doctor", AdminUI.RED);
        JButton clearButton = AdminUI.actionButton("Clear", AdminUI.BLUE);

        addButton.addActionListener(event -> openAddDoctorForm());
        updateButton.addActionListener(event -> updateSelectedDoctor(table));
        removeButton.addActionListener(event -> removeSelectedDoctor(table));
        clearButton.addActionListener(event -> table.clearSelection());

        panel.add(AdminUI.searchBar("Search doctor by name or ID", table, 0, 1), BorderLayout.BEFORE_FIRST_LINE);
        panel.add(AdminUI.tableCard(table), BorderLayout.CENTER);
        panel.add(AdminUI.buttonBar(addButton, updateButton, removeButton, clearButton), BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
    }

    private void openAddDoctorForm() {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Add Doctor", true);
        dialog.setSize(460, 390);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        form.setBackground(AdminUI.LIGHT);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField nameField = new JTextField();
        JTextField specializationField = new JTextField();
        JTextField feeField = new JTextField();
        JTextField scheduleField = new JTextField("e.g. Mon-Fri 09:00-17:00");

        form.add(AdminUI.label("Username:"));
        form.add(usernameField);
        form.add(AdminUI.label("Password:"));
        form.add(passwordField);
        form.add(AdminUI.label("Full Name:"));
        form.add(nameField);
        form.add(AdminUI.label("Specialization:"));
        form.add(specializationField);
        form.add(AdminUI.label("Appointment Fee:"));
        form.add(feeField);
        form.add(AdminUI.label("Schedule:"));
        form.add(scheduleField);

        JButton submitButton = AdminUI.actionButton("Add Doctor", AdminUI.GREEN);
        submitButton.addActionListener(event -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String name = nameField.getText().trim();
            String specialization = specializationField.getText().trim();
            String fee = feeField.getText().trim();
            String schedule = scheduleField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty()
                    || specialization.isEmpty() || fee.isEmpty() || schedule.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String doctorId = "D" + String.format("%03d", doctorModel.getRowCount() + 1);
            doctorModel.addRow(new Object[]{doctorId, name, specialization, schedule, fee, "Available"});
            JOptionPane.showMessageDialog(this, "Doctor added successfully.");
            dialog.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(AdminUI.LIGHT);
        buttonPanel.add(submitButton);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateSelectedDoctor(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor row to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        openUpdateScheduleFeeForm(table.convertRowIndexToModel(row));
    }

    private void openUpdateScheduleFeeForm(int modelRow) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Update Schedule / Fee", true);
        dialog.setSize(430, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        form.setBackground(AdminUI.LIGHT);

        JLabel doctorName = new JLabel(doctorModel.getValueAt(modelRow, 1).toString());
        doctorName.setFont(UITheme.BUTTON_FONT);
        doctorName.setForeground(AdminUI.NAVY);

        JTextField scheduleField = new JTextField(doctorModel.getValueAt(modelRow, 3).toString());
        JTextField feeField = new JTextField(doctorModel.getValueAt(modelRow, 4).toString());

        form.add(AdminUI.label("Doctor:"));
        form.add(doctorName);
        form.add(AdminUI.label("Schedule:"));
        form.add(scheduleField);
        form.add(AdminUI.label("Fee (PKR):"));
        form.add(feeField);

        JButton submitButton = AdminUI.actionButton("Update", AdminUI.ORANGE);
        submitButton.addActionListener(event -> {
            String schedule = scheduleField.getText().trim();
            String fee = feeField.getText().trim();
            if (schedule.isEmpty() || fee.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Both fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            doctorModel.setValueAt(schedule, modelRow, 3);
            doctorModel.setValueAt(fee, modelRow, 4);
            JOptionPane.showMessageDialog(this, "Schedule and fee updated successfully.");
            dialog.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(AdminUI.LIGHT);
        buttonPanel.add(submitButton);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void removeSelectedDoctor(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor row to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        String name = doctorModel.getValueAt(modelRow, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Remove " + name + "?", "Confirm Remove", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            doctorModel.removeRow(modelRow);
            JOptionPane.showMessageDialog(this, "Doctor removed successfully.");
        }
    }
}
