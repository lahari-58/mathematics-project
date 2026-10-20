package com.mathlab;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class LeastSquaresRSSController {

    @PostMapping("/api/least-squares")
    public Map<String, Object> calculate(@RequestBody Map<String, Object> request) {

        Map<String, Object> result = new LinkedHashMap<>();

        try {
            int n = ((Number) request.get("n")).intValue();

            List<?> xValues = (List<?>) request.get("x");
            List<?> yValues = (List<?>) request.get("y");

            if (n < 2 || xValues.size() != n || yValues.size() != n) {
                result.put("error", "Enter at least 2 data points with matching x and y values.");
                return result;
            }

            double[] x = new double[n];
            double[] y = new double[n];

            double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

            for (int i = 0; i < n; i++) {
                x[i] = ((Number) xValues.get(i)).doubleValue();
                y[i] = ((Number) yValues.get(i)).doubleValue();

                sumX += x[i];
                sumY += y[i];
                sumXY += x[i] * y[i];
                sumX2 += x[i] * x[i];
            }

            double denominator = n * sumX2 - sumX * sumX;

            if (Math.abs(denominator) < 1e-12) {
                result.put("error", "The x values must not all be identical.");
                return result;
            }

            double b = (n * sumXY - sumX * sumY) / denominator;
            double a = (sumY - b * sumX) / n;

            double rss = 0;
            List<Map<String, Object>> predictions = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                double predicted = a + b * x[i];
                double residual = y[i] - predicted;

                rss += residual * residual;

                Map<String, Object> point = new LinkedHashMap<>();
                point.put("x", x[i]);
                point.put("actualY", y[i]);
                point.put("predictedY", predicted);
                point.put("residual", residual);

                predictions.add(point);
            }

            result.put("intercept", a);
            result.put("slope", b);
            result.put("model", String.format(Locale.US, "y = %.4f + %.4fx", a, b));
            result.put("predictions", predictions);
            result.put("rss", rss);

        } catch (Exception e) {
            result.put("error", "Invalid input. Please enter valid numeric data.");
        }

        return result;
    }
}