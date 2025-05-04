package com.secchamp.chal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.el.ELProcessor;

@Controller
public class CalcController {

    @GetMapping("/pages/calc")
    public String evaluateExpression(@RequestParam(value = "expression", required = false) String expression, Model model) {
        if (expression != null && !expression.isEmpty()) {
            try {
                // Create an ELProcessor instance
                ELProcessor elProcessor = new ELProcessor();

                // Evaluate the expression
                Object result = elProcessor.eval(expression);

                // Store the result in the model to display on the page
                model.addAttribute("result", result != null ? result.toString() : "null");
            } catch (Exception e) {
                // Handle any errors and show them on the page
                model.addAttribute("result", "Error evaluating expression: " + e.getMessage());
            }
        }

        return "calc";
    }
}