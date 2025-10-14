package org.example.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jacoco.core.analysis.*;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.*;
import static org.example.listeners.MethodSignatureRetriever.getMethodSignatureParsed;

public class JaCoCoCoverageMatrix {

    private static final String JACOCO_MBEAN_NAME = "org.jacoco:type=Runtime";
    private static final String COVERAGE_MATRIX_BASIC_FILENAME = "coverage-matrix.json";
    private static String COVERAGE_MATRIX_FILEPATH = "";

    public static void updateCoverageMatrix(String testMethodName, String testClassName, String outputPath, String execFile, String classesPath) {
        try {
            COVERAGE_MATRIX_FILEPATH = "";
            COVERAGE_MATRIX_FILEPATH = outputPath + COVERAGE_MATRIX_BASIC_FILENAME;
//            System.out.println("Updating coverage matrix...");
            // Connect to the platform MBean server
            MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName(JACOCO_MBEAN_NAME);

            // Invoke the dump command with no reset (you can set to true if you want to reset coverage after each dump)
//            Object[] flush = outputPath.equals("report/junit/")? new Object[]{true}: outputPath.equals("report/jmh/")? new Object[]{true}: null;

            byte[] executionData = (byte[]) mbsc.invoke(objectName, "getExecutionData", new Object[]{true}, new String[]{"boolean"});
            JaCoCoAppender.appendExecutionData(executionData, new File(execFile));

//            System.out.println(outputPath);

//            if (outputPath == "report/junit/"){
//                executionData = (byte[]) mbsc.invoke(objectName, "getExecutionData", new Object[]{true}, new String[]{"boolean"});
//                JaCoCoAppender.appendExecutionData(executionData, new File("target/jacoco.exec"));
//            }
//            else if (outputPath == "report/jmh/"){
//                executionData = (byte[]) mbsc.invoke(objectName, "getExecutionData", new Object[]{false}, new String[]{"boolean"});
//                JaCoCoAppender.appendExecutionData(executionData, new File("target/jacoco-bench.exec"));
//            }

            // Use JaCoCo's ExecutionDataReader to parse the data
            ExecutionDataStore executionDataStore = new ExecutionDataStore();
            SessionInfoStore sessionInfoStore = new SessionInfoStore();
            ExecutionDataReader reader = new ExecutionDataReader(new ByteArrayInputStream(executionData));
            reader.setExecutionDataVisitor(executionDataStore);
            reader.setSessionInfoVisitor(sessionInfoStore);
            reader.read();

//            System.out.println("Covered source code methods for test case: " + context.getRequiredTestMethod().getName());

            // Analyze the covered classes to determine methods
            CoverageBuilder coverageBuilder = new CoverageBuilder();
            Analyzer analyzer = new Analyzer(executionDataStore, coverageBuilder);

//            System.out.println(executionDataStore.getContents().toString());

            // Specify the directory where your compiled classes are located
            File classesDir = new File(classesPath); // Adjust the classesDir as needed

            ArrayList<String> fullyQualifiedCurrentMethods = new ArrayList<>();

            // Analyze each class file to extract covered methods
            for (ExecutionData data : executionDataStore.getContents()) {
                if (data.hasHits()) {
                    String className = data.getName().replace("/", ".");
//                    System.out.println("Class: " + className);

                    // Analyze the corresponding .class file
                    File classFile = new File(classesDir, data.getName() + ".class");
                    if (classFile.exists()) {
                        try (FileInputStream classStream = new FileInputStream(classFile)) {
                            analyzer.analyzeClass(classStream, data.getName());
                        }
                    }

                    // Print the covered method names
                    Set<String> coveredMethods = getCoveredMethods(coverageBuilder, className);
                    ArrayList<String> coveredMethodsFullyQualified = new ArrayList<>();
                    for (String method : coveredMethods) {
                        if(method.contains("<init>"))
                            method = method.replace("<init>", className.substring(className.lastIndexOf('.') + 1));
                        fullyQualifiedCurrentMethods.add(className+"."+method);
                        coveredMethodsFullyQualified.add(className+"."+method);
                    }

                    // Update the json coverage-matrix file
                    updateCoverageMatrixFile(testClassName+"." + testMethodName,coveredMethodsFullyQualified);
                }
            }

            deleteOlderCoveredMethodsFromMatrix(testClassName+"." + testMethodName,fullyQualifiedCurrentMethods);

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Set<String> getCoveredMethods(CoverageBuilder coverageBuilder, String className) {
        Set<String> coveredMethods = new HashSet<>();
        className = className.replace(".", "/");
        for (IClassCoverage classCoverage : coverageBuilder.getClasses()) {
//            System.out.println(classCoverage.getName());
            if (classCoverage.getName().equals(className)) {
                for (IMethodCoverage methodCoverage : classCoverage.getMethods()) {
//                    System.out.println(methodCoverage.getName() + methodCoverage.getInstructionCounter().getCoveredCount());
                    if (methodCoverage.getInstructionCounter().getCoveredCount() > 0) {
                        StringBuilder methodName = new StringBuilder();

                        int methodLine = methodCoverage.getFirstLine();

                        if (!methodCoverage.getName().equals("<init>")) {
                            methodLine = methodCoverage.getFirstLine() - 1;
                        }

                        methodName.append(methodCoverage.getName()); // Get method name
                        String methodDescriptor = methodCoverage.getDesc();// Get method descriptor

//                        String signature = getMethodSignatureDescriptor(methodDescriptor);
                        String signature = getMethodSignatureParsed(methodLine, className);

                        methodName.append("(").append(signature).append(")");

                        int firstLine = methodCoverage.getFirstLine();
                        int lastLine = methodCoverage.getLastLine();

                        StringBuilder coveredlines = new StringBuilder();
                        coveredlines.append("{");

                        for (int line = firstLine; line <= lastLine; line++) {
                            ILine lineCoverage =  methodCoverage.getLine(line);

                            if (lineCoverage.getInstructionCounter().getCoveredCount() > 0) {
//                                System.out.println("Covered line: " + line);
                                coveredlines.append(line).append(";");
//                                coveredlines.append("L").append(line).append(";");
                            } else {
//                                System.out.println("Uncovered line: " + line);
                            }
                        }

                        coveredlines.append("}");

                        methodName.append(coveredlines);

                        coveredMethods.add(methodName.toString());

//                        System.out.println(methodName);
                    }
                }
            }
        }
        return coveredMethods;
    }

    private static void updateCoverageMatrixFile(String testName, ArrayList<String> coveredMethods) {
        ObjectMapper objectMapper = new ObjectMapper();
        TreeMap<String, Set<String>> coverageMatrix = new TreeMap<>();

        // Read existing coverage-matrix.json if it exists
        File coverageFile = new File(COVERAGE_MATRIX_FILEPATH);
        if (coverageFile.exists()) {
            try {
                coverageMatrix = objectMapper.readValue(coverageFile, new TypeReference<TreeMap<String, Set<String>>>() {});
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to read coverage-matrix.json");
            }
        }

        // Update the coverage matrix
        coverageMatrix.computeIfAbsent(testName, k -> new HashSet<>());

        for (String method : coveredMethods) {
            coverageMatrix.get(testName).add(method);
        }

        // Write the updated coverage matrix back to the file, creating the file if it doesn't exist
        try {
            if (!coverageFile.exists()) {
                coverageFile.createNewFile();
            }
            try (FileWriter fileWriter = new FileWriter(coverageFile)) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(fileWriter, coverageMatrix);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to write coverage-matrix.json");
        }
    }

    private static void deleteOlderCoveredMethodsFromMatrix(String testName, ArrayList<String> fullyQualifiedCurrentMethods) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Set<String>> coverageMatrix = new HashMap<>();

        // Read existing coverage-matrix.json if it exists
        File coverageFile = new File(COVERAGE_MATRIX_FILEPATH);
        if (coverageFile.exists()) {
            try {
                coverageMatrix = objectMapper.readValue(coverageFile, new TypeReference<Map<String, Set<String>>>() {});
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to read coverage-matrix.json");
            }
        }

        // Update the coverage matrix
        Set<String> existingMethods = coverageMatrix.computeIfAbsent(testName, k -> new HashSet<>());

//        System.out.println("Pre remove: " + existingMethods.toString());

        Set<String> methodsToRemove = new HashSet<>(existingMethods);
        for (String method : methodsToRemove) {
            if (!fullyQualifiedCurrentMethods.contains(method)) {
                existingMethods.remove(method);
            }
        }

//        System.out.println("Post remove: " + existingMethods.toString());

        // Write the updated coverage matrix back to the file, creating the file if it doesn't exist
        try {
            if (!coverageFile.exists()) {
                coverageFile.createNewFile();
            }
            try (FileWriter fileWriter = new FileWriter(coverageFile)) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(fileWriter, coverageMatrix);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to write coverage-matrix.json");
        }
    }

}
