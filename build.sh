#!/usr/bin/env bash
# Compila el mod sin Gradle: Minecraft 26.x no esta ofuscado, asi que basta javac
# contra el jar del juego y las librerias que ya instalo Modrinth.
# Uso (Git Bash):  ./build.sh
set -euo pipefail
cd "$(dirname "$0")"

VERSION=$(grep '^mod_version=' gradle.properties | cut -d= -f2)
JDK="${JAVA_HOME:-/c/Program Files/Eclipse Adoptium/jdk-25.0.4.101-hotspot}"
MC_JAR="${MC_JAR:-$APPDATA/ModrinthApp/meta/versions/26.3-0.20.1/26.3-0.20.1.jar}"
LIBS="${LIBS:-$APPDATA/ModrinthApp/meta/libraries}"

rm -rf build/classes build/res build/libs
mkdir -p build/classes build/res build/libs

# classpath: juego + librerias (las versiones mas nuevas primero)
CP="$(cygpath -w "$MC_JAR")"
while IFS= read -r jar; do CP="$CP;$(cygpath -w "$jar")"; done < <(find "$LIBS" -name '*.jar' | sort -r)
# Mod Menu y Cloth Config solo para compilar la pantalla de configuracion (no se empaquetan)
MODS="${MODS:-$APPDATA/ModrinthApp/profiles/Fabric 26.3 (1) (1)/mods}"
for jar in "$MODS"/modmenu-*.jar "$MODS"/cloth-config-*.jar; do CP="$CP;$(cygpath -w "$jar")"; done

find src/main/java -name '*.java' > build/sources.txt
"$JDK/bin/javac" --release 25 -encoding UTF-8 -proc:none -nowarn \
  -cp "$CP" -d build/classes @build/sources.txt

cp -r src/main/resources/. build/res/
sed -i "s/\${version}/$VERSION/" build/res/fabric.mod.json

JAR="build/libs/classic-fixes-$VERSION.jar"
"$JDK/bin/jar" --create --file "$JAR" -C build/classes . -C build/res .
echo "OK -> $JAR"
