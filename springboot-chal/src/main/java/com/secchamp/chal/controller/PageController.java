package com.secchamp.chal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Route for login page
    @GetMapping("/pages/login")
    public String login() {
        return "public/login";
    }

    // Route for register page
    @GetMapping("/pages/register")
    public String register() {
        return "public/register";
    }

    @GetMapping("/pages/tools")
    public String tools() {
        return "public/tools";
    }

    @GetMapping("/pages/upload")
    public String upload() {
        return "public/upload";
    }

    @GetMapping("/pages/safe_upload")
    public String safeUpload() {
        return "public/safe_upload";
    }

    @GetMapping("/pages/protected")
    public String protectedPage() {
        return "protected/home";
    }

    @GetMapping("/pages/protected/usermanagement")
    public String usermanagement() {
        return "protected/usermanagement"; 
    }

     @GetMapping("/pages/protected/edituser")
    public String edituser() {
        return "protected/edituser"; 
    }

    @GetMapping("/pages/protected/notice")
    public String notice() {
        return "protected/notice"; 
    }
}
