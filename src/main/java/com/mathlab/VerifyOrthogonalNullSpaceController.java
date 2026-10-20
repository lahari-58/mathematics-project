package com.mathlab;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class VerifyOrthogonalNullSpaceController {

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

            if (Math.abs(a[best][col]) < 1e-10) continue;

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

    static double[][] getNullSpace(double[][] A) {
        int m = A.length;
        int n = A[0].length;
        double[][] r = new double[m][n];

        for (int i = 0; i < m; i++) {
            r[i] = A[i].clone();
        }

        rref(r);

        boolean[] pivot = new boolean[n];
        int rank = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (Math.abs(r[i][j] - 1) < 1e-10) {
                    boolean ok = true;

                    for (int k = 0; k < j; k++) {
                        if (Math.abs(r[i][k]) > 1e-10) {
                            ok = false;
                            break;
                        }
                    }

                    if (ok) {
                        pivot[j] = true;
                        rank++;
                        break;
                    }
                }
            }
        }

        double[][] basis = new double[n - rank][n];
        int b = 0;

        for (int free = 0; free < n; free++) {
            if (!pivot[free]) {
                double[] v = new double[n];
                v[free] = 1;
                int row = 0;

                for (int col = 0; col < n && row < rank; col++) {
                    if (pivot[col]) {
                        v[col] = -r[row][free];
                        row++;
                    }
                }

                basis[b++] = v;
            }
        }

        return basis;
    }

    static double dot(double[] a, double[] b) {
        double sum = 0;

        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }

        return sum;
    }

    static double[] multiply(double[][] A, double[] v) {
        double[] result = new double[A.length];

        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                result[i] += A[i][j] * v[j];
            }
        }

        return result;
    }

    @PostMapping("/api/verify-orthogonal")
    public Map<String, Object> verify(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            int m = ((Number) request.get("rows")).intValue();
            int n = ((Number) request.get("cols")).intValue();

            if (m < 1 || n < 1 || m > 10 || n > 10) {
                throw new IllegalArgumentException(
                    "Rows and columns must be between 1 and 10."
                );
            }

            List<?> values = (List<?>) request.get("matrix");

            if (values == null || values.size() != m * n) {
                throw new IllegalArgumentException(
                    "Please enter a value for every matrix cell."
                );
            }

            double[][] A = new double[m][n];

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    Object value = values.get(i * n + j);

                    if (!(value instanceof Number)) {
                        throw new IllegalArgumentException(
                            "All matrix entries must be numbers."
                        );
                    }

                    A[i][j] = ((Number) value).doubleValue();
                }
            }

            double[][] nullBasis = getNullSpace(A);
            List<List<Double>> products = new ArrayList<>();
            boolean verified = true;

            for (double[] vector : nullBasis) {
                double[] Av = multiply(A, vector);
                List<Double> product = new ArrayList<>();

                for (double value : Av) {
                    product.add(value);
                    if (Math.abs(value) > 1e-8) {
                        verified = false;
                    }
                }

                products.add(product);

                for (int i = 0; i < m; i++) {
                    if (Math.abs(dot(A[i], vector)) > 1e-8) {
                        verified = false;
                    }
                }
            }

            response.put("basis", nullBasis);
            response.put("products", products);
            response.put("dimension", nullBasis.length);
            response.put("verified", verified);
            response.put(
                "message",
                verified
                    ? "Verified: Orthogonal complement of Row(A) equals Null(A)."
                    : "Verification failed."
            );

        } catch (Exception e) {
            response.put(
                "error",
                e.getMessage() == null
                    ? "Please check your matrix input."
                    : e.getMessage()
            );
        }

        return response;
    }
}