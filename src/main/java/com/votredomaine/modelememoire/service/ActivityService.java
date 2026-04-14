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
            "published course: \"" + courseTitle + "\"",
            teacherName,
            "TEACHER"
        );
        activity.setTargetId(courseId);
        activity.setTargetName(courseTitle);
        activityRepository.save(activity);
        System.out.println("📝 Activity: " + teacherName + " published a course");
    }
    
    public void logUserRegistered(String userName, String role) {
        Activity activity = new Activity(
            "USER_REGISTERED",
            "registered as " + role.toLowerCase(),
            userName,
            role
        );
        activityRepository.save(activity);
        System.out.println("📝 Activity: " + userName + " registered");
    }
    
    public void logCourseUpdated(String teacherName, String courseTitle, Long courseId) {
        Activity activity = new Activity(
            "COURSE_UPDATED",
            "updated course: \"" + courseTitle + "\"",
            teacherName,
            "TEACHER"
        );
        activity.setTargetId(courseId);
        activity.setTargetName(courseTitle);
        activityRepository.save(activity);
        System.out.println("📝 Activity: " + teacherName + " updated a course");
    }
    
    public void logCourseDownloaded(String studentName, String courseTitle, Long courseId) {
        Activity activity = new Activity(
            "COURSE_DOWNLOADED",
            "downloaded course: \"" + courseTitle + "\"",
            studentName,
            "STUDENT"
        );
        activity.setTargetId(courseId);
        activity.setTargetName(courseTitle);
        activityRepository.save(activity);
        System.out.println("📝 Activity: " + studentName + " downloaded a course");
    }
}