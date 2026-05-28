package patient.management.system.ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Dimension;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import patient.management.system.model.User;

public class ManageAppointmentsPanel extends JPanel {
    private final User loggedInUser;
    private final DefaultTableModel appointmentModel;
    private final JTable appointmentTable;
    private final TableRowSorter<DefaultTableModel> appointmentSorter;
    private final JComboBox<DoctorSchedule> doctorBox;
    private final JComboBox<AppointmentSlot> slotBox;
    private final JComboBox<String> rescheduleBox;
    private final JTextArea descriptionArea;
    private final JLabel selectedAppointmentLabel;

    public ManageAppointmentsPanel() {
        this(null);
    }

    public ManageAppointmentsPanel(User loggedInUser) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        appointmentModel = buildAppointmentModel();
        appointmentTable = AppUI.table(appointmentModel);
        appointmentSorter = new TableRowSorter<>(appointmentModel);
        appointmentTable.setRowSorter(appointmentSorter);

        doctorBox = new JComboBox<>(doctorSchedules());
        slotBox = new JComboBox<>();
        rescheduleBox = AppUI.comboBox(new String[]{"Select option", "Yes", "No"});
        descriptionArea = AppUI.textArea("Updated patient description");
        descriptionArea.setPreferredSize(new Dimension(360, 80));
        descriptionArea.setMinimumSize(new Dimension(360, 80));
        descriptionArea.setMaximumSize(new Dimension(360, 80));
        selectedAppointmentLabel = new JLabel("No appointment selected");

        add(AppUI.pageTitle("Manage Appointments"), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        updateSlots((DoctorSchedule) doctorBox.getSelectedItem());
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(18, 18));
        content.setOpaque(false);
        content.add(buildUpdateCard(), BorderLayout.NORTH);
        content.add(buildTableCard(), BorderLayout.CENTER);
        return content;
    }

    private JPanel buildUpdateCard() {
        JPanel card = AppUI.cardPanel();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        selectedAppointmentLabel.setFont(UITheme.BODY_FONT);
        selectedAppointmentLabel.setForeground(UITheme.MUTED_TEXT);
        styleComboBox(doctorBox);
        styleComboBox(slotBox);
        doctorBox.addActionListener(event -> updateSlots((DoctorSchedule) doctorBox.getSelectedItem()));

        form.add(AppUI.smallLabel("Selected Appointment"), gbc(0, 0));
        form.add(selectedAppointmentLabel, gbc(0, 1));
        form.add(AppUI.smallLabel("Doctor"), gbc(1, 0));
        form.add(doctorBox, gbc(1, 1));
        form.add(AppUI.smallLabel("New Slot"), gbc(2, 0));
        form.add(slotBox, gbc(2, 1));
        form.add(AppUI.smallLabel("Description"), gbc(0, 2));

        GridBagConstraints descriptionGbc = gbc(0, 3);
        descriptionGbc.gridwidth = 2;
        form.add(descriptionArea, descriptionGbc);

        form.add(AppUI.smallLabel("Allow Early Reschedule?"), gbc(2, 2));
        form.add(rescheduleBox, gbc(2, 3));

        JButton updateButton = AppUI.primaryButton("Update Appointment");
        JButton cancelButton = AppUI.dangerButton("Cancel Appointment");
        JButton clearButton = AppUI.secondaryButton("Clear");
        updateButton.addActionListener(event -> updateAppointment());
        cancelButton.addActionListener(event -> cancelAppointment());
        clearButton.addActionListener(event -> clearForm());

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 14, 0));
        buttons.setOpaque(false);
        buttons.add(clearButton);
        buttons.add(cancelButton);
        buttons.add(updateButton);

        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildTableCard() {
        JPanel card = AppUI.cardPanel();
        card.add(buildSearchPanel(), BorderLayout.NORTH);
        card.add(AppUI.tableScrollPane(appointmentTable), BorderLayout.CENTER);

        appointmentTable.getSelectionModel().addListSelectionListener(event -> updateSelectedAppointment());
        return card;
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setOpaque(false);

        JTextField searchField = AppUI.textField("Search by appointment, doctor, patient, status, or CNIC");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                filterAppointments(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                filterAppointments(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                filterAppointments(searchField.getText());
            }
        });

        JLabel hint = new JLabel("Only scheduled and rescheduled appointments are shown here.");
        hint.setFont(UITheme.BODY_FONT);
        hint.setForeground(UITheme.MUTED_TEXT);

        panel.add(searchField);
        panel.add(hint);
        return panel;
    }

    private DefaultTableModel buildAppointmentModel() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{
                        "Appointment ID", "Doctor", "Patient", "Date", "Time",
                        "Status", "Allow Early Reschedule", "Description"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        /*
         * Backend integration point:
         * Later, load only SCHEDULED and RESCHEDULED appointments here.
         * CANCELLED, COMPLETED, and IN_PROGRESS appointments should not appear on this screen.
         */
        model.addRow(new Object[]{"A101", "Dr. Ahmed Khan", "Ali Raza", "2026-05-27", "09:00 AM", "SCHEDULED", "Yes", "Chest pain"});
        model.addRow(new Object[]{"A102", "Dr. Sara Ali", "Sara Ali", "2026-05-27", "10:00 AM", "RESCHEDULED", "Yes", "Follow-up"});
        model.addRow(new Object[]{"A105", "Dr. Bilal Sheikh", "Bilal Ahmed", "2026-05-28", "02:00 PM", "SCHEDULED", "No", "Knee pain"});
        model.addRow(new Object[]{"A106", "Dr. Hina Noor", "Hina Noor", "2026-05-29", "11:00 AM", "SCHEDULED", "Yes", "Consultation"});

        return model;
    }

    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.ipady = 0;
        return gbc;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(UITheme.BODY_FONT);
        comboBox.setBackground(UITheme.FIELD_BACKGROUND);
        comboBox.setForeground(UITheme.TEXT);
        comboBox.setFocusable(false);
        comboBox.setPreferredSize(new Dimension(260, 34));
        comboBox.setMinimumSize(new Dimension(260, 34));
        comboBox.setMaximumSize(new Dimension(260, 34));
    }

    private void filterAppointments(String searchText) {
        String text = searchText.trim();
        if (text.isEmpty()) {
            appointmentSorter.setRowFilter(null);
        } else {
            appointmentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
        }
    }

    private void updateSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            selectedAppointmentLabel.setText("No appointment selected");
            descriptionArea.setText("");
            rescheduleBox.setSelectedIndex(0);
            return;
        }

        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        String appointmentId = appointmentModel.getValueAt(modelRow, 0).toString();
        String doctorName = appointmentModel.getValueAt(modelRow, 1).toString();
        String patientName = appointmentModel.getValueAt(modelRow, 2).toString();
        String description = appointmentModel.getValueAt(modelRow, 7).toString();
        String canReschedule = appointmentModel.getValueAt(modelRow, 6).toString();

        selectedAppointmentLabel.setText(appointmentId + " | " + patientName);
        selectDoctorByName(doctorName);
        descriptionArea.setText(description);
        rescheduleBox.setSelectedItem(canReschedule);
    }

    private void selectDoctorByName(String doctorName) {
        for (int index = 0; index < doctorBox.getItemCount(); index++) {
            DoctorSchedule schedule = doctorBox.getItemAt(index);
            if (schedule.name.equals(doctorName)) {
                doctorBox.setSelectedIndex(index);
                updateSlots(schedule);
                return;
            }
        }
    }

    private void updateSlots(DoctorSchedule schedule) {
        slotBox.removeAllItems();
        if (schedule == null) {
            slotBox.addItem(new AppointmentSlot(null, -1, "Select a doctor first"));
            return;
        }

        for (AppointmentSlot slot : generateCurrentWeekSlots(schedule)) {
            slotBox.addItem(slot);
        }
    }

    private void updateAppointment() {
        try {
            int modelRow = selectedModelRow();
            DoctorSchedule doctor = (DoctorSchedule) doctorBox.getSelectedItem();
            AppointmentSlot slot = (AppointmentSlot) slotBox.getSelectedItem();
            String description = descriptionArea.getText().trim();
            String reschedule = (String) rescheduleBox.getSelectedItem();

            if (doctor == null || slot == null || !slot.isRealSlot()
                    || description.isEmpty() || reschedule == null || reschedule.startsWith("Select")) {
                throw new IllegalArgumentException("All fields must be filled.");
            }

            /*
             * Backend integration point:
             * Later, call AppointmentService.updateAppointment(...) with:
             * appointmentId, doctor.id, slot.getYear(), slot.getMonth(), slot.getDay(),
             * slot.getHour(), description, reschedule.equals("Yes").
             * Backend should set status = RESCHEDULED.
             */
            appointmentModel.setValueAt(doctor.name, modelRow, 1);
            appointmentModel.setValueAt(slot.getDateString(), modelRow, 3);
            appointmentModel.setValueAt(slot.getTimeString(), modelRow, 4);
            appointmentModel.setValueAt("RESCHEDULED", modelRow, 5);
            appointmentModel.setValueAt(reschedule, modelRow, 6);
            appointmentModel.setValueAt(description, modelRow, 7);

            JOptionPane.showMessageDialog(this, "Appointment updated as RESCHEDULED.");
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cancelAppointment() {
        try {
            int modelRow = selectedModelRow();
            String appointmentId = appointmentModel.getValueAt(modelRow, 0).toString();

            /*
             * Backend integration point:
             * Later, call AppointmentService.cancelAppointment(appointmentId).
             * Backend should set status = CANCELLED, then this table reloads without that row.
             */
            appointmentModel.removeRow(modelRow);
            clearForm();
            JOptionPane.showMessageDialog(this, "Appointment " + appointmentId + " cancelled and removed from this list.");
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private int selectedModelRow() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            throw new IllegalArgumentException("Please select an appointment from the table.");
        }
        return appointmentTable.convertRowIndexToModel(selectedRow);
    }

    private void clearForm() {
        appointmentTable.clearSelection();
        selectedAppointmentLabel.setText("No appointment selected");
        doctorBox.setSelectedIndex(0);
        updateSlots((DoctorSchedule) doctorBox.getSelectedItem());
        descriptionArea.setText("");
        rescheduleBox.setSelectedIndex(0);
    }

    private DoctorSchedule[] doctorSchedules() {
        return new DoctorSchedule[]{
                new DoctorSchedule("D101", "Dr. Ahmed Khan", "Cardiology", "Mon-Fri", 10, 15),
                new DoctorSchedule("D102", "Dr. Sara Ali", "Pediatrics", "Mon-Sat", 9, 14),
                new DoctorSchedule("D103", "Dr. Bilal Sheikh", "Orthopedics", "Mon-Fri", 14, 19),
                new DoctorSchedule("D104", "Dr. Hina Noor", "Gynecology", "Mon-Sat", 11, 17),
                new DoctorSchedule("D105", "Dr. Usman Tariq", "Dermatology", "Mon, Tue, Thu, Sat", 15, 19),
                new DoctorSchedule("D106", "Dr. Areeba Malik", "Neurology", "Mon, Wed, Fri", 16, 20),
                new DoctorSchedule("D107", "Dr. Zain Hassan", "Ophthalmology", "Tue, Thu, Fri, Sun", 9, 13),
                new DoctorSchedule("D108", "Dr. Mahnoor Qureshi", "ENT", "Wed, Thu, Sat, Sun", 13, 17),
                new DoctorSchedule("D109", "Dr. Farhan Siddiqui", "General Surgery", "Tue, Thu, Sat", 17, 21),
                new DoctorSchedule("D110", "Dr. Daniyal Raza", "Internal Medicine", "Daily", 9, 13)
        };
    }

    private List<AppointmentSlot> generateCurrentWeekSlots(DoctorSchedule schedule) {
        List<AppointmentSlot> slots = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            LocalDate date = monday.plusDays(dayOffset);
            if (!schedule.worksOn(date.getDayOfWeek())) {
                continue;
            }

            for (int hour = schedule.startHour; hour < schedule.endHour; hour++) {
                /*
                 * Backend integration point:
                 * Later, skip slots already booked for this doctor by checking AppointmentDAO.
                 */
                slots.add(new AppointmentSlot(date, hour));
            }
        }

        if (slots.isEmpty()) {
            slots.add(new AppointmentSlot(null, -1, "No available slots this week"));
        }
        return slots;
    }

    private String formatHour(int hour) {
        int displayHour = hour % 12 == 0 ? 12 : hour % 12;
        String suffix = hour < 12 ? "AM" : "PM";
        return String.format("%02d:00 %s", displayHour, suffix);
    }

    private class AppointmentSlot {
        private final LocalDate date;
        private final int hour;
        private final String label;

        AppointmentSlot(LocalDate date, int hour) {
            this(date, hour, null);
        }

        AppointmentSlot(LocalDate date, int hour, String label) {
            this.date = date;
            this.hour = hour;
            this.label = label;
        }

        boolean isRealSlot() {
            return date != null && hour >= 0;
        }

        int getYear() {
            return date.getYear();
        }

        int getMonth() {
            return date.getMonthValue();
        }

        int getDay() {
            return date.getDayOfMonth();
        }

        int getHour() {
            return hour;
        }

        String getDateString() {
            return date.toString();
        }

        String getTimeString() {
            return formatHour(hour);
        }

        @Override
        public String toString() {
            if (label != null) {
                return label;
            }
            return date.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")) + " - " + formatHour(hour);
        }
    }

    private static class DoctorSchedule {
        private final String id;
        private final String name;
        private final String specialization;
        private final String days;
        private final int startHour;
        private final int endHour;

        DoctorSchedule(String id, String name, String specialization, String days, int startHour, int endHour) {
            this.id = id;
            this.name = name;
            this.specialization = specialization;
            this.days = days;
            this.startHour = startHour;
            this.endHour = endHour;
        }

        boolean worksOn(DayOfWeek dayOfWeek) {
            if (days.equals("Daily")) {
                return true;
            }
            if (days.equals("Mon-Fri")) {
                return dayOfWeek.getValue() >= DayOfWeek.MONDAY.getValue()
                        && dayOfWeek.getValue() <= DayOfWeek.FRIDAY.getValue();
            }
            if (days.equals("Mon-Sat")) {
                return dayOfWeek.getValue() >= DayOfWeek.MONDAY.getValue()
                        && dayOfWeek.getValue() <= DayOfWeek.SATURDAY.getValue();
            }

            String shortDay = dayOfWeek.toString().substring(0, 3);
            String formattedDay = shortDay.charAt(0) + shortDay.substring(1).toLowerCase();
            return days.contains(formattedDay);
        }

        @Override
        public String toString() {
            return name + " (" + specialization + ")";
        }
    }
}
