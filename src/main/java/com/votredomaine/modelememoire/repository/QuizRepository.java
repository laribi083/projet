// QuizRepository.java
package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByTeacherId(Long teacherId);
    List<Quiz> findByCourseId(Long courseId);
    List<Quiz> findByCourseModuleAndCourseNiveau(String module, String niveau);
    List<Quiz> findByStatus(String status);
}