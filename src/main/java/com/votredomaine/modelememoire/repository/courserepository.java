package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface courserepository extends JpaRepository<Course, Long> {
    List<Course> findByTeacherId(Long teacherId);
    List<Course> findByTeacherIdAndStatus(Long teacherId, String status);
    List<Course> findByNiveau(String niveau);
    List<Course> findByNiveauAndStatus(String niveau, String status);
    List<Course> findByStatus(String status);
    long countByTeacherId(Long teacherId);
}