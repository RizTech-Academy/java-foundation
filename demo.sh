#!/bin/sh
# Drive the packaged jar the way the course does, end to end.
#
#   mvn package && ./demo.sh
set -e

DIR=$(cd "$(dirname "$0")" && pwd)
JAR="$DIR/target/tiffin-tracker.jar"
DATA="$DIR/demo-data.csv"

if [ ! -f "$JAR" ]; then
  echo "Build it first: mvn package" >&2
  exit 1
fi

run() { java -Dtiffin.data="$DATA" -jar "$JAR" "$@"; }

rm -f "$DATA"

echo "=== help ==="
java -jar "$JAR" help

echo
echo "=== add four deliveries ==="
run add 2026-09-01 Priya 2
run add 2026-09-01 Arjun 1
run add 2026-09-02 Kavita 3
# A comma inside a name. Any reader written as line.split(",") loses this one.
run add 2026-09-02 "Kale, Kavita" 2

echo
echo "=== the data file ==="
cat "$DATA"

echo
echo "=== list ==="
run list

echo
echo "=== report — note the unregistered customer is listed, not fatal ==="
run report 2026-09

rm -f "$DATA"
