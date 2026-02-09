package eu.asgardschmiede.programmverwaltung.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.asgardschmiede.programmverwaltung.model.Program;
import eu.asgardschmiede.programmverwaltung.model.Purpose;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProgramService {
    private static final String APP_NAME = "ProgrammVerwaltung";
    private static final String DATA_FILE_NAME = "programs.json";
    private final String dataFilePath;
    private final ObjectMapper mapper = new ObjectMapper();
    private List<Program> programs;

    public ProgramService() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty Printing aktivieren

        // Plattformübergreifenden Dateipfad ermitteln
        this.dataFilePath = getDataFilePath();

        // Stelle sicher, dass das Verzeichnis existiert
        ensureDataDirectoryExists();

        // Lade die Programme
        loadPrograms(dataFilePath);
    }

    /**
     * Ermittelt den plattformübergreifenden Pfad für die Datendatei.
     * Windows: C:\Users\<username>\AppData\Local\ProgrammVerwaltung\programs.json
     * Linux:   /home/<username>/.local/share/ProgrammVerwaltung/programs.json
     * macOS:   /Users/<username>/Library/Application Support/ProgrammVerwaltung/programs.json
     */
    private String getDataFilePath() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        Path dataDir;
        if (os.contains("win")) {
            // Windows: AppData\Local
            dataDir = Paths.get(userHome, "AppData", "Local", APP_NAME);
        } else if (os.contains("mac")) {
            // macOS: ~/Library/Application Support
            dataDir = Paths.get(userHome, "Library", "Application Support", APP_NAME);
        } else {
            // Linux: ~/.local/share
            dataDir = Paths.get(userHome, ".local", "share", APP_NAME);
        }

        return dataDir.resolve(DATA_FILE_NAME).toString();
    }

    /**
     * Stellt sicher, dass das Datenverzeichnis existiert.
     */
    private void ensureDataDirectoryExists() {
        try {
            Path dataDir = Paths.get(dataFilePath).getParent();
            if (dataDir != null && !Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
                System.out.println("Datenverzeichnis erstellt: " + dataDir);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Erstellen des Datenverzeichnisses: " + e.getMessage());
            throw new RuntimeException("Fehler beim Erstellen des Datenverzeichnisses: " + e.getMessage(), e);
        }
    }

    private void loadPrograms(String filePath) {
        File file = new File(filePath);
        try {
            if (file.exists()) {
                programs = mapper.readValue(file, new TypeReference<List<Program>>() {});
                System.out.println("Programme geladen von: " + filePath);
            } else {
                programs = new ArrayList<>();
                // Erstelle eine leere Datei
                savePrograms();
                System.out.println("Neue Datendatei erstellt: " + filePath);
            }
        } catch (IOException e) {
            programs = new ArrayList<>();
            System.err.println("Fehler beim Laden der Datei " + filePath + ": " + e.getMessage());
            throw new RuntimeException("Fehler beim Laden: " + e.getMessage(), e);
        }
    }

    public void savePrograms() {
        File file = new File(dataFilePath);
        try {
            // Stelle sicher, dass das Verzeichnis existiert
            ensureDataDirectoryExists();

            mapper.writeValue(file, programs);
            System.out.println("Daten erfolgreich gespeichert in " + dataFilePath + ". Anzahl: " + programs.size());
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern in " + dataFilePath + ": " + e.getMessage());
            throw new RuntimeException("Fehler beim Speichern: " + e.getMessage(), e);
        }
    }

    public void saveNewEntry(Program program) {
        programs.add(program);
        savePrograms();
    }

    public void loadPrograms() {
        loadPrograms(dataFilePath);
    }

    public void importPrograms(String filePath) {
        File file = new File(filePath);
        try {
            if (file.exists()) {
                List<Program> importedPrograms = mapper.readValue(file, new TypeReference<List<Program>>() {});
                programs = importedPrograms;
                // Speichere die importierten Daten in die Standard-Datei
                savePrograms();
                System.out.println("Programme importiert von: " + filePath);
            } else {
                throw new RuntimeException("Datei nicht gefunden: " + filePath);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Importieren von " + filePath + ": " + e.getMessage());
            throw new RuntimeException("Fehler beim Importieren: " + e.getMessage(), e);
        }
    }

    public void exportPrograms(String filePath) {
        try {
            Path exportPath = Paths.get(filePath);
            Path parentDir = exportPath.getParent();

            // Stelle sicher, dass das Zielverzeichnis existiert
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            mapper.writeValue(new File(filePath), programs);
            System.out.println("Programme exportiert nach: " + filePath);
        } catch (IOException e) {
            System.err.println("Fehler beim Exportieren in " + filePath + ": " + e.getMessage());
            throw new RuntimeException("Fehler beim Exportieren: " + e.getMessage(), e);
        }
    }

    public void addProgram(Program program) {
        programs.add(program);
        savePrograms();
    }

    public void updateProgram(int index, Program program) {
        programs.set(index, program);
        savePrograms();
    }

    public void deleteProgram(int index) {
        programs.remove(index);
        savePrograms();
    }

    public List<Program> getAllPrograms() {
        return new ArrayList<>(programs);
    }

    public List<Program> searchPrograms(String name, Set<Purpose> purposes) {
        return programs.stream()
                .filter(p -> name == null || p.name().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> purposes == null || purposes.isEmpty() || p.purposes().containsAll(purposes))
                .collect(Collectors.toList());
    }

    /**
     * Gibt den aktuellen Dateipfad zurück (für Debugging).
     */
    public String getCurrentDataFilePath() {
        return dataFilePath;
    }
}