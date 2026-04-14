package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Admin;
import com.votredomaine.modelememoire.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public Admin registerAdmin(String username, String email, String password, String fullName) {
        if (adminRepository.existsByEmail(email)) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }
        if (adminRepository.existsByUsername(username)) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà utilisé");
        }
        
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setFullName(fullName);
        admin.setRole("ADMIN");
        admin.setIsActive(true);
        
        return adminRepository.save(admin);
    }
    
    public Optional<Admin> loginAdmin(String email, String password) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (admin.getIsActive() && passwordEncoder.matches(password, admin.getPassword())) {
                return adminOpt;
            }
        }
        return Optional.empty();
    }
    
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }
    
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
    
    public long countAdmins() {
        return adminRepository.count();
    }
}