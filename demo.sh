#!/bin/sh
# Drive the packaged jar the way a reader would.
set -e
DIR=/private/tmp/claude-501/-Users-rizwanulhaque-Web-riztechacademy/d141afb5-5a19-4b9c-8540-908d61fa571a/scratchpad/tiffin
JAVA=/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/java
DATA=$DIR/demo-data.csv
JAR=$DIR/target/tiffin-tracker.jar

rm -f "$DATA"

echo "=== help ==="
"$JAVA" -jar "$JAR" help

echo
echo "=== add three deliveries ==="
"$JAVA" -Dtiffin.data="$DATA" -jar "$JAR" add 2026-09-01 Priya 2
"$JAVA" -Dtiffin.data="$DATA" -jar "$JAR" add 2026-09-01 Arjun 1
"$JAVA" -Dtiffin.data="$DATA" -jar "$JAR" add 2026-09-02 Kavita 3

echo
echo "=== the data file ==="
cat "$DATA"

echo
echo "=== list ==="
"$JAVA" -Dtiffin.data="$DATA" -jar "$JAR" list

echo
echo "=== report ==="
"$JAVA" -Dtiffin.data="$DATA" -jar "$JAR" report 2026-09

echo
echo "=== a bad row, added by hand ==="
echo "2026-09-03,Amit,99" >> "$DATA"
echo "2026-09-04,Rahul" >> "$DATA"
"$JAVA" -Dtiffin.data="$DATA" -jar "$JAR" report 2026-09 || echo "(exit code $?)"

echo
echo "=== an invalid argument ==="
"$JAVA" -Dtiffin.data="$DATA" -jar "$JAR" add 2026-09-05 Priya 9 || echo "(exit code $?)"

echo
echo "=== unknown command ==="
"$JAVA" -Dtiffin.data="$DATA" -jar "$JAR" frobnicate || echo "(exit code $?)"
