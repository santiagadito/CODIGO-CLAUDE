# Cómo compilar Crystal Drops Addon

## Requisitos en tu PC
- Java 17 (JDK): https://adoptium.net/
- Git

## Pasos

### 1. Clonar el repositorio
```bash
git clone <url-del-repo>
cd crystal-drops-addon
```

### 2. Verificar que el jar de Crystal Leveling está en libs/
El archivo `libs/crystal_leveling-1.0-forge1.20.1.jar` ya está incluido en el repo.

### 3. Compilar
En Windows:
```bat
gradlew.bat build
```

En Mac/Linux:
```bash
chmod +x gradlew
./gradlew build
```

La primera vez descarga Minecraft + Forge (~5-8 GB). Tarda 20-30 min.
Las siguientes veces tarda menos de 1 minuto.

### 4. Encontrar el JAR
El archivo compilado queda en:
```
build/libs/crystal-drops-addon-1.0.0.jar
```

### 5. Instalarlo en el servidor
Copiar `crystal-drops-addon-1.0.0.jar` a la carpeta `mods/` del servidor
junto con:
- `crystal_leveling1.0forge1.20.1.jar`
- `Dangerous_Forge__1.20.1_v1.5.1.jar`

## Configuración recomendada para Dangerous Forge
Ver `DANGEROUS_FORGE_CONFIG.md` para los valores de `config/dangerous-common.toml`.
