
package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.service.Courseservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/receive-courses")
public class ReceiveCoursesController {
    
    @Autowired
    private Courseservice courseService;
    
    @GetMapping
    public String showReceiveCourses(HttpSession session, Model model) {
        String userName = (String) session.getAttribute("userName");
        String niveau = (String) session.getAttribute("niveau");
        
        if (userName == null) {
            return "redirect:/login";
        }
        
        // Récupérer tous les cours actifs
        List<Course> allCourses = courseService.getAllActiveCourses();
        
        // Compter les cours par niveau
        long totalCourses = allCourses.size();
        long total1stYear = allCourses.stream().filter(c -> "1year".equals(c.getNiveau())).count();
        long total2ndYear = allCourses.stream().filter(c -> "2year".equals(c.getNiveau())).count();
        long total3rdYear = allCourses.stream().filter(c -> "3year".equals(c.getNiveau())).count();
        
        // Récupérer tous les modules uniques
        List<String> modules = allCourses.stream()
            .map(Course::getModule)
            .filter(module -> module != null && !module.isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        model.addAttribute("userName", userName);
        model.addAttribute("niveau", niveau);
        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("total1stYear", total1stYear);
        model.addAttribute("total2ndYear", total2ndYear);
        model.addAttribute("total3rdYear", total3rdYear);
        model.addAttribute("modules", modules);
        
        return "htmlstudent/receive-courses";
    }
    
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<Course>> getAllCourses(@RequestParam(required = false) String niveau,
                                                       @RequestParam(required = false) String module,
                                                       @RequestParam(required = false) String search) {
        List<Course> courses = courseService.getAllActiveCourses();
        
        // Filtrer par niveau
        if (niveau != null && !niveau.equals("all")) {
            courses = courses.stream()
                .filter(c -> niveau.equals(c.getNiveau()))
                .collect(Collectors.toList());
        }
        
        // Filtrer par module
        if (module != null && !module.equals("all")) {
            courses = courses.stream()
                .filter(c -> module.equals(c.getModule()))
                .collect(Collectors.toList());
        }
        
        // Filtrer par recherche
        if (search != null && !search.isEmpty()) {
            String searchLower = search.toLowerCase();
            courses = courses.stream()
                .filter(c -> c.getTitle().toLowerCase().contains(searchLower) ||
                             c.getDescription().toLowerCase().contains(searchLower) ||
                             (c.getTeacherName() != null && c.getTeacherName().toLowerCase().contains(searchLower)))
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/api/modules")
    @ResponseBody
    public ResponseEntity<List<String>> getAllModules() {
        List<Course> courses = courseService.getAllActiveCourses();
        List<String> modules = courses.stream()
            .map(Course::getModule)
            .filter(module -> module != null && !module.isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        return ResponseEntity.ok(modules);
    }
}