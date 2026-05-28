package patient.management.system.ui;

import patient.management.system.model.User;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmergencyMedicalRecordsPanel extends JPanel {
    private final User loggedInUser;
    private final Color accentColor;
    private final DefaultTableModel caseModel;
    private final JTable caseTable;
    private final JTextArea diagnosisArea;
    private final JTextArea treatmentArea;
    private final JTextArea prescriptionArea;
    private final JTextArea notesArea;
    private final JTextField recordDateTimeField;
    private final JComboBox<String> outcomeBox;
    private final JLabel selectedCaseLabel;

    public EmergencyMedicalRecordsPanel(User loggedInUser, Color accentColor) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        this.accentColor = accentColor;
        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        caseModel = buildCaseModel();
        caseTable = AppUI.table(caseModel);
        configureCaseTable();
        diagnosisArea = AppUI.textArea("Diagnosis");
        treatmentArea = AppUI.textArea("Treatment given");
        prescriptionArea = AppUI.textArea("Prescription");
        notesArea = AppUI.textArea("Additional notes");
        recordDateTimeField = AppUI.textField("Record date and time");
        recordDateTimeField.setEditable(false);
        recordDateTimeField.setText(currentRecordDateTime());
        outcomeBox = AppUI.comboBox(new String[]{
                "Select outcome",
                "DISCHARGED",
                "MOVED_TO_ICU",
                "MOVED_TO_WARD",
                "REFERRED_TO_REGULAR_APPOINTMENT",
                "DECEASED"
        });
        selectedCaseLabel = new JLabel("No case selected");

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = AppUI.pageTitle("Medical Records");
        JLabel hint = new JLabel("Select a case, then complete the emergency medical record.");
        hint.setFont(UITheme.BODY_FONT);
        hint.setForeground(UITheme.MUTED_TEXT);

        header.add(title, BorderLayout.WEST);
        header.add(hint, BorderLayout.EAST);
        return header;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 18, 18));
        content.setOpaque(false);
        content.add(buildRecordCard());
        content.add(buildCasesCard());
        return content;
    }

    private JPanel buildRecordCard() {
        JPanel card = AppUI.cardPanel();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        selectedCaseLabel.setFont(UITheme.BODY_FONT);
        selectedCaseLabel.setForeground(UITheme.MUTED_TEXT);

        form.add(AppUI.smallLabel("Selected Case"), AppUI.gbc(0, 0));
        form.add(selectedCaseLabel, AppUI.gbc(0, 1));
        form.add(AppUI.smallLabel("Diagnosis *"), AppUI.gbc(0, 2));
        form.add(diagnosisArea, AppUI.gbc(0, 3));
        form.add(AppUI.smallLabel("Treatment Given *"), AppUI.gbc(0, 4));
        form.add(treatmentArea, AppUI.gbc(0, 5));
        form.add(AppUI.smallLabel("Prescription"), AppUI.gbc(0, 6));
        form.add(prescriptionArea, AppUI.gbc(0, 7));
        form.add(AppUI.smallLabel("Final Outcome *"), AppUI.gbc(0, 8));
        form.add(outcomeBox, AppUI.gbc(0, 9));
        form.add(AppUI.smallLabel("Record Date & Time"), AppUI.gbc(0, 10));
        form.add(recordDateTimeField, AppUI.gbc(0, 11));

        JButton saveButton = AppUI.primaryButton("Save Medical Record");
        JButton clearButton = AppUI.secondaryButton("Clear");
        saveButton.addActionListener(event -> saveMedicalRecord());
        clearButton.addActionListener(event -> clearForm());

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 14, 0));
        buttons.setOpaque(false);
        buttons.add(clearButton);
        buttons.add(saveButton);

        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildCasesCard() {
        JPanel card = AppUI.cardPanel();
        JPanel body = new JPanel(new BorderLayout(10, 10));
        body.setOpaque(false);

        JPanel tableHeader = new JPanel(new BorderLayout(4, 4));
        tableHeader.setOpaque(false);
        tableHeader.add(new JLabel("Emergency Cases"), BorderLayout.NORTH);
        tableHeader.add(AppUI.smallLabel("Select Patient / Case"), BorderLayout.CENTER);
        caseTable.getSelectionModel().addListSelectionListener(event -> updateSelectedCase());

        JScrollPane scrollPane = AppUI.tableScrollPane(caseTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(420, 235));

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setOpaque(false);
        tablePanel.add(tableHeader, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.setPreferredSize(new Dimension(420, 310));

        JPanel notesPanel = new JPanel(new BorderLayout(4, 4));
        notesPanel.setOpaque(false);
        notesPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 0, 0, 0));
        notesPanel.add(AppUI.smallLabel("Notes"), BorderLayout.NORTH);
        notesPanel.add(notesArea, BorderLayout.CENTER);

        body.add(tablePanel, BorderLayout.NORTH);
        body.add(notesPanel, BorderLayout.CENTER);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private DefaultTableModel buildCaseModel() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Case ID", "Patient Name", "Age", "Gender", "Initial Complaint", "Arrival Time"},
                0
        );

        /*
         * Backend integration point:
         * Later, load ACTIVE emergency cases for loggedInUser.getTriageColor().
         * Linked cases should show complete patient info; unlinked temporary cases can show partial/unknown info.
         */
        model.addRow(new Object[]{"E101", "Unknown Male", "45", "Male", "Chest pain", "2026-05-28 13:10"});
        model.addRow(new Object[]{"E102", "Usman Shah", "33", "Male", "Severe headache", "2026-05-28 13:45"});
        model.addRow(new Object[]{"E103", "Unknown", "", "", "Road accident", "2026-05-28 14:05"});
        return model;
    }

    private void configureCaseTable() {
        caseTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        caseTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        caseTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        caseTable.getColumnModel().getColumn(2).setPreferredWidth(55);
        caseTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        caseTable.getColumnModel().getColumn(4).setPreferredWidth(190);
        caseTable.getColumnModel().getColumn(5).setPreferredWidth(150);
    }

    private void updateSelectedCase() {
        int selectedRow = caseTable.getSelectedRow();
        if (selectedRow < 0) {
            selectedCaseLabel.setText("No case selected");
            return;
        }

        int modelRow = caseTable.convertRowIndexToModel(selectedRow);
        selectedCaseLabel.setText(caseModel.getValueAt(modelRow, 0) + " | " + caseModel.getValueAt(modelRow, 1));
        recordDateTimeField.setText(currentRecordDateTime());
    }

    private void saveMedicalRecord() {
        try {
            if (caseTable.getSelectedRow() < 0
                    || diagnosisArea.getText().trim().isEmpty()
                    || treatmentArea.getText().trim().isEmpty()
                    || outcomeBox.getSelectedItem().toString().startsWith("Select")) {
                throw new IllegalArgumentException("Select a case and fill all required fields.");
            }

            /*
             * Backend integration point:
             * Create MedicalRecord:
             * recordType = EMERGENCY
             * handledBy = loggedInUser.getUsername()
             * triageColor = loggedInUser.getTriageColor()
             * emergencyCaseId = selected case
             * recordDateTime = recordDateTimeField text / LocalDateTime.now()
             * finalOutcome = outcomeBox selected value
             * EmergencyCase.status = COMPLETED
             */
            JOptionPane.showMessageDialog(this, "Emergency medical record saved.");
            clearForm();
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearForm() {
        caseTable.clearSelection();
        selectedCaseLabel.setText("No case selected");
        diagnosisArea.setText("");
        treatmentArea.setText("");
        prescriptionArea.setText("");
        notesArea.setText("");
        recordDateTimeField.setText(currentRecordDateTime());
        outcomeBox.setSelectedIndex(0);
    }

    private String currentRecordDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
