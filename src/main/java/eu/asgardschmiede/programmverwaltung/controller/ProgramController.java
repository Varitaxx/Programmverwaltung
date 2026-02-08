package eu.asgardschmiede.programmverwaltung.controller;

import eu.asgardschmiede.programmverwaltung.model.Purpose;
import eu.asgardschmiede.programmverwaltung.model.Program;
import eu.asgardschmiede.programmverwaltung.service.ProgramService;
import eu.asgardschmiede.programmverwaltung.view.ProgramPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProgramController {
    private final ProgramService service;
    private final ProgramPanel view;
    private static final String PROGRAM_NAME = "ProgrammVerwaltung";
    private static final String VERSION = "1.0.0";
    private static final String AUTHOR = "AsgardSchmiede";
    private Set<Purpose> lastAddedPurposes; // Speichert die Kategorien des letzten Eintrags
    private int selectedIndex = -1; // Index des ausgewählten Programms
    private boolean isEditing = false; // Gibt an, ob der Editierbereich schreibbar ist
    private boolean isNewEntry = false; // Gibt an, ob ein neuer Datensatz erstellt wird

    public ProgramController(ProgramService service, ProgramPanel view) {
        this.service = service;
        this.view = view;
        this.lastAddedPurposes = null;
        initView();
        initListeners();
        refreshTable(service.getAllPrograms());
        updateRecentProgramsList();
        syncCategorySelection();
        setEditFieldsEditable(false); // Editierbereich zunächst nur lesbar
        view.getUpdateButton().setVisible(false); // Update-Button zunächst unsichtbar
        view.getDeleteButton().setVisible(false); // Delete-Button zunächst unsichtbar
        view.getSaveButton().setVisible(false); // Save-Button zunächst unsichtbar
        view.getAbortButton().setVisible(false); // Abort-Button zunächst unsichtbar
    }

    private void initView() {
        String[] columnNames = {"Name", "Kategorie", "Beschreibung", "Betriebssystem", "Alternativen"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        view.getProgramTable().setModel(model);
        // Renderer für die Betriebssystem-Spalte
        view.getProgramTable().getColumnModel().getColumn(3).setCellRenderer(new OSIconRenderer());
        // Feste Zeilenhöhe setzen, um Layout-Probleme zu vermeiden
        view.getProgramTable().setRowHeight(24); // Anpassbar je nach Icon-Größe
    }

    private void initListeners() {
        view.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prepareNewEntry();
            }
        });
        view.getUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startEditing();
            }
        });
        view.getDeleteButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProgram();
            }
        });
        view.getSearchButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchDialog();
            }
        });
        view.getSaveButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });
        view.getAbortButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abortChanges();
            }
        });
        view.getLoadMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPrograms();
            }
        });
        view.getImportMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importPrograms();
            }
        });
        view.getExportMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportPrograms();
            }
        });
        view.getExitMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitProgram();
            }
        });
        view.getOffButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitProgram();
            }
        });
        view.getInfoMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInfo();
            }
        });
        view.getProgramTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectProgram();
            }
        });
        view.getEditButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isNewEntry) {
                    saveChanges();
                } else {
                    toggleEditMode();
                }
            }
        });
        view.getCategoryComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                syncCategorySelection();
            }
        });
        view.getRecentProgramsList().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectProgramFromList(e);
            }
        });
    }

    private void syncCategorySelection() {
        Purpose selectedCategory = (Purpose) view.getCategoryComboBox().getSelectedItem();
        if (selectedCategory != null) {
            for (Component item : view.getCategoryPopup().getComponents()) {
                if (item instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem checkItem = (JCheckBoxMenuItem) item;
                    checkItem.setSelected(checkItem.getText().equals(selectedCategory.toString()));
                }
            }
        }
    }

    private void updateRecentProgramsList() {
        List<Program> allPrograms = service.getAllPrograms();
        int size = Math.min(allPrograms.size(), 25);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (int i = Math.max(0, allPrograms.size() - size); i < allPrograms.size(); i++) {
            listModel.addElement(allPrograms.get(i).name());
        }
        view.getRecentProgramsList().setModel(listModel);
    }

    private void selectProgramFromList(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String selectedProgramName = view.getRecentProgramsList().getSelectedValue();
            if (selectedProgramName != null) {
                List<Program> allPrograms = service.getAllPrograms();
                for (int i = 0; i < allPrograms.size(); i++) {
                    if (allPrograms.get(i).name().equals(selectedProgramName)) {
                        selectedIndex = i;
                        Program program = allPrograms.get(i);
                        displayProgramDetails(program);
                        view.getEditButton().setText("Bearbeiten");
                        setEditFieldsEditable(false);
                        view.getUpdateButton().setVisible(true);
                        view.getDeleteButton().setVisible(true);
                        view.getSaveButton().setVisible(false);
                        view.getAbortButton().setVisible(false);
                        view.getAddButton().setVisible(true);
                        isNewEntry = false;
                        refreshTableWithPurposeFilter(program.purposes()); // Tabelle mit Kategorien filtern
                        break;
                    }
                }
            }
        }
    }

    private void displayProgramDetails(Program program) {
        view.getNameField().setText(program.name());
        for (Component item : view.getCategoryPopup().getComponents()) {
            if (item instanceof JCheckBoxMenuItem) {
                JCheckBoxMenuItem checkItem = (JCheckBoxMenuItem) item;
                checkItem.setSelected(program.purposes().stream()
                        .anyMatch(p -> p.toString().equals(checkItem.getText())));
            }
        }
        view.getDescriptionArea().setText(program.description());
        // Betriebssysteme auswählen
        String os = program.operatingSystem();
        if (os != null) {
            String[] osList = os.split(",\\s*");
            view.getWindowsCheck().setSelected(false);
            view.getLinuxCheck().setSelected(false);
            view.getMacosCheck().setSelected(false);
            view.getAndroidCheck().setSelected(false);
            view.getIosCheck().setSelected(false);
            for (String osItem : osList) {
                switch (osItem.trim().toLowerCase()) {
                    case "windows":
                        view.getWindowsCheck().setSelected(true);
                        break;
                    case "linux":
                        view.getLinuxCheck().setSelected(true);
                        break;
                    case "macos":
                        view.getMacosCheck().setSelected(true);
                        break;
                    case "android":
                        view.getAndroidCheck().setSelected(true);
                        break;
                    case "ios":
                        view.getIosCheck().setSelected(true);
                        break;
                }
            }
        } else {
            view.getWindowsCheck().setSelected(false);
            view.getLinuxCheck().setSelected(false);
            view.getMacosCheck().setSelected(false);
            view.getAndroidCheck().setSelected(false);
            view.getIosCheck().setSelected(false);
        }
        view.getAlternativesField().setText(String.join(", ", program.alternatives()));
        Set<Purpose> purposes = program.purposes();
        if (!purposes.isEmpty()) {
            view.getCategoryComboBox().setSelectedItem(purposes.iterator().next());
        }
    }

    private void selectProgram() {
        int selectedRow = view.getProgramTable().getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) view.getProgramTable().getModel();
            String selectedProgramName = (String) model.getValueAt(selectedRow, 0); // Name aus der ersten Spalte
            List<Program> allPrograms = service.getAllPrograms();
            for (int i = 0; i < allPrograms.size(); i++) {
                if (allPrograms.get(i).name().equals(selectedProgramName)) {
                    selectedIndex = i;
                    Program program = allPrograms.get(i);
                    displayProgramDetails(program); // Automatisch im Editorbereich anzeigen
                    view.getEditButton().setText("Bearbeiten");
                    setEditFieldsEditable(false); // Lese-Modus
                    view.getUpdateButton().setVisible(true);
                    view.getDeleteButton().setVisible(true);
                    view.getSaveButton().setVisible(false);
                    view.getAbortButton().setVisible(false);
                    view.getAddButton().setVisible(true);
                    isNewEntry = false;
                    break;
                }
            }
        } else {
            selectedIndex = -1;
            view.getEditButton().setText("Bearbeiten");
            setEditFieldsEditable(false);
            view.getUpdateButton().setVisible(false);
            view.getDeleteButton().setVisible(false);
            view.getSaveButton().setVisible(false);
            view.getAbortButton().setVisible(false);
            view.getAddButton().setVisible(true);
        }
    }

    private void prepareNewEntry() {
        selectedIndex = -1;
        isNewEntry = true;
        view.clearInputFields();
        view.getEditButton().setText("Speichern");
        setEditFieldsEditable(true);
        view.getUpdateButton().setVisible(false);
        view.getDeleteButton().setVisible(false);
        view.getSaveButton().setVisible(true);
        view.getAbortButton().setVisible(true);
        view.getAddButton().setVisible(false);
    }

    private void saveChanges() {
        if (isNewEntry || selectedIndex >= 0) {
            Program program = createProgramFromInput();
            if (program != null) {
                if (isNewEntry) {
                    service.addProgram(program);
                    JOptionPane.showMessageDialog(view, "Neuer Datensatz gespeichert!");
                } else {
                    service.updateProgram(selectedIndex, program);
                    JOptionPane.showMessageDialog(view, "Datensatz aktualisiert!");
                }
                lastAddedPurposes = program.purposes();
                refreshTableWithPurposeFilter(lastAddedPurposes);
                updateRecentProgramsList(); // Aktualisiere die Liste der letzten Programme
                view.clearInputFields();
                selectedIndex = -1;
                isNewEntry = false;
                view.getEditButton().setText("Bearbeiten");
                setEditFieldsEditable(false);
                view.getUpdateButton().setVisible(false);
                view.getDeleteButton().setVisible(false);
                view.getSaveButton().setVisible(false);
                view.getAbortButton().setVisible(false);
                view.getAddButton().setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(view, "Kein Datensatz zum Speichern vorhanden!");
        }
    }

    private void startEditing() {
        if (selectedIndex >= 0) {
            setEditFieldsEditable(true);
            view.getEditButton().setText("Speichern");
            view.getUpdateButton().setVisible(false);
            view.getDeleteButton().setVisible(false);
            view.getSaveButton().setVisible(true);
            view.getAbortButton().setVisible(true);
            view.getAddButton().setVisible(false);
            isEditing = true;
        } else {
            JOptionPane.showMessageDialog(view, "Bitte ein Programm auswählen!");
        }
    }

    private void updateProgram() {
        // Diese Methode wird jetzt durch startEditing() ersetzt
    }

    private void deleteProgram() {
        if (selectedIndex >= 0) {
            service.deleteProgram(selectedIndex);
            refreshTable(service.getAllPrograms());
            updateRecentProgramsList(); // Aktualisiere die Liste der letzten Programme
            view.clearInputFields();
            selectedIndex = -1;
            view.getEditButton().setText("Bearbeiten");
            setEditFieldsEditable(false);
            view.getUpdateButton().setVisible(false);
            view.getDeleteButton().setVisible(false);
            view.getSaveButton().setVisible(false);
            view.getAbortButton().setVisible(false);
            view.getAddButton().setVisible(true);
            JOptionPane.showMessageDialog(view, "Programm gelöscht und gespeichert!");
        } else {
            JOptionPane.showMessageDialog(view, "Bitte ein Programm auswählen!");
        }
    }

    private void toggleEditMode() {
        if (selectedIndex >= 0) {
            if (!isEditing) {
                setEditFieldsEditable(true);
                view.getEditButton().setText("Speichern");
                view.getSaveButton().setVisible(true);
                view.getAbortButton().setVisible(true);
                view.getUpdateButton().setVisible(false);
                view.getDeleteButton().setVisible(false);
                view.getAddButton().setVisible(false);
                isEditing = true;
            } else {
                saveChanges();
                isEditing = false;
            }
        }
    }

    private void abortChanges() {
        if (isNewEntry || isEditing) {
            view.clearInputFields();
            selectedIndex = -1;
            isNewEntry = false;
            isEditing = false;
            view.getEditButton().setText("Bearbeiten");
            setEditFieldsEditable(false);
            view.getUpdateButton().setVisible(false);
            view.getDeleteButton().setVisible(false);
            view.getSaveButton().setVisible(false);
            view.getAbortButton().setVisible(false);
            view.getAddButton().setVisible(true);
            refreshTable(service.getAllPrograms());
            JOptionPane.showMessageDialog(view, "Änderungen abgebrochen!");
        }
    }

    private void exitProgram() {
        System.exit(0);
    }

    private void setEditFieldsEditable(boolean editable) {
        view.getNameField().setEditable(editable);
        view.getCategoryComboBox().setEnabled(editable);
        view.getDescriptionArea().setEditable(editable);
        // Checkboxes explizit aktivieren/deaktivieren
        view.getWindowsCheck().setEnabled(editable);
        view.getLinuxCheck().setEnabled(editable);
        view.getMacosCheck().setEnabled(editable);
        view.getAndroidCheck().setEnabled(editable);
        view.getIosCheck().setEnabled(editable);
        view.getAlternativesField().setEditable(editable);
        for (Component item : view.getCategoryPopup().getComponents()) {
            if (item instanceof JCheckBoxMenuItem) {
                item.setEnabled(editable);
            }
        }
    }

    private void showSearchDialog() {
        JDialog searchDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Suche", true);
        searchDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        searchDialog.add(new JLabel("Suchmethode:"), gbc);

        JComboBox<String> searchMethodCombo = new JComboBox<>(new String[]{"Nach Name suchen", "Nach Kategorie suchen"});
        gbc.gridx = 1;
        searchDialog.add(searchMethodCombo, gbc);

        JPanel dynamicPanel = new JPanel(new GridBagLayout());
        JTextField nameField = new JTextField(20);
        JComboBox<Purpose> categoryCombo = new JComboBox<>(Purpose.values());
        categoryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "" : ((Purpose) value).toString());
                return this;
            }
        });

        searchMethodCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dynamicPanel.removeAll();
                GridBagConstraints dynGbc = new GridBagConstraints();
                dynGbc.insets = new Insets(5, 5, 5, 5);
                dynGbc.fill = GridBagConstraints.HORIZONTAL;
                dynGbc.gridx = 0;
                dynGbc.gridy = 0;
                if (searchMethodCombo.getSelectedItem().equals("Nach Name suchen")) {
                    dynamicPanel.add(new JLabel("Name:"), dynGbc);
                    dynGbc.gridx = 1;
                    dynamicPanel.add(nameField, dynGbc);
                } else {
                    dynamicPanel.add(new JLabel("Kategorie:"), dynGbc);
                    dynGbc.gridx = 1;
                    dynamicPanel.add(categoryCombo, dynGbc);
                }
                dynamicPanel.revalidate();
                dynamicPanel.repaint();
            }
        });

        // Initialisiere mit "Nach Name suchen"
        GridBagConstraints dynGbc = new GridBagConstraints();
        dynGbc.insets = new Insets(5, 5, 5, 5);
        dynGbc.fill = GridBagConstraints.HORIZONTAL;
        dynGbc.gridx = 0;
        dynGbc.gridy = 0;
        dynamicPanel.add(new JLabel("Name:"), dynGbc);
        dynGbc.gridx = 1;
        dynamicPanel.add(nameField, dynGbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        searchDialog.add(dynamicPanel, gbc);

        JButton searchButton = new JButton("Suchen");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (searchMethodCombo.getSelectedItem().equals("Nach Name suchen")) {
                    String name = nameField.getText().trim();
                    if (!name.isEmpty()) {
                        List<Program> results = service.searchPrograms(name, null);
                        refreshTable(results); // Nur die Tabelle aktualisieren
                    }
                } else {
                    Purpose category = (Purpose) categoryCombo.getSelectedItem();
                    if (category != null) {
                        List<Program> results = service.searchPrograms(null, Set.of(category));
                        refreshTable(results); // Nur die Tabelle aktualisieren
                    }
                }
                searchDialog.dispose();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        searchDialog.add(searchButton, gbc);

        searchDialog.pack();
        searchDialog.setLocationRelativeTo(view);
        searchDialog.setVisible(true);
    }

    private void loadPrograms() {
        try {
            service.loadPrograms();
            refreshTable(service.getAllPrograms());
            updateRecentProgramsList(); // Aktualisiere die Liste der letzten Programme
            JOptionPane.showMessageDialog(view, "Daten erfolgreich geladen!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Fehler beim Laden: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importPrograms() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Dateien", "json"));
        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                service.importPrograms(fileChooser.getSelectedFile().getAbsolutePath());
                refreshTable(service.getAllPrograms());
                updateRecentProgramsList(); // Aktualisiere die Liste der letzten Programme
                JOptionPane.showMessageDialog(view, "Daten erfolgreich importiert!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Fehler beim Importieren: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportPrograms() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Dateien", "json"));
        if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".json")) {
                filePath += ".json";
            }
            try {
                service.exportPrograms(filePath);
                JOptionPane.showMessageDialog(view, "Daten erfolgreich exportiert!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Fehler beim Exportieren: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showInfo() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        String currentDateTime = now.format(formatter);
        String info = String.format("Programm: %s\nVersion: %s\nAutor: %s\nAktuelles Datum/Uhrzeit: %s",
                PROGRAM_NAME, VERSION, AUTHOR, currentDateTime);
        JOptionPane.showMessageDialog(view, info, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private Program createProgramFromInput() {
        String name = view.getNameField().getText().trim();
        Set<Purpose> categories = Arrays.stream(view.getCategoryPopup().getComponents())
                .filter(c -> c instanceof JCheckBoxMenuItem && ((JCheckBoxMenuItem) c).isSelected())
                .map(c -> {
                    String text = ((JCheckBoxMenuItem) c).getText();
                    String enumName = text.toUpperCase().replace(" ", "_").replace("/", "_");
                    return Purpose.valueOf(enumName);
                })
                .collect(Collectors.toSet());
        if (categories.isEmpty()) {
            Purpose selectedCategory = (Purpose) view.getCategoryComboBox().getSelectedItem();
            if (selectedCategory != null) {
                categories.add(selectedCategory);
            }
        }
        System.out.println("Kategorien: " + categories);
        String description = view.getDescriptionArea().getText().trim();
        // Betriebssysteme aus Checkboxes sammeln
        StringBuilder osBuilder = new StringBuilder();
        if (view.getWindowsCheck().isSelected()) osBuilder.append("Windows");
        if (view.getLinuxCheck().isSelected()) {
            if (osBuilder.length() > 0) osBuilder.append(", ");
            osBuilder.append("Linux");
        }
        if (view.getMacosCheck().isSelected()) {
            if (osBuilder.length() > 0) osBuilder.append(", ");
            osBuilder.append("macOS");
        }
        if (view.getAndroidCheck().isSelected()) {
            if (osBuilder.length() > 0) osBuilder.append(", ");
            osBuilder.append("Android");
        }
        if (view.getIosCheck().isSelected()) {
            if (osBuilder.length() > 0) osBuilder.append(", ");
            osBuilder.append("iOS");
        }
        String os = osBuilder.length() > 0 ? osBuilder.toString() : null;
        List<String> alternatives = Arrays.stream(view.getAlternativesField().getText().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (name.isEmpty() || categories.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Name und Kategorie sind erforderlich!");
            return null;
        }

        return new Program(name, categories, description, os, alternatives);
    }

    private void refreshTable(List<Program> programs) {
        DefaultTableModel model = (DefaultTableModel) view.getProgramTable().getModel();
        model.setRowCount(0);
        for (Program p : programs) {
            model.addRow(new Object[]{
                    p.name(),
                    p.purposes().stream().map(Purpose::toString).collect(Collectors.joining(", ")),
                    p.description(),
                    p.operatingSystem(), // Renderer wandelt in Icons um
                    String.join(", ", p.alternatives())
            });
        }
    }

    private void refreshTableWithPurposeFilter(Set<Purpose> purposes) {
        List<Program> filteredPrograms = service.getAllPrograms().stream()
                .filter(p -> !p.purposes().isEmpty() && p.purposes().stream().anyMatch(purposes::contains))
                .collect(Collectors.toList());
        refreshTable(filteredPrograms);
    }

    // Renderer für mehrere Betriebssystem-Icons
    private static class OSIconRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Abstand zwischen Icons
            panel.setOpaque(true);
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }

            if (value != null) {
                String os = (String) value;
                if (os != null && !os.isEmpty()) {
                    String[] osList = os.split(",\\s*");
                    for (String osItem : osList) {
                        String osLower = osItem.trim().toLowerCase();
                        String iconPath = "/buttons/" + osLower + "_sm.png"; // Helles Icon für aktive Systeme
                        ImageIcon icon = new ImageIcon(OSIconRenderer.class.getResource(iconPath));
                        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                            // Skaliere das Icon, um einheitliche Größe sicherzustellen
                            Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                            ImageIcon scaledIcon = new ImageIcon(scaledImage);
                            JLabel iconLabel = new JLabel(scaledIcon);
                            iconLabel.setToolTipText(osItem); // Tooltip für bessere Benutzbarkeit
                            panel.add(iconLabel);
                        } else {
                            // Fallback: Text anzeigen, wenn Icon nicht geladen werden kann
                            JLabel textLabel = new JLabel(osItem);
                            panel.add(textLabel);
                        }
                    }
                }
            }
            return panel;
        }
    }
}