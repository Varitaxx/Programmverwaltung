package eu.asgardschmiede;

import eu.asgardschmiede.controller.ProgramController;
import eu.asgardschmiede.service.ProgramService;
import eu.asgardschmiede.view.ProgramView;

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