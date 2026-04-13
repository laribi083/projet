package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.service.Courseservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CourseController {

    @Autowired
    private Courseservice courseService;

    // ========== PAGES PRINCIPALES ==========
    
    @GetMapping("/niveux")
    public String showNiveux() {
        return "htmlstudent/niveux";
    }
    
    // Page interface 1ère année - sans filière
    @GetMapping("/interface1er")
    public String showCours1(Model model) {
        // Récupérer tous les cours de 1ère année
        List<Course> allCourses = courseService.findByNiveau("1year");
        
        // Grouper les cours par module
        Map<String, List<Course>> coursesByModule = allCourses.stream()
            .collect(Collectors.groupingBy(Course::getModule));
        
        model.addAttribute("coursesByModule", coursesByModule);
        return "htmlstudent/1year/interface1er";
    }
    
    // Page interface 2ème année - sans filière
    @GetMapping("/interface2eme")
    public String showCours2(Model model) {
        List<Course> allCourses = courseService.findByNiveau("2year");
        
        Map<String, List<Course>> coursesByModule = allCourses.stream()
            .collect(Collectors.groupingBy(Course::getModule));
        
        model.addAttribute("coursesByModule", coursesByModule);
        return "htmlstudent/2year/interface2eme";
    }
    
    // Page interface 3ème année - sans filière
    @GetMapping("/interface3eme")
    public String showCours3(Model model) {
        List<Course> allCourses = courseService.findByNiveau("3year");
        
        Map<String, List<Course>> coursesByModule = allCourses.stream()
            .collect(Collectors.groupingBy(Course::getModule));
        
        model.addAttribute("coursesByModule", coursesByModule);
        return "htmlstudent/3year/interface3eme";
    }
    
    // ========== MODULES 1ÈRE ANNÉE - SANS FILIÈRE ==========
    
    @GetMapping("/cours/module/1")
    public String getAlgebra01(Model model) {
        List<Course> allCourses = courseService.findByNiveau("1year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Algebra 01".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Algebra 01");
        return "htmlstudent/1year/cours/Algebra01";
    }
    
    @GetMapping("/cours/module/2")
    public String getAnalysis01(Model model) {
        List<Course> allCourses = courseService.findByNiveau("1year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Analysis 01".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Analysis 01");
        return "htmlstudent/1year/cours/Ana";
    }
    
    @GetMapping("/cours/module/3")
    public String getCRI(Model model) {
        List<Course> allCourses = courseService.findByNiveau("1year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Component & Representation of Information".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Component & Representation of Information");
        return "htmlstudent/1year/cours/CRI";
    }
    
    @GetMapping("/cours/module/4")
    public String getElectricite(Model model) {
        List<Course> allCourses = courseService.findByNiveau("1year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Electricity & Electronics".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Electricity & Electronics");
        return "htmlstudent/1year/cours/tricité";
    }
    
    @GetMapping("/cours/module/5")
    public String getCodage(Model model) {
        List<Course> allCourses = courseService.findByNiveau("1year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Information Coding & Representation".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Information Coding & Representation");
        return "htmlstudent/1year/cours/codage";
    }
    
    @GetMapping("/cours/module/6")
    public String getAlgebra02(Model model) {
        List<Course> allCourses = courseService.findByNiveau("1year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Algebra 02".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Algebra 02");
        return "htmlstudent/1year/cours/Algebra02";
    }
    
    @GetMapping("/cours/module/7")
    public String getAnalysis02(Model model) {
        List<Course> allCourses = courseService.findByNiveau("1year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Analysis 02".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Analysis 02");
        return "htmlstudent/1year/cours/Ana2";
    }
    
    @GetMapping("/cours/module/8")
    public String getIpoo(Model model) {
        List<Course> allCourses = courseService.findByNiveau("1year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Introduction to project-oriented programming".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Introduction to project-oriented programming");
        return "htmlstudent/1year/cours/Ipoo";
    }
    
    @GetMapping("/cours/module/9")
    public String getSM(Model model) {
        List<Course> allCourses = courseService.findByNiveau("1year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Machine Structure".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Machine Structure");
        return "htmlstudent/1year/cours/SM";
    }
    
    @GetMapping("/cours/module/10")
    public String getASD1(Model model) {
        List<Course> allCourses = courseService.findByNiveau("1year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Advanced Data Structures 01".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Advanced Data Structures 01");
        return "htmlstudent/1year/cours/ASD1";
    }
    
    // ========== MODULES 2ÈME ANNÉE - SANS FILIÈRE ==========
    
    @GetMapping("/cours/module/11")
    public String getPooa(Model model) {
        List<Course> allCourses = courseService.findByNiveau("2year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "advanced project-oriented programming".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Advanced Project-Oriented Programming");
        return "htmlstudent/2year/cours/pooa";
    }
    
    @GetMapping("/cours/module/12")
    public String getASD2(Model model) {
        List<Course> allCourses = courseService.findByNiveau("2year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Advanced Data Structures 02".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Advanced Data Structures 02");
        return "htmlstudent/2year/cours/ASD";
    }
    
    @GetMapping("/cours/module/13")
    public String getLM(Model model) {
        List<Course> allCourses = courseService.findByNiveau("2year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Logique mathématiques".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Logique Mathématiques");
        return "htmlstudent/2year/cours/LM";
    }
    
    @GetMapping("/cours/module/14")
    public String getAO(Model model) {
        List<Course> allCourses = courseService.findByNiveau("2year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Computer Architecture".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Computer Architecture");
        return "htmlstudent/2year/cours/Ao";
    }
    
    @GetMapping("/cours/module/15")
    public String getTL(Model model) {
        List<Course> allCourses = courseService.findByNiveau("2year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "langages theorique".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Langages Théoriques");
        return "htmlstudent/2year/cours/TL";
    }
    
    @GetMapping("/cours/module/16")
    public String getSe(Model model) {
        List<Course> allCourses = courseService.findByNiveau("2year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "operating system".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Operating System");
        return "htmlstudent/2year/cours/Se";
    }
    
    @GetMapping("/cours/module/17")
    public String getBDA(Model model) {
        List<Course> allCourses = courseService.findByNiveau("2year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "DataBases and SQL".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Databases and SQL");
        return "htmlstudent/2year/cours/BDA";
    }
    
    @GetMapping("/cours/module/18")
    public String getRC(Model model) {
        List<Course> allCourses = courseService.findByNiveau("2year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "comunication network 01".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Communication Networks 01");
        return "htmlstudent/2year/cours/RC";
    }
    
    @GetMapping("/cours/module/19")
    public String getGL(Model model) {
        List<Course> allCourses = courseService.findByNiveau("2year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Software ingenery 01".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Software Engineering 01");
        return "htmlstudent/2year/cours/GL";
    }
    
    @GetMapping("/cours/module/20")
    public String getDaw(Model model) {
        List<Course> allCourses = courseService.findByNiveau("2year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "web development 01".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Web Development 01");
        return "htmlstudent/2year/cours/Daw";
    }
    
    // ========== MODULES 3ÈME ANNÉE - SANS FILIÈRE ==========
    
    @GetMapping("/cours/module/21")
    public String getGl2(Model model) {
        List<Course> allCourses = courseService.findByNiveau("3year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Software ingenery 02".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Software Engineering 02");
        return "htmlstudent/3year/cours/Gl2";
    }
    
    @GetMapping("/cours/module/22")
    public String getBd2(Model model) {
        List<Course> allCourses = courseService.findByNiveau("3year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "advanced DataBases and SQL".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Advanced Databases");
        return "htmlstudent/3year/cours/bd2";
    }
    
    @GetMapping("/cours/module/23")
    public String getRc2(Model model) {
        List<Course> allCourses = courseService.findByNiveau("3year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "comunication network 02".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Communication Networks 02");
        return "htmlstudent/3year/cours/Rc2";
    }
    
    @GetMapping("/cours/module/24")
    public String getGpl(Model model) {
        List<Course> allCourses = courseService.findByNiveau("3year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "project management".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Project Management");
        return "htmlstudent/3year/cours/gpl";
    }
    
    @GetMapping("/cours/module/25")
    public String getDaw2(Model model) {
        List<Course> allCourses = courseService.findByNiveau("3year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "web development 02".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Web Development 02");
        return "htmlstudent/3year/cours/Daw2";
    }
    
    @GetMapping("/cours/module/26")
    public String getDac(Model model) {
        List<Course> allCourses = courseService.findByNiveau("3year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "development of competing applications".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Development of Competing Applications");
        return "htmlstudent/3year/cours/dac";
    }
    
    @GetMapping("/cours/module/27")
    public String getMel(Model model) {
        List<Course> allCourses = courseService.findByNiveau("3year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "software maintenance".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Software Maintenance");
        return "htmlstudent/3year/cours/mel";
    }
    
    @GetMapping("/cours/module/28")
    public String getTql(Model model) {
        List<Course> allCourses = courseService.findByNiveau("3year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "software quality test".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Software Quality Test");
        return "htmlstudent/3year/cours/tql";
    }
    
    @GetMapping("/cours/module/29")
    public String getDli(Model model) {
        List<Course> allCourses = courseService.findByNiveau("3year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Interactive Software Development".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Interactive Software Development");
        return "htmlstudent/3year/cours/dli";
    }
    
    @GetMapping("/cours/module/30")
    public String getGit(Model model) {
        List<Course> allCourses = courseService.findByNiveau("3year");
        List<Course> courses = allCourses.stream()
            .filter(c -> "Git and GitHub".equals(c.getModule()))
            .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        model.addAttribute("moduleName", "Git and GitHub");
        return "htmlstudent/3year/cours/git";
    }
}