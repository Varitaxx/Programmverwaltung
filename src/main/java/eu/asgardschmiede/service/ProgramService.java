package eu.asgardschmiede.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.asgardschmiede.model.Program;
import eu.asgardschmiede.model.Purpose;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProgramService {
    private static final String DEFAULT_FILE_PATH = "src/main/resources/programs.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private List<Program> programs;

    public ProgramService() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty Printing aktivieren
        loadPrograms(DEFAULT_FILE_PATH);
    }

    private void loadPrograms(String filePath) {
        File file = new File(filePath);
        try {
            if (file.exists()) {
                programs = mapper.readValue(file, new TypeReference<List<Program>>() {});
            } else {
                programs = new ArrayList<>();
                Files.createDirectories(Path.of("src/main/resources"));
                Files.createFile(Path.of(filePath));
            }
        } catch (IOException e) {
            programs = new ArrayList<>();
            System.err.println("Fehler beim Laden der Datei " + filePath + ": " + e.getMessage());
            throw new RuntimeException("Fehler beim Laden: " + e.getMessage());
        }
    }

    public void savePrograms() {
        File file = new File(DEFAULT_FILE_PATH);
        try {
            Files.createDirectories(file.getParentFile().toPath());
            mapper.writeValue(file, programs);
            System.out.println("Daten erfolgreich in " + DEFAULT_FILE_PATH + " gespeichert. Inhalt: " + programs);
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern in " + DEFAULT_FILE_PATH + ": " + e.getMessage());
            throw new RuntimeException("Fehler beim Speichern: " + e.getMessage());
        }
    }

    public void saveNewEntry(Program program) {
        programs.add(program);
        savePrograms();
    }

    public void loadPrograms() {
        loadPrograms(DEFAULT_FILE_PATH);
    }

    public void importPrograms(String filePath) {
        loadPrograms(filePath);
    }

    public void exportPrograms(String filePath) {
        try {
            Files.createDirectories(Path.of(filePath).getParent());
            mapper.writeValue(new File(filePath), programs);
        } catch (IOException e) {
            System.err.println("Fehler beim Exportieren in " + filePath + ": " + e.getMessage());
            throw new RuntimeException("Fehler beim Exportieren: " + e.getMessage());
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
}