package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    List<Quiz> findByTeacherId(Long teacherId);
    
    List<Quiz> findByCourseId(Long courseId);
    
    List<Quiz> findByCourseIdAndStatus(Long courseId, String status);
    
    @Query("SELECT q FROM Quiz q WHERE q.module = :module AND q.niveau = :niveau")
    List<Quiz> findByCourseModuleAndCourseNiveau(@Param("module") String module, @Param("niveau") String niveau);
    
    List<Quiz> findByStatus(String status);
    
    long countByCourseId(Long courseId);
    
    @Query("SELECT q FROM Quiz q WHERE q.courseId = :courseId AND q.status = 'ACTIVE'")
    List<Quiz> findActiveQuizzesByCourse(@Param("courseId") Long courseId);
    
    List<Quiz> findByTitleContainingIgnoreCase(String title);
    
    List<Quiz> findByModule(String module);
    
    List<Quiz> findByNiveau(String niveau);
    
    // ⭐ MÉTHODE AJOUTÉE POUR LA SÉCURITÉ ⭐
    boolean existsById(Long id);
}