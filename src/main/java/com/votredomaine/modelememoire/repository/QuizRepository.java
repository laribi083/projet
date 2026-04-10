package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    // Trouver tous les quiz d'un enseignant
    List<Quiz> findByTeacherId(Long teacherId);
    
    // Trouver les quiz d'un cours
    List<Quiz> findByCourseId(Long courseId);
    
    // Trouver les quiz actifs d'un cours
    List<Quiz> findByCourseIdAndStatus(Long courseId, String status);
    
    // Trouver les quiz par module et niveau
    @Query("SELECT q FROM Quiz q WHERE q.module = :module AND q.niveau = :niveau")
    List<Quiz> findByCourseModuleAndCourseNiveau(@Param("module") String module, @Param("niveau") String niveau);
    
    // Trouver les quiz actifs
    List<Quiz> findByStatus(String status);
    
    // Compter les quiz d'un cours
    long countByCourseId(Long courseId);
    
    // Requête personnalisée pour les quiz actifs d'un cours
    @Query("SELECT q FROM Quiz q WHERE q.courseId = :courseId AND q.status = 'ACTIVE'")
    List<Quiz> findActiveQuizzesByCourse(@Param("courseId") Long courseId);
    
   
    List<Quiz> findByTitleContainingIgnoreCase(String title);
    
    
    List<Quiz> findByModule(String module);
    
    
    List<Quiz> findByNiveau(String niveau);
}