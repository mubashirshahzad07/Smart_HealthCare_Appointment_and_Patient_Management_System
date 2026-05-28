package pms.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class AdminDashboardPanel extends JPanel {
    public AdminDashboardPanel() {
        setLayout(new BorderLayout());
        JPanel panel = AdminUI.basePanel("Dashboard", "Overall summary of hospital activity");

        JPanel cards = new JPanel(new GridLayout(2, 3, 12, 12));
        cards.setBackground(AdminUI.LIGHT);
        cards.add(AdminUI.infoCard("Total Doctors", "12", AdminUI.BLUE));
        cards.add(AdminUI.infoCard("Total Receptionists", "5", AdminUI.PURPLE));
        cards.add(AdminUI.infoCard("Emergency Teams", "4", AdminUI.RED));
        cards.add(AdminUI.infoCard("Appointments Today", "18", AdminUI.GREEN));
        cards.add(AdminUI.infoCard("Emergency Cases", "3", AdminUI.ORANGE));
        cards.add(AdminUI.infoCard("Total Patients", "85", AdminUI.NAVY));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AdminUI.LIGHT);
        wrapper.add(cards, BorderLayout.NORTH);
        panel.add(wrapper, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);
    }
}
