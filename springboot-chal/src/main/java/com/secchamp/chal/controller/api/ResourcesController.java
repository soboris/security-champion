package com.secchamp.chal.controller.api;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/protected")
public class ResourcesController {

    @GetMapping("/resources")
    public Map<String, String> getProtectedResources() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Authenticated.");
        return response;
    }
}
