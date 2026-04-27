package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.model.Rating;
import com.votredomaine.modelememoire.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RatingService {
    
    @Autowired
    private RatingRepository ratingRepository;
    
    @Autowired
    private Courseservice courseService;
    
    @Transactional
    public Rating saveOrUpdateRating(Long studentId, String studentName, Long courseId, Integer ratingValue, String comment) {
        Optional<Rating> existingRating = ratingRepository.findByStudentIdAndCourseId(studentId, courseId);
        
        Rating rating;
        if (existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setRatingValue(ratingValue);
            rating.setComment(comment);
            rating.setUpdatedAt(LocalDateTime.now());
        } else {
            rating = new Rating();
            rating.setStudentId(studentId);
            rating.setStudentName(studentName);
            rating.setCourseId(courseId);
            rating.setRatingValue(ratingValue);
            rating.setComment(comment);
            
            Course course = courseService.getCourseById(courseId);
            if (course != null) {
                rating.setCourseTitle(course.getTitle());
            }
        }
        
        return ratingRepository.save(rating);
    }
    
    public List<Rating> getRatingsByCourse(Long courseId) {
        return ratingRepository.findByCourseId(courseId);
    }
    
    public List<Rating> getRatingsByStudent(Long studentId) {
        return ratingRepository.findByStudentId(studentId);
    }
    
    public List<Rating> getRatingsByTeacher(Long teacherId) {
        return ratingRepository.findByTeacherId(teacherId);
    }
    
    public Optional<Rating> getRatingByStudentAndCourse(Long studentId, Long courseId) {
        return ratingRepository.findByStudentIdAndCourseId(studentId, courseId);
    }
    
    public boolean hasRated(Long studentId, Long courseId) {
        return ratingRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }
    
    public double getAverageRatingForCourse(Long courseId) {
        Double avg = ratingRepository.getAverageRatingByCourseId(courseId);
        return avg != null ? avg : 0;
    }
    
    public long getRatingCountForCourse(Long courseId) {
        return ratingRepository.countByCourseId(courseId);
    }
    
    public Map<String, Object> getRatingStatsForCourse(Long courseId) {
        Map<String, Object> stats = new HashMap<>();
        double average = getAverageRatingForCourse(courseId);
        long count = getRatingCountForCourse(courseId);
        
        stats.put("average", Math.round(average * 10) / 10.0);
        stats.put("count", count);
        stats.put("stars", getStarDistribution(courseId));
        
        return stats;
    }
    
    private Map<Integer, Long> getStarDistribution(Long courseId) {
        List<Rating> ratings = ratingRepository.findByCourseId(courseId);
        Map<Integer, Long> distribution = new HashMap<>();
        
        for (int i = 1; i <= 5; i++) {
            final int star = i;
            long count = ratings.stream().filter(r -> r.getRatingValue() == star).count();
            distribution.put(star, count);
        }
        
        return distribution;
    }
    
    @Transactional
    public void deleteRating(Long ratingId, Long studentId) {
        Optional<Rating> rating = ratingRepository.findById(ratingId);
        if (rating.isPresent() && rating.get().getStudentId().equals(studentId)) {
            ratingRepository.deleteById(ratingId);
        }
    }
}