package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Activity;
import com.votredomaine.modelememoire.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ActivityService {
    
    @Autowired
    private ActivityRepository activityRepository;
    
    public void saveActivity(Activity activity) {
        activityRepository.save(activity);
    }
    
    public List<Activity> getRecentActivities() {
        return activityRepository.findTop5ByOrderByCreatedAtDesc();
    }
    
    public List<Activity> getAllActivities() {
        return activityRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    public void logCoursePublished(String teacherName, String courseTitle, Long courseId) {
        Activity activity = new Activity(
            "COURSE_PUBLISHED",
            "a publié le cours \"" + courseTitle + "\"",
            teacherName,
            "TEACHER"
        );
        activity.setTargetId(courseId);
        activity.setTargetName(courseTitle);
        activityRepository.save(activity);
        System.out.println("📝 Activité enregistrée: " + teacherName + " a publié un cours");
    }
    
    public void logUserRegistered(String userName, String role) {
        Activity activity = new Activity(
            "USER_REGISTERED",
            "s'est inscrit comme " + role.toLowerCase(),
            userName,
            role
        );
        activityRepository.save(activity);
        System.out.println("📝 Activité enregistrée: " + userName + " s'est inscrit");
    }
    
    public void logCourseUpdated(String teacherName, String courseTitle, Long courseId) {
        Activity activity = new Activity(
            "COURSE_UPDATED",
            "a mis à jour le cours \"" + courseTitle + "\"",
            teacherName,
            "TEACHER"
        );
        activity.setTargetId(courseId);
        activity.setTargetName(courseTitle);
        activityRepository.save(activity);
        System.out.println("📝 Activité enregistrée: " + teacherName + " a mis à jour un cours");
    }
    
    public void logCourseDownloaded(String studentName, String courseTitle, Long courseId) {
        Activity activity = new Activity(
            "COURSE_DOWNLOADED",
            "a téléchargé le cours \"" + courseTitle + "\"",
            studentName,
            "STUDENT"
        );
        activity.setTargetId(courseId);
        activity.setTargetName(courseTitle);
        activityRepository.save(activity);
        System.out.println("📝 Activité enregistrée: " + studentName + " a téléchargé un cours");
    }
}