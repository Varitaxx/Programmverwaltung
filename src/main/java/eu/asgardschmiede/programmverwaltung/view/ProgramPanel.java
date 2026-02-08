package eu.asgardschmiede.programmverwaltung.view;

import eu.asgardschmiede.programmverwaltung.model.Purpose;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProgramPanel extends JPanel {
    private JTextField nameField;
    private JComboBox<Purpose> categoryComboBox;
    private JPopupMenu categoryPopup;
    private JTextArea descriptionArea;
    private JPanel osPanel; // Panel für Checkboxes
    private JCheckBox windowsCheck, linuxCheck, macosCheck, androidCheck, iosCheck; // Checkboxes für Betriebssysteme
    private JTextField alternativesField;
    private JTable programTable;
    private JList<String> recentProgramsList;
    private JButton addButton, updateButton, deleteButton, searchButton, saveButton, abortButton, offButton;
    private JMenuItem loadMenuItem, importMenuItem, exportMenuItem, infoMenuItem, exitMenuItem, showLegendMenuItem;
    private JButton editButton;

    public ProgramPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        // Menüleiste und Symbolleiste
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(245, 245, 245)); // Konsistenter Hintergrund

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 245, 245)); // Konsistenter Hintergrund für Menüleiste

        JMenu fileMenu = new JMenu("Datei");
        fileMenu.setBackground(new Color(245, 245, 245)); // Konsistenter Hintergrund
        fileMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                fileMenu.setBackground(new Color(230, 230, 230)); // Dunkleres Grau beim Hovern
            }
            @Override
            public void mouseExited(MouseEvent e) {
                fileMenu.setBackground(new Color(245, 245, 245)); // Zurück zum Standard-Hintergrund
            }
        });
        loadMenuItem = new JMenuItem("Laden", new ImageIcon(getClass().getResource("/icons/load.png")));
        importMenuItem = new JMenuItem("Importieren", new ImageIcon(getClass().getResource("/icons/import.png")));
        exportMenuItem = new JMenuItem("Exportieren", new ImageIcon(getClass().getResource("/icons/export.png")));
        exitMenuItem = new JMenuItem("Beenden", new ImageIcon(getClass().getResource("/icons/off.png")));
        fileMenu.add(loadMenuItem);
        fileMenu.add(importMenuItem);
        fileMenu.add(exportMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        JMenu helpMenu = new JMenu("Hilfe");
        helpMenu.setBackground(new Color(245, 245, 245)); // Konsistenter Hintergrund
        helpMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                helpMenu.setBackground(new Color(230, 230, 230)); // Dunkleres Grau beim Hovern
            }
            @Override
            public void mouseExited(MouseEvent e) {
                helpMenu.setBackground(new Color(245, 245, 245)); // Zurück zum Standard-Hintergrund
            }
        });
        infoMenuItem = new JMenuItem("Info", new ImageIcon(getClass().getResource("/icons/info.png")));
        showLegendMenuItem = new JMenuItem("Legende anzeigen");
        showLegendMenuItem.addActionListener(e -> showLegendDialog());
        helpMenu.add(infoMenuItem);
        helpMenu.add(showLegendMenuItem);
        menuBar.add(helpMenu);

        toolBar.add(menuBar);

        addButton = new JButton(new ImageIcon(getClass().getResource("/icons/add.png"))); // Soll "+" sein
        updateButton = new JButton(new ImageIcon(getClass().getResource("/icons/update.png"))); // Soll "↺" sein
        deleteButton = new JButton(new ImageIcon(getClass().getResource("/icons/delete.png"))); // Soll "−" sein
        saveButton = new JButton(new ImageIcon(getClass().getResource("/icons/save.png"))); // Soll "✔" sein
        abortButton = new JButton(new ImageIcon(getClass().getResource("/icons/abort.png"))); // Soll "✖" sein
        toolBar.add(addButton);
        toolBar.add(updateButton);
        toolBar.add(deleteButton);
        toolBar.add(saveButton);
        toolBar.add(abortButton);

        toolBar.add(Box.createHorizontalGlue());

        searchButton = new JButton(new ImageIcon(getClass().getResource("/icons/search.png"))); // Soll "🔍" sein
        offButton = new JButton(new ImageIcon(getClass().getResource("/icons/off.png"))); // Soll "🔴" sein
        toolBar.add(searchButton);
        toolBar.add(offButton);

        // Eingabebereich (Editierbereich)
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(20) {
            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.height = (int) (getFontMetrics(getFont()).getHeight() * 2.5);
                return size;
            }
        };
        nameField.setAlignmentY(TOP_ALIGNMENT);
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Kategorie:"), gbc);
        categoryComboBox = new JComboBox<>(Purpose.values());
        categoryComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "" : ((Purpose) value).toString());
                return this;
            }
        });
        categoryPopup = new JPopupMenu();
        for (Purpose purpose : Purpose.values()) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(purpose.toString());
            categoryPopup.add(item);
        }
        categoryComboBox.setComponentPopupMenu(categoryPopup);
        gbc.gridx = 1;
        inputPanel.add(categoryComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Beschreibung:"), gbc);
        descriptionArea = new JTextArea(3, 40) { // 40 Zeichen Breite
            @Override
            public void setText(String t) {
                super.setText(t);
                setCaretPosition(0);
            }
        };
        descriptionArea.setLineWrap(true); // Automatischer Umbruch
        descriptionArea.setWrapStyleWord(true); // Umbruch nur zwischen Wörtern
        descriptionArea.setAlignmentY(TOP_ALIGNMENT);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setPreferredSize(new Dimension(400, 100)); // Anpassung der Größe
        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Scrollbalken bei Bedarf
        descriptionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Kein horizontaler Scrollbalken
        gbc.gridx = 1;
        inputPanel.add(descriptionScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Betriebssystem:"), gbc);
        osPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Initialisiere Checkboxes mit hellen und dunklen Icons
        ImageIcon windowsLight = new ImageIcon(getClass().getResource("/buttons/windows_sm.png"));
        ImageIcon windowsDark = new ImageIcon(getClass().getResource("/buttons/windows_sm_dark.png"));
        windowsCheck = new JCheckBox();
        windowsCheck.setIcon(windowsDark);
        windowsCheck.setSelectedIcon(windowsLight);
        windowsCheck.setOpaque(false);
        windowsCheck.setBorderPainted(false);

        ImageIcon linuxLight = new ImageIcon(getClass().getResource("/buttons/linux_sm.png"));
        ImageIcon linuxDark = new ImageIcon(getClass().getResource("/buttons/linux_sm_dark.png"));
        linuxCheck = new JCheckBox();
        linuxCheck.setIcon(linuxDark);
        linuxCheck.setSelectedIcon(linuxLight);
        linuxCheck.setOpaque(false);
        linuxCheck.setBorderPainted(false);

        ImageIcon macosLight = new ImageIcon(getClass().getResource("/buttons/macos_sm.png"));
        ImageIcon macosDark = new ImageIcon(getClass().getResource("/buttons/macos_sm_dark.png"));
        macosCheck = new JCheckBox();
        macosCheck.setIcon(macosDark);
        macosCheck.setSelectedIcon(macosLight);
        macosCheck.setOpaque(false);
        macosCheck.setBorderPainted(false);

        ImageIcon androidLight = new ImageIcon(getClass().getResource("/buttons/android_sm.png"));
        ImageIcon androidDark = new ImageIcon(getClass().getResource("/buttons/android_sm_dark.png"));
        androidCheck = new JCheckBox();
        androidCheck.setIcon(androidDark);
        androidCheck.setSelectedIcon(androidLight);
        androidCheck.setOpaque(false);
        androidCheck.setBorderPainted(false);

        ImageIcon iosLight = new ImageIcon(getClass().getResource("/buttons/ios_sm.png"));
        ImageIcon iosDark = new ImageIcon(getClass().getResource("/buttons/ios_sm_dark.png"));
        iosCheck = new JCheckBox();
        iosCheck.setIcon(iosDark);
        iosCheck.setSelectedIcon(iosLight);
        iosCheck.setOpaque(false);
        iosCheck.setBorderPainted(false);

        osPanel.add(windowsCheck);
        osPanel.add(linuxCheck);
        osPanel.add(macosCheck);
        osPanel.add(androidCheck);
        osPanel.add(iosCheck);
        gbc.gridx = 1;
        inputPanel.add(osPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Alternativen:"), gbc);
        alternativesField = new JTextField(20);
        alternativesField.setAlignmentY(TOP_ALIGNMENT);
        gbc.gridx = 1;
        inputPanel.add(alternativesField, gbc);

        // Tabelle
        programTable = new JTable();
        programTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(programTable);
        tableScrollPane.setPreferredSize(new Dimension(900, 200));

        // Liste der letzten 25 Einträge
        recentProgramsList = new JList<>(new DefaultListModel<>());
        recentProgramsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane recentProgramsScrollPane = new JScrollPane(recentProgramsList);
        recentProgramsScrollPane.setPreferredSize(new Dimension(200, 400));
        JLabel recentLabel = new JLabel("Letzte Programme:");
        recentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel recentPanel = new JPanel();
        recentPanel.setLayout(new BoxLayout(recentPanel, BoxLayout.Y_AXIS));
        recentPanel.add(recentLabel);
        recentPanel.add(recentProgramsScrollPane);

        // Neuer Button "Bearbeiten"
        editButton = new JButton("Bearbeiten");
        editButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Layout zusammenfügen
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        centerPanel.add(editButton, BorderLayout.SOUTH);

        add(toolBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(recentPanel, BorderLayout.EAST);

        // Modernes Design
        setBackground(new Color(245, 245, 245));
        inputPanel.setBackground(new Color(255, 255, 255));
        recentPanel.setBackground(new Color(255, 255, 255));
    }

    private void showLegendDialog() {
        JDialog legendDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Legende", true);
        legendDialog.setLayout(new BorderLayout(10, 10));

        // Überschrift
        JLabel title = new JLabel("Legende", SwingConstants.CENTER);
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, 14));
        legendDialog.add(title, BorderLayout.NORTH);

        // Container für die beiden Spalten
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Abschnitt für Aktionen
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Aktionen"));
        actionsPanel.setPreferredSize(new Dimension(200, 300)); // Explizite Größe setzen

        // Aktionen mit Icon in derselben Zeile
        actionsPanel.add(createActionPanel("Hinzufügen:", "/icons/add.png", "Hinzufügen"));
        actionsPanel.add(createActionPanel("Aktualisieren:", "/icons/update.png", "Aktualisieren"));
        actionsPanel.add(createActionPanel("Löschen:", "/icons/delete.png", "Löschen"));
        actionsPanel.add(createActionPanel("Speichern:", "/icons/save.png", "Speichern"));
        actionsPanel.add(createActionPanel("Abbrechen:", "/icons/abort.png", "Abbrechen"));
        actionsPanel.add(createActionPanel("Suchen:", "/icons/search.png", "Suchen"));
        actionsPanel.add(createActionPanel("Beenden:", "/icons/off.png", "Beenden"));

        // Abschnitt für Betriebssysteme
        JPanel osPanelLegend = new JPanel();
        osPanelLegend.setLayout(new BoxLayout(osPanelLegend, BoxLayout.Y_AXIS));
        osPanelLegend.setBorder(BorderFactory.createTitledBorder("Betriebssysteme"));
        osPanelLegend.setPreferredSize(new Dimension(200, 300)); // Explizite Größe setzen

        // Betriebssysteme mit beiden Zuständen (ausgewählt/nicht ausgewählt)
        osPanelLegend.add(createOsPanel("Windows (ausgewählt):", "/buttons/windows_sm.png", "Windows (ausgewählt)"));
        osPanelLegend.add(createOsPanel("Windows (nicht ausgewählt):", "/buttons/windows_sm_dark.png", "Windows (nicht ausgewählt)"));
        osPanelLegend.add(createOsPanel("Linux (ausgewählt):", "/buttons/linux_sm.png", "Linux (ausgewählt)"));
        osPanelLegend.add(createOsPanel("Linux (nicht ausgewählt):", "/buttons/linux_sm_dark.png", "Linux (nicht ausgewählt)"));
        osPanelLegend.add(createOsPanel("macOS (ausgewählt):", "/buttons/macos_sm.png", "macOS (ausgewählt)"));
        osPanelLegend.add(createOsPanel("macOS (nicht ausgewählt):", "/buttons/macos_sm_dark.png", "macOS (nicht ausgewählt)"));
        osPanelLegend.add(createOsPanel("Android (ausgewählt):", "/buttons/android_sm.png", "Android (ausgewählt)"));
        osPanelLegend.add(createOsPanel("Android (nicht ausgewählt):", "/buttons/android_sm_dark.png", "Android (nicht ausgewählt)"));
        osPanelLegend.add(createOsPanel("iOS (ausgewählt):", "/buttons/ios_sm.png", "iOS (ausgewählt)"));
        osPanelLegend.add(createOsPanel("iOS (nicht ausgewählt):", "/buttons/ios_sm_dark.png", "iOS (nicht ausgewählt)"));

        // Hinzufügen der Panels nebeneinander
        contentPanel.add(actionsPanel);
        contentPanel.add(osPanelLegend);

        legendDialog.add(contentPanel, BorderLayout.CENTER);

        // Setze eine Mindestgröße für das Dialogfenster
        legendDialog.setMinimumSize(new Dimension(500, 400));
        legendDialog.pack();
        legendDialog.setLocationRelativeTo(this);
        legendDialog.setVisible(true);
    }

    private JPanel createActionPanel(String labelText, String iconPath, String tooltip) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.add(new JLabel(labelText));
        panel.add(createIconLabel(iconPath, tooltip));
        return panel;
    }

    private JPanel createOsPanel(String labelText, String iconPath, String tooltip) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.add(new JLabel(labelText));
        panel.add(createIconLabel(iconPath, tooltip));
        return panel;
    }

    private JLabel createIconLabel(String iconPath, String tooltip) {
        ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(scaledImage));
            label.setToolTipText(tooltip);
            return label;
        } else {
            System.err.println("Fehler: Icon konnte nicht geladen werden: " + iconPath + " (Tooltipp: " + tooltip + ")");
            JLabel fallbackLabel = new JLabel("[" + tooltip + "]"); // Fallback-Text
            fallbackLabel.setToolTipText("Icon fehlt: " + iconPath);
            return fallbackLabel;
        }
    }

    // Getter für Controller
    public JTextField getNameField() {
        return nameField;
    }

    public JComboBox<Purpose> getCategoryComboBox() {
        return categoryComboBox;
    }

    public JPopupMenu getCategoryPopup() {
        return categoryPopup;
    }

    public JTextArea getDescriptionArea() {
        return descriptionArea;
    }

    public JPanel getOsPanel() { // Getter für das Panel
        return osPanel;
    }

    public JCheckBox getWindowsCheck() { // Getter für Windows Checkbox
        return windowsCheck;
    }

    public JCheckBox getLinuxCheck() { // Getter für Linux Checkbox
        return linuxCheck;
    }

    public JCheckBox getMacosCheck() { // Getter für macOS Checkbox
        return macosCheck;
    }

    public JCheckBox getAndroidCheck() { // Getter für Android Checkbox
        return androidCheck;
    }

    public JCheckBox getIosCheck() { // Getter für iOS Checkbox
        return iosCheck;
    }

    public JTextField getAlternativesField() {
        return alternativesField;
    }

    public JTable getProgramTable() {
        return programTable;
    }

    public JList<String> getRecentProgramsList() {
        return recentProgramsList;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getAbortButton() {
        return abortButton;
    }

    public JButton getOffButton() {
        return offButton;
    }

    public JMenuItem getLoadMenuItem() {
        return loadMenuItem;
    }

    public JMenuItem getImportMenuItem() {
        return importMenuItem;
    }

    public JMenuItem getExportMenuItem() {
        return exportMenuItem;
    }

    public JMenuItem getExitMenuItem() {
        return exitMenuItem;
    }

    public JMenuItem getInfoMenuItem() {
        return infoMenuItem;
    }

    public JButton getEditButton() {
        return editButton;
    }

    public void clearInputFields() {
        nameField.setText("");
        for (Component item : categoryPopup.getComponents()) {
            if (item instanceof JCheckBoxMenuItem) {
                ((JCheckBoxMenuItem) item).setSelected(false);
            }
        }
        descriptionArea.setText("");
        windowsCheck.setSelected(false); // Deselektieren der Checkboxes
        linuxCheck.setSelected(false);
        macosCheck.setSelected(false);
        androidCheck.setSelected(false);
        iosCheck.setSelected(false);
        alternativesField.setText("");
        categoryComboBox.setSelectedIndex(0);
    }
}