package org.example;

import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class DecisionSystem {
    private String[] attrNames;
    private int[] attrTypes;
    private int attributes;

    private String fileName;
    private int numbInstances;
    private int classes;
    private int classIndex;
    private String[][] values;
    private boolean[][] bdsm; //the discernibility-similarity matrix
    private boolean[][] discM;//the discernibility matrix

    /**
     * @param ri row in the decision system
     * @param rj row in the decision system
     * @return a vector with the comparison between ri and rj
     */
    private boolean @Nullable [] getBinaryRow(int ri, int rj, boolean discM) {
        boolean[] result = new boolean[attributes - 1];
        boolean allZeros = true;
        boolean sameClass = values[ri][attributes - 1].equals(values[rj][attributes - 1]);
        if (sameClass) {
            if (discM)
                return null;
            for (int i = 0; i < attributes - 1; i++) {
                if (values[ri][i].equalsIgnoreCase("?") || values[rj][i].equalsIgnoreCase("?")) {
                    result[i] = true;
                    allZeros = false;
                } else {
                    result[i] = values[ri][i].equals(values[rj][i]);
                    if (result[i]) {
                        allZeros = false;
                    }
                }
            }
        } else {
            for (int i = 0; i < attributes - 1; i++) {
                if (values[ri][i].equalsIgnoreCase("?") || values[rj][i].equalsIgnoreCase("?")) {
                    result[i] = true;
                    allZeros = false;
                } else {
                    result[i] = !values[ri][i].equals(values[rj][i]);
                    if (result[i]) {
                        allZeros = false;
                    }
                }
            }
        }
        if (allZeros)
            return null;
        else
            return result;
    }

    public boolean[][] getDiscSimilarityMatrix() {
        BinaryMatrix discSimilM=new BinaryMatrix();
        for (int i = 0; i < values.length - 1; i++) {
            for (int j = i + 1; j < values.length; j++) {
                boolean[] newRow = getBinaryRow(i, j,false);
                if (newRow != null)//Zero rows is not taken in account
                    discSimilM.add(newRow);
            }
        }
        return discSimilM.getBm();
    }

    public boolean[][] getDiscMatrix() {
        BinaryMatrix discMatrix=new BinaryMatrix();
        for (int i = 0; i < values.length - 1; i++) {
            for (int j = i + 1; j < values.length; j++) {
                boolean[] newRow = getBinaryRow(i, j,true);
                if (newRow != null)//Zero rows is not taken in account
                    discMatrix.add(newRow);
            }
        }
        return discMatrix.getBm();
    }

    public void writeDiscIndiscMatrix(String filename) throws IOException {
        System.out.println("Creating the BDSM..., this can take some minutes...");
        bdsm = getDiscSimilarityMatrix();
        writeMatrix(bdsm, filename, false);
    }

    public void writeDiscMatrix(String filename) throws IOException {
        System.out.println("Creating the Discernibility..., this can take some minutes...");
        discM = getDiscMatrix();
        writeMatrix(discM, filename,true);
    }

    private void writeMatrix(boolean[][] binaryMatrix, String filename, boolean isDiscM) throws IOException {
        final long[] c = {0};
        List<String> matrix = Arrays.stream(binaryMatrix).map(value -> {
            StringBuilder line = new StringBuilder();
            for (boolean b : value) {
                if (b) {
                    line.append("1 ");
                    c[0]++;
                } else {
                    line.append("0 ");
                }
            }
            return line.toString();
        }).toList();
        float density = (float) c[0] / (binaryMatrix.length * binaryMatrix[0].length);
        File file;
        if (isDiscM){
            file= new File(filename + "_" + String.format("%.2f", density) + "_discM.bol");
            System.out.println("The discernibility matrix was generated successfully, and saved as: " + file.getName());
        }else {
            file= new File(filename + "_" + String.format("%.2f", density) + "_discIndiscM.bol");
            System.out.println("The SBDSM was generated successfully, and saved as: " + file.getName());
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(binaryMatrix.length + "\n" + (attributes - 1) + "\n");
        for (String line : matrix) {
            fileWriter.write(line + "\n");
        }
        fileWriter.close();
    }

}
