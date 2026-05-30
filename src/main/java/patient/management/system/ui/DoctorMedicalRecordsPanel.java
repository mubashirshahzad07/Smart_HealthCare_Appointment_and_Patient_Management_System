package patient.management.system.ui;

import patient.management.system.model.User;

import javax.swing.JButton;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DoctorMedicalRecordsPanel extends JPanel {
    private final User loggedInUser;
    private final DefaultTableModel appointmentModel;
    private final JTable appointmentTable;
    private final List<HistoryRecord> allHistoryRecords;
    private final DefaultListModel<HistoryRecord> historyListModel;
    private final JList<HistoryRecord> historyList;
    private final JTextArea historyDetailArea;
    private final JLabel selectedAppointmentLabel;
    private final JTextArea diagnosisArea;
    private final JTextArea treatmentArea;
    private final JTextArea prescriptionArea;
    private final JTextArea notesArea;
    private final JTextField recordDateTimeField;

    public DoctorMedicalRecordsPanel(User loggedInUser) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        appointmentModel = buildAppointmentModel();
        appointmentTable = AppUI.table(appointmentModel);
        configureAppointmentTable();

        allHistoryRecords = buildHistoryRecords();
        historyListModel = new DefaultListModel<>();
        historyList = new JList<>(historyListModel);
        historyDetailArea = AppUI.textArea("Selected record details");
        historyDetailArea.setEditable(false);
        historyDetailArea.setText("Select a patient record to view its details.");
        configureHistoryList();
        refreshHistoryList("");

        selectedAppointmentLabel = mutedLabel("No appointment / patient selected");
        diagnosisArea = AppUI.textArea("Diagnosis");
        treatmentArea = AppUI.textArea("Treatment given");
        prescriptionArea = AppUI.textArea("Prescription");
        notesArea = AppUI.textArea("Additional notes");
        diagnosisArea.setRows(2);
        treatmentArea.setRows(2);
        prescriptionArea.setRows(2);
        notesArea.setRows(2);
        recordDateTimeField = AppUI.textField("Record date and time");
        recordDateTimeField.setEditable(false);
        recordDateTimeField.setText(currentRecordDateTime());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = AppUI.pageTitle("Medical Records");
        JLabel hint = new JLabel("Create patient records and review previous history.");
        hint.setFont(UITheme.BODY_FONT);
        hint.setForeground(UITheme.MUTED_TEXT);

        header.add(title, BorderLayout.WEST);
        header.add(hint, BorderLayout.EAST);
        return header;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.BUTTON_FONT);
        tabs.addTab("New Record", buildNewRecordTab());
        tabs.addTab("Patient History", buildHistoryTab());
        return tabs;
    }

    private JPanel buildNewRecordTab() {
        JPanel content = new JPanel(new GridLayout(1, 2, 16, 16));
        content.setBackground(UITheme.BACKGROUND);
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 0, 0, 0));
        content.add(buildRecordFormCard());
        content.add(buildAppointmentsCard());
        return content;
    }

    private JPanel buildRecordFormCard() {
        JPanel card = AppUI.cardPanel();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        form.add(AppUI.smallLabel("Selected Appointment / Patient"), AppUI.gbc(0, 0));
        form.add(selectedAppointmentLabel, AppUI.gbc(0, 1));
        form.add(AppUI.smallLabel("Diagnosis *"), AppUI.gbc(0, 2));
        form.add(diagnosisArea, AppUI.gbc(0, 3));
        form.add(AppUI.smallLabel("Treatment Given *"), AppUI.gbc(0, 4));
        form.add(treatmentArea, AppUI.gbc(0, 5));
        form.add(AppUI.smallLabel("Prescription"), AppUI.gbc(0, 6));
        form.add(prescriptionArea, AppUI.gbc(0, 7));
        form.add(AppUI.smallLabel("Notes"), AppUI.gbc(0, 8));
        form.add(notesArea, AppUI.gbc(0, 9));
        form.add(AppUI.smallLabel("Record Date & Time"), AppUI.gbc(0, 10));
        form.add(recordDateTimeField, AppUI.gbc(0, 11));

        JButton clearButton = AppUI.secondaryButton("Clear");
        JButton saveButton = AppUI.primaryButton("Save Medical Record");
        clearButton.addActionListener(event -> clearForm());
        saveButton.addActionListener(event -> saveMedicalRecord());

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 14, 0));
        buttons.setOpaque(false);
        buttons.add(clearButton);
        buttons.add(saveButton);

        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildAppointmentsCard() {
        JPanel card = AppUI.cardPanel();
        JPanel body = new JPanel(new BorderLayout(8, 8));
        body.setOpaque(false);

        JTextField searchField = AppUI.textField("Search appointment");
        addSearchFilter(searchField, appointmentModel, appointmentTable);
        appointmentTable.getSelectionModel().addListSelectionListener(event -> updateSelectedAppointment());

        JScrollPane scrollPane = AppUI.tableScrollPane(appointmentTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel top = new JPanel(new BorderLayout(4, 4));
        top.setOpaque(false);
        top.add(new JLabel("Appointments"), BorderLayout.NORTH);
        top.add(searchField, BorderLayout.CENTER);

        body.add(top, BorderLayout.NORTH);
        body.add(scrollPane, BorderLayout.CENTER);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildHistoryTab() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(UITheme.BACKGROUND);
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JTextField searchField = AppUI.textField("Search by patient ID, patient name, or diagnosis");
        addHistorySearchFilter(searchField);

        JPanel historyListCard = AppUI.cardPanel();
        JPanel historyListHeader = new JPanel(new BorderLayout(4, 6));
        historyListHeader.setOpaque(false);
        historyListHeader.add(new JLabel("Patient Records"), BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout(4, 4));
        searchPanel.setOpaque(false);
        searchPanel.add(AppUI.smallLabel("Search Patient History"), BorderLayout.NORTH);
        searchPanel.add(searchField, BorderLayout.CENTER);

        historyListHeader.add(searchPanel, BorderLayout.CENTER);
        historyListCard.add(historyListHeader, BorderLayout.NORTH);
        historyListCard.add(new JScrollPane(historyList), BorderLayout.CENTER);

        JPanel detailCard = AppUI.cardPanel();
        detailCard.add(new JLabel("Record Details"), BorderLayout.NORTH);
        detailCard.add(new JScrollPane(historyDetailArea), BorderLayout.CENTER);

        JPanel records = new JPanel(new GridLayout(1, 2, 16, 16));
        records.setOpaque(false);
        records.add(historyListCard);
        records.add(detailCard);

        content.add(records, BorderLayout.CENTER);
        return content;
    }

    private DefaultTableModel buildAppointmentModel() {
        DefaultTableModel model = readOnlyModel(
                new String[]{"Appointment ID", "Patient ID", "Patient Name", "Date", "Time", "Description", "Status"}
        );

        /*
         * Backend integration point:
         * Load only this doctor's assigned appointments that still need a regular medical record.
         */
        model.addRow(new Object[]{"A101", "P101", "Ali Raza", "2026-05-30", "09:00 AM", "Chest pain", "SCHEDULED"});
        model.addRow(new Object[]{"A102", "P102", "Sara Ali", "2026-05-30", "10:00 AM", "Follow-up", "RESCHEDULED"});
        model.addRow(new Object[]{"A104", "P104", "Hina Noor", "2026-05-31", "12:00 PM", "Consultation", "SCHEDULED"});
        return model;
    }

    private List<HistoryRecord> buildHistoryRecords() {
        List<HistoryRecord> records = new ArrayList<>();
        /*
         * Backend integration point:
         * Use MedicalRecordService.getPatientHistory(patientId), or load all records and filter.
         */
        records.add(new HistoryRecord("MR001", "P101", "Ali Raza", "2026-05-10 09:35",
                "Hypertension", "Lifestyle guidance", "Amlodipine", "Monitor blood pressure.", "Dr. Ahmed"));
        records.add(new HistoryRecord("MR002", "P102", "Sara Ali", "2026-05-18 11:20",
                "Seasonal allergy", "Avoid triggers", "Cetirizine", "Review if symptoms continue.", "Dr. Ahmed"));
        records.add(new HistoryRecord("MR003", "P101", "Ali Raza", "2026-05-25 10:10",
                "Follow-up", "Continue treatment", "Amlodipine", "Condition improving.", "Dr. Ahmed"));
        return records;
    }

    private DefaultTableModel readOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void configureAppointmentTable() {
        appointmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {100, 80, 130, 100, 85, 190, 100};
        setColumnWidths(appointmentTable, widths);
    }

    private void configureHistoryList() {
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setCellRenderer(new HistoryRecordRenderer());
        historyList.setFixedCellHeight(76);
        historyList.setBackground(UITheme.CARD);
        historyList.addListSelectionListener(event -> updateHistoryDetails());
    }

    private void setColumnWidths(JTable table, int[] widths) {
        for (int index = 0; index < widths.length; index++) {
            table.getColumnModel().getColumn(index).setPreferredWidth(widths[index]);
        }
    }

    private void addSearchFilter(JTextField searchField, DefaultTableModel model, JTable table) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) {
                filter();
            }

            public void removeUpdate(DocumentEvent event) {
                filter();
            }

            public void changedUpdate(DocumentEvent event) {
                filter();
            }

            private void filter() {
                String query = searchField.getText().trim();
                sorter.setRowFilter(query.isEmpty() ? null : javax.swing.RowFilter.regexFilter("(?i)" + query));
            }
        });
    }

    private void addHistorySearchFilter(JTextField searchField) {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) {
                filter();
            }

            public void removeUpdate(DocumentEvent event) {
                filter();
            }

            public void changedUpdate(DocumentEvent event) {
                filter();
            }

            private void filter() {
                refreshHistoryList(searchField.getText().trim());
            }
        });
    }

    private void refreshHistoryList(String query) {
        historyListModel.clear();
        String normalizedQuery = query.toLowerCase();

        for (HistoryRecord record : allHistoryRecords) {
            if (record.matches(normalizedQuery)) {
                historyListModel.addElement(record);
            }
        }

        historyDetailArea.setText("Select a patient record to view its details.");
    }

    private void updateHistoryDetails() {
        HistoryRecord record = historyList.getSelectedValue();
        if (record == null) {
            return;
        }

        historyDetailArea.setText(
                "Patient: " + record.patientName + " (" + record.patientId + ")\n"
                        + "Record ID: " + record.recordId + "\n"
                        + "Date & Time: " + record.recordDateTime + "\n"
                        + "Handled By: " + record.handledBy + "\n\n"
                        + "Diagnosis\n" + record.diagnosis + "\n\n"
                        + "Treatment Given\n" + record.treatment + "\n\n"
                        + "Prescription\n" + record.prescription + "\n\n"
                        + "Notes\n" + record.notes
        );
        historyDetailArea.setCaretPosition(0);
    }

    private void updateSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            selectedAppointmentLabel.setText("No appointment / patient selected");
            return;
        }

        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        selectedAppointmentLabel.setText(
                appointmentModel.getValueAt(modelRow, 0) + " | " + appointmentModel.getValueAt(modelRow, 2)
        );
        recordDateTimeField.setText(currentRecordDateTime());
    }

    private void saveMedicalRecord() {
        try {
            if (appointmentTable.getSelectedRow() < 0
                    || diagnosisArea.getText().trim().isEmpty()
                    || treatmentArea.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Select an appointment and fill all required fields.");
            }

            /*
             * Backend integration point:
             * Save a REGULAR MedicalRecord for the selected appointment and patient.
             * Then mark the appointment COMPLETED and refresh the dashboard/table.
             */
            JOptionPane.showMessageDialog(this, "Medical record is ready to be connected to the backend.");
            clearForm();
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearForm() {
        appointmentTable.clearSelection();
        selectedAppointmentLabel.setText("No appointment / patient selected");
        diagnosisArea.setText("");
        treatmentArea.setText("");
        prescriptionArea.setText("");
        notesArea.setText("");
        recordDateTimeField.setText(currentRecordDateTime());
    }

    private JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.BODY_FONT);
        label.setForeground(UITheme.MUTED_TEXT);
        return label;
    }

    private String currentRecordDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private static class HistoryRecord {
        private final String recordId;
        private final String patientId;
        private final String patientName;
        private final String recordDateTime;
        private final String diagnosis;
        private final String treatment;
        private final String prescription;
        private final String notes;
        private final String handledBy;

        HistoryRecord(String recordId, String patientId, String patientName, String recordDateTime,
                      String diagnosis, String treatment, String prescription, String notes, String handledBy) {
            this.recordId = recordId;
            this.patientId = patientId;
            this.patientName = patientName;
            this.recordDateTime = recordDateTime;
            this.diagnosis = diagnosis;
            this.treatment = treatment;
            this.prescription = prescription;
            this.notes = notes;
            this.handledBy = handledBy;
        }

        boolean matches(String query) {
            return query.isEmpty()
                    || patientId.toLowerCase().contains(query)
                    || patientName.toLowerCase().contains(query)
                    || diagnosis.toLowerCase().contains(query);
        }
    }

    private static class HistoryRecordRenderer extends JPanel implements ListCellRenderer<HistoryRecord> {
        private final JLabel patientLabel;
        private final JLabel summaryLabel;

        HistoryRecordRenderer() {
            super(new GridLayout(2, 1, 0, 4));
            patientLabel = new JLabel();
            summaryLabel = new JLabel();
            patientLabel.setFont(UITheme.BUTTON_FONT);
            summaryLabel.setFont(UITheme.BODY_FONT);
            summaryLabel.setForeground(UITheme.MUTED_TEXT);
            setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 10, 8, 10));
            add(patientLabel);
            add(summaryLabel);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends HistoryRecord> list, HistoryRecord record,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            patientLabel.setText(record.patientName + " | " + record.patientId);
            summaryLabel.setText(record.recordDateTime + " | " + record.diagnosis);
            setBackground(isSelected ? UITheme.SOFT_BLUE : (index % 2 == 0 ? Color.WHITE : new Color(0xF6FAFE)));
            setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createMatteBorder(
                            0, isSelected ? 4 : 0, 1, 0,
                            isSelected ? UITheme.BLUE : UITheme.TABLE_GRID
                    ),
                    javax.swing.BorderFactory.createEmptyBorder(8, isSelected ? 8 : 12, 8, 10)
            ));
            return this;
        }
    }
}
