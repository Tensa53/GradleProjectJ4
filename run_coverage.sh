#!/bin/sh

# commands to generate jacoco report and coverage matrix from junit tests

rm -r reports-coverage/junit/*

./gradlew clean

./gradlew build

cp -r build/reports/jacoco/ reports-coverage/junit/

# commands to generate jacoco report and coverage matrix from jmh benchmarks
# minimal jmh conf used: -f 1 -i 1 -wi 0 -bm ss -tu ms (1 fork-iteration, no warmup, single-shot, time in milliseconds)

rm -r reports-coverage/jmh/*

./gradlew jmhJar

java -javaagent:/home/daniele/.gradle/caches/modules-2/files-2.1/org.jacoco/org.jacoco.agent/0.8.13/850ba9544f357712728f89fe3e1fd51b265a0192/org.jacoco.agent-0.8.13-runtime.jar=destfile=build/jacoco/bench.exec,jmx=true,excludes=org/example/runners/** -jar build/libs/GradleProjectJ4-1.0-SNAPSHOT-jmh.jar -f 1 -i 1 -wi 0 -bm ss -tu ms -jvmArgs "-javaagent:/home/daniele/.gradle/caches/modules-2/files-2.1/org.jacoco/org.jacoco.agent/0.8.13/850ba9544f357712728f89fe3e1fd51b265a0192/org.jacoco.agent-0.8.13-runtime.jar=destfile=build/jacoco/bench.exec,jmx=true,excludes=org/example/runners/**" -prof benchmarks.profilers.JaCoCoProfiler

./gradlew jacocoExternalReport

java -cp build/libs/GradleProjectJ4-1.0-SNAPSHOT-jmh.jar org.example.runners.JaCoCoXMLUncoveredMethods "reports-coverage/jmh/jacoco/jacocoTestReport.xml" "reports-coverage/jmh/uncovered.json"