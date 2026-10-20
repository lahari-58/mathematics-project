package com.mathlab;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GradientQuiverPlotController {

    @GetMapping("/api/gradient-quiver")
    public Map<String, Object> getGradientField() {

        List<Map<String, Object>> field = new ArrayList<>();

        for (int y = 3; y >= -3; y--) {
            for (int x = -3; x <= 3; x++) {

                int gx = 2 * x;
                int gy = 2 * y;

                String direction;

                if (gx == 0 && gy == 0) {
                    direction = "O";
                } else if (gx == 0 && gy > 0) {
                    direction = "N";
                } else if (gx == 0 && gy < 0) {
                    direction = "S";
                } else if (gx > 0 && gy == 0) {
                    direction = "E";
                } else if (gx < 0 && gy == 0) {
                    direction = "W";
                } else if (gx > 0 && gy > 0) {
                    direction = "NE";
                } else if (gx < 0 && gy > 0) {
                    direction = "NW";
                } else if (gx > 0 && gy < 0) {
                    direction = "SE";
                } else {
                    direction = "SW";
                }

                Map<String, Object> point = new LinkedHashMap<>();
                point.put("x", x);
                point.put("y", y);
                point.put("gx", gx);
                point.put("gy", gy);
                point.put("direction", direction);

                field.add(point);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("function", "f(x,y) = x² + y²");
        result.put("gradient", "grad f = (2x, 2y)");
        result.put("field", field);

        return result;
    }
}