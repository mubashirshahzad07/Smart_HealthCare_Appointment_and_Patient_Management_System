package patient.management.system.ui;

import patient.management.system.service.AdminService;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class AdminDashboardPanel extends JPanel {
    private final AdminService adminService = new AdminService();

    public AdminDashboardPanel() {
        setLayout(new BorderLayout());
        JPanel panel = AdminUI.basePanel("Dashboard", "Overall summary of hospital activity");

        JPanel cards = new JPanel(new GridLayout(2, 3, 12, 12));
        cards.setBackground(AdminUI.LIGHT);

        try {
            cards.add(AdminUI.infoCard("Total Doctors",
                    String.valueOf(adminService.getTotalDoctorsCount()), AdminUI.BLUE));

            cards.add(AdminUI.infoCard("Total Receptionists",
                    String.valueOf(adminService.getTotalReceptionistCount()), AdminUI.PURPLE));

            cards.add(AdminUI.infoCard("Emergency Teams",
                    String.valueOf(adminService.getTotalEmergencyTeamsCount()), AdminUI.RED));

            cards.add(AdminUI.infoCard("Appointments Today",
                    String.valueOf(adminService.getTodayAppointmentsCount()), AdminUI.GREEN));

            cards.add(AdminUI.infoCard("Emergency Cases",
                    String.valueOf(adminService.getTotalEmergencyCasesCount()), AdminUI.ORANGE));

            cards.add(AdminUI.infoCard("Total Patients",
                    String.valueOf(adminService.getTotalPatientsCount()), AdminUI.NAVY));

        } catch (RuntimeException e) {
            cards.add(AdminUI.infoCard("Total Doctors", "0", AdminUI.BLUE));
            cards.add(AdminUI.infoCard("Total Receptionists", "0", AdminUI.PURPLE));
            cards.add(AdminUI.infoCard("Emergency Teams", "0", AdminUI.RED));
            cards.add(AdminUI.infoCard("Appointments Today", "0", AdminUI.GREEN));
            cards.add(AdminUI.infoCard("Emergency Cases", "0", AdminUI.ORANGE));
            cards.add(AdminUI.infoCard("Total Patients", "0", AdminUI.NAVY));
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AdminUI.LIGHT);
        wrapper.add(cards, BorderLayout.NORTH);
        panel.add(wrapper, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);
    }
}