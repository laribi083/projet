package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Teacher;
import com.votredomaine.modelememoire.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class TeacherService {
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public Teacher registerTeacher(String name, String email, String password, String department, String phone) {
        // Vérifier si le teacher existe déjà
        if (teacherRepository.existsByEmail(email)) {
            throw new RuntimeException("Cet email est déjà utilisé par un teacher");
        }
        
        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setEmail(email);
        teacher.setPassword(passwordEncoder.encode(password));
        teacher.setDepartment(department);
        teacher.setPhone(phone);
        
        return teacherRepository.save(teacher);
    }
    
    public Optional<Teacher> loginTeacher(String email, String password) {
        Optional<Teacher> teacherOpt = teacherRepository.findByEmail(email);
        
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            if (passwordEncoder.matches(password, teacher.getPassword())) {
                return teacherOpt;
            }
        }
        return Optional.empty();
    }
    
    public Optional<Teacher> findByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }
    
    public boolean isTeacher(String email) {
        return teacherRepository.findByEmail(email).isPresent();
    }
    
    // ✅ AJOUTEZ CETTE MÉTHODE
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }
}