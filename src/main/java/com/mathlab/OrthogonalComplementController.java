package com.mathlab;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class OrthogonalComplementController {

    static void rref(double[][] a) {
        int rows = a.length;
        int cols = a[0].length;
        int pivotRow = 0;

        for (int col = 0; col < cols && pivotRow < rows; col++) {
            int best = pivotRow;

            for (int i = pivotRow + 1; i < rows; i++) {
                if (Math.abs(a[i][col]) > Math.abs(a[best][col])) {
                    best = i;
                }
            }

            if (Math.abs(a[best][col]) < 1e-10) {
                continue;
            }

            double[] temp = a[pivotRow];
            a[pivotRow] = a[best];
            a[best] = temp;

            double pivot = a[pivotRow][col];

            for (int j = 0; j < cols; j++) {
                a[pivotRow][j] /= pivot;
            }

            for (int i = 0; i < rows; i++) {
                if (i != pivotRow) {
                    double factor = a[i][col];

                    for (int j = 0; j < cols; j++) {
                        a[i][j] -= factor * a[pivotRow][j];
                    }
                }
            }

            pivotRow++;
        }
    }

    static double[][] nullSpace(double[][] original) {
        int m = original.length;
        int n = original[0].length;
        double[][] a = new double[m][n];

        for (int i = 0; i < m; i++) {
            a[i] = original[i].clone();
        }

        rref(a);

        boolean[] pivotColumn = new boolean[n];
        int rank = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (Math.abs(a[i][j] - 1.0) < 1e-10) {
                    boolean isPivot = true;

                    for (int k = 0; k < j; k++) {
                        if (Math.abs(a[i][k]) > 1e-10) {
                            isPivot = false;
                            break;
                        }
                    }

                    if (isPivot) {
                        pivotColumn[j] = true;
                        rank++;
                        break;
                    }
                }
            }
        }

        int freeCount = n - rank;
        double[][] basis = new double[freeCount][n];
        int b = 0;

        for (int free = 0; free < n; free++) {
            if (!pivotColumn[free]) {
                double[] vector = new double[n];
                vector[free] = 1.0;
                int row = 0;

                for (int col = 0; col < n && row < rank; col++) {
                    if (pivotColumn[col]) {
                        vector[col] = -a[row][free];
                        row++;
                    }
                }

                basis[b++] = vector;
            }
        }

        return basis;
    }

    @PostMapping("/api/orthogonal")
    public Map<String, Object> calculate(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            int rows = ((Number) request.get("rows")).intValue();
            int cols = ((Number) request.get("cols")).intValue();

            if (rows < 1 || cols < 1 || rows > 10 || cols > 10) {
                throw new IllegalArgumentException(
                    "Rows and columns must be between 1 and 10."
                );
            }

            List<?> values = (List<?>) request.get("matrix");

            if (values == null || values.size() != rows * cols) {
                throw new IllegalArgumentException(
                    "Please enter a value for every matrix cell."
                );
            }

            double[][] matrix = new double[rows][cols];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Object value = values.get(i * cols + j);

                    if (!(value instanceof Number)) {
                        throw new IllegalArgumentException(
                            "All matrix entries must be numbers."
                        );
                    }

                    matrix[i][j] = ((Number) value).doubleValue();
                }
            }

            double[][] basis = nullSpace(matrix);

            response.put("basis", basis);
            response.put("dimension", basis.length);
            response.put("message", basis.length == 0
                ? "Only the zero vector belongs to the orthogonal complement."
                : "Basis for the orthogonal complement:");

        } catch (Exception e) {
            response.put("error", e.getMessage() == null
                ? "Please check your matrix input."
                : e.getMessage());
        }

        return response;
    }
}