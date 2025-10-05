package org.example.listeners;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MethodSignatureRetriever {

    // New method for retrieving the signature using a parser, but missing fully qualified name of method parameters
    public static String getMethodSignatureParsed(int methodLine, String className) {
        String classSourceDir = "src/main/java/";

        String classPath = classSourceDir + className +  ".java";

//        System.out.println("classPath: " + classPath);

        CompilationUnit cu = null;
        try {
            cu = StaticJavaParser.parse(Files.newInputStream(Paths.get(classPath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder methodSignature = new StringBuilder();

        cu.findAll(MethodDeclaration.class).forEach((methodDeclaration) -> {
            methodDeclaration.getBegin().ifPresent(begin -> {
                if (begin.line == methodLine) {
                    methodDeclaration.getParameters().forEach(p -> {
                        methodSignature.append(p.getType()).append(",");
                    });
                }
            });
        });

        cu.findAll(ConstructorDeclaration.class).forEach(constructorDeclaration -> {
            constructorDeclaration.getBegin().ifPresent(begin -> {
                if (begin.line == methodLine) {
                    constructorDeclaration.getParameters().forEach(p -> {
                        methodSignature.append(p.getType()).append(",");
                    });
                }
            });
        });

        int lastColumn = methodSignature.lastIndexOf(",");

        if (lastColumn != -1) {
            String sig = methodSignature.substring(0,lastColumn);
//            System.out.println("Signature:" + sig);
            return sig;
        } else {
//            System.out.println("Signature:" + signature.toString());

            return methodSignature.toString();
        }
    }

    // Old method to retrieve signature using JVM Descriptor, but missing the type of a generic method parameter
    public static String getMethodSignatureDescriptor(String methodDescriptor) {

        //TODO Vedi i generics

        String parameters = methodDescriptor.substring(methodDescriptor.indexOf("(") + 1, methodDescriptor.indexOf(")"));

        String array = "[]";

        int i = 0;

        int len = parameters.length();

        StringBuilder signature = new StringBuilder();

        while (i < len) {
            String typeName;
            //squares to know the new position for substring: 1 in case of array, 2 in case of matrix
            int squares = 0;
            //skip to know the new position for substring: 2 in case of class instance ("type letter" and semicolon)
            int skip = 2;
            char character = parameters.charAt(i);

            if (character == '[') {
                if (parameters.charAt(i + 1) == '[') {
                    squares = 2;
                    typeName = getTypeNamebyDescriptor(parameters.substring(i + squares));
                    signature.append(typeName).append(array).append(array).append(",");
                    if (parameters.charAt(i + squares) == 'L') {
                        //in case of a class, other than the letter, the descriptor tells also the name of the class
                        i += squares + skip + typeName.length();
                    } else {
                        i += squares + 1;
                    }
                } else {
                    squares = 1;
                    typeName = getTypeNamebyDescriptor(parameters.substring(i + 1));
                    signature.append(typeName).append(array).append(",");
                    if (parameters.charAt(i + squares) == 'L') {
                        i += squares + skip + typeName.length();
                    } else {
                        i += squares + 1;
                    }
                }
            } else {
                typeName = getTypeNamebyDescriptor(parameters.substring(i));
                signature.append(typeName).append(",");
                if (parameters.charAt(i) == 'L') {
                    i += skip + typeName.length();
                } else {
                    i += 1;
                }
            }
        }

        int lastColumn = signature.lastIndexOf(",");

        if (lastColumn != -1) {
            String sig = signature.substring(0,lastColumn);
//            System.out.println("Signature:" + sig);
            return sig;
        } else {
//            System.out.println("Signature:" + signature.toString());

            return signature.toString();
        }

    }

    private static String getTypeNamebyDescriptor(String desc) {
        String name = "";

        switch (desc.charAt(0)) {
            case 'B':
                name = "byte";
                break;
            case 'C':
                name = "char";
                break;
            case 'D':
                name = "double";
                break;
            case 'F':
                name = "float";
                break;
            case 'I':
                name = "int";
                break;
            case 'J':
                name = "long";
                break;
            case 'S':
                name = "short";
                break;
            case 'Z':
                name = "boolean";
                break;
            case 'L':
                String s = desc.substring(1,desc.indexOf(";"));
                name = s.replace("/",".");
        }

        return name;
    }
}
