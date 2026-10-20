package com.mathlab;

import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class RREFController {

    @PostMapping("/api/rref")
    public Map<String, Object> calculateRREF(@RequestBody Map<String, Object> request) {

        int rows = ((Number) request.get("rows")).intValue();
        int cols = ((Number) request.get("cols")).intValue();

        java.util.List<?> input = (java.util.List<?>) request.get("matrix");

        double[][] original = new double[rows][cols];

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                original[i][j] = ((Number) input.get(index++)).doubleValue();
            }
        }

        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            result[i] = original[i].clone();
        }

        RREFMatrix.rref(result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("original", original);
        response.put("rref", result);

        return response;
    }
}