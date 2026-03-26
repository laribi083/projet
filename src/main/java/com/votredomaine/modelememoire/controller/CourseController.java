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
    
    // ========== MODULES 1ÈRE ANNÉE ==========
    
    @GetMapping("/cours/module/1")
    public String getModule1() {
        return "htmlstudent/1year/cours/Algebra01";
    }
    
    @GetMapping("/cours/module/2")
    public String getModule2() {
        return "htmlstudent/1year/cours/Ana";
    }
    
    @GetMapping("/cours/module/3")
    public String getModule3() {
        return "htmlstudent/1year/cours/CRI";
    }
    
    @GetMapping("/cours/module/4")
    public String getModule4() {
        return "htmlstudent/1year/cours/tricité";
    }
    
    @GetMapping("/cours/module/5")
    public String getModule5() {
        return "htmlstudent/1year/cours/codage";
    }
    
    @GetMapping("/cours/module/6")
    public String getModule6() {
        return "htmlstudent/1year/cours/Algebra02";
    }
    
    @GetMapping("/cours/module/7")
    public String getModule7() {
        return "htmlstudent/1year/cours/Ana2";
    }
    
    @GetMapping("/cours/module/8")
    public String getModule8() {
        return "htmlstudent/1year/cours/Ipoo";
    }
    
    @GetMapping("/cours/module/9")
    public String getModule9() {
        return "htmlstudent/1year/cours/SM";
    }
    
    @GetMapping("/cours/module/10")
    public String getModule10() {
        return "htmlstudent/1year/cours/ASD1";
    }
    
    // ========== MODULES 2ÈME ANNÉE ==========
    
    @GetMapping("/cours/module/11")
    public String getModule11() {
        return "htmlstudent/2year/cours/pooa";
    }
    
    @GetMapping("/cours/module/12")
    public String getModule12() {
        return "htmlstudent/2year/cours/ASD";
    }
    
    @GetMapping("/cours/module/13")
    public String getModule13() {
        return "htmlstudent/2year/cours/LM";
    }
    
    @GetMapping("/cours/module/14")
    public String getModule14() {
        return "htmlstudent/2year/cours/Ao";
    }
    
    @GetMapping("/cours/module/15")
    public String getModule15() {
        return "htmlstudent/2year/cours/TL";
    }
    
    @GetMapping("/cours/module/16")
    public String getModule16() {
        return "htmlstudent/2year/cours/Se";
    }
    
    @GetMapping("/cours/module/17")
    public String getModule17() {
        return "htmlstudent/2year/cours/BDA";
    }
    
    @GetMapping("/cours/module/18")
    public String getModule18() {
        return "htmlstudent/2year/cours/RC";
    }
    
    @GetMapping("/cours/module/19")
    public String getModule19() {
        return "htmlstudent/2year/cours/GL";
    }
    
    @GetMapping("/cours/module/20")
    public String getModule20() {
        return "htmlstudent/2year/cours/Daw";
    }
    
    // ========== MODULES 3ÈME ANNÉE ==========
    
    @GetMapping("/cours/module/21")
    public String getModule21() {
        return "htmlstudent/3year/cours/Gl2";
    }
    
    @GetMapping("/cours/module/22")
    public String getModule22() {
        return "htmlstudent/3year/cours/bd2";
    }
    
    @GetMapping("/cours/module/23")
    public String getModule23() {
        return "htmlstudent/3year/cours/Rc2";
    }
    
    @GetMapping("/cours/module/24")
    public String getModule24() {
        return "htmlstudent/3year/cours/gpl";
    }
    
    @GetMapping("/cours/module/25")
    public String getModule25() {
        return "htmlstudent/3year/cours/Daw2";
    }
    
    @GetMapping("/cours/module/26")
    public String getModule26() {
        return "htmlstudent/3year/cours/dac";
    }
    
    @GetMapping("/cours/module/27")
    public String getModule27() {
        return "htmlstudent/3year/cours/mel";
    }
    
    @GetMapping("/cours/module/28")
    public String getModule28() {
        return "htmlstudent/3year/cours/tql";
    }
    
    @GetMapping("/cours/module/29")
    public String getModule29() {
        return "htmlstudent/3year/cours/dli";
    }
    
    @GetMapping("/cours/module/30")
    public String getModule30() {
        return "htmlstudent/3year/cours/git";
    }
}