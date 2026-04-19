package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    List<Rating> findByCourseId(Long courseId);
    
    List<Rating> findByStudentId(Long studentId);
    
    Optional<Rating> findByCourseIdAndStudentId(Long courseId, Long studentId);
    
    long countByCourseId(Long courseId);
    
    boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);
    
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.courseId = :courseId")
    Double getAverageRatingByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT r.courseId, AVG(r.rating) as avgRating, COUNT(r) as count FROM Rating r " +
           "GROUP BY r.courseId ORDER BY avgRating DESC")
    List<Object[]> findTopRatedCourses();
    
    void deleteByCourseId(Long courseId);
}