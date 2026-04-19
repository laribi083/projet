package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.model.Rating;
import com.votredomaine.modelememoire.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public Rating addOrUpdateRating(Long courseId, Long studentId, String studentName, Integer ratingValue, String comment) {
        Optional<Rating> existingRating = ratingRepository.findByCourseIdAndStudentId(courseId, studentId);
        
        if (existingRating.isPresent()) {
            Rating rating = existingRating.get();
            rating.setRating(ratingValue);
            rating.setComment(comment);
            rating.setUpdatedAt(java.time.LocalDateTime.now());
            return ratingRepository.save(rating);
        } else {
            Rating rating = new Rating(courseId, studentId, studentName, ratingValue, comment);
            return ratingRepository.save(rating);
        }
    }
    
    @Transactional
    public void deleteRating(Long ratingId, Long studentId) {
        Optional<Rating> rating = ratingRepository.findById(ratingId);
        if (rating.isPresent() && rating.get().getStudentId().equals(studentId)) {
            ratingRepository.deleteById(ratingId);
        }
    }
    
    @Transactional
    public void deleteRatingsByCourse(Long courseId) {
        ratingRepository.deleteByCourseId(courseId);
    }
    
    public List<Rating> getRatingsByCourse(Long courseId) {
        return ratingRepository.findByCourseId(courseId);
    }
    
    public List<Rating> getRatingsByStudent(Long studentId) {
        return ratingRepository.findByStudentId(studentId);
    }
    
    public double getAverageRating(Long courseId) {
        Double avg = ratingRepository.getAverageRatingByCourseId(courseId);
        return avg != null ? avg : 0.0;
    }
    
    public long getRatingCount(Long courseId) {
        return ratingRepository.countByCourseId(courseId);
    }
    
    public boolean hasRated(Long courseId, Long studentId) {
        return ratingRepository.existsByCourseIdAndStudentId(courseId, studentId);
    }
    
    public Optional<Rating> getStudentRating(Long courseId, Long studentId) {
        return ratingRepository.findByCourseIdAndStudentId(courseId, studentId);
    }
    
    public Map<String, Object> getRatingStats(Long courseId) {
        Map<String, Object> stats = new HashMap<>();
        List<Rating> ratings = ratingRepository.findByCourseId(courseId);
        long totalRatings = ratings.size();
        double average = getAverageRating(courseId);
        
        int[] distribution = new int[5];
        for (Rating rating : ratings) {
            int stars = rating.getRating();
            if (stars >= 1 && stars <= 5) {
                distribution[stars - 1]++;
            }
        }
        
        stats.put("totalRatings", totalRatings);
        stats.put("averageRating", Math.round(average * 10) / 10.0);
        stats.put("distribution", distribution);
        
        return stats;
    }
}