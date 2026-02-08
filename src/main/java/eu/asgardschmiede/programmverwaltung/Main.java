package eu.asgardschmiede.programmverwaltung;

import eu.asgardschmiede.programmverwaltung.controller.ProgramController;
import eu.asgardschmiede.programmverwaltung.service.ProgramService;
import eu.asgardschmiede.programmverwaltung.view.ProgramView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProgramView view = new ProgramView();
            ProgramService service = new ProgramService();
            new ProgramController(service, view.getPanel());
            view.setVisible(true);
        });
    }
}