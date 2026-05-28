package patient.management.system.ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import patient.management.system.model.User;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

public class RegisterPatientPanel extends JPanel {
    private final JTextField nameField;
    private final JTextField cnicField;
    private final JComboBox<String> genderComboBox;
    private final JTextField ageField;
    private final JTextField emailField;
    private final User loggedInUser;

    public RegisterPatientPanel() {
        this(null);
    }

    public RegisterPatientPanel(User loggedInUser) {
        super(new BorderLayout(18, 18));
        this.loggedInUser = loggedInUser;
        setBackground(UITheme.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));

        nameField = AppUI.textField("Full name");
        cnicField = AppUI.textField("CNIC, e.g. 35202-1234567-1");
        genderComboBox = AppUI.comboBox(new String[]{"Select gender", "Male", "Female", "Other"});
        ageField = AppUI.textField("Age");
        emailField = AppUI.textField("Email address");

        add(AppUI.pageTitle("Register Patient"), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 18, 18));
        content.setOpaque(false);
        content.add(buildFormCard());
        content.add(buildPatientsCard());
        return content;
    }

    private JPanel buildFormCard() {
        JPanel card = AppUI.cardPanel();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        form.add(AppUI.smallLabel("Full Name *"), AppUI.gbc(0, 0));
        form.add(nameField, AppUI.gbc(0, 1));
        form.add(AppUI.smallLabel("CNIC *"), AppUI.gbc(0, 2));
        form.add(cnicField, AppUI.gbc(0, 3));
        form.add(AppUI.smallLabel("Gender *"), AppUI.gbc(0, 4));
        form.add(genderComboBox, AppUI.gbc(0, 5));
        form.add(AppUI.smallLabel("Age *"), AppUI.gbc(0, 6));
        form.add(ageField, AppUI.gbc(0, 7));
        form.add(AppUI.smallLabel("Email *"), AppUI.gbc(0, 8));
        form.add(emailField, AppUI.gbc(0, 9));

        JButton registerButton = AppUI.primaryButton("Register Patient");
        JButton clearButton = AppUI.secondaryButton("Clear");
        registerButton.addActionListener(event -> registerPatient());
        clearButton.addActionListener(event -> clearForm());

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 14, 0));
        buttons.setOpaque(false);
        buttons.add(clearButton);
        buttons.add(registerButton);

        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildPatientsCard() {
        JPanel card = AppUI.cardPanel();
        card.add(new JLabel("Registered Patients"), BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "CNIC", "Gender", "Age", "Email"}, 0);
        /*
         * Backend integration point:
         * Later, load patients from PatientDAO/PatientService.
         */
        model.addRow(new Object[]{"P101", "Ali Raza", "35202-1234567-1", "Male", "29", "ali@gmail.com"});
        model.addRow(new Object[]{"P102", "Sara Ali", "35202-7654321-2", "Female", "24", "sara@gmail.com"});
        model.addRow(new Object[]{"P103", "Usman Shah", "35201-1111111-3", "Male", "33", "usman@gmail.com"});
        model.addRow(new Object[]{"P104", "Hina Noor", "35201-2222222-4", "Female", "27", "hina@gmail.com"});

        JTable table = AppUI.table(model);
        configurePatientTable(table);
        card.add(scrollPaneWithBottomBar(table), BorderLayout.CENTER);
        return card;
    }

    private void configurePatientTable(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(65);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(135);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(55);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
    }

    private JScrollPane scrollPaneWithBottomBar(JTable table) {
        JScrollPane scrollPane = AppUI.tableScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private void registerPatient() {
        try {
            if (hasEmptyRequiredField()) {
                throw new IllegalArgumentException("All fields must be filled.");
            }

            String name = nameField.getText().trim();
            String cnic = cnicField.getText().trim();
            String gender = (String) genderComboBox.getSelectedItem();
            int age = Integer.parseInt(ageField.getText().trim());
            String email = emailField.getText().trim();

            if (age <= 0) {
                throw new IllegalArgumentException("Age must be greater than 0.");
            }
            if (!email.contains("@") || !email.contains(".")) {
                throw new IllegalArgumentException("Please enter a valid email address.");
            }

            /*
             * Backend integration point:
             * After your teammates update Patient, create something like:
             * new Patient(name, cnic, gender, age, email)
             * Then call PatientDAO/PatientService to save it.
             */
            JOptionPane.showMessageDialog(
                    this,
                    "Patient data is valid. Backend saving will be connected later.",
                    "Validation Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Age must be a number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Could not register patient: " + exception.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean hasEmptyRequiredField() {
        String gender = (String) genderComboBox.getSelectedItem();
        return nameField.getText().trim().isEmpty()
                || cnicField.getText().trim().isEmpty()
                || gender == null
                || gender.startsWith("Select")
                || ageField.getText().trim().isEmpty()
                || emailField.getText().trim().isEmpty();
    }

    private void clearForm() {
        nameField.setText("");
        cnicField.setText("");
        genderComboBox.setSelectedIndex(0);
        ageField.setText("");
        emailField.setText("");
    }
}
