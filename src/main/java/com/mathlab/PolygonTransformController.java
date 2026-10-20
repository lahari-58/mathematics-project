package com.mathlab;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class PolygonTransformController {

    static double[] multiply(double[][] T, double[] p) {
        double[] result = new double[3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i] += T[i][j] * p[j];
            }
        }

        return result;
    }

    @PostMapping("/api/polygon")
    public Map<String, Object> transform(
            @RequestBody Map<String, Object> request) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            int n = ((Number) request.get("n")).intValue();

            if (n < 3 || n > 20) {
                throw new IllegalArgumentException(
                    "Enter between 3 and 20 polygon vertices."
                );
            }

            List<?> vertices = (List<?>) request.get("vertices");

            if (vertices == null || vertices.size() != n) {
                throw new IllegalArgumentException(
                    "Please enter all polygon vertices."
                );
            }

            double tx = ((Number) request.get("tx")).doubleValue();
            double ty = ((Number) request.get("ty")).doubleValue();
            double angle = ((Number) request.get("angle")).doubleValue();

            double theta = Math.toRadians(angle);

            double[][] translation = {
                {1, 0, tx},
                {0, 1, ty},
                {0, 0, 1}
            };

            double[][] rotation = {
                {Math.cos(theta), -Math.sin(theta), 0},
                {Math.sin(theta), Math.cos(theta), 0},
                {0, 0, 1}
            };

            List<List<Double>> original = new ArrayList<>();
            List<List<Double>> transformed = new ArrayList<>();

            for (Object item : vertices) {
                List<?> point = (List<?>) item;

                if (point.size() != 2 ||
                    !(point.get(0) instanceof Number) ||
                    !(point.get(1) instanceof Number)) {
                    throw new IllegalArgumentException(
                        "Each vertex must contain an x and y number."
                    );
                }

                double x = ((Number) point.get(0)).doubleValue();
                double y = ((Number) point.get(1)).doubleValue();

                original.add(Arrays.asList(x, y));

                double[] p = {x, y, 1};

                double[] translated = multiply(translation, p);
                double[] result = multiply(rotation, translated);

                transformed.add(
                    Arrays.asList(result[0], result[1])
                );
            }

            response.put("original", original);
            response.put("transformed", transformed);
            response.put("message",
                "Polygon translated and then rotated successfully.");

        } catch (Exception e) {
            response.put("error",
                e.getMessage() == null
                    ? "Please check your inputs."
                    : e.getMessage());
        }

        return response;
    }
}