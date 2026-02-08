# ProgrammVerwaltung

Verwaltung von Programmen und deren Zwecke - Eine Java Swing Anwendung mit FlatLaf Design.

## Features

- Verwaltung von Software-Programmen
- Kategorisierung nach Zweck (Textverarbeitung, Entwicklung, Grafik, etc.)
- Betriebssystem-Zuordnung
- Alternativen-Verwaltung
- Import/Export-Funktionalität
- Moderne Benutzeroberfläche mit FlatLaf

## Voraussetzungen

- Java 21 oder höher
- Maven 3.9 oder höher

## Lokaler Build

### Anwendung ausführen

```bash
mvn clean compile exec:java
```

### JAR erstellen

```bash
mvn clean package
```

Das ausführbare JAR befindet sich dann in `target/ProgrammVerwaltung-1.0.0-shaded.jar`

### Ausführen des JAR

```bash
java -jar target/ProgrammVerwaltung-1.0.0-shaded.jar
```

## Installer erstellen

### Voraussetzungen für Installer

Für die Erstellung von Installern wird JDK 21 mit jpackage benötigt.

#### Icon-Dateien

Die folgenden Icon-Dateien müssen im Verzeichnis `src/main/resources/icons/` vorhanden sein:

- **Windows**: `app-icon.ico` (256x256 oder Multi-Size ICO)
- **Linux**: `app-icon.png` (512x512 PNG)
- **macOS**: `app-icon.icns` (Multi-Size ICNS)

#### Icon-Konvertierung

Du kannst deine Basis-PNG-Datei (512x512) mit den folgenden Tools konvertieren:

**Für Windows (.ico):**
```bash
# Mit ImageMagick
convert app-icon.png -define icon:auto-resize=256,128,96,64,48,32,16 app-icon.ico
```

**Für macOS (.icns):**
```bash
# Mit iconutil (macOS)
mkdir app-icon.iconset
sips -z 16 16     app-icon.png --out app-icon.iconset/icon_16x16.png
sips -z 32 32     app-icon.png --out app-icon.iconset/icon_16x16@2x.png
sips -z 32 32     app-icon.png --out app-icon.iconset/icon_32x32.png
sips -z 64 64     app-icon.png --out app-icon.iconset/icon_32x32@2x.png
sips -z 128 128   app-icon.png --out app-icon.iconset/icon_128x128.png
sips -z 256 256   app-icon.png --out app-icon.iconset/icon_128x128@2x.png
sips -z 256 256   app-icon.png --out app-icon.iconset/icon_256x256.png
sips -z 512 512   app-icon.png --out app-icon.iconset/icon_256x256@2x.png
sips -z 512 512   app-icon.png --out app-icon.iconset/icon_512x512.png
cp app-icon.png app-icon.iconset/icon_512x512@2x.png
iconutil -c icns app-icon.iconset
```

### Windows Installer (MSI)

```bash
mvn clean package -Pwindows-installer
```

Installer: `target/dist/ProgrammVerwaltung-1.0.0.msi`

### Linux Installer (DEB)

```bash
mvn clean package -Plinux-installer
```

Installer: `target/dist/programmverwaltung_1.0.0-1_amd64.deb`

### macOS Installer (DMG)

```bash
mvn clean package -Pmac-installer
```

Installer: `target/dist/ProgrammVerwaltung-1.0.0.dmg`

## GitHub Actions - Automatische Builds

Bei jedem Tag-Push (z.B. `v1.0.0`) werden automatisch Installer für alle drei Plattformen erstellt:

1. Tag erstellen und pushen:
```bash
git tag v1.0.0
git push origin v1.0.0
```

2. GitHub Actions erstellt automatisch:
   - Windows MSI
   - Linux DEB
   - macOS DMG

3. Release wird automatisch mit allen Installern erstellt

### Manueller Build-Trigger

Du kannst den Build auch manuell über die GitHub Actions UI triggern:
- Gehe zu "Actions" Tab
- Wähle "Build and Release"
- Klicke "Run workflow"

## Projektstruktur

```
ProgrammVerwaltung/
├── src/
│   └── main/
│       ├── java/
│       │   └── eu/asgardschmiede/programmverwaltung/
│       │       ├── Main.java
│       │       ├── controller/
│       │       │   └── ProgramController.java
│       │       ├── model/
│       │       │   ├── Program.java
│       │       │   └── Purpose.java
│       │       ├── service/
│       │       │   └── ProgramService.java
│       │       └── view/
│       │           ├── ProgramView.java
│       │           └── ProgramPanel.java
│       └── resources/
│           ├── icons/
│           │   ├── app-icon.ico (Windows)
│           │   ├── app-icon.png (Linux)
│           │   └── app-icon.icns (macOS)
│           └── programs.json
├── .github/
│   └── workflows/
│       └── build-release.yml
├── pom.xml
└── README.md
```

## Technologie-Stack

- **Java**: 21
- **UI Framework**: Java Swing
- **Look & Feel**: FlatLaf 3.3
- **JSON Verarbeitung**: Jackson 2.17.2
- **Build Tool**: Maven
- **Packaging**: jpackage (via jpackage-maven-plugin)

## Lizenz

Siehe LICENSE Datei

## Autor

Varitaxx - [asgardschmiede.eu](https://asgardschmiede.eu)
