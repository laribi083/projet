package com.modelememoire.model;

public class Utilisateur {
    private int id;
    private String email;
    private String motDePasse;
    private String telephone;
    
    // Constructeurs
    public Utilisateur() {}
    
    public Utilisateur(String email, String motDePasse, String telephone) {
        this.email = email;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    @Override
    public String toString() {
        return "Utilisateur{id=" + id + ", email='" + email + "', telephone='" + telephone + "'}";
    }
}
