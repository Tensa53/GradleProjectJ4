#!/bin/sh

rm -r report/junit/*

rm -r report/jmh/*

./gradlew clean

./gradlew build

cp -r build/reports/jacoco/ report/junit/

./gradlew jmhJar

java -javaagent:/home/daniele/.gradle/caches/modules-2/files-2.1/org.jacoco/org.jacoco.agent/0.8.13/850ba9544f357712728f89fe3e1fd51b265a0192/org.jacoco.agent-0.8.13-runtime.jar=destfile=build/jacoco/bench.exec,jmx=true,excludes=org/example/runners/** -jar build/libs/GradleProjectJ4-1.0-SNAPSHOT-jmh.jar -jvmArgs "-javaagent:/home/daniele/.gradle/caches/modules-2/files-2.1/org.jacoco/org.jacoco.agent/0.8.13/850ba9544f357712728f89fe3e1fd51b265a0192/org.jacoco.agent-0.8.13-runtime.jar=destfile=build/jacoco/bench.exec,jmx=true,excludes=org/example/runners/**" -prof benchmarks.profilers.JaCoCoProfiler -rf json -rff report/jmh/jmh-results.json

./gradlew jacocoExternalReport

java -cp build/libs/GradleProjectJ4-1.0-SNAPSHOT-jmh.jar org.example.runners.JaCoCoXMLUncoveredMethods "report/jmh/jacoco/jacocoTestReport.xml" "report/jmh/uncovered.json"