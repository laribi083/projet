package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface courserepository extends JpaRepository<Course, Long> {
    
    // ========== MÉTHODES POUR LES ENSEIGNANTS ==========
    List<Course> findByTeacherId(Long teacherId);
    long countByTeacherId(Long teacherId);
    
    // ========== MÉTHODES POUR LES ÉTUDIANTS ==========
    List<Course> findByNiveau(String niveau);
    List<Course> findByNiveauAndStatus(String niveau, String status);
    
    // ⭐ CORRIGÉ : Méthode pour filtrer par module et niveau
    List<Course> findByModuleAndNiveau(String module, String niveau);
    
    // Méthode avec statut
    List<Course> findByModuleAndNiveauAndStatus(String module, String niveau, String status);
    
    List<Course> findByStatus(String status);
    
    @Query("SELECT c FROM Course c WHERE LOWER(c.module) = LOWER(:module) AND c.niveau = :niveau")
    List<Course> findCoursesByModuleIgnoreCase(@Param("module") String module, @Param("niveau") String niveau);
    
    /**
     * Recherche les cours contenant un texte dans le module
     */
    @Query("SELECT c FROM Course c WHERE LOWER(c.module) LIKE LOWER(CONCAT('%', :keyword, '%')) AND c.niveau = :niveau")
    List<Course> findCoursesByModuleContaining(@Param("keyword") String keyword, @Param("niveau") String niveau);
}