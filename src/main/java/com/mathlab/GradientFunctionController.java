package com.mathlab;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class GradientFunctionController {

    @GetMapping("/api/gradient")
    public Map<String, Object> calculateGradient(
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam double z) {

        double df_dx = 2 * x;
        double df_dy = 2 * y;
        double df_dz = 2 * z;

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("function", "f(x,y,z) = x² + y² + z²");
        result.put("x", x);
        result.put("y", y);
        result.put("z", z);
        result.put("df_dx", df_dx);
        result.put("df_dy", df_dy);
        result.put("df_dz", df_dz);

        result.put(
            "gradient",
            String.format(
                java.util.Locale.US,
                "(%.4f, %.4f, %.4f)",
                df_dx, df_dy, df_dz
            )
        );

        return result;
    }
}