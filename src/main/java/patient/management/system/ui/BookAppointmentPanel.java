package patient.management.system.ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import patient.management.system.model.User;

public class BookAppointmentPanel extends JPanel {
    private final User loggedInUser;
    private final DefaultTableModel patientModel;
    private final JTable patientTable;
    private final TableRowSorter<DefaultTableModel> patientSorter;
    private final JComboBox<DoctorSchedule> doctorBox;
    private final JComboBox<String> slotBox;
    private final JComboBox<String> rescheduleBox;
    private final JTextArea descriptionArea;
    private final JLabel selectedPatientLabel;

    public BookAppointmentPanel() {
        this(null);
    }

    public BookAppointmentPanel(User loggedInUser) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        patientModel = buildPatientModel();
        patientTable = AppUI.table(patientModel);
        configurePatientTable();
        patientSorter = new TableRowSorter<>(patientModel);
        patientTable.setRowSorter(patientSorter);

        doctorBox = new JComboBox<>(doctorSchedules());
        slotBox = AppUI.comboBox(new String[]{"Select doctor first"});
        rescheduleBox = AppUI.comboBox(new String[]{"Select option", "Yes", "No"});
        descriptionArea = AppUI.textArea("Patient description / reason for appointment");
        selectedPatientLabel = new JLabel("No patient selected");

        add(AppUI.pageTitle("Book Appointment"), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        updateSlots();
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 18, 18));
        content.setOpaque(false);
        content.add(buildBookingCard());
        content.add(buildPatientSelectionCard());
        return content;
    }

    private JPanel buildPatientSelectionCard() {
        JPanel card = AppUI.cardPanel();

        JTextField searchField = AppUI.textField("Search by patient name or CNIC");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                filterPatients(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                filterPatients(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                filterPatients(searchField.getText());
            }
        });

        patientTable.getSelectionModel().addListSelectionListener(event -> updateSelectedPatient());

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        top.add(new JLabel("Select Patient"), BorderLayout.NORTH);
        top.add(searchField, BorderLayout.CENTER);

        card.add(top, BorderLayout.NORTH);
        card.add(scrollPaneWithBottomBar(patientTable), BorderLayout.CENTER);
        return card;
    }

    private void configurePatientTable() {
        patientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        patientTable.getColumnModel().getColumn(0).setPreferredWidth(65);
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(135);
        patientTable.getColumnModel().getColumn(2).setPreferredWidth(135);
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        patientTable.getColumnModel().getColumn(4).setPreferredWidth(55);
    }

    private JScrollPane scrollPaneWithBottomBar(JTable table) {
        JScrollPane scrollPane = AppUI.tableScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private JPanel buildBookingCard() {
        JPanel card = AppUI.cardPanel();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        selectedPatientLabel.setFont(UITheme.BODY_FONT);
        selectedPatientLabel.setForeground(UITheme.MUTED_TEXT);

        doctorBox.setFont(UITheme.BODY_FONT);
        doctorBox.setBackground(UITheme.FIELD_BACKGROUND);
        doctorBox.setForeground(UITheme.TEXT);
        doctorBox.setFocusable(false);
        doctorBox.addActionListener(event -> updateSlots());

        form.add(AppUI.smallLabel("Selected Patient"), AppUI.gbc(0, 0));
        form.add(selectedPatientLabel, AppUI.gbc(0, 1));
        form.add(AppUI.smallLabel("Doctor / Specialization"), AppUI.gbc(0, 2));
        form.add(doctorBox, AppUI.gbc(0, 3));
        form.add(AppUI.smallLabel("Available Slot This Week"), AppUI.gbc(0, 4));
        form.add(slotBox, AppUI.gbc(0, 5));
        form.add(AppUI.smallLabel("Patient Description"), AppUI.gbc(0, 6));
        form.add(descriptionArea, AppUI.gbc(0, 7));
        form.add(AppUI.smallLabel("Willing To Be Rescheduled?"), AppUI.gbc(0, 8));
        form.add(rescheduleBox, AppUI.gbc(0, 9));

        JButton bookButton = AppUI.primaryButton("Book Appointment");
        JButton clearButton = AppUI.secondaryButton("Clear");
        bookButton.addActionListener(event -> bookAppointment());
        clearButton.addActionListener(event -> clearForm());

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 14, 0));
        buttons.setOpaque(false);
        buttons.add(clearButton);
        buttons.add(bookButton);

        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private DefaultTableModel buildPatientModel() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "CNIC", "Gender", "Age"}, 0);
        /*
         * Backend integration point:
         * Later, load registered patients from PatientDAO/PatientService.
         */
        model.addRow(new Object[]{"P101", "Ali Raza", "35202-1234567-1", "Male", "29"});
        model.addRow(new Object[]{"P102", "Sara Ali", "35202-7654321-2", "Female", "24"});
        model.addRow(new Object[]{"P103", "Usman Shah", "35201-1111111-3", "Male", "33"});
        model.addRow(new Object[]{"P104", "Hina Noor", "35201-2222222-4", "Female", "27"});
        return model;
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

    private void filterPatients(String searchText) {
        String text = searchText.trim();
        if (text.isEmpty()) {
            patientSorter.setRowFilter(null);
        } else {
            patientSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text), 1, 2));
        }
    }

    private void updateSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            selectedPatientLabel.setText("No patient selected");
            return;
        }

        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        String id = patientModel.getValueAt(modelRow, 0).toString();
        String name = patientModel.getValueAt(modelRow, 1).toString();
        String cnic = patientModel.getValueAt(modelRow, 2).toString();
        selectedPatientLabel.setText(id + " - " + name + " (" + cnic + ")");
    }

    private void updateSlots() {
        slotBox.removeAllItems();
        DoctorSchedule schedule = (DoctorSchedule) doctorBox.getSelectedItem();
        if (schedule == null) {
            slotBox.addItem("Select doctor first");
            return;
        }

        for (String slot : generateCurrentWeekSlots(schedule)) {
            slotBox.addItem(slot);
        }
    }

    private List<String> generateCurrentWeekSlots(DoctorSchedule schedule) {
        List<String> slots = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy");

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
                slots.add(date.format(formatter) + " - " + formatHour(hour));
            }
        }

        if (slots.isEmpty()) {
            slots.add("No available slots this week");
        }
        return slots;
    }

    private String formatHour(int hour) {
        int displayHour = hour % 12 == 0 ? 12 : hour % 12;
        String suffix = hour < 12 ? "AM" : "PM";
        return String.format("%02d:00 %s", displayHour, suffix);
    }

    private void bookAppointment() {
        try {
            String slot = (String) slotBox.getSelectedItem();
            String description = descriptionArea.getText().trim();
            String reschedule = (String) rescheduleBox.getSelectedItem();

            if (patientTable.getSelectedRow() < 0
                    || doctorBox.getSelectedItem() == null
                    || slot == null
                    || slot.startsWith("No available")
                    || slot.startsWith("Select")
                    || description.isEmpty()
                    || reschedule == null
                    || reschedule.startsWith("Select")) {
                throw new IllegalArgumentException("All fields must be filled.");
            }

            /*
             * Backend integration point:
             * Use selected patient ID, selected DoctorSchedule.id, selected slot,
             * description, and reschedule.equals("Yes") to create Appointment.
             * status = SCHEDULED, type = REGULAR, receptionistId = logged-in receptionist.
             */
            JOptionPane.showMessageDialog(
                    this,
                    "Appointment data is valid. Backend booking will be connected later.",
                    "Validation Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Could not book appointment: " + exception.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        patientTable.clearSelection();
        selectedPatientLabel.setText("No patient selected");
        doctorBox.setSelectedIndex(0);
        updateSlots();
        descriptionArea.setText("");
        rescheduleBox.setSelectedIndex(0);
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
            return id + " - " + name + " (" + specialization + ")";
        }
    }
}
