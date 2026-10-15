Elytra Swap (Fabric, Minecraft 26.3)
Voraussetzungen: JDK 25, Gradle 9.3+
Bauen:  gradle wrapper --gradle-version 9.3.1   (einmalig)
        ./gradlew build        (Windows: gradlew.bat build)
Ergebnis: build/libs/elytraswap-1.0.0+26.3.jar  (NICHT die -sources.jar)
In den mods-Ordner legen, zusammen mit Fabric API 0.161.0+26.3.
Taste ändern: Optionen > Steuerung > Tastenbelegung > "Elytra Swap".
Wichtig: Mod muss auch auf dem Server laufen (Singleplayer: automatisch).

Bauen über GitHub (ohne lokale Installation):
1. Neues Repository auf github.com anlegen und alle Dateien hochladen
   (inkl. des versteckten Ordners .github).
2. Tab "Actions" -> "Build Mod" -> "Run workflow" (oder einfach pushen).
3. Nach ca. 2-5 Minuten: auf den Lauf klicken -> unten bei "Artifacts"
   "elytraswap-jar" herunterladen -> ZIP entpacken -> .jar in den mods-Ordner.
Optional: Tag "v1.0.0" erstellen -> die .jar erscheint unter "Releases".
