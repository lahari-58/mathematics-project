package com.mathlab;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class GaussianPivoting {

    private static final int N = 4;

    @PostMapping("/api/gaussian")
    public Map<String, Object> solve(@RequestBody Map<String, Object> request) {

        List<?> input = (List<?>) request.get("matrix");
        double[][] a = new double[N][N + 1];

        int index = 0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j <= N; j++) {
                a[i][j] = ((Number) input.get(index++)).doubleValue();
            }
        }

        double[][] original = new double[N][N + 1];

        for (int i = 0; i < N; i++) {
            original[i] = a[i].clone();
        }

        for (int k = 0; k < N - 1; k++) {

            int pivot = k;

            for (int i = k + 1; i < N; i++) {
                if (Math.abs(a[i][k]) > Math.abs(a[pivot][k])) {
                    pivot = i;
                }
            }

            double[] temp = a[k];
            a[k] = a[pivot];
            a[pivot] = temp;

            if (Math.abs(a[k][k]) < 1e-10) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("error", "No unique solution exists.");
                return response;
            }

            for (int i = k + 1; i < N; i++) {
                double factor = a[i][k] / a[k][k];

                for (int j = k; j <= N; j++) {
                    a[i][j] = a[i][j] - factor * a[k][j];
                }
            }
        }

        if (Math.abs(a[N - 1][N - 1]) < 1e-10) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("error", "No unique solution exists.");
            return response;
        }

        double[] x = new double[N];

        for (int i = N - 1; i >= 0; i--) {
            x[i] = a[i][N];

            for (int j = i + 1; j < N; j++) {
                x[i] -= a[i][j] * x[j];
            }

            x[i] /= a[i][i];
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("original", original);
        response.put("solution", x);

        return response;
    }
}