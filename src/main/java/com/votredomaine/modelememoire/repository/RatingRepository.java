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
    
    Optional<Rating> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    
    @Query("SELECT r FROM Rating r WHERE r.courseId IN (SELECT c.id FROM Course c WHERE c.teacherId = :teacherId)")
    List<Rating> findByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.courseId = :courseId")
    Double getAverageRatingByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.courseId = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
    
    List<Rating> findTop5ByOrderByCreatedAtDesc();
    
    @Query("SELECT r.courseId, AVG(r.ratingValue), COUNT(r) FROM Rating r GROUP BY r.courseId")
    List<Object[]> getAverageRatingsByCourse();
}