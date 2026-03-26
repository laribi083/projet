package com.votredomaine.modelememoire.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CourseController {

    // ========== PAGES PRINCIPALES ==========
    
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
    
    // ========== MODULES 1ÈRE ANNÉE (IDs 1-10) ==========
    
    // Semestre 1 - Modules 1-5
    @GetMapping("/cours/module/1")
    public String getModule1Algebra01(Model model) {
        model.addAttribute("moduleId", 1);
        model.addAttribute("moduleTitle", "Algebra 01");
        model.addAttribute("moduleNumber", "01");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Prof. Ahmed Benali");
        model.addAttribute("duration", "8 hours");
        model.addAttribute("totalChapters", 6);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "📐");
        return "htmlstudent/1year/cours/module1-algebra01";
    }
    
    @GetMapping("/cours/module/2")
    public String getModule2Analysis01(Model model) {
        model.addAttribute("moduleId", 2);
        model.addAttribute("moduleTitle", "Analysis 01");
        model.addAttribute("moduleNumber", "02");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Dr. Sarah Mansouri");
        model.addAttribute("duration", "12 hours");
        model.addAttribute("totalChapters", 8);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "📊");
        return "htmlstudent/1year/cours/module2-analysis01";
    }
    
    @GetMapping("/cours/module/3")
    public String getModule3ComponentRepresentation(Model model) {
        model.addAttribute("moduleId", 3);
        model.addAttribute("moduleTitle", "Component & Representation of Information");
        model.addAttribute("moduleNumber", "03");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Dr. Karim Hadjadj");
        model.addAttribute("duration", "15 hours");
        model.addAttribute("totalChapters", 8);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "💾");
        return "htmlstudent/1year/cours/module3-component-representation";
    }
    
    @GetMapping("/cours/module/4")
    public String getModule4ElectricityElectronics(Model model) {
        model.addAttribute("moduleId", 4);
        model.addAttribute("moduleTitle", "Electricity & Electronics");
        model.addAttribute("moduleNumber", "04");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Dr. Leila Bouzid");
        model.addAttribute("duration", "6 hours");
        model.addAttribute("totalChapters", 5);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "💡");
        return "htmlstudent/1year/cours/module4-electricity-electronics";
    }
    
    @GetMapping("/cours/module/5")
    public String getModule5InformationCoding(Model model) {
        model.addAttribute("moduleId", 5);
        model.addAttribute("moduleTitle", "Information Coding & Representation");
        model.addAttribute("moduleNumber", "05");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Dr. Rachid Ouali");
        model.addAttribute("duration", "10 hours");
        model.addAttribute("totalChapters", 6);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🔢");
        return "htmlstudent/1year/cours/module5-information-coding";
    }
    
    // Semestre 2 - Modules 6-10
    @GetMapping("/cours/module/6")
    public String getModule6Algebra02(Model model) {
        model.addAttribute("moduleId", 6);
        model.addAttribute("moduleTitle", "Algebra 02");
        model.addAttribute("moduleNumber", "06");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Prof. Ahmed Benali");
        model.addAttribute("duration", "8 hours");
        model.addAttribute("totalChapters", 6);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "📐");
        return "htmlstudent/1year/cours/module6-algebra02";
    }
    
    @GetMapping("/cours/module/7")
    public String getModule7Analysis02(Model model) {
        model.addAttribute("moduleId", 7);
        model.addAttribute("moduleTitle", "Analysis 02");
        model.addAttribute("moduleNumber", "07");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Dr. Sarah Mansouri");
        model.addAttribute("duration", "14 hours");
        model.addAttribute("totalChapters", 7);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "📈");
        return "htmlstudent/1year/cours/module7-analysis02";
    }
    
    @GetMapping("/cours/module/8")
    public String getModule8IntroOOP(Model model) {
        model.addAttribute("moduleId", 8);
        model.addAttribute("moduleTitle", "Introduction to Object-Oriented Programming");
        model.addAttribute("moduleNumber", "08");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Dr. Samir Khelil");
        model.addAttribute("duration", "6 hours");
        model.addAttribute("totalChapters", 5);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "☕");
        return "htmlstudent/1year/cours/module8-intro-oop";
    }
    
    @GetMapping("/cours/module/9")
    public String getModule9MachineStructure(Model model) {
        model.addAttribute("moduleId", 9);
        model.addAttribute("moduleTitle", "Machine Structure");
        model.addAttribute("moduleNumber", "09");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Dr. Karim Hadjadj");
        model.addAttribute("duration", "20 hours");
        model.addAttribute("totalChapters", 7);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "⚙️");
        return "htmlstudent/1year/cours/module9-machine-structure";
    }
    
    @GetMapping("/cours/module/10")
    public String getModule10AdvancedDataStructures(Model model) {
        model.addAttribute("moduleId", 10);
        model.addAttribute("moduleTitle", "Advanced Data Structures");
        model.addAttribute("moduleNumber", "10");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Dr. Samir Khelil");
        model.addAttribute("duration", "4 hours");
        model.addAttribute("totalChapters", 5);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🗂️");
        return "htmlstudent/1year/cours/module10-advanced-data-structures";
    }
    
    // ========== MODULES 2ÈME ANNÉE (IDs 11-20) ==========
    
    // Semestre 1 - Modules 11-15
    @GetMapping("/cours/module/11")
    public String getModule11POOA(Model model) {
        model.addAttribute("moduleId", 11);
        model.addAttribute("moduleTitle", "Programming Oriented Object Advanced (POOA)");
        model.addAttribute("moduleNumber", "01");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Dr. Samir Khelil");
        model.addAttribute("duration", "8 hours");
        model.addAttribute("totalChapters", 6);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "☕");
        return "htmlstudent/2year/cours/module11-pooa";
    }
    
    @GetMapping("/cours/module/12")
    public String getModule12AdvancedDataStructures2(Model model) {
        model.addAttribute("moduleId", 12);
        model.addAttribute("moduleTitle", "Advanced Data Structures");
        model.addAttribute("moduleNumber", "02");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Dr. Samir Khelil");
        model.addAttribute("duration", "12 hours");
        model.addAttribute("totalChapters", 8);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🌲");
        return "htmlstudent/2year/cours/module12-advanced-data-structures";
    }
    
    @GetMapping("/cours/module/13")
    public String getModule13LogicMathematique(Model model) {
        model.addAttribute("moduleId", 13);
        model.addAttribute("moduleTitle", "Logic Mathématique");
        model.addAttribute("moduleNumber", "03");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Prof. Ahmed Benali");
        model.addAttribute("duration", "15 hours");
        model.addAttribute("totalChapters", 7);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🧠");
        return "htmlstudent/2year/cours/module13-logic-mathematique";
    }
    
    @GetMapping("/cours/module/14")
    public String getModule14ArchitectureOrdinary(Model model) {
        model.addAttribute("moduleId", 14);
        model.addAttribute("moduleTitle", "Architecture of Ordinary");
        model.addAttribute("moduleNumber", "04");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Dr. Karim Hadjadj");
        model.addAttribute("duration", "6 hours");
        model.addAttribute("totalChapters", 4);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🖥️");
        return "htmlstudent/2year/cours/module14-architecture-ordinary";
    }
    
    @GetMapping("/cours/module/15")
    public String getModule15LongevityTheory(Model model) {
        model.addAttribute("moduleId", 15);
        model.addAttribute("moduleTitle", "Longevity Theory");
        model.addAttribute("moduleNumber", "05");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Dr. Rachid Ouali");
        model.addAttribute("duration", "10 hours");
        model.addAttribute("totalChapters", 5);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "⏳");
        return "htmlstudent/2year/cours/module15-longevity-theory";
    }
    
    // Semestre 2 - Modules 16-20
    @GetMapping("/cours/module/16")
    public String getModule16OperatingSystem(Model model) {
        model.addAttribute("moduleId", 16);
        model.addAttribute("moduleTitle", "Operating System");
        model.addAttribute("moduleNumber", "06");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Dr. Karim Hadjadj");
        model.addAttribute("duration", "8 hours");
        model.addAttribute("totalChapters", 6);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "⚙️");
        return "htmlstudent/2year/cours/module16-operating-system";
    }
    
    @GetMapping("/cours/module/17")
    public String getModule17Database(Model model) {
        model.addAttribute("moduleId", 17);
        model.addAttribute("moduleTitle", "Introduction to Database");
        model.addAttribute("moduleNumber", "07");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Dr. Sarah Mansouri");
        model.addAttribute("duration", "14 hours");
        model.addAttribute("totalChapters", 8);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🗄️");
        return "htmlstudent/2year/cours/module17-database";
    }
    
    @GetMapping("/cours/module/18")
    public String getModule18NetworkCommunication(Model model) {
        model.addAttribute("moduleId", 18);
        model.addAttribute("moduleTitle", "Network and Communication");
        model.addAttribute("moduleNumber", "08");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Hamadi");
        model.addAttribute("duration", "6 hours");
        model.addAttribute("totalChapters", 6);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🌐");
        return "htmlstudent/2year/cours/module18-network-communication";
    }
    
    @GetMapping("/cours/module/19")
    public String getModule19SoftwareEngineering(Model model) {
        model.addAttribute("moduleId", 19);
        model.addAttribute("moduleTitle", "Software Engineering");
        model.addAttribute("moduleNumber", "09");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Dr. Samir Khelil");
        model.addAttribute("duration", "20 hours");
        model.addAttribute("totalChapters", 8);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "📋");
        return "htmlstudent/2year/cours/module19-software-engineering";
    }
    
    @GetMapping("/cours/module/20")
    public String getModule20WebDevelopment(Model model) {
        model.addAttribute("moduleId", 20);
        model.addAttribute("moduleTitle", "Development of Web Applications");
        model.addAttribute("moduleNumber", "10");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Dr. Samir Khelil");
        model.addAttribute("duration", "4 hours");
        model.addAttribute("totalChapters", 5);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🌍");
        return "htmlstudent/2year/cours/module20-web-development";
    }
    
    // ========== MODULES 3ÈME ANNÉE (IDs 21-30) ==========
    
    // Semestre 1 - Modules 21-25
    @GetMapping("/cours/module/21")
    public String getModule21SoftwareEngineering2(Model model) {
        model.addAttribute("moduleId", 21);
        model.addAttribute("moduleTitle", "Software Engineering 02");
        model.addAttribute("moduleNumber", "01");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Dr. Samir Khelil");
        model.addAttribute("duration", "8 hours");
        model.addAttribute("totalChapters", 6);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🏗️");
        return "htmlstudent/3year/cours/module21-software-engineering2";
    }
    
    @GetMapping("/cours/module/22")
    public String getModule22Database2(Model model) {
        model.addAttribute("moduleId", 22);
        model.addAttribute("moduleTitle", "Database 02");
        model.addAttribute("moduleNumber", "02");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Dr. Sarah Mansouri");
        model.addAttribute("duration", "12 hours");
        model.addAttribute("totalChapters", 7);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🗄️");
        return "htmlstudent/3year/cours/module22-database2";
    }
    
    @GetMapping("/cours/module/23")
    public String getModule23Network2(Model model) {
        model.addAttribute("moduleId", 23);
        model.addAttribute("moduleTitle", "Network and Communication 02");
        model.addAttribute("moduleNumber", "03");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Hamadi");
        model.addAttribute("duration", "15 hours");
        model.addAttribute("totalChapters", 6);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🌐");
        return "htmlstudent/3year/cours/module23-network2";
    }
    
    @GetMapping("/cours/module/24")
    public String getModule24ProjectManagement(Model model) {
        model.addAttribute("moduleId", 24);
        model.addAttribute("moduleTitle", "Software Project Management");
        model.addAttribute("moduleNumber", "04");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Dr. Rachid Ouali");
        model.addAttribute("duration", "6 hours");
        model.addAttribute("totalChapters", 5);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "📊");
        return "htmlstudent/3year/cours/module24-project-management";
    }
    
    @GetMapping("/cours/module/25")
    public String getModule25WebDevelopment2(Model model) {
        model.addAttribute("moduleId", 25);
        model.addAttribute("moduleTitle", "Development of Web Applications");
        model.addAttribute("moduleNumber", "05");
        model.addAttribute("semester", "Semester 1");
        model.addAttribute("instructor", "Dr. Samir Khelil");
        model.addAttribute("duration", "10 hours");
        model.addAttribute("totalChapters", 6);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🌍");
        return "htmlstudent/3year/cours/module25-web-development2";
    }
    
    // Semestre 2 - Modules 26-30
    @GetMapping("/cours/module/26")
    public String getModule26CompetingApplications(Model model) {
        model.addAttribute("moduleId", 26);
        model.addAttribute("moduleTitle", "Development of Competing Applications");
        model.addAttribute("moduleNumber", "06");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Dr. Samir Khelil");
        model.addAttribute("duration", "8 hours");
        model.addAttribute("totalChapters", 5);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "⚡");
        return "htmlstudent/3year/cours/module26-competing-applications";
    }
    
    @GetMapping("/cours/module/27")
    public String getModule27SoftwareMaintenance(Model model) {
        model.addAttribute("moduleId", 27);
        model.addAttribute("moduleTitle", "Software Maintenance");
        model.addAttribute("moduleNumber", "07");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Dr. Rachid Ouali");
        model.addAttribute("duration", "14 hours");
        model.addAttribute("totalChapters", 6);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🔧");
        return "htmlstudent/3year/cours/module27-software-maintenance";
    }
    
    @GetMapping("/cours/module/28")
    public String getModule28QualityTest(Model model) {
        model.addAttribute("moduleId", 28);
        model.addAttribute("moduleTitle", "Software Quality Test");
        model.addAttribute("moduleNumber", "08");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Dr. Sarah Mansouri");
        model.addAttribute("duration", "6 hours");
        model.addAttribute("totalChapters", 5);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "✅");
        return "htmlstudent/3year/cours/module28-quality-test";
    }
    
    @GetMapping("/cours/module/29")
    public String getModule29InteractiveSoftware(Model model) {
        model.addAttribute("moduleId", 29);
        model.addAttribute("moduleTitle", "Interactive Software Development");
        model.addAttribute("moduleNumber", "09");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Dr. Karim Hadjadj");
        model.addAttribute("duration", "20 hours");
        model.addAttribute("totalChapters", 6);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "🎮");
        return "htmlstudent/3year/cours/module29-interactive-software";
    }
    
    @GetMapping("/cours/module/30")
    public String getModule30GitGitHub(Model model) {
        model.addAttribute("moduleId", 30);
        model.addAttribute("moduleTitle", "Git and GitHub");
        model.addAttribute("moduleNumber", "10");
        model.addAttribute("semester", "Semester 2");
        model.addAttribute("instructor", "Dr. Samir Khelil");
        model.addAttribute("duration", "4 hours");
        model.addAttribute("totalChapters", 4);
        model.addAttribute("completedChapters", 0);
        model.addAttribute("progress", 0);
        model.addAttribute("moduleIcon", "📦");
        return "htmlstudent/3year/cours/module30-git-github";
    }
}