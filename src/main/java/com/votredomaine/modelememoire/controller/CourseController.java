package com.votredomaine.modelememoire.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CourseController {

    @GetMapping("/niveux")
    public String showNiveux() {
        return "htmlstudent/niveux"; 
    }
    
    @GetMapping("/interface1er")
    public String showCours1() {
        return "htmlstudent/1year/interface1er"; 
    }
    
    @GetMapping("/interface2eme")
    public String showCours2() {
        return "htmlstudent/2year/interface2eme"; 
    }
    
    @GetMapping("/interface3eme")
    public String showCours3() {
        return "htmlstudent/3year/interface3eme"; 
    }
}