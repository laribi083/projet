// courserepository.java - COMPLET
package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.Course;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface courserepository extends JpaRepository<Course, Long> {
    
    // ========== MÉTHODES EXISTANTES ==========
    
    List<Course> findByTeacherId(Long teacherId);
    
    List<Course> findByNiveau(String niveau);
    
    List<Course> findByNiveauAndStatus(String niveau, String status);
    
    List<Course> findByStatus(String status);
    
    long countByTeacherId(Long teacherId);
    
    long countByStatus(String status);
    
    long countByNiveau(String niveau);
    
    List<Course> findByModuleAndNiveau(String module, String niveau);
    
    List<Course> findByModuleAndNiveauAndStatus(String module, String niveau, String status);
    
    List<Course> findByTitleContainingIgnoreCase(String title);
    
    List<Course> findByModule(String module);
    
    List<Course> findByTeacherNameContainingIgnoreCase(String teacherName);
    
    List<Course> findByTeacherIdAndStatus(Long teacherId, String status);
    
    @Query("SELECT DISTINCT c.status FROM Course c WHERE c.teacherId = :teacherId")
    List<String> findDistinctStatusByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' ORDER BY c.createdAt DESC")
    List<Course> findAllPublishedCoursesOrderByDate();
    
    @Query("SELECT c FROM Course c WHERE c.status = 'PENDING' ORDER BY c.createdAt DESC")
    List<Course> findAllPendingCoursesOrderByDate();
    
    @Query("SELECT c FROM Course c WHERE c.status = 'VALIDATED' ORDER BY c.createdAt DESC")
    List<Course> findAllValidatedCoursesOrderByDate();
    
    @Query("SELECT c FROM Course c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:niveau IS NULL OR c.niveau = :niveau) AND " +
           "(:module IS NULL OR c.module = :module) AND " +
           "(:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.teacherName) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Course> searchCourses(@Param("status") String status,
                               @Param("niveau") String niveau,
                               @Param("module") String module,
                               @Param("search") String search);
    
    // ========== NOUVELLES MÉTHODES POUR LE CHATBOT ==========
    
    /**
     * Compte tous les cours
     */
    @Query("SELECT COUNT(c) FROM Course c")
    long countAll();
    
    /**
     * Trouve les cours par module (insensible à la casse)
     */
    List<Course> findByModuleIgnoreCase(String module);
    
    /**
     * Trouve les cours les plus populaires (par nombre de téléchargements)
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' ORDER BY c.downloadCount DESC")
    List<Course> findTopPopularCourses(Pageable pageable);
    
    /**
     * Compte les cours par niveau
     */
    @Query("SELECT c.niveau, COUNT(c) FROM Course c GROUP BY c.niveau")
    List<Object[]> countCoursesByNiveau();
    
    /**
     * Compte les cours par statut
     */
    @Query("SELECT c.status, COUNT(c) FROM Course c GROUP BY c.status")
    List<Object[]> countCoursesByStatus();
    
    /**
     * Récupère les cours récemment publiés
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' ORDER BY c.createdAt DESC")
    List<Course> findRecentlyPublishedCourses(Pageable pageable);
    
    /**
     * Récupère les cours par niveau et module (recherche avancée)
     */
    @Query("SELECT c FROM Course c WHERE " +
           "(:niveau IS NULL OR c.niveau = :niveau) AND " +
           "(:module IS NULL OR LOWER(c.module) LIKE LOWER(CONCAT('%', :module, '%'))) AND " +
           "c.status = 'PUBLISHED'")
    List<Course> findCoursesByNiveauAndModule(@Param("niveau") String niveau, 
                                               @Param("module") String module);
    
    /**
     * Récupère les cours avec le plus d'étudiants inscrits
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' ORDER BY c.downloadCount DESC")
    List<Course> findMostEnrolledCourses(Pageable pageable);
    
    /**
     * Récupère les statistiques des cours par enseignant
     */
    @Query("SELECT c.teacherId, COUNT(c), AVG(c.downloadCount) FROM Course c GROUP BY c.teacherId")
    List<Object[]> getCourseStatisticsByTeacher();
    
    /**
     * Vérifie si un cours existe par titre
     */
    boolean existsByTitleIgnoreCase(String title);
    
    /**
     * Récupère les cours par statut avec pagination
     */
    List<Course> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    
    /**
     * Récupère le nombre total de téléchargements pour un enseignant
     */
    @Query("SELECT SUM(c.downloadCount) FROM Course c WHERE c.teacherId = :teacherId")
    Long getTotalDownloadsByTeacherId(@Param("teacherId") Long teacherId);
}