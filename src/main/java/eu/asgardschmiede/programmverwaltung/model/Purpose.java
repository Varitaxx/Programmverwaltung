package eu.asgardschmiede.programmverwaltung.model;

public enum Purpose {
    TEXTVERARBEITUNG("Textverarbeitung"),
    TABELLENKALKULATION("Tabellenkalkulation"),
    DATENBANK("Datenbank"),
    PRÄSENTATION("Präsentation"),
    GRAFIK("Grafik"),
    AUDIO("Audio"),
    VIDEO("Video"),
    ENTWICKLUNG("Entwicklung"),
    SPIEL("Spiel"),
    ANDERE("Andere"),
    INTERNET("Internet"),
    DIENSTPROGRAMM("Dienstprogramm"),
    WISSENSCHAFT_LERNEN("Wissenschaft/Lernen"),
    EMAIL_FTP("EMail/FTP"),
    SYSTEMEINSTELLUNG("Systemeinstellung");

    private final String displayName;

    Purpose(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}