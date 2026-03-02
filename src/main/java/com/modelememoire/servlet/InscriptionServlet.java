package com.modelememoire.servlet;

import com.modelememoire.dao.UtilisateurDAO;
import com.modelememoire.model.Utilisateur;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/inscription")
public class InscriptionServlet extends HttpServlet {
    
    private UtilisateurDAO utilisateurDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
        gson = new Gson();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        PrintWriter out = resp.getWriter();
        JsonObject jsonResponse = new JsonObject();
        
        try {
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            Utilisateur utilisateur = gson.fromJson(sb.toString(), Utilisateur.class);
            
            // Validation
            if (utilisateur.getEmail() == null || utilisateur.getEmail().trim().isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "L'email est requis");
                out.print(gson.toJson(jsonResponse));
                return;
            }
            
            if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().length() < 6) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Le mot de passe doit contenir au moins 6 caractères");
                out.print(gson.toJson(jsonResponse));
                return;
            }
            
            // Vérifier si l'email existe
            if (utilisateurDAO.emailExiste(utilisateur.getEmail())) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Cet email est déjà utilisé");
                out.print(gson.toJson(jsonResponse));
                return;
            }
            
            // Ajouter l'utilisateur
            boolean success = utilisateurDAO.ajouterUtilisateur(utilisateur);
            
            if (success) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Inscription réussie!");
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Erreur lors de l'inscription");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Erreur serveur: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
