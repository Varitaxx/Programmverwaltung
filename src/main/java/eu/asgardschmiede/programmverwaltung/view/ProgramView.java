package eu.asgardschmiede.programmverwaltung.view;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class ProgramView extends JFrame {
    private final ProgramPanel panel;

    public ProgramView() {
        FlatLightLaf.setup();
        setTitle("ProgrammVerwaltung");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Entferne setSize und verwende pack() für automatische Größenanpassung
        setMinimumSize(new java.awt.Dimension(1000, 700)); // Mindestgröße für bessere Sichtbarkeit
        setLocationRelativeTo(null);

        panel = new ProgramPanel();
        add(panel);
        pack(); // Passt die Fenstergröße an die Komponenten an
    }

    public ProgramPanel getPanel() {
        return panel;
    }
}