package com.votredomaine.modelememoire.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CourseController {

    @GetMapping("/niveux")
    public String showNiveux() {
        return "htmlstudent/niveux"; 
    }
    
    @GetMapping("/cours1")
    public String showCours1() {
        return "htmlstudent/1year/cours1"; 
    }
    
    @GetMapping("/cours2")
    public String showCours2() {
        return "htmlstudent/2year/cours2"; 
    }
    
    @GetMapping("/cours3")
    public String showCours3() {
        return "htmlstudent/3year/cours3"; 
    }
}