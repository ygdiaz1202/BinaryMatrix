package org.example;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String  [] args) {
        String fileName="./base.arff";
        if (args.length == 0){
            createMatrices(fileName);
        }else{
            if (args[0].equals("-all")){
                // Get the current directory
                File currentDirectory = new File(System.getProperty("user.dir"));

                // List all files in the current directory
                File[] files = currentDirectory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            String filename= file.getName();
                            if (filename.endsWith(".arff")){
                                createMatrices(filename);
                            }
                        }
                    }
                } else {
                    System.err.println("Failed to list files in the current directory.");
                }
            }else{
                for (String filename:args) {
                    createMatrices(filename);
                }
            }
        }
    }

    private static void createMatrices(@NotNull String filename) {
        if (filename.endsWith("arff")) {
            DecisionSystem ds = ParseFile.getDS(filename);
            assert ds != null;
            try {
                ds.writeDiscIndiscMatrix(ds.getFileName());
                ds.writeDiscMatrix(ds.getFileName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}